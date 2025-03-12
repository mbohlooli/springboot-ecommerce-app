package com.example.order.order;

import com.example.order.exception.BusinessException;
import com.example.order.customer.CustomerClient;
import com.example.order.kafka.OrderConfirmation;
import com.example.order.kafka.OrderProducer;
import com.example.order.orderline.OrderLineRequest;
import com.example.order.orderline.OrderLineService;
import com.example.order.payment.PaymentClient;
import com.example.order.payment.PaymentRequest;
import com.example.order.product.ProductClient;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderRepository repository;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(@Valid OrderRequest request) {
        var customer = customerClient.findCustomerById(request.customerId())
            .orElseThrow(() -> new BusinessException("Cannot create order:: no customer exists with customer id " + request.customerId()));

        var purchasedProducts = productClient.purchaseProducts(request.products());

        var order = repository.save(mapper.toOrder(request));

        for (var purchaseRequest: request.products()) {
            orderLineService.saveOrderLine(
                new OrderLineRequest(
                    null,
                    order.getId(),
                    purchaseRequest.productId(),
                    purchaseRequest.quantity()
                )
            );
        }

        paymentClient.requestOrderPayment(
            new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
            )
        );

        orderProducer.sendOrderConfirmation(
            new OrderConfirmation(
                request.reference(),
                request.amount(),
                request.paymentMethod(),
                customer,
                purchasedProducts
            )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
            .stream()
            .map(mapper::fromOrder)
            .toList();
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
            .map(mapper::fromOrder)
            .orElseThrow(() -> new EntityNotFoundException("No order found with id " + orderId));
    }
}
