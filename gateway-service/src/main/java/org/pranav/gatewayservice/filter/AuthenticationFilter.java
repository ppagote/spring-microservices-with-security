package org.pranav.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.pranav.gatewayservice.client.IdentityClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
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

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (((exchange, chain) -> {
            if (!Arrays.asList("/app/home", "/restaurant/home", "/auth/home", "/auth/register", "/auth/token", "/eureka")
                    .contains(exchange.getRequest().getURI().getPath())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
                }

                String authHeader = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION))
                        .get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    try {
                        HttpHeaders headers = new HttpHeaders();
                        headers.set(HttpHeaders.AUTHORIZATION, authHeader);
                        headers.setContentType(MediaType.APPLICATION_JSON);

                        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
                        ResponseEntity<String> response = restTemplate.exchange(
                                "http://localhost:8084/auth/validateToken",
                                HttpMethod.GET, requestEntity, String.class, 1);
                        String username = response.getBody();
                        log.info(username);

                        // String username = identityClient.validate();

                        //TO pass the name of the user in every call. Fetch the username from token
                        ServerHttpRequest loggedInUser = exchange.getRequest()
                                .mutate()
                                .header("loggedInUser", username)
                                .build();
                        return chain.filter(exchange.mutate().request(loggedInUser).build());
                    } catch (Exception e) {
                        return this.onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED);
                    }
                }

            }

            return chain.filter(exchange);
        }));

    }

    public static class Config {

    }
}
