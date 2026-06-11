package com.example.Iot_Project.service;

import com.example.Iot_Project.dto.request.IntrospectRequest;
import com.example.Iot_Project.dto.response.IntrospectResponse;
import com.example.Iot_Project.repository.jpa.InvalidatedTokenRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IntrospectService {

    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.secret-key}")
    String SECRET_KEY;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        String token = request.getToken();

        try {
            JWSVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);
            var verify = signedJWT.verify(verifier);
            var expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            boolean isValid = verify && expiryTime.after(new Date()) &&
                    !invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID());
            return IntrospectResponse.builder()
                    .isValid(isValid)
                    .build();
        } catch (Exception e) {
            return IntrospectResponse.builder()
                    .isValid(false)
                    .build();
        }
    }
}
