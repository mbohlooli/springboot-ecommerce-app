package com.example.customer.cusomter;

public record CustomerResponse (
    String id,
    String firstName,
    String lastName,
    String email,
    Address address
) { }
