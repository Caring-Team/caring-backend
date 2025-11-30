package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.api.internal.auth.dto.GenerateTemporaryTokenDto;
import com.caring.caringbackend.api.internal.auth.dto.GenerateTokenDto;
import com.caring.caringbackend.api.internal.auth.dto.RefreshTokenPayloadDto;
import com.caring.caringbackend.api.internal.auth.dto.request.institution.local.InstitutionLocalLoginRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.institution.local.InstitutionLocalRegisterRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.user.oauth.UserOAuth2LoginRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.SendCertificationCodeRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.TokenRefreshRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.user.local.UserLocalLoginRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.user.local.UserLocalRegisterRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.user.oauth.UserOAuth2RegisterRequest;
import com.caring.caringbackend.api.internal.auth.dto.request.VerifyPhoneRequest;
import com.caring.caringbackend.api.internal.auth.dto.response.InstitutionAdminMeResponse;
import com.caring.caringbackend.api.internal.auth.dto.response.InstitutionAdminMeResponse.InstitutionAdminMeResponseBuilder;
import com.caring.caringbackend.api.internal.auth.dto.response.JwtTokenResponse;
import com.caring.caringbackend.api.internal.auth.dto.response.OAuth2ProviderTokenResponse;
import com.caring.caringbackend.api.internal.auth.dto.response.OAuth2ProviderUserInfoResponse;
import com.caring.caringbackend.domain.auth.entity.TemporaryUserInfo;
import com.caring.caringbackend.domain.auth.repository.TemporaryUserInfoRepository;
import com.caring.caringbackend.domain.institution.profile.entity.Institution;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdmin;
import com.caring.caringbackend.domain.institution.profile.entity.InstitutionAdminRole;
import com.caring.caringbackend.domain.institution.profile.repository.InstitutionAdminRepository;
import com.caring.caringbackend.domain.user.guardian.entity.AuthCredential;
import com.caring.caringbackend.domain.user.guardian.entity.CredentialType;
import com.caring.caringbackend.domain.user.guardian.entity.Member;
import com.caring.caringbackend.domain.user.guardian.entity.MemberRole;
import com.caring.caringbackend.domain.user.guardian.repository.AuthCredentialRepository;
import com.caring.caringbackend.domain.user.guardian.repository.MemberRepository;
import com.caring.caringbackend.global.exception.BusinessException;
import com.caring.caringbackend.global.exception.ErrorCode;
import com.caring.caringbackend.global.exception.MemberNotFoundException;
import com.caring.caringbackend.global.model.Gender;
import com.caring.caringbackend.global.security.JwtUtils;
import com.caring.caringbackend.global.security.details.InstitutionAdminDetails;
import com.caring.caringbackend.global.security.details.TemporaryInstitutionAdminDetails;
import com.caring.caringbackend.global.security.details.TemporaryUserDetails;
import com.caring.caringbackend.global.security.details.MemberDetails;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService {
    private final TransactionTemplate transactionTemplate;

    private final MemberRepository memberRepository;
    private final AuthCredentialRepository authCredentialRepository;
    private final InstitutionAdminRepository institutionAdminRepository;
    private final OAuth2ServiceFactory oAuth2ServiceFactory;
    private final TokenService tokenService;
    private final VerifyPhoneService verifyPhoneService;
    private final TemporaryUserInfoRepository temporaryUserInfoRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public JwtTokenResponse oAuth2LoginOrGenerateTemporaryToken(String provider,
                                                                UserOAuth2LoginRequest userOAuth2LoginRequest) {
        OAuth2Service service = oAuth2ServiceFactory.getService(provider);
        OAuth2ProviderTokenResponse tokenFromProvider = service.getTokenFromProvider(userOAuth2LoginRequest);
        OAuth2ProviderUserInfoResponse userInfoFromProvider = service.getUserInfoFromProvider(tokenFromProvider);

        return transactionTemplate.execute(
                status -> authCredentialRepository
                        .findAuthCredentialByIdentifierAndType(
                                userInfoFromProvider.getUserId(),
                                userInfoFromProvider.getProviderType())
                        .map(AuthCredential::getMember)
                        .map(this::generateTokenByMember)
                        .orElseGet(() -> generateTemporaryTokenOAuth2(userInfoFromProvider)));
    }

    private JwtTokenResponse generateTokenByMember(Member member) {

        GenerateTokenDto dto = GenerateTokenDto.builder()
                .id(member.getId())
                .role(member.getRole().getKey())
                .build();
        return tokenService.generateToken(dto);
    }

    private JwtTokenResponse generateTokenByInstitutionAdmin(InstitutionAdmin institutionAdmin) {

        GenerateTokenDto dto = GenerateTokenDto.builder()
                .id(institutionAdmin.getId())
                .role(institutionAdmin.getRole().getKey())
                .build();
        return tokenService.generateToken(dto);
    }

    private JwtTokenResponse generateTemporaryTokenOAuth2(
            OAuth2ProviderUserInfoResponse oAuth2ProviderUserInfoResponse) {

        return tokenService.generateTemporaryToken(
                GenerateTemporaryTokenDto.of(oAuth2ProviderUserInfoResponse.getProviderType(),
                        oAuth2ProviderUserInfoResponse.getUserId(), MemberRole.TEMP_OAUTH));
    }

    public void sendCertificationCode(SendCertificationCodeRequest certificationCodeRequest) {

        verifyPhoneService.sendCertificationCode(certificationCodeRequest);
    }

    public JwtTokenResponse verifyPhoneOAuth2(TemporaryUserDetails userDetails,
                                              VerifyPhoneRequest verifyPhoneRequest) {

        verifyPhoneService.verifyPhone(verifyPhoneRequest);
        try {
            return transactionTemplate.execute(status -> {
                Optional<Member> byPhoneNumber = memberRepository.findByPhoneNumber(
                        verifyPhoneRequest.getPhoneNumber());
                if (byPhoneNumber.isPresent()) {
                    Member member = byPhoneNumber.get();
                    String duplicationInformation = Member.makeDuplicationInformation(verifyPhoneRequest.getName(),
                            verifyPhoneRequest.getBirthDate(), verifyPhoneRequest.getPhoneNumber());
                    if (!member.getDuplicationInformation().equals(duplicationInformation)) {
                        throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
                    }
                    AuthCredential credential = AuthCredential.createSocialCredential(member,
                            findCredentialType(userDetails.getCredentialType()),
                            userDetails.getCredentialId());
                    authCredentialRepository.save(credential);
                    return generateTokenByMember(member);
                } else {
                    String accessToken = userDetails.getAccessToken();
                    Long expiresIn = jwtUtils.getTokenRemainTime(accessToken);
                    TemporaryUserInfo temporaryUserInfo = TemporaryUserInfo.builder()
                            .accessToken(accessToken)
                            .phone(verifyPhoneRequest.getPhoneNumber())
                            .name(verifyPhoneRequest.getName())
                            .birthDate(verifyPhoneRequest.getBirthDate())
                            .expiresIn(expiresIn)
                            .build();
                    temporaryUserInfoRepository.save(temporaryUserInfo);
                    return JwtTokenResponse.builder()
                            .accessToken(accessToken)
                            .expiresIn(expiresIn)
                            .build();
                }
            });
        } catch (IllegalStateException e) {
            log.warn("Failed to save phone number", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "전화번호 인증 과정에서 예상치 못한 오류가 발생했습니다.");
        }
    }

    public JwtTokenResponse completeRegisterOAuth2(
            TemporaryUserDetails userDetails,
            UserOAuth2RegisterRequest userOAuth2RegisterRequest) {

        TemporaryUserInfo temporaryUserInfo = temporaryUserInfoRepository
                .findByAccessToken(userDetails.getAccessToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "회원가입 도중 문제가 발생했습니다."));

        String duplicationInformation = Member.makeDuplicationInformation(temporaryUserInfo.getName(),
                temporaryUserInfo.getBirthDate(), temporaryUserInfo.getPhone());
        if (memberRepository.existsByDuplicationInformation(duplicationInformation)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 가입된 계정이 있습니다.");
        }

        return transactionTemplate.execute(status -> {
            Member member = Member.builder()
                    .name(temporaryUserInfo.getName())
                    .phoneNumber(temporaryUserInfo.getPhone())
                    .birthDate(temporaryUserInfo.getBirthDate())
                    .gender(Gender.findEnumByCode(userOAuth2RegisterRequest.getGender()))
                    .address(userOAuth2RegisterRequest.getAddress())
                    .duplicationInformation(duplicationInformation)
                    .role(MemberRole.USER)
                    .build();

            AuthCredential authCredential = AuthCredential.createSocialCredential(member,
                    findCredentialType(userDetails.getCredentialType()), userDetails.getCredentialId());
            memberRepository.save(member);
            authCredentialRepository.save(authCredential);
            temporaryUserInfoRepository.delete(temporaryUserInfo);
            return generateTokenByMember(member);
        });
    }

    public JwtTokenResponse verifyPhoneNumberLocal(VerifyPhoneRequest verifyPhoneRequest) {

        verifyPhoneService.verifyPhone(verifyPhoneRequest);
        try {
            return transactionTemplate.execute(status -> {
                Optional<Member> byPhoneNumber = memberRepository.findByPhoneNumber(
                        verifyPhoneRequest.getPhoneNumber());
                if (byPhoneNumber.isPresent()) {
                    Member member = byPhoneNumber.get();
                    String duplicationInformation = Member.makeDuplicationInformation(verifyPhoneRequest.getName(),
                            verifyPhoneRequest.getBirthDate(), verifyPhoneRequest.getPhoneNumber());
                    if (!member.getDuplicationInformation().equals(duplicationInformation)) {
                        throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
                    }

                    authCredentialRepository
                            .findAuthCredentialByMemberAndType(member, CredentialType.LOCAL)
                            .ifPresent((authCredential) -> {
                                throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 가입된 계정이 있습니다.");
                            });
                    return generateTokenByMember(member); // 로그인 후 사용할 아이디 비밀번호 입력창으로 넘어간다.
                } else {
                    return generateTemporaryTokenMemberLocal(verifyPhoneRequest); // 회원가입을 진행한다.
                }
            });
        } catch (IllegalStateException e) {
            log.warn("Failed to save phone number", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "전화번호 인증 과정에서 예상치 못한 오류가 발생했습니다.");
        }
    }

    public JwtTokenResponse completeRegisterLocal(
            TemporaryUserDetails userDetails,
            UserLocalRegisterRequest userLocalRegisterRequest) {

        TemporaryUserInfo temporaryUserInfo = temporaryUserInfoRepository
                .findByAccessToken(userDetails.getAccessToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "회원가입 도중 문제가 발생했습니다."));

        String duplicationInformation = Member
                .makeDuplicationInformation(
                        temporaryUserInfo.getName(),
                        temporaryUserInfo.getBirthDate(),
                        temporaryUserInfo.getPhone());

        if (authCredentialRepository.existsByIdentifierAndType(userLocalRegisterRequest.getUsername(),
                CredentialType.LOCAL)) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS, "이미 사용중인 아이디 입니다.");
        }

        if (memberRepository.existsByDuplicationInformation(duplicationInformation)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 가입된 계정이 있습니다.");
        }

        return transactionTemplate.execute(status -> {
            Member member = Member.builder()
                    .name(temporaryUserInfo.getName())
                    .phoneNumber(temporaryUserInfo.getPhone())
                    .birthDate(temporaryUserInfo.getBirthDate())
                    .gender(Gender.findEnumByCode(userLocalRegisterRequest.getGender()))
                    .address(userLocalRegisterRequest.getAddress())
                    .duplicationInformation(duplicationInformation)
                    .role(MemberRole.USER)
                    .build();
            AuthCredential credential = AuthCredential.createLocalCredential(member,
                    userLocalRegisterRequest.getUsername(),
                    passwordEncoder.encode(userLocalRegisterRequest.getPassword()));

            memberRepository.save(member);
            authCredentialRepository.save(credential);
            temporaryUserInfoRepository.delete(temporaryUserInfo);
            return generateTokenByMember(member);
        });
    }

    private JwtTokenResponse generateTemporaryTokenMemberLocal(VerifyPhoneRequest verifyPhoneRequest) {

        JwtTokenResponse jwtTokenResponse = tokenService.generateTemporaryToken(
                GenerateTemporaryTokenDto.of(CredentialType.LOCAL, "", MemberRole.TEMP_LOCAL));

        TemporaryUserInfo temporaryUserInfo = TemporaryUserInfo.builder()
                .accessToken(jwtTokenResponse.getAccessToken())
                .phone(verifyPhoneRequest.getPhoneNumber())
                .name(verifyPhoneRequest.getName())
                .birthDate(verifyPhoneRequest.getBirthDate())
                .expiresIn(jwtTokenResponse.getExpiresIn())
                .build();

        temporaryUserInfoRepository.save(temporaryUserInfo);
        return jwtTokenResponse;
    }

    @Transactional
    public boolean addLocalCredential(MemberDetails memberDetails, UserLocalRegisterRequest userLocalRegisterRequest) {

        Member member = memberRepository.findById(memberDetails.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        authCredentialRepository.findAuthCredentialByMemberAndType(member, CredentialType.LOCAL)
                .ifPresent((authCredential) -> {
                    throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 가입된 계정이 있습니다.");
                });

        if (authCredentialRepository.existsByIdentifierAndType(userLocalRegisterRequest.getUsername(),
                CredentialType.LOCAL)) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS, "이미 사용중인 아이디 입니다.");
        }
        AuthCredential credential = AuthCredential.createLocalCredential(member, userLocalRegisterRequest.getUsername(),
                passwordEncoder.encode(userLocalRegisterRequest.getPassword()));
        authCredentialRepository.save(credential);
        return true;
    }

    public JwtTokenResponse verifyPhoneInstitution(VerifyPhoneRequest verifyPhoneRequest) {

        verifyPhoneService.verifyPhone(verifyPhoneRequest);
        try {
            return transactionTemplate.execute(status -> {
                String duplicationInformation = InstitutionAdmin.makeDuplicationInformation(
                        verifyPhoneRequest.getName(),
                        verifyPhoneRequest.getBirthDate(), verifyPhoneRequest.getPhoneNumber());

                if (institutionAdminRepository.existsByDuplicationInformation(duplicationInformation)) {
                    throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 가입된 계정이 있습니다.");
                }

                return generateTemporaryTokenInstitution(verifyPhoneRequest); // 회원가입을 진행한다.
            });
        } catch (IllegalStateException e) {
            log.warn("Failed to save phone number", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "전화번호 인증 과정에서 예상치 못한 오류가 발생했습니다.");
        }
    }

    private JwtTokenResponse generateTemporaryTokenInstitution(VerifyPhoneRequest verifyPhoneRequest) {

        JwtTokenResponse jwtTokenResponse = tokenService.generateTemporaryToken(
                GenerateTemporaryTokenDto.of(CredentialType.LOCAL_INSTITUTION, "",
                        InstitutionAdminRole.TEMP_INSTITUTION));

        TemporaryUserInfo temporaryUserInfo = TemporaryUserInfo.builder()
                .accessToken(jwtTokenResponse.getAccessToken())
                .phone(verifyPhoneRequest.getPhoneNumber())
                .name(verifyPhoneRequest.getName())
                .birthDate(verifyPhoneRequest.getBirthDate())
                .expiresIn(jwtTokenResponse.getExpiresIn())
                .build();

        temporaryUserInfoRepository.save(temporaryUserInfo);
        return jwtTokenResponse;
    }

    public JwtTokenResponse completeRegisterInstitution(
            TemporaryInstitutionAdminDetails userDetails,
            InstitutionLocalRegisterRequest institutionLocalRegisterRequest) {

        TemporaryUserInfo temporaryUserInfo = temporaryUserInfoRepository
                .findByAccessToken(userDetails.getAccessToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "회원가입 도중 문제가 발생했습니다."));

        String duplicationInformation = InstitutionAdmin
                .makeDuplicationInformation(
                        temporaryUserInfo.getName(),
                        temporaryUserInfo.getBirthDate(),
                        temporaryUserInfo.getPhone());

        if (institutionAdminRepository.existsByUsername(institutionLocalRegisterRequest.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS, "이미 사용중인 아이디 입니다.");
        }

        if (institutionAdminRepository.existsByDuplicationInformation(duplicationInformation)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 가입된 계정이 있습니다.");
        }

        return transactionTemplate.execute(status -> {
            InstitutionAdmin institutionAdmin = InstitutionAdmin.builder()
                    .name(temporaryUserInfo.getName())
                    .phoneNumber(temporaryUserInfo.getPhone())
                    .birthDate(temporaryUserInfo.getBirthDate())
                    .username(institutionLocalRegisterRequest.getUsername())
                    .passwordHash(passwordEncoder.encode(institutionLocalRegisterRequest.getPassword()))
                    .role(InstitutionAdminRole.STAFF)
                    .duplicationInformation(duplicationInformation)
                    .build();

            institutionAdminRepository.save(institutionAdmin);
            temporaryUserInfoRepository.delete(temporaryUserInfo);
            return generateTokenByInstitutionAdmin(institutionAdmin);
        });
    }

    @Transactional
    public JwtTokenResponse loginMemberLocal(UserLocalLoginRequest userLocalLoginRequest) {

        AuthCredential credential = authCredentialRepository.findAuthCredentialByIdentifierAndType(
                        userLocalLoginRequest.getUsername(),
                        CredentialType.LOCAL)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_USERNAME_PASSWORD));
        if (!passwordEncoder.matches(userLocalLoginRequest.getPassword(), credential.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_USERNAME_PASSWORD);
        }
        return generateTokenByMember(credential.getMember());
    }

    @Transactional
    public JwtTokenResponse loginInstitutionAdmin(InstitutionLocalLoginRequest institutionLocalLoginRequest) {

        InstitutionAdmin institutionAdmin = institutionAdminRepository.findByUsername(
                        institutionLocalLoginRequest.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_USERNAME_PASSWORD));
        if (!passwordEncoder.matches(institutionLocalLoginRequest.getPassword(), institutionAdmin.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_USERNAME_PASSWORD);
        }
        return generateTokenByInstitutionAdmin(institutionAdmin);
    }

    private CredentialType findCredentialType(String credentialName) {

        CredentialType credentialType = CredentialType.fromKey(credentialName);
        if (CredentialType.UNKNOWN.equals(credentialType)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST);
        }
        return credentialType;
    }

    public JwtTokenResponse regenerateAccessTokenMember(TokenRefreshRequest tokenRefreshRequest) {
        RefreshTokenPayloadDto refreshTokenPayloadDto = tokenService.decodeRefreshToken(tokenRefreshRequest);

        Member member = memberRepository.findById(refreshTokenPayloadDto.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        GenerateTokenDto generateTokenDto = GenerateTokenDto.builder()
                .id(member.getId())
                .role(member.getRole().getKey())
                .build();
        return tokenService.regenerateAccessToken(generateTokenDto);
    }

    @Transactional
    public JwtTokenResponse regenerateAccessTokenInstitutionAdmin(TokenRefreshRequest tokenRefreshRequest) {
        if (tokenRefreshRequest.getRequestToken() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        RefreshTokenPayloadDto refreshTokenPayloadDto = tokenService.decodeRefreshToken(tokenRefreshRequest);
        InstitutionAdmin institutionAdmin = institutionAdminRepository.findById(refreshTokenPayloadDto.getId())
                .orElseThrow(MemberNotFoundException::new);

        GenerateTokenDto generateTokenDto = GenerateTokenDto.builder()
                .id(institutionAdmin.getId())
                .role(institutionAdmin.getRole().getKey())
                .build();
        institutionAdmin.activity();
        return tokenService.regenerateAccessToken(generateTokenDto);
    }

    @Transactional(readOnly = true)
    public InstitutionAdminMeResponse getInstitutionAdminInformation(InstitutionAdminDetails institutionAdminDetails) {
        InstitutionAdmin institutionAdmin = institutionAdminRepository.findById(institutionAdminDetails.getId())
                .orElseThrow(MemberNotFoundException::new);

        Institution institution = institutionAdmin.getInstitution();
        InstitutionAdminMeResponseBuilder builder = InstitutionAdminMeResponse.builder()
                .name(institutionAdmin.getName())
                .role(institutionAdmin.getRole().name());
        if (institution != null) {
            builder.institutionId(institution.getId())
                    .institutionName(institution.getName())
                    .institutionStatus(institution.getApprovalStatus().name());
        }
        return builder.build();
    }
}
