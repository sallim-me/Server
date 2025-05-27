package me.sallim.api.domain.product_scrap.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.member.repository.MemberRepository;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product.repository.ProductRepository;
import me.sallim.api.domain.product_scrap.dto.request.ScrapRequest;
import me.sallim.api.domain.product_scrap.dto.response.ScrapListResponse;
import me.sallim.api.domain.product_scrap.dto.response.ScrapResponse;
import me.sallim.api.domain.product_scrap.model.Scrap;
import me.sallim.api.domain.product_scrap.repository.ScrapRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScrapService {

    private final ScrapRepository scrapRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public ScrapResponse createScrap(Long memberId, ScrapRequest requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("Member not found with id: " + memberId));

        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + requestDto.getProductId()));

        // Check if already scrapped
        if (scrapRepository.findByMemberIdAndProductId(memberId, requestDto.getProductId()).isPresent()) {
            throw new IllegalStateException("Product already scrapped by this member");
        }

        Scrap scrap = Scrap.builder()
                .member(member)
                .product(product)
                .memo(requestDto.getMemo())
                .build();

        return ScrapResponse.from(scrapRepository.save(scrap));
    }

    @Transactional(readOnly = true)
    public ScrapListResponse getScrapsByMemberId(Long memberId, Pageable pageable) {
        if (!memberRepository.existsById(memberId)) {
            throw new EntityNotFoundException("Member not found with id: " + memberId);
        }

        Page<ScrapResponse> scrapPage = scrapRepository.findByMemberId(memberId, pageable)
                .map(ScrapResponse::from);

        return ScrapListResponse.from(scrapPage);
    }

    @Transactional
    public void deleteScrap(Long scrapId, Long memberId) {
        Scrap scrap = scrapRepository.findById(scrapId)
                .orElseThrow(() -> new EntityNotFoundException("Scrap not found with id: " + scrapId));

        // Verify the scrap belongs to the member
        if (!scrap.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("Scrap does not belong to this member");
        }

        scrap.delete();
    }

    @Transactional
    public ScrapResponse updateScrapMemo(Long scrapId, Long memberId, String memo) {
        Scrap scrap = scrapRepository.findById(scrapId)
                .orElseThrow(() -> new EntityNotFoundException("Scrap not found with id: " + scrapId));

        // Verify the scrap belongs to the member
        if (!scrap.getMember().getId().equals(memberId)) {
            throw new IllegalStateException("Scrap does not belong to this member");
        }

        scrap.updateMemo(memo);
        return ScrapResponse.from(scrap);
    }

    @Transactional(readOnly = true)
    public boolean isProductScrappedByMember(Long productId, Long memberId) {
        return scrapRepository.findByMemberIdAndProductId(memberId, productId).isPresent();
    }

    @Transactional(readOnly = true)
    public long getScrapCountByProduct(Long productId) {
        return scrapRepository.countByProductId(productId);
    }
}
