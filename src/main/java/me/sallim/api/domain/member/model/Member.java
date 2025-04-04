package me.sallim.api.domain.member.model;

import jakarta.persistence.*;
import lombok.*;
import me.sallim.api.global.BaseEntity;

@Entity
@Table(name = "members")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // 로그인용 아이디

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String nickname; // 닉네임

    @Column(nullable = false)
    private String name; // 실명

    @Column(nullable = false)
    private Boolean isBuyer; // 바이어 여부

    // 비밀번호 수정
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // 닉네임 수정
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }

    // 회원 탈퇴 (soft delete 처리)
    public void withdraw() {
        this.delete(); // BaseEntity의 메서드
    }
}