package org.pranav.identityservice.repository;

import org.pranav.identityservice.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Using JPA Repository to store the new user details
 */
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {

    Optional<UserInfo> findByUsername(String username);
}
