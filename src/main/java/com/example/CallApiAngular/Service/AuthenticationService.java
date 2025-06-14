package com.example.CallApiAngular.Service;

import com.example.CallApiAngular.DTO.Request.AuthenticationRequest;
import com.example.CallApiAngular.DTO.Request.IntrospectRequest;
import com.example.CallApiAngular.DTO.Response.AuthenticationResponse;
import com.example.CallApiAngular.DTO.Response.IntrospectResponse;
import com.example.CallApiAngular.Repository.UserRepository;
import com.example.CallApiAngular.entity.Users;
import com.example.CallApiAngular.exception.AppException;
import com.example.CallApiAngular.exception.ErrorCode;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    @NonFinal
    @Value("${JWT_SIGNER_KEY}")
    protected String signerkey;

    public IntrospectResponse introspect(IntrospectRequest request) // Kiểm tra tính hợp lệ của token JWT
            throws JOSEException, ParseException {
        var token = request.getToken();

        JWSVerifier verifier = new MACVerifier(signerkey.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        return IntrospectResponse.builder()
                .valid(verified && expiryTime.after(new Date()))
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUserName())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassWord(),
                user.getPassword());
        if (!authenticated)
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .userName(user.getUsername())
                .token(token)
                .authenticated(true)
                .id(user.getId())
                .build();
    }

    private String generateToken(Users users) {// Tạo JWT token mới
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimNames = new JWTClaimsSet.Builder()
                .subject(users.getUsername())
                .issuer("DinhCongVy")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .claim("scope", buildScope(users))
                .build();
        Payload payload = new Payload(jwtClaimNames.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(signerkey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token ", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(Users users) { // Gắn SCOPE
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(users.getRoles()))
            users.getRoles().forEach(role -> stringJoiner.add("SCOPE_" + role));
        return stringJoiner.toString();
    }
}
