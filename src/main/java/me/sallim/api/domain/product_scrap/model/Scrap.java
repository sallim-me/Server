package me.sallim.api.domain.product_scrap.model;

import jakarta.persistence.*;
import lombok.*;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.global.entity.BaseEntity;

@Entity
@Table(name = "scrap", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_id", "product_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Scrap extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    public void updateMemo(String memo) {
        this.memo = memo;
        this.setUpdatedAt();
    }
}
