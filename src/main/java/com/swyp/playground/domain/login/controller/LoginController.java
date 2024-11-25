package com.swyp.playground.domain.login.controller;

import com.swyp.playground.domain.login.dto.req.LoginReqDto;
import com.swyp.playground.domain.login.dto.res.LoginResDto;

import com.swyp.playground.domain.login.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class LoginController {

    private final LoginService loginService;


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
}
