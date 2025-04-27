package me.sallim.api.domain.product_buying.repository;

import me.sallim.api.domain.product_buying.model.ProductBuying;
import me.sallim.api.domain.product_buying_comment.model.ProductBuyingComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductBuyingRepository extends JpaRepository<ProductBuying, Long> {}
