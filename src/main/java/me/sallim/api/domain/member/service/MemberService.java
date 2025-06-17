package me.sallim.api.domain.member.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.converter.MemberConverter;
import me.sallim.api.domain.member.dto.request.MemberJoinRequestDTO;
import me.sallim.api.domain.member.dto.request.MemberUpdateRequestDTO;
import me.sallim.api.domain.member.dto.response.MemberInfoResponseDTO;
import me.sallim.api.domain.member.dto.response.MemberPostSummaryResponse;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.member.repository.MemberRepository;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProductRepository productRepository;

    @Value("${spring.minio.endpoint}")
    private String minioEndpoint;

    @Transactional(readOnly = true)
    public boolean isUsernameAvailable(String username) {
        return memberRepository.findByUsername(username).isEmpty();
    }

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

    @Transactional(readOnly = true)
    public MemberInfoResponseDTO getMyInfo(Member loginMember) {
        return MemberConverter.toMemberInfo(loginMember);
    }

    @Transactional(readOnly = true)
    public List<MemberPostSummaryResponse> getMyPosts(Member loginMember) {
        List<Product> products = productRepository.findByMember(loginMember);
        return products.stream()
                .map(product -> MemberPostSummaryResponse.from(product, minioEndpoint))
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateFcmToken(Long memberId, String fcmToken) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.updateFcmToken(fcmToken);
    }
}
