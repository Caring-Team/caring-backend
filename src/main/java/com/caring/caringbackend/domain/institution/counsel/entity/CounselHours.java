package com.caring.caringbackend.domain.institution.counsel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class CounselHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * TODO: 예약 항목 삭제 시 기존 예약이 있는 경우 어떻게 처리해야 하는가
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_counsel_id", nullable = false)
    private InstitutionCounsel counsel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Builder
    public CounselHours(InstitutionCounsel counsel, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.counsel = counsel;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
