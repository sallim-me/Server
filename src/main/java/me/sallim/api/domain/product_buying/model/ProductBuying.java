package me.sallim.api.domain.product_buying.model;

import jakarta.persistence.*;
import lombok.*;
import me.sallim.api.domain.product.model.Product;

@Entity
@Table(name = "product_buying")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductBuying {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_buying_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false,unique = true)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    public void update(Integer quantity) {
        this.quantity = quantity;
    }
}

