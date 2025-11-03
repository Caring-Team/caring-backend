package com.caring.caringbackend.domain.auth.entity;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "refresh_token")
public class RefreshToken {

    @Indexed
    @Id
    private String refreshToken;

    @TimeToLive
    private Long expiresIn;
}
