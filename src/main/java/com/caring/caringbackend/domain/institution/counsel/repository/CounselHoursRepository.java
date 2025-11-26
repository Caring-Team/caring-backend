package com.caring.caringbackend.domain.institution.counsel.repository;

import com.caring.caringbackend.domain.institution.counsel.entity.CounselHours;
import java.time.DayOfWeek;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounselHoursRepository extends JpaRepository<CounselHours, Long> {

    Set<CounselHours> findAllByCounselId(Long counselId);

    Set<CounselHours> findAllByCounselIdAndDayOfWeek(Long counselId, DayOfWeek dayOfWeek);

    void deleteAllByCounselId(Long counselId);
}
