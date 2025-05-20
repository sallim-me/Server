package me.sallim.api.domain.product.repository;

import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    void delete(Product product);
    List<Product> findByMember(Member member);
}
