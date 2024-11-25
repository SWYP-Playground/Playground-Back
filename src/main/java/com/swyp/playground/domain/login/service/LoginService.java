package com.swyp.playground.domain.login.service;

import com.swyp.playground.common.domain.TypeChange;
import com.swyp.playground.common.jwt.JwtTokenProvider;
import com.swyp.playground.common.redis.RedisService;
import com.swyp.playground.domain.login.dto.req.LoginReqDto;
import com.swyp.playground.domain.login.dto.res.LoginResDto;
import com.swyp.playground.domain.parent.domain.Parent;
import com.swyp.playground.domain.parent.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final JwtTokenProvider tokenProvider;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final ParentRepository parentRepository;


    public LoginResDto login(LoginReqDto loginReqDto) {
        String email = loginReqDto.getEmail();
        String password = loginReqDto.getPassword();

        // DB에서 사용자 조회
        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

        // 비밀번호 비교
        if (!passwordEncoder.matches(password, parent.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }

        // JWT 토큰 생성
        String token = tokenProvider.generateToken(email);
        String refreshToken = tokenProvider.generateRefreshToken(email);


        // Redis에 토큰 저장
        redisService.saveToken(token, email, 3600000); // 1시간 유지
        redisService.saveToken("refresh_" + email, refreshToken, 604800000);  // 7일 유지

        return LoginResDto.builder()
                .email(email)
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    public void logout(String token) {
        // Access Token 삭제
        redisService.deleteToken(token);

        // Refresh Token 삭제
        String email = tokenProvider.getEmailFromToken(token);
        String refreshTokenKey = "refresh_" + email; // Refresh Token 키 규칙
        redisService.deleteToken(refreshTokenKey);
    }
}