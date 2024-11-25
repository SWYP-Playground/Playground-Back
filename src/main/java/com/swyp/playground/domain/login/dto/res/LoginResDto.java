package com.swyp.playground.domain.login.dto.res;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginResDto {
    private String email;
    private String token;
    private String refreshToken;
}
