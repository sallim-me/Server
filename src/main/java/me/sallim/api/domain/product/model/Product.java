package me.sallim.api.domain.product.model;

import jakarta.persistence.*;
import lombok.*;
import me.sallim.api.domain.appliance_type_question.model.ApplianceType;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product_buying.model.ProductBuying;
import me.sallim.api.domain.product_photo.model.ProductPhoto;
import me.sallim.api.domain.product_selling.model.ProductSelling;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product")
@EntityListeners(AuditingEntityListener.class)
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_photo_id", nullable = true)
    private ProductPhoto productPhotoId;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductPhoto> productPhotos = new ArrayList<>();

    @OneToOne(mappedBy = "product")
    private ProductSelling productSelling;

    @OneToOne(mappedBy = "product")
    private ProductBuying productBuying;

    public void updateProductInfo(String title, String content, ApplianceType applianceType, boolean isActive) {
        this.title = title;
        this.content = content;
        this.applianceType = applianceType;
        this.isActive = isActive;
    }

    public boolean isDeleted() {
        return this.isActive == null || !this.isActive;
    }
}

