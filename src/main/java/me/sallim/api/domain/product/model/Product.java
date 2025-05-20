package me.sallim.api.domain.product.model;

import jakarta.persistence.*;
import lombok.*;
import me.sallim.api.domain.appliance.ApplianceType;
import me.sallim.api.domain.member.model.Member;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class Product {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "appliance_type")
    private ApplianceType applianceType;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "post_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostTypeEnum postType;

    @Column(name = "product_photo_id")
    private Long productPhotoId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void updateProductInfo(String title, String content, ApplianceType applianceType, boolean isActive) {
        this.title = title;
        this.content = content;
        this.applianceType = applianceType;
        this.isActive = isActive;
    }
}