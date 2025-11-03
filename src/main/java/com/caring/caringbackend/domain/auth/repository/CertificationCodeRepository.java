package com.caring.caringbackend.domain.auth.repository;

import com.caring.caringbackend.domain.auth.entity.CertificationCode;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CertificationCodeRepository extends CrudRepository<CertificationCode, String> {

    Optional<CertificationCode> findCertificationCodeByPhone(String phone);
}
