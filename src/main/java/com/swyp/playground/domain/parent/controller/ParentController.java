package com.swyp.playground.domain.parent.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.playground.common.response.CommonResponse;
import com.swyp.playground.domain.parent.domain.Parent;
import com.swyp.playground.domain.parent.dto.req.ParentCreateReqDto;
import com.swyp.playground.domain.parent.dto.req.ParentUpdateReqDto;
import com.swyp.playground.domain.parent.dto.res.ParentCreateResDto;
import com.swyp.playground.domain.parent.service.ParentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

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
    @PostMapping("/users/delete/{id}")
    public ResponseEntity<Void> deleteParent(@PathVariable Long id) {
        parentService.deleteParentById(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/get-nickname")
    public String getNickname(@RequestParam String email) {
        return parentService.getNicknameByEmail(email);
    }

    @GetMapping("/users/{id}/manner-temp")
    public ResponseEntity<BigDecimal> getParentMannerTemp(@PathVariable Long id) {
        BigDecimal averageTemp = parentService.getParentMannerTemp(id);
        return ResponseEntity.ok(averageTemp);
    }

}
