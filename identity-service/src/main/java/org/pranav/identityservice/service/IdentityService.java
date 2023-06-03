package org.pranav.identityservice.service;

import lombok.AllArgsConstructor;
import org.pranav.identityservice.entity.UserInfo;
import org.pranav.identityservice.repository.UserInfoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class IdentityService {

    private final UserInfoRepository repository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates a new user in the db by encoding the password.
     * @param userInfo
     * @return
     */
    public String register(UserInfo userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        Optional<UserInfo> byUsername = repository.findByUsername(userInfo.getUsername());
        if (byUsername.isEmpty()) {
            repository.save(userInfo);
            return "User registered in the system";
        } else {
            return "User already exists";
        }
    }
}
