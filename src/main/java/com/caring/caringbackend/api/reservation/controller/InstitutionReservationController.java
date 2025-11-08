import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/my-institution/reservations")
@RequiredArgsConstructor
@Tag(name = "🏥 Institution Reservation", description = "기관 예약 관리 API")
public class InstitutionReservationController {

    private final InstitutionReservationService institutionReservationService;

    @GetMapping
    @Operation(summary = "내 기관 예약 목록 조회")
    public ApiResponse<Page<InstitutionReservationResponseDto>> getMyInstitutionReservations(
            @AuthenticationPrincipal InstitutionAdminDetails adminDetails,
            @ParameterObject InstitutionReservationSearchRequestDto searchRequest
    ) {
        // Pageable 생성
        Pageable pageable = PageRequest.of(
                searchRequest.getPage(),
                searchRequest.getSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<InstitutionReservationResponseDto> reservations = institutionReservationService
                .getMyInstitutionReservations(
                        adminDetails.getId(),
                        searchRequest.getStatus(),
                        searchRequest.getStartDate(),
                        searchRequest.getEndDate(),
                        pageable
                );

        return ApiResponse.success(reservations);
    }
