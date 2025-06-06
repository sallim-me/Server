package me.sallim.api.domain.product.repository;

import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    void delete(Product product);
    List<Product> findByMember(Member member);
    @Query("SELECT p FROM Product p JOIN FETCH p.member WHERE p.id = :id")
    Optional<Product> findByIdWithMember(@Param("id") Long id);
}
