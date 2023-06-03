package org.pranav.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.pranav.gatewayservice.client.IdentityClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final IdentityClient identityClient;
    private final RestTemplate restTemplate;

    public AuthenticationFilter(IdentityClient identityClient, RestTemplate restTemplate) {
        super(Config.class);
        this.identityClient = identityClient;
        this.restTemplate = restTemplate;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            if (!Arrays.asList("/app/home", "/restaurant/home", "/auth/register", "/auth/token", "/eureka")
                    .contains(exchange.getRequest().getURI().getPath())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("missing authorization header");
                }

                String authHeader = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION))
                        .get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        HttpEntity requestEntity = new HttpEntity(headers);
                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:8084/auth/validateToken",
                                HttpMethod.GET, requestEntity, String.class, 1);


                        log.info(response.getBody());
                       // String username = identityClient.validate();
                       /* ServerHttpRequest loggedInUser = exchange.getRequest()
                                .mutate()
                                .header("loggedInUser", username)
                                .build();
                        return chain.filter(exchange.mutate().request(loggedInUser).build());*/
                    } catch (Exception e) {
                        throw new RuntimeException("Unauthorized Access");
                    }
                }

            }

            return chain.filter(exchange);
        }));

    }

    public static class Config {

    }
}
