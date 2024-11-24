package com.swyp.playground.domain.parent.controller;

import com.swyp.playground.domain.parent.dto.req.ParentCreateReqDto;
import com.swyp.playground.domain.parent.dto.res.ParentCreateResDto;
import com.swyp.playground.domain.parent.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @PostMapping("/signup")
    public ResponseEntity<ParentCreateResDto> signUp(@Validated @RequestBody ParentCreateReqDto request) {
        ParentCreateResDto response = parentService.signUp(request);
        return ResponseEntity.ok(response);
    }
}
