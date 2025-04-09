package me.sallim.api.domain.product.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_photo")
public class ProductPhoto {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_photo_id")
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "url", columnDefinition = "varchar(2048)")
    private String url;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private String createdAt;
}
