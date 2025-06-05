package me.sallim.api.domain.product_comment.service;

import lombok.RequiredArgsConstructor;
import me.sallim.api.domain.member.model.Member;
import me.sallim.api.domain.product.model.Product;
import me.sallim.api.domain.product.repository.ProductRepository;
import me.sallim.api.domain.product_comment.dto.request.CreateProductCommentRequest;
import me.sallim.api.domain.product_comment.dto.response.ProductCommentResponse;
import me.sallim.api.domain.product_comment.model.ProductComment;
import me.sallim.api.domain.product_comment.repository.ProductCommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductCommentService {

    private final ProductRepository productRepository;
    private final ProductCommentRepository productCommentRepository;

    @Transactional
    public ProductCommentResponse createComment(Member loginMember, Long productId, CreateProductCommentRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글을 찾을 수 없습니다."));

        ProductComment comment = ProductComment.builder()
                .product(product)
                .member(loginMember)
                .content(request.content())
                .build();

        productCommentRepository.save(comment);
        return ProductCommentResponse.from(comment);
    }

    @Transactional(readOnly = true)
    public List<ProductCommentResponse> getComments(Long productId) {
        return productCommentRepository.findByProductId(productId).stream()
                .map(ProductCommentResponse::from)
                .toList();
    }

    @Transactional
    public void deleteComment(Member loginMember, Long commentId) {
        ProductComment comment = productCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().getId().equals(loginMember.getId())) {
            throw new IllegalArgumentException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        productCommentRepository.delete(comment);
    }
}