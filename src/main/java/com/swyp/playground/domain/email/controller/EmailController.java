package com.swyp.playground.domain.email.controller;

import com.swyp.playground.common.response.CommonResponse;
import com.swyp.playground.domain.email.service.EmailService;
import com.swyp.playground.domain.parent.domain.Parent;
import com.swyp.playground.domain.parent.repository.ParentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class EmailController {

    private final EmailService emailService;
    private final ParentRepository parentRepository;
    private final PasswordEncoder passwordEncoder;


    @Operation(
            summary = "비밀번호 초기화",
            description = "사용자가 이메일을 입력하면 새 비밀번호를 생성하여 이메일로 발송합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "비밀번호 초기화 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommonResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "등록되지 않은 이메일로 요청한 경우",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<CommonResponse> resetPassword(@RequestParam(value = "email") String email) throws Exception {
        System.out.println("이메일" + email);
        // 이메일이 존재하지 않으면 에러 반환
        Parent parent = parentRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("등록되지 않은 이메일입니다."));

        // 새 비밀번호 생성 및 이메일 발송
        String newPassword = emailService.sendPasswordResetEmail(email);

        // 데이터베이스에 새 비밀번호 저장
        parent.setPassword(passwordEncoder.encode(newPassword));
        parentRepository.save(parent);

        return ResponseEntity.ok(new CommonResponse(HttpStatus.OK, "비밀번호 초기화 완료. 이메일을 확인하세요.", null));
    }
}