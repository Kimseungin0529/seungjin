package com.project.doongdoong.global.fliter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.doongdoong.global.common.ApiResponse;
import com.project.doongdoong.global.util.JwtProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.project.doongdoong.global.config.SecurityConfig.ALLOW_REQUEST;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/actuator")) { // 엑츄에이터 요청인 경우 필터링을 건너뛰기
            filterChain.doFilter(request, response);
            return;
        }

        try{
            boolean isAllowed = Arrays.stream(ALLOW_REQUEST).anyMatch(uri -> request.getRequestURI().equals(uri));
            log.info("isAllowed = {}", isAllowed);
            if(!isAllowed){
                String token = resolveToken(request);
                log.info("JWT 필터 검사 시작.");
                log.info("Bearer 삭제한 token 값 = {}", token);

                //토큰이 존재하면서 유효하다면 Authentication 객체 생성, 시큐리티 컨텍스트 홀더에 Authentication 저장
                if(jwtProvider.validateToken(token) && !jwtProvider.isBlackToken(token)) {
                    Authentication authentication = jwtProvider.generateAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                log.info("토큰 검증 성공");
            }else{
                log.info("허용한 uri");
            }
            filterChain.doFilter(request,response);

        }catch (SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 토큰입니다.");
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."); // 만료된 토큰이라면 rft이 있는 경우, 재발급해주기
        } catch (UnsupportedJwtException e) {
            log.info("지원하지 않는 토큰입니다.");
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "지원하지 않는 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
            log.error("잘못된 JWT 토큰 오류 = {}", e.getMessage());
            setErrorResponse(response, HttpStatus.UNAUTHORIZED, "JWT 토큰이 잘못되었습니다.");
        }



    }

    private void setErrorResponse(
            HttpServletResponse response,
            HttpStatus status,
            String message
    ){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ApiResponse<Object> result = ApiResponse.of(status, message, null);
        try{
            response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(result));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
