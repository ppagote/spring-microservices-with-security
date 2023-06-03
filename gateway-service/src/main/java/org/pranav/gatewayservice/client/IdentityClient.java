package org.pranav.gatewayservice.client;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface IdentityClient {

    @GetExchange("/auth/validateToken")
    String validate();
}
