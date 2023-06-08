package org.pranav.identityservice.config;

import lombok.AllArgsConstructor;
import org.pranav.identityservice.filter.JwtAuthFilter;
import org.pranav.identityservice.service.UserInfoDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//role based authorization on the api
//@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Authenticates the user
     *
     * @return
     */
    @Bean
    public UserDetailsService userDetailsService() {
        /* UserDetails userDetails = User.withUsername("Pranav")
                .password(passwordEncoder.encode("pranav"))
                .roles("ADMIN")
                .build();

        UserDetails userDetails1 = User.withUsername("Dhana")
                .password(passwordEncoder().encode("dhana"))
                .roles("USER")
                .build();
    return new InMemoryUserDetailsManager(userDetails, userDetails1);*/
        return new UserInfoDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Authorize a user
     * Adds the pre Filter
     *
     * @param security
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/app/home", "/restaurant/home", "/auth/register", "/auth/token", "/eureka",
                        "/auth/home", "auth/refreshToken").permitAll()
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/app/**", "/restaurant/**", "/auth/**").authenticated()
                /*.and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)*/
                .and()
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * Authentication provider will talk with UserDetailsService
     *
     * @return
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
