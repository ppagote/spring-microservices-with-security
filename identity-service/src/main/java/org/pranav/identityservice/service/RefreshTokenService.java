package org.pranav.identityservice.service;

import lombok.AllArgsConstructor;
import org.pranav.identityservice.entity.RefreshToken;
import org.pranav.identityservice.repository.RefreshTokenRepository;
import org.pranav.identityservice.repository.UserInfoRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserInfoRepository userInfoRepository;

    public RefreshToken createRefreshToken(String username) {
        return userInfoRepository.findByUsername(username)
                .map(userInfo -> {
                    RefreshToken refreshToken = RefreshToken.builder()
                            .userInfo(userInfo)
                            .token(UUID.randomUUID().toString())
                            .expiredDate(Instant.now().plusMillis(600000))
                            .build();

                    return refreshTokenRepository.save(refreshToken);
                }).orElseThrow(() ->
                        new UsernameNotFoundException("Cannot create refreshToken due to invalid Username"));
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiredDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh Token Expired. Please make a new request");
        }
        return refreshToken;
    }
}
