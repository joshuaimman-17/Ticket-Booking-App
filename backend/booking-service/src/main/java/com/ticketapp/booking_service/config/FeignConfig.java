package com.ticketapp.booking_service.config;
import feign.Client;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import feign.httpclient.ApacheHttpClient;

@Configuration
public class FeignConfig {

    @Bean
    public Client feignClient() {
        // Use Apache HttpClient which supports PATCH
        return new ApacheHttpClient(HttpClients.createDefault());
    }
}
