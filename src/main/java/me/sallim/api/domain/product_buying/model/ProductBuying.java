package me.sallim.api.domain.product_buying.model;

import jakarta.persistence.*;
import lombok.*;
import me.sallim.api.domain.appliance.ApplianceType;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.global.entity.BaseEntity;

@Entity
@Table(name = "product_buying")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductBuying extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_buying_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false,unique = true)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer price;

    public void update(Integer quantity, Integer price) {
        this.quantity = quantity;
        this.price = price;
    }
}