package com.rs.cancel.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.rs.cancel.dto.LoginDto;
import com.rs.cancel.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;


    public String generateToken(LoginDto loginDto) {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder().subject(loginDto.getUsernameOrEmail())

                .issuer("jwt.com")
                .issueTime(new Date())

                .expirationTime(new Date(Instant.now().plusSeconds(60 * 60).toEpochMilli())).build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(jwtSecret.getBytes()));

            return jwsObject.serialize();
        } catch (JOSEException e) {

            throw new RuntimeException(e);
        }
    }
    public JWTClaimsSet parseToken(String token) throws ParseException, JOSEException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Xác minh token bằng khóa bí mật
        if (signedJWT.verify(new MACVerifier(jwtSecret))) {
            return signedJWT.getJWTClaimsSet(); // Trả về thông tin các claims
        } else {
            throw new JOSEException("Token verification failed");
        }
    }

    public boolean isTokenExpired(String token) throws ParseException, JOSEException {
        return extractExpiration(token).before(new Date());
    }

public boolean checkToken(String token) throws ParseException, JOSEException {
             JWSObject jwsObject = JWSObject.parse(token);
             if (jwsObject.verify(new MACVerifier(jwtSecret)) ) {
                 return true;
             }
             return false;
}
    // Trích xuất username từ JWT token
    public String extractUsername(String token) throws ParseException, JOSEException {
        return parseToken(token).getSubject();
    }

    // Trích xuất expiration date từ JWT token
    public Date extractExpiration(String token) throws ParseException, JOSEException {
        return parseToken(token).getExpirationTime();
    }
    // trichs xuat date khoi tao
    public Date ok(String token) throws ParseException, JOSEException {
        return parseToken(token).getIssueTime();
    }

    // Trích xuất các thông tin khác từ JWT token
    public Object extractClaim(String token, String claimKey) throws ParseException, JOSEException {
        return parseToken(token).getClaim(claimKey);
    }
}
