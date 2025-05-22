package me.sallim.api.domain.product_selling_answer.model;

import jakarta.persistence.*;
import lombok.*;
import me.sallim.api.domain.appliance_type_question.model.ApplianceTypeQuestion;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "product_selling_answer")
public class ProductSellingAnswer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_selling_answer_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private ApplianceTypeQuestion question;

    @Column(length = 2048, nullable = false)
    private String content;

    public void updateAnswerContent(String newContent) {
        this.content = newContent;
    }
}