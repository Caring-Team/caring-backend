package com.caring.caringbackend.domain.auth.repository;

import com.caring.caringbackend.domain.auth.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    Optional<RefreshToken> findRefreshTokenByRefreshToken(String jwtRefreshToken);
}
