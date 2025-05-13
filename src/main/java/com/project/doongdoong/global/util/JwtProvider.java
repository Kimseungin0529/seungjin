package com.project.doongdoong.global.util;

import com.project.doongdoong.domain.user.model.SocialIdentifier;
import com.project.doongdoong.global.dto.response.TokenDto;
import com.project.doongdoong.global.exception.MissingRoleClaimException;
import com.project.doongdoong.global.exception.MissingSocialTypeClaimException;
import com.project.doongdoong.global.repositoty.BlackAccessTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Join;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;

    private final long ACCESS_TOKEN_VALIDATION_TIME = 30 * 60 * 1000L; // 30분
    private final long REFRESH_TOKEN_VALIDATION_TIME = 1000L * 60L * 60L * 24L * 14; // 2주
    private final static String BEARER_PREFIX = "Bearer ";
    private final static String SOCIAL_TYPE_PREFIX = "socialType";
    private final static String ROLE_PREFIX = "role";
    private final static String UNIQUE_PREFIX = "unique";
    private final static String JOINING_PREFIX = ",";

    private final BlackAccessTokenRepository blackAccessTokenRepository;


    @PostConstruct
    protected void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    public TokenDto generateToken(String socialId, String socialType, List<String> roles) {
        String role = convertToSingleRoleBy(roles);
        String refreshToken = createRefreshToken(socialId, socialType, role);
        String accessToken = createAccessToken(socialId, socialType, role);

        return TokenDto.of(accessToken, refreshToken);
    }

    public Authentication generateAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (claims.get(ROLE_PREFIX) == null) {
            throw new MissingRoleClaimException();
        }
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(ROLE_PREFIX).toString().split(JOINING_PREFIX))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        if (claims.get(SOCIAL_TYPE_PREFIX) == null) {
            throw new MissingSocialTypeClaimException();
        }
        SocialIdentifier socialIdentifier = SocialIdentifier.of(claims.getSubject(), claims.get(SOCIAL_TYPE_PREFIX, String.class));
        UserDetails principal = new User(socialIdentifier.toUniqueValue(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {  // 토큰의 유효성 검증을 수행
        if (token == null) {
            return false;
        }
        Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return true;
    }

    public boolean isBlackToken(String token) {
        return blackAccessTokenRepository.findByAccessToken(BEARER_PREFIX + token).isPresent();
    }

    public String extractSocialId(String token) {
        String value = token.substring(BEARER_PREFIX.length());
        return Jwts.parser().setSigningKey(key).parseClaimsJws(value).getBody().getSubject();
    }

    public List<String> extractRole(String token) {
        String value = token.substring(BEARER_PREFIX.length());
        String roleString = Jwts.parser().setSigningKey(key).parseClaimsJws(value).getBody().get(ROLE_PREFIX, String.class);

        return Arrays
                .stream(roleString.split(JOINING_PREFIX))
                .toList();
    }

    public String extractSocialType(String token) {
        String value = token.substring(BEARER_PREFIX.length());
        return Jwts.parser().setSigningKey(key).parseClaimsJws(value).getBody().get(SOCIAL_TYPE_PREFIX, String.class);
    }

    private String convertToSingleRoleBy(List<String> roles) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(JOINING_PREFIX));
    }

    private String createRefreshToken(String socialId, String socialType, String role) {

        Claims claims = Jwts.claims().setSubject(socialId);
        claims.put(SOCIAL_TYPE_PREFIX, socialType);
        claims.put(ROLE_PREFIX, role);

        return BEARER_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(new Date().getTime() + REFRESH_TOKEN_VALIDATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String createAccessToken(String socialId, String socialType, String role) {

        Claims claims = Jwts.claims().setSubject(socialId);
        claims.put(SOCIAL_TYPE_PREFIX, socialType);
        claims.put(ROLE_PREFIX, role);
        claims.put(UNIQUE_PREFIX, UUID.randomUUID().toString());

        return BEARER_PREFIX + Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(new Date().getTime() + ACCESS_TOKEN_VALIDATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseClaims(String accessToken) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    }
}
