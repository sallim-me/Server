package me.sallim.api.domain.product.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_buying") // TODO: index
public class ProductBuying {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_buying_id")
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "quantity")
    private int quantity;
}
