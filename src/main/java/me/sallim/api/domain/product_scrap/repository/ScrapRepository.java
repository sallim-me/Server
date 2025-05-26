package me.sallim.api.domain.product_scrap.repository;

import me.sallim.api.domain.product_scrap.model.Scrap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {

    Page<Scrap> findByMemberId(Long memberId, Pageable pageable);

    Optional<Scrap> findByMemberIdAndProductId(Long memberId, Long productId);

    @Query("SELECT COUNT(s) FROM Scrap s WHERE s.product.id = :productId AND s.deletedAt IS NULL")
    long countByProductId(@Param("productId") Long productId);
}
