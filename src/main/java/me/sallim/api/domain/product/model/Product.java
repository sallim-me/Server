package me.sallim.api.domain.product.model;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "appliance_id")
    private Long appliance_id;

    @Column(name = "title", length = 500)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_active", nullable = false, columnDefinition = "bit(1) default 1")
    private Boolean isActive;

    @Column(name = "post_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PostTypeEnum postType;

    @Column(name = "product_photo_id")
    private Long productPhotoId;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}