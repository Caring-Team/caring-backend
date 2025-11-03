package com.caring.caringbackend.domain.auth.repository;

import com.caring.caringbackend.domain.auth.entity.TemporaryUserInfo;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface TemporaryUserInfoRepository extends CrudRepository<TemporaryUserInfo, String> {
    public Optional<TemporaryUserInfo> findByAccessToken(String accessToken);
}
