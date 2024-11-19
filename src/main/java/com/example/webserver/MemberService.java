package com.example.webserver;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public ResponseEntity<MemberResponseDto> createMember(MemberRequestDto dto){
        Member member = new Member(dto.getName());
        Member saved = memberRepository.save(member);
        return ResponseEntity.status(200).body(new MemberResponseDto(saved.getId(), saved.getName()));
    }
}
