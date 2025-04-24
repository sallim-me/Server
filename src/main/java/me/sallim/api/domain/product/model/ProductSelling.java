package me.sallim.api.domain.product.model;

import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_selling")
public class ProductSelling {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_selling_id")
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "model_number")
    private String modelNumber;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "model_specification", columnDefinition = "varchar(2048)")
    private String modelSpecification;

    @Column(name = "price")
    private int price;
}
