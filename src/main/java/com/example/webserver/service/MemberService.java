package com.example.webserver.service;

import com.example.webserver.dto.MemberRequestDto;
import com.example.webserver.dto.MemberResponseDto;
import com.example.webserver.entity.Member;
import com.example.webserver.login.JwtToken;
import com.example.webserver.login.JwtTokenProvider;
import com.example.webserver.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    @Transactional
    public String signUp(String username, String password){
        Optional<Member> user = memberRepository.findByUsername(username);
        if(user.isPresent()){
            return "already exist";
        }
        Member member = new Member();
        member.setUsername(username);
        member.setPassword(password);
        ArrayList<String> roles = new ArrayList<>();
        roles.add("USER");
        member.setRoles(roles);
        memberRepository.save(member);
        return "sign up";
    }

    @Transactional
    public JwtToken signIn(String username, String password) {
        // 1. username + password 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        log.info("itsok 11");

        // 2. 실제 검증. authenticate() 메서드를 통해 요청된 CustomUserDetails 에 대한 검증 진행
        // authenticate 메서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        log.info("itsok 22");

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        log.info("itsok 33");

        return jwtToken;
    }

}
