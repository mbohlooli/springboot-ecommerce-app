package com.example.order.orderline;

import com.example.order.order.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderLineMapper {
    public OrderLine toOrderLine(OrderLineRequest request) {
        return OrderLine.builder()
            .id(request.id())
            .quantity(request.quantity())
            .order(
                Order.builder()
                    .id(request.orderId())
                    .build()
            )
            .productId(request.productId())
            .build();
    }

    public OrderLineResponse toOrderLineResponse(OrderLine orderLine) {
        return new OrderLineResponse(orderLine.getId(), orderLine.getQuantity());
    }
}
