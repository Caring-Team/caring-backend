package com.caring.caringbackend.domain.institution.profile.repository;

import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstitutionAdminRepository extends JpaRepository<InstitutionAdmin, Long> {

    public Optional<InstitutionAdmin> findByDuplicationInformation(String duplicationInformation);

    public Optional<InstitutionAdmin> findByUsernameAndPasswordHash(String username, String passwordHash);
}
