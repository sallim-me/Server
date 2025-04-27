package me.sallim.api.domain.member.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.converter.MemberConverter;
import me.sallim.api.domain.member.dto.request.MemberJoinRequestDTO;
import me.sallim.api.domain.member.dto.request.MemberUpdateRequestDTO;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long join(MemberJoinRequestDTO request) {
        if (memberRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.builder()
                .username(request.username())
                .password(encodedPassword)
                .nickname(request.nickname())
                .name(request.name())
                .isBuyer(request.isBuyer())
                .build();

        return memberRepository.save(member).getId();
    }

    @Transactional
    public void updateProfile(Member member, MemberUpdateRequestDTO request) {
        MemberConverter.updateMember(member, request, passwordEncoder);
        memberRepository.save(member);
        memberRepository.flush();
    }
}
