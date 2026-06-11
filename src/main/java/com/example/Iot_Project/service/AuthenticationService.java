package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.AuthenticationRequest;
import com.example.Iot_Project.dto.request.LogoutRequest;
import com.example.Iot_Project.dto.request.RefreshRequest;
import com.example.Iot_Project.dto.response.AuthenticationResponse;
import com.example.Iot_Project.entity.InvalidatedToken;
import com.example.Iot_Project.entity.User;
import com.example.Iot_Project.enums.ErrorCode;
import com.example.Iot_Project.exception.AppException;
import com.example.Iot_Project.repository.jpa.InvalidatedTokenRepository;
import com.example.Iot_Project.repository.jpa.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.secret-key}")
    String SECRET_KEY;

    @NonFinal
    @Value("${jwt.expiration-hours}")
    long expirationHours;

    @NonFinal
    @Value("${jwt.refreshable-hours:24}")
    long refreshableHours;

    @NonFinal
    @Value("${jwt.issuer}")
    String issuer;

    public AuthenticationResponse authenticate(AuthenticationRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_DID_NOT_EXIST));

        boolean result = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if(!result) throw new AppException(ErrorCode.UNAUTHENTICATED);

        String token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {
            var signedJWT = verifyToken(request.getToken(), true);

            String jit = signedJWT.getJWTClaimsSet().getJWTID();
            Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                    .id(jit)
                    .expiryTime(expiryTime)
                    .build();

            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException e) {
            log.info("Token already expired or invalid, no need to invalidate");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        String jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder()
                .id(jit)
                .expiryTime(expiryTime)
                .build();

        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        boolean verified = signedJWT.verify(verifier);

        if (!verified) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        if (!isRefresh) {
            if (expiryTime.before(new Date())) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
        } else {
            var issueTime = signedJWT.getJWTClaimsSet().getIssueTime();
            var refreshExpiry = Instant.ofEpochMilli(issueTime.getTime())
                    .plus(refreshableHours, ChronoUnit.HOURS);
            if (Instant.now().isAfter(refreshExpiry)) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }
        }

        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(User user){
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer(issuer)
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(expirationHours, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(jwsHeader, payload);

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("do not create token");
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        var roles = user.getRoles();
        if(!CollectionUtils.isEmpty(roles)){
            user.getRoles().forEach(role -> {
                stringJoiner.add(role.getName());
                if(!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions().forEach(permission -> {
                        stringJoiner.add(permission.getName());
                    });
            });
        }
        return stringJoiner.toString();
    }

    @Scheduled(cron = "0 0 2 * * ?") // Runs every day at 2:00 AM
    public void cleanExpiredTokens() {
        invalidatedTokenRepository.deleteByExpiryTimeBefore(new Date());
    }
}
