package com.example.webserver.service;

import com.example.webserver.entity.Member;
import com.example.webserver.login.JwtToken;
import com.example.webserver.login.JwtTokenProvider;
import com.example.webserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SimpMessagingTemplate messagingTemplate;
    private final CounterService counterService;

    @Transactional
    public String signUp(String username, String password){
        Optional<Member> user = memberRepository.findByUsername(username);
        if(user.isPresent()){
            return "already exist";
        }

        // 자동 증가 idx 생성
        long idx = counterService.getNextUserIdxSequence("member_idx");

        Member member = new Member();
        member.setUsername(username);
        member.setPassword(password);
        member.setIdx(String.valueOf(idx)); // idx를 문자열로 변환하여 설정
        ArrayList<String> roles = new ArrayList<>();
        roles.add("USER");
        member.setRoles(roles);
        memberRepository.save(member);
        return "sign up";
    }

    @Transactional
    public JwtToken signIn(String username, String password) {
        // 1. username + password를 기반으로 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        log.info("Step 1: AuthenticationToken Generate - username: {}", username);

        // 2. authenticate() 메서드 실행 (CustomUserDetailsService.loadUserByUsername 호출)
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("Step 2: Authentication Success - isAuthenticated: {}", authentication.isAuthenticated());
        log.info("{}", authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        log.info("Step 3: JWT Generated");

        return jwtToken;
    }


}
