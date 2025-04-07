package me.sallim.api.domain.member.converter;

import me.sallim.api.domain.member.dto.request.MemberUpdateRequestDTO;
import me.sallim.api.domain.member.dto.response.MemberInfoResponseDTO;
import me.sallim.api.domain.member.model.Member;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class MemberConverter {

    public static MemberInfoResponseDTO toMemberInfo(Member member) {
        return new MemberInfoResponseDTO(
                member.getUsername(),
                member.getName(),
                member.getNickname(),
                member.getIsBuyer()
        );
    }

    public static void updateMember(Member member, MemberUpdateRequestDTO request, PasswordEncoder passwordEncoder) {
        member.updateUsername(request.username());
        member.updateName(request.name());
        member.updateNickname(request.nickname());
        member.updateIsBuyer(request.isBuyer());

        if (request.password() != null && !request.password().isBlank()) {
            member.updatePassword(passwordEncoder.encode(request.password()));
        }
    }
}