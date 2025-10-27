package com.caring.caringbackend.domain.auth.entity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "temp_user")
public class TemporaryUserInfo {

    // ID를 휴대폰 번호로 함. 이후 추가 휴대폰 번호 인증이 들어온 경우 덮어씌워져 기존 회원가입은 막힘.
    @Id
    private String phone;

    @Indexed
    private String accessToken;

    private LocalDate birthDate;

    private String name;

    @TimeToLive
    private Long expiresIn;
}
