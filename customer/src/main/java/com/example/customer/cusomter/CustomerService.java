package com.example.customer.cusomter;

import com.example.customer.exception.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public String createCustomer(@Valid CustomerRequest request) {
        var customer = repository.save(mapper.toCustomer(request));
        return customer.getId();
    }

    public void updateCustomer(@Valid CustomerRequest request) {
        var customer = repository.findById(request.id())
            .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        mergeCustomer(customer, request);
        repository.save(customer);
    }

    public List<CustomerResponse> findAll() {
        return repository.findAll()
            .stream()
            .map(mapper::fromCustomer)
            .toList();
    }

    public CustomerResponse findById(String customerId) {
        return repository.findById(customerId)
            .map(mapper::fromCustomer)
            .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }

    public Boolean existsById(String customerId) {
        return repository.existsById(customerId);
    }

    public void deleteCustomer(String customerId) {
        repository.deleteById(customerId);
    }

    private void mergeCustomer(Customer customer, CustomerRequest request) {
        if (StringUtils.isNotBlank(request.firstName()))
            customer.setFirstName(request.firstName());
        if (StringUtils.isNotBlank(request.lastName()))
            customer.setLastName(request.lastName());
        if (StringUtils.isNotBlank(request.email()))
            customer.setEmail(request.email());
        if (request.address() != null)
            customer.setAddress(request.address());
    }
}
