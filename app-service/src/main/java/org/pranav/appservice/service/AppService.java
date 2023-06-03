package org.pranav.appservice.service;

import org.springframework.stereotype.Service;

@Service
public class AppService {

    public String greeting() {
        return "Welcome to App Service";
    }
}
