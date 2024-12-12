package com.swyp.playground.domain.parent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.playground.common.response.CommonResponse;
import com.swyp.playground.domain.parent.domain.Parent;
import com.swyp.playground.domain.parent.dto.req.ParentCreateReqDto;
import com.swyp.playground.domain.parent.dto.req.ParentPasswordChangeReqDto;
import com.swyp.playground.domain.parent.dto.req.ParentUpdateReqDto;
import com.swyp.playground.domain.parent.dto.res.ParentCreateResDto;
import com.swyp.playground.domain.parent.dto.res.ParentPasswordChangeResDto;
import com.swyp.playground.domain.parent.service.ParentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;
    private final ObjectMapper objectMapper;


    @PostMapping(value = "/signup", consumes = {"multipart/form-data"})
    public ResponseEntity<ParentCreateResDto> signUp(
            @RequestPart("data") String requestData,
            @RequestPart(value = "file", required = false) MultipartFile profileImage) {
        try {
            // JSON 데이터를 DTO로 변환
            ParentCreateReqDto request = objectMapper.readValue(requestData, ParentCreateReqDto.class);

            if (profileImage != null) {
                request.setProfileImage(profileImage);
            }

            ParentCreateResDto response = parentService.signUp(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/users/{id}")
    public ResponseEntity<ParentCreateResDto> getParentById(@PathVariable Long id){
        ParentCreateResDto response = parentService.getParentById(id);
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping(value = "/users/edit/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateParent(
            @PathVariable Long id,
            @RequestPart("data") String requestData,
            @RequestPart(value = "file", required = false) MultipartFile profileImage,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            // JSON 데이터를 DTO로 변환
            ParentUpdateReqDto request = objectMapper.readValue(requestData, ParentUpdateReqDto.class);
            String authenticatedEmail = authenticatedUser.getUsername();
            Parent parent = parentService.getParentEntityById(id);
            if (!parent.getEmail().equals(authenticatedEmail)) {
                return ResponseEntity.status(403).body("자신의 정보만 수정할 수 있습니다.");
            }
            if (profileImage != null) {
                request.setProfileImage(profileImage);
            }
            ParentCreateResDto response = parentService.updateParent(id, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        }
    }



    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/users/all")
    public ResponseEntity<List<ParentCreateResDto>> getAllParents() {
        List<ParentCreateResDto> response = parentService.getAllParents();
        return ResponseEntity.ok(response);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/users/delete/{id}")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParentById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/get-nickname")
    @Operation(summary = "사용자 닉네임 조회", description = "이메일을 이용해 사용자의 닉네임을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "닉네임 조회 성공", content = @Content(schema = @Schema(example = "{\"nickname\": \"사용자 닉네임\"}")))
    @ApiResponse(responseCode = "400", description = "닉네임 조회 실패", content = @Content(schema = @Schema(example = "{\"error\": \"해당 이메일을 가진 사용자가 존재하지 않습니다.\"}")))
    public ResponseEntity<Map<String, String>> getNickname(@RequestParam String email) {
        try {
            String nickname = parentService.getNicknameByEmail(email);
            Map<String, String> response = new HashMap<>();
            response.put("nickname", nickname);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }


    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/password/{parentId}")
    public ResponseEntity<?> changePassword(
            @PathVariable Long parentId,
            @Valid @RequestBody ParentPasswordChangeReqDto request,
            @AuthenticationPrincipal User authenticatedUser) {
        try {
            String authenticatedEmail = authenticatedUser.getUsername();
            Parent parent = parentService.getParentEntityById(parentId);
            if (!parent.getEmail().equals(authenticatedEmail)) {
                return ResponseEntity.status(403).body("자신의 비밀번호만 변경할 수 있습니다.");
            }
            parentService.changePassword(parentId, request);
            return ResponseEntity.ok(new ParentPasswordChangeResDto("비밀번호가 성공적으로 변경되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("비밀번호 변경 실패: " + e.getMessage());
        }
    }
}
