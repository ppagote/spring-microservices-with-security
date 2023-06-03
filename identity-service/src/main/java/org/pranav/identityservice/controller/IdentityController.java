package org.pranav.identityservice.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.pranav.identityservice.entity.UserInfo;
import org.pranav.identityservice.service.IdentityService;
import org.pranav.identityservice.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@Slf4j
public class IdentityController {

    private final IdentityService identityService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @GetMapping("/home")
    public String testService() {
        return "Identity-Service";
    }

    /**
     * Register a new user
     *
     * @param userInfo UserInfo object
     */
    @PostMapping("/register")
    public String register(@RequestBody UserInfo userInfo) {
        return identityService.register(userInfo);
    }

    /**
     * Authenticate & Fetch the token of the registered user.
     * AuthenticationManager is used to check if the user is registered.
     *
     * @param userInfo UserInfo object
     * @return token
     */
    @PostMapping("/token")
    public String getToken(@RequestBody UserInfo userInfo) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                userInfo.getUsername(), userInfo.getPassword()));
        if (authenticate.isAuthenticated()) {
            return jwtService.generateToken(userInfo);
        } else {
            throw new UsernameNotFoundException("Invalid Request");
        }
    }

    /**
     * Validates the input token
     * @return
     */
    @GetMapping("/validateToken")
    public String validate(/*@RequestHeader(HttpHeaders.AUTHORIZATION) String token*/) {
       // jwtService.validateToken1(token.substring(7));
        //Token validation will be done during filter
        //log.info("{}", token);
        return "Success";
        /*token = token.substring(7);
        return jwtService.extractUsernameFromToken(token);*/
    }

}
