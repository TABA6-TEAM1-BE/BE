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

        // authenticationManager 가 사용자 인증을 진행해 주는데 로그인 정보와 DB에 있는 사용자 정보를 비교하기 위해
        // UserDetailsService 에 있는 loadUserByUsername 메서드를 내부적으로 '알아서' 호출해서
        // 사용자를 인증한 후 성공하면(db에 있는 유저의 아이디, 비밀번호와 입력받은 로그인 정보가 일치하면) Authentication 객체를 반환
        // 2. authenticationManager -> authenticate() 메서드 실행 (CustomUserDetailsService.loadUserByUsername 호출)
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("Step 2: Authentication Success - isAuthenticated: {}", authentication.isAuthenticated());
        log.info("{}", authentication);
        // 사용자 인증이 끝나고 Authentication 이 성공적으로 반환되었다면 이를 SecurityContextHolder 에 저장 -> 내부적으로 실행

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        log.info("Step 3: JWT Generated");

        return jwtToken;
    }


}
