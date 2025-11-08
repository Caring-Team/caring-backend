
import com.caring.caringbackend.api.reservation.dto.response.InstitutionReservationDetailResponseDto;
import com.caring.caringbackend.api.reservation.dto.response.InstitutionReservationResponseDto;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.reservation.entity.Reservation;
import com.caring.caringbackend.domain.reservation.entity.ReservationStatus;
import com.caring.caringbackend.domain.reservation.repository.ReservationRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 기관 예약 관리 서비스 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InstitutionReservationServiceImpl implements InstitutionReservationService {

    private final ReservationRepository reservationRepository;
    private final InstitutionAdminRepository institutionAdminRepository;

    @Override
    public Page<InstitutionReservationResponseDto> getMyInstitutionReservations(
            Long adminId,
            ReservationStatus status,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        // adminId로 institutionId 조회
        Long institutionId = getInstitutionIdByAdminId(adminId);

        Page<Reservation> reservations = reservationRepository.findByInstitutionIdWithFilters(
                institutionId, status, startDate, endDate, pageable
        );

        return reservations.map(InstitutionReservationResponseDto::from);
    }
