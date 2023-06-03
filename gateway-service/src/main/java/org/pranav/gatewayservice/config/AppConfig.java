package org.pranav.gatewayservice.config;

import lombok.AllArgsConstructor;
import org.pranav.gatewayservice.client.IdentityClient;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@AllArgsConstructor
public class AppConfig {

    private final LoadBalancedExchangeFilterFunction filterFunction;

    @Bean
    public WebClient client() {
        return WebClient.builder()
                .baseUrl("http://IDENTITY-SERVICE")
                .filter(filterFunction)
                .build();
    }

    @Bean
    public IdentityClient identityClient() {
        HttpServiceProxyFactory httpServiceProxyFactory
                = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client()))
                .build();

        return httpServiceProxyFactory.createClient(IdentityClient.class);
    }

    @Bean
    //@LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
