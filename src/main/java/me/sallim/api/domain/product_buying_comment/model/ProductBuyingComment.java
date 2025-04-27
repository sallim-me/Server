package me.sallim.api.domain.product_buying_comment.model;

import jakarta.persistence.*;
import lombok.*;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product_buying.model.ProductBuying;
import me.sallim.api.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_comment")
public class ProductBuyingComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Long memberId;

    @Column(length = 2048)
    private String content;
}

