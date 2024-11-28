package com.example.webserver.controller;

import com.example.webserver.login.CustomUserDetails;
import com.example.webserver.login.JwtToken;
import com.example.webserver.login.SignInDto;
import com.example.webserver.login.SignUpDto;
import com.example.webserver.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/test")
    private String test(){
        return "test okkkk";
    }

    @PostMapping("/sign-up")
    public String signup(@RequestBody SignUpDto signUpDto){
        String username = signUpDto.getUsername();
        String password = signUpDto.getPassword();
        String result = memberService.signUp(username,password);
        log.info("회원가입 결과 :"+result);
        return result;
    }

    @PostMapping("/sign-in")
    public JwtToken signIn(@RequestBody SignInDto signInDto) {
        String username = signInDto.getUsername();
        String password = signInDto.getPassword();
        JwtToken jwtToken = memberService.signIn(username, password);
        log.info("request username = {}, password = {}", username, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }

    @GetMapping("/test")
    public String testEndpoint() {
        // 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "사용자가 인증되지 않았습니다.";
        }

        // 사용자 정보를 CustomUserDetails로 캐스팅
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return String.format(
                "인증 성공! 사용자명: %s, ID: %s, 권한: %s",
                userDetails.getUsername(),
                userDetails.getIdx(),
                userDetails.getAuthorities()
        );
    }


}