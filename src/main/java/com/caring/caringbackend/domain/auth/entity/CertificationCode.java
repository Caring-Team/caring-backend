package com.caring.caringbackend.domain.auth.entity;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "certification_code")
public class CertificationCode {

    @Id
    private String phone;

    private String code;

    private String name;

    private LocalDate birthDate;

    @TimeToLive
    private Long expiresIn;
}
