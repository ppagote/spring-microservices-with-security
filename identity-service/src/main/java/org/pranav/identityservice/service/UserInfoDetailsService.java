package org.pranav.identityservice.service;

import org.pranav.identityservice.entity.UserInfo;
import org.pranav.identityservice.entity.UserInfoUserDetails;
import org.pranav.identityservice.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Using the spring security UserDetailsService to loads the userInfo from repository based on username.
 * Then converting the UserInfo to spring security UserDetails object
 */
@Component
//@AllArgsConstructor
public class UserInfoDetailsService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userInfo = repository.findByUsername(username);
        return userInfo.map(UserInfoUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found " + username));
    }
}
