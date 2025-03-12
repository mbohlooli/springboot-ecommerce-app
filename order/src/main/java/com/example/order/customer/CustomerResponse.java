package com.example.order.customer;

public record CustomerResponse(
    String id,
    String firstName,
    String lastName,
    String email
) { }
