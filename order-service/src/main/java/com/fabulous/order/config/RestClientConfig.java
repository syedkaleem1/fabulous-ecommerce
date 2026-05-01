package com.fabulous.order.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient cartServiceRestClient(@Value("${cart.service.url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient inventoryServiceRestClient(@Value("${inventory.service.url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }

    @Bean
    public RestClient paymentServiceRestClient(@Value("${payment.service.url}") String baseUrl) {
        return RestClient.builder().baseUrl(baseUrl).build();
    }
}
