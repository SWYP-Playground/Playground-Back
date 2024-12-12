package com.swyp.playground.domain.login.controller;

import com.swyp.playground.common.jwt.JwtTokenProvider;
import com.swyp.playground.domain.login.dto.req.LoginReqDto;
import com.swyp.playground.domain.login.dto.res.LoginResDto;

import com.swyp.playground.domain.login.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class LoginController {

    private final LoginService loginService;
    private final JwtTokenProvider tokenProvider;


    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResDto> login(@Valid @RequestBody LoginReqDto loginReqDto) {
        LoginResDto response = loginService.login(loginReqDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        loginService.logout(token.substring(7));
        return ResponseEntity.ok().build();
    }
//    @GetMapping("/islogin")
//    public ResponseEntity<Map<String, String>> getUserInfo(@RequestHeader("Authorization") String token) {
//        String cleanToken = token.substring(7); // "Bearer " 제거
//        String email = tokenProvider.getEmailFromToken(cleanToken);
//        String nickname = tokenProvider.getNicknameFromToken(cleanToken);
//
//        Map<String, String> userInfo = Map.of(
//                "email", email,
//                "nickname", nickname
//        );
//        return ResponseEntity.ok(userInfo);
//    }
}
