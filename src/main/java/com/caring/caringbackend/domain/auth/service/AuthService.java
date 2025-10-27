package com.caring.caringbackend.domain.auth.service;

import com.caring.caringbackend.domain.auth.dto.GenerateTemporaryTokenDto;
import com.caring.caringbackend.domain.auth.dto.GenerateTokenDto;
import com.caring.caringbackend.domain.auth.dto.request.InstitutionLocalLoginRequest;
import com.caring.caringbackend.domain.auth.dto.request.InstitutionLocalRegisterRequest;
import com.caring.caringbackend.domain.auth.dto.request.OAuthLoginRequest;
import com.caring.caringbackend.domain.auth.dto.request.SendCertificationCodeRequest;
import com.caring.caringbackend.domain.auth.dto.request.TokenRefreshRequest;
import com.caring.caringbackend.domain.auth.dto.request.UserLocalLoginRequest;
import com.caring.caringbackend.domain.auth.dto.request.UserLocalRegisterRequest;
import com.caring.caringbackend.domain.auth.dto.request.UserOAuth2RegisterRequest;
import com.caring.caringbackend.domain.auth.dto.request.VerifyPhoneRequest;
import com.caring.caringbackend.domain.auth.dto.response.JwtTokenResponse;
import com.caring.caringbackend.domain.auth.dto.response.OAuth2ProviderTokenResponse;
import com.caring.caringbackend.domain.auth.dto.response.OAuth2ProviderUserInfoResponse;
import com.caring.caringbackend.domain.auth.entity.TemporaryUserInfo;
import com.caring.caringbackend.domain.auth.repository.TemporaryUserInfoRepository;
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
import com.caring.caringbackend.global.model.Gender;
import com.caring.caringbackend.global.security.JwtUtils;
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

    public JwtTokenResponse oAuth2LoginOrGenerateTemporaryToken(String provider, OAuthLoginRequest request) {
        OAuth2Service service = oAuth2ServiceFactory.getService(provider);
        OAuth2ProviderTokenResponse tokenFromProvider = service.getTokenFromProvider(request);
        OAuth2ProviderUserInfoResponse userInfoFromProvider = service.getUserInfoFromProvider(tokenFromProvider);

        return transactionTemplate.execute(
                status -> findUserByOAuth2UserInfo(userInfoFromProvider).map(this::generateTokenByMember)
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

    private JwtTokenResponse generateTemporaryTokenOAuth2(OAuth2ProviderUserInfoResponse userInfoFromProvider) {
        GenerateTemporaryTokenDto dto = GenerateTemporaryTokenDto.builder()
                .credentialType(userInfoFromProvider.getProviderType().getKey())
                .credentialId(userInfoFromProvider.getUserId())
                .build();
        return tokenService.generateTemporaryTokenOAuth2(dto);
    }

    /*    private JwtTokenResponse generateOAuth2RegisterToken(TemporaryUserDetails userDetails,
                                                             VerifyPhoneRequest verifyPhoneRequest) {
            GenerateTemporaryTokenDto dto = GenerateTemporaryTokenDto.builder()
                    .credentialType(userDetails.getCredentialType())
                    .credentialId(userDetails.getCredentialId())
                    .build();
            // TODO: add old access token to black list
            return transactionTemplate.execute(state -> {
                JwtTokenResponse jwtTokenResponse = tokenService.generateOAuth2RegisterToken(dto);
                TemporaryUserInfo temporaryUserInfo = TemporaryUserInfo.builder()
                        .accessToken(jwtTokenResponse.getAccessToken())
                        .phone(verifyPhoneRequest.getPhoneNumber())
                        .name(verifyPhoneRequest.getName())
                        .birthDate(verifyPhoneRequest.getBirthDate())
                        .expiresIn(jwtTokenResponse.getExpiresIn())
                        .build();
                temporaryUserInfoRepository.save(temporaryUserInfo);
                return jwtTokenResponse;
            });
        }*/
    private Optional<Member> findUserByOAuth2UserInfo(OAuth2ProviderUserInfoResponse userInfo) {
        return authCredentialRepository.findAuthCredentialByIdentifierAndType(userInfo.getUserId(),
                userInfo.getProviderType()).map(AuthCredential::getMember);
    }

    public void sendCertificationCode(SendCertificationCodeRequest certificationCodeRequest) {
        verifyPhoneService.sendCertificationCode(certificationCodeRequest);
    }

    public JwtTokenResponse verifyPhoneOAuth2(TemporaryUserDetails userDetails,
                                              VerifyPhoneRequest request) {
        verifyPhoneService.verifyPhone(request);
        try {
            // TODO: add old access token to black list
            return transactionTemplate.execute(status -> {
                Optional<Member> byPhoneNumber = memberRepository.findByPhoneNumber(
                        request.getPhoneNumber());
                if (byPhoneNumber.isPresent()) {
                    Member member = byPhoneNumber.get();
                    String duplicationInformation = Member.makeDuplicationInformation(request.getName(),
                            request.getBirthDate(), request.getPhoneNumber());
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
                            .phone(request.getPhoneNumber())
                            .name(request.getName())
                            .birthDate(request.getBirthDate())
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
            UserOAuth2RegisterRequest registerRequest) {
        TemporaryUserInfo temporaryUserInfo = temporaryUserInfoRepository.findByAccessToken(
                        userDetails.getAccessToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "회원가입 도중 문제가 발생했습니다."));

        String duplicationInformation = Member.makeDuplicationInformation(temporaryUserInfo.getName(),
                temporaryUserInfo.getBirthDate(), temporaryUserInfo.getPhone());
        if (memberRepository.existsByDuplicationInformation(duplicationInformation)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // TODO: add old access token to black list
        return transactionTemplate.execute(status -> {
            Member member = Member.builder()
                    .name(temporaryUserInfo.getName())
                    .phoneNumber(temporaryUserInfo.getPhone())
                    .birthDate(temporaryUserInfo.getBirthDate())
                    .gender(Gender.findEnumByCode(registerRequest.getGender()))
                    .address(registerRequest.getAddress())
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

    public JwtTokenResponse verifyPhoneNumberLocal(VerifyPhoneRequest request) {
        verifyPhoneService.verifyPhone(request);
        try {
            return transactionTemplate.execute(status -> {
                Optional<Member> byPhoneNumber = memberRepository.findByPhoneNumber(request.getPhoneNumber());
                if (byPhoneNumber.isPresent()) {
                    Member member = byPhoneNumber.get();
                    String duplicationInformation = Member.makeDuplicationInformation(request.getName(),
                            request.getBirthDate(), request.getPhoneNumber());
                    if (!member.getDuplicationInformation().equals(duplicationInformation)) {
                        throw new BusinessException(ErrorCode.PHONE_ALREADY_EXISTS);
                    }

                    authCredentialRepository.findAuthCredentialByMemberAndType(
                            member, CredentialType.LOCAL).ifPresent((authCredential) -> {
                        throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
                    });
                    return generateTokenByMember(member); // 로그인 후 사용할 아이디 비밀번호 입력창으로 넘어간다.
                } else {
                    return generateTemporaryTokenMemberLocal(request); // 회원가입을 진행한다.
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
            UserLocalRegisterRequest request) {
        TemporaryUserInfo temporaryUserInfo = temporaryUserInfoRepository.findByAccessToken(
                        userDetails.getAccessToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "회원가입 도중 문제가 발생했습니다."));

        String duplicationInformation = Member.makeDuplicationInformation(temporaryUserInfo.getName(),
                temporaryUserInfo.getBirthDate(), temporaryUserInfo.getPhone());
        if (memberRepository.existsByDuplicationInformation(duplicationInformation)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 가입된 계정이 있습니다.");
        }

        if (authCredentialRepository.existsByIdentifierAndType(request.getUsername(), CredentialType.LOCAL)) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS, "이미 사용중인 아이디 입니다.");
        }

        // TODO: add old access token to black list
        return transactionTemplate.execute(status -> {
            Member member = Member.builder()
                    .name(temporaryUserInfo.getName())
                    .phoneNumber(temporaryUserInfo.getPhone())
                    .birthDate(temporaryUserInfo.getBirthDate())
                    .gender(Gender.findEnumByCode(request.getGender()))
                    .address(request.getAddress())
                    .duplicationInformation(duplicationInformation)
                    .role(MemberRole.USER)
                    .build();
            AuthCredential credential = AuthCredential.createLocalCredential(member, request.getUsername(),
                    passwordEncoder.encode(request.getPassword()));

            memberRepository.save(member);
            authCredentialRepository.save(credential);
            temporaryUserInfoRepository.delete(temporaryUserInfo);
            return generateTokenByMember(member);
        });
    }

    private JwtTokenResponse generateTemporaryTokenMemberLocal(VerifyPhoneRequest verifyPhoneRequest) {
        GenerateTemporaryTokenDto dto = GenerateTemporaryTokenDto.builder()
                .credentialType(CredentialType.LOCAL.getKey())
                .credentialId(null)
                .build();
        return transactionTemplate.execute(state -> {
            JwtTokenResponse jwtTokenResponse = tokenService.generateTemporaryTokenLocal(dto);
            TemporaryUserInfo temporaryUserInfo = TemporaryUserInfo.builder()
                    .accessToken(jwtTokenResponse.getAccessToken())
                    .phone(verifyPhoneRequest.getPhoneNumber())
                    .name(verifyPhoneRequest.getName())
                    .birthDate(verifyPhoneRequest.getBirthDate())
                    .expiresIn(jwtTokenResponse.getExpiresIn())
                    .build();
            temporaryUserInfoRepository.save(temporaryUserInfo);
            return jwtTokenResponse;
        });
    }

    @Transactional
    public boolean addLocalCredential(MemberDetails memberDetails, UserLocalRegisterRequest request) {
        Member member = memberRepository.findById(memberDetails.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        authCredentialRepository.findAuthCredentialByMemberAndType(member, CredentialType.LOCAL)
                .ifPresent((authCredential) -> {
                    throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 아이디, 비밀번호가 등록 되어있습니다.");
                });
        if (authCredentialRepository.existsByIdentifierAndType(request.getUsername(), CredentialType.LOCAL)) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS, "이미 사용중인 아이디 입니다.");
        }
        AuthCredential credential = AuthCredential.createLocalCredential(member, request.getUsername(),
                passwordEncoder.encode(request.getPassword()));
        authCredentialRepository.save(credential);
        return true;
    }

    public JwtTokenResponse verifyPhoneInstitution(VerifyPhoneRequest request) {
        verifyPhoneService.verifyPhone(request);
        try {
            return transactionTemplate.execute(status -> {
                String duplicationInformation = InstitutionAdmin.makeDuplicationInformation(request.getName(),
                        request.getBirthDate(), request.getPhoneNumber());
                Optional<InstitutionAdmin> byDuplicationInformation = institutionAdminRepository.findByDuplicationInformation(
                        duplicationInformation);

                byDuplicationInformation.ifPresent((institutionAdmin) -> {
                    throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
                });
                return generateTemporaryTokenInstitution(request); // 회원가입을 진행한다.
            });
        } catch (IllegalStateException e) {
            log.warn("Failed to save phone number", e);
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR,
                    "전화번호 인증 과정에서 예상치 못한 오류가 발생했습니다.");
        }
    }

    private JwtTokenResponse generateTemporaryTokenInstitution(VerifyPhoneRequest verifyPhoneRequest) {
        GenerateTemporaryTokenDto dto = GenerateTemporaryTokenDto.builder()
                .credentialType(CredentialType.LOCAL_INSTITUTION.getKey())
                .credentialId(null)
                .build();
        return transactionTemplate.execute(state -> {
            JwtTokenResponse jwtTokenResponse = tokenService.generateTemporaryTokenInstitutionAdmin(dto);
            TemporaryUserInfo temporaryUserInfo = TemporaryUserInfo.builder()
                    .accessToken(jwtTokenResponse.getAccessToken())
                    .phone(verifyPhoneRequest.getPhoneNumber())
                    .name(verifyPhoneRequest.getName())
                    .birthDate(verifyPhoneRequest.getBirthDate())
                    .expiresIn(jwtTokenResponse.getExpiresIn())
                    .build();
            temporaryUserInfoRepository.save(temporaryUserInfo);
            return jwtTokenResponse;
        });
    }

    public JwtTokenResponse completeRegisterInstitution(
            TemporaryInstitutionAdminDetails userDetails,
            InstitutionLocalRegisterRequest request) {
        TemporaryUserInfo temporaryUserInfo = temporaryUserInfoRepository.findByAccessToken(
                        userDetails.getAccessToken())
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "회원가입 도중 문제가 발생했습니다."));

        String duplicationInformation = InstitutionAdmin.makeDuplicationInformation(temporaryUserInfo.getName(),
                temporaryUserInfo.getBirthDate(), temporaryUserInfo.getPhone());
        if (memberRepository.existsByDuplicationInformation(duplicationInformation)) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "이미 가입된 계정이 있습니다.");
        }

        if (authCredentialRepository.existsByIdentifierAndType(request.getUsername(), CredentialType.LOCAL)) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS, "이미 사용중인 아이디 입니다.");
        }

        // TODO: add old access token to black list
        return transactionTemplate.execute(status -> {
            InstitutionAdmin institutionAdmin = InstitutionAdmin.builder()
                    .name(temporaryUserInfo.getName())
                    .phoneNumber(temporaryUserInfo.getPhone())
                    .birthDate(temporaryUserInfo.getBirthDate())
                    .username(request.getUsername())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(InstitutionAdminRole.STAFF)
                    .duplicationInformation(duplicationInformation)
                    .build();

            institutionAdminRepository.save(institutionAdmin);
            temporaryUserInfoRepository.delete(temporaryUserInfo);
            return generateTokenByInstitutionAdmin(institutionAdmin);
        });
    }

    @Transactional
    public JwtTokenResponse loginMemberLocal(UserLocalLoginRequest request) {
        AuthCredential credential = authCredentialRepository.findAuthCredentialByIdentifierAndType(
                        request.getUsername(),
                        CredentialType.LOCAL)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_USERNAME_PASSWORD));
        if (!passwordEncoder.matches(request.getPassword(), credential.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_USERNAME_PASSWORD);
        }
        return generateTokenByMember(credential.getMember());
    }

    @Transactional
    public JwtTokenResponse loginInstitutionAdmin(InstitutionLocalLoginRequest request) {
        InstitutionAdmin institutionAdmin = institutionAdminRepository.findByUsername(
                        request.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_USERNAME_PASSWORD));
        if (!passwordEncoder.matches(request.getPassword(), institutionAdmin.getPasswordHash())) {
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
        return tokenService.regenerateAccessToken(tokenRefreshRequest);
    }

    public JwtTokenResponse regenerateAccessTokenInstitutionAdmin(TokenRefreshRequest tokenRefreshRequest) {
        return tokenService.regenerateAccessTokenInstitutionAdmin(tokenRefreshRequest);
    }
}
