package me.sallim.api.domain.product_buying_comment.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product.model.PostTypeEnum;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product.repository.ProductRepository;
import me.sallim.api.domain.product_buying.model.ProductBuying;
import me.sallim.api.domain.product_buying.repository.ProductBuyingRepository;
import me.sallim.api.domain.product_buying_comment.dto.request.CreateProductBuyingCommentRequest;
import me.sallim.api.domain.product_buying_comment.dto.response.ProductBuyingCommentResponse;
import me.sallim.api.domain.product_buying_comment.model.ProductBuyingComment;
import me.sallim.api.domain.product_buying_comment.repository.ProductBuyingCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductBuyingCommentService {

    private final ProductRepository productRepository;
    private final ProductBuyingCommentRepository productBuyingCommentRepository;

    @Transactional
    public ProductBuyingCommentResponse createComment(Member loginMember, Long productBuyingId, CreateProductBuyingCommentRequest request) {
        Product product = productRepository.findById(productBuyingId)
                .orElseThrow(() -> new IllegalArgumentException("구매 글을 찾을 수 없습니다."));

        ProductBuyingComment comment = ProductBuyingComment.builder()
                .product(product)
                .memberId(loginMember.getId())
                .content(request.content())
                .build();

        productBuyingCommentRepository.save(comment);

        return ProductBuyingCommentResponse.from(comment);
    }

    @Transactional(readOnly = true)
    public List<ProductBuyingCommentResponse> getComments(Long productBuyingId) {
        List<ProductBuyingComment> comments = productBuyingCommentRepository.findByProductId(productBuyingId);

        return comments.stream()
                .map(ProductBuyingCommentResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Member loginMember, Long commentId) {
        ProductBuyingComment comment = productBuyingCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMemberId().equals(loginMember.getId())) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        productBuyingCommentRepository.delete(comment);
    }
}
