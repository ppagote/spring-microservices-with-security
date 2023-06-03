package org.pranav.gatewayservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Objects;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final WebClient webClient;

    public AuthenticationFilter(WebClient webClient) {
        super(Config.class);
        this.webClient = webClient;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        return response.setComplete();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!Arrays.asList("/app/home", "/restaurant/home", "/auth/home", "/auth/register", "/auth/token", "/eureka")
                    .contains(exchange.getRequest().getURI().getPath())) {
                //header contains token or not
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    return this.onError(exchange, "No Authorization header", HttpStatus.UNAUTHORIZED);
                }

                String authHeader = Objects.requireNonNull(exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION))
                        .get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {

                    return webClient.get()
                            .uri("/auth/validateToken")
                            .header(HttpHeaders.AUTHORIZATION, authHeader)
                            .retrieve().bodyToMono(String.class)
                            .map(response -> {
                                exchange.getRequest()
                                        .mutate()
                                        .header("loggedInUser", response);
                                return exchange;
                            })
                            .flatMap(chain::filter)
                            .onErrorResume(e ->
                                    this.onError(exchange, "Invalid Authorization header", HttpStatus.UNAUTHORIZED)
                            );
                }
            }

            return chain.filter(exchange);
        };

    }

    public static class Config {
    }
}
