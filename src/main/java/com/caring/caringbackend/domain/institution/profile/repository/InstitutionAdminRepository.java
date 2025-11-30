package com.caring.caringbackend.domain.institution.profile.repository;

import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InstitutionAdminRepository extends JpaRepository<InstitutionAdmin, Long> {

    public Optional<InstitutionAdmin> findByDuplicationInformation(String duplicationInformation);

    public boolean existsByDuplicationInformation(String duplicationInformation);

    public boolean existsByUsername(String username);

    public Optional<InstitutionAdmin> findByUsername(String username);

    @Query("""
            SELECT ia
            FROM InstitutionAdmin ia
            JOIN FETCH ia.institution
            WHERE ia.id = :id
            """)
    public Optional<InstitutionAdmin> findByIdWithInstitution(Long id);

    public List<InstitutionAdmin> findAllByInstitution(Institution institution);


}
