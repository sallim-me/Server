package me.sallim.api.domain.product_selling.model;

import jakarta.persistence.*;
import lombok.*;
import me.sallim.api.domain.product.model.Product;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_selling")
public class ProductSelling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_selling_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "model_number", nullable = false)
    private String modelNumber;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "brand", nullable = false)
    private String brand;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "user_price", nullable = false)
    private int userPrice;

    public void updateSellingInfo(String modelNumber, String modelName, String brand, int price, int userPrice) {
        this.modelNumber = modelNumber;
        this.modelName = modelName;
        this.brand = brand;
        this.price = price;
        this.userPrice = userPrice;
    }
}
