package org.pranav.gatewayservice.config;

import lombok.AllArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@AllArgsConstructor
public class AppConfig {

    private final LoadBalancedExchangeFilterFunction filterFunction;

    @Bean
    public WebClient client() {
        return WebClient.builder()
                .baseUrl("lb://IDENTITY-SERVICE")
                .filter(filterFunction)
                .build();
    }

    /*@Bean
    //@LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }*/

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }
}
