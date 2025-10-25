package com.auth.app.tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Transactional
    @Modifying
    @Query("""
            UPDATE Token t SET t.revoked=true ,t.expired=true 
            WHERE t.user.id=:userId AND t.deviceId=:deviceId AND (t.expired=false AND t.revoked=false ) 
            """)
    int UpdateAllValidTokenByUserAndDeviceId(Integer userId, String deviceId);

    Optional<Token> findTokenByRefreshToken(String refreshToken);

    Optional<Token> findTokenByAccessToken(String accessToken);
}
