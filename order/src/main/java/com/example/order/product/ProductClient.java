package com.example.order.product;

import com.example.order.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductClient {
    @Value("${application.config.product-url}")
    private String productUrl;

    private final RestTemplate restTemplate;

    public List<PurchaseResponse> purchaseProducts(List<PurchaseRequest> requestBody) {
        var headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        var requestEntity = new HttpEntity<>(requestBody, headers);
        var responseType = new ParameterizedTypeReference<List<PurchaseResponse>>() {};
        var purchaseResponse = restTemplate.exchange(
            productUrl + "/purchase",
            HttpMethod.POST,
            requestEntity,
            responseType
        );

        if(purchaseResponse.getStatusCode().isError())
            throw new BusinessException("An error occurred while trying to retrieve the purchase response " + purchaseResponse.getStatusCode());

        return purchaseResponse.getBody();
    }
}
