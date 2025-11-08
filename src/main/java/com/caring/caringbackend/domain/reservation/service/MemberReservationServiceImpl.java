package com.caring.caringbackend.domain.reservation.service;

import com.caring.caringbackend.api.reservation.dto.request.MemberReservationCreateRequestDto;
import com.caring.caringbackend.domain.institution.counsel.entity.InstitutionCounselDetail;
import com.caring.caringbackend.domain.institution.counsel.repository.InstitutionCounselDetailRepository;
import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.repository.ReservationRepository;
import com.caring.caringbackend.domain.user.elderly.entity.ElderlyProfile;
import com.caring.caringbackend.domain.user.elderly.repository.ElderlyProfileRepository;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberReservationServiceImpl implements MemberReservationService {

    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final ElderlyProfileRepository elderlyProfileRepository;
    private final InstitutionCounselDetailRepository institutionCounselDetailRepository;


    @Override
    public void createMemberReservation(Long memberId, MemberReservationCreateRequestDto requestDto) {
        // 기관 상담 ID, 날짜, 예약 시간, erderlyProfile,
        Member member = getMember(memberId);
        ElderlyProfile elderlyProfile = getElderlyProfile(requestDto);
        InstitutionCounselDetail counselDetail = getCounselDetail(requestDto);

        // 예약이 가능한지 확인
        validateIsAvailable(requestDto, counselDetail);

        // 예약 생성
        Reservation reservation = Reservation.createReservation(
                counselDetail,
                member,
                elderlyProfile,
                requestDto.getStartTime());

        // 예약 시간대 비트마스크 업데이트
        counselDetail.markSlotAsReserved(requestDto.getSlotIndex());

        // 예약 저장
        reservationRepository.save(reservation);
    }

    private ElderlyProfile getElderlyProfile(MemberReservationCreateRequestDto requestDto) {
        return elderlyProfileRepository.findById(requestDto.getElderlyProfileId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ELDERLY_PROFILE_NOT_FOUND));
    }

    private static void validateIsAvailable(MemberReservationCreateRequestDto requestDto, InstitutionCounselDetail counselDetail) {
        if (!counselDetail.isSlotAvailable(requestDto.getSlotIndex())) {
            throw new BusinessException(ErrorCode.RESERVATION_TIME_NOT_AVAILABLE);
        }
    }

    private InstitutionCounselDetail getCounselDetail(MemberReservationCreateRequestDto requestDto) {
        return institutionCounselDetailRepository.findByCounselIdAndServiceDate(
                        requestDto.getCounselId(),
                        requestDto.getReservationDate())
                .orElseThrow(() -> new BusinessException(ErrorCode.INSTITUTION_COUNSEL_DETAIL_NOT_FOUND));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
