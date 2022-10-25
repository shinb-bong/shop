package com.shop.entity.cart.domain;

import com.shop.entity.cart.dto.CartDetailDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {

    /**
     * 상품이 장바구니에 들어있는지 조회하는 쿼리 메소드
     */
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    /**
     * 장바구니 페이지에 전달할 CartDetailDto리스트를 쿼리 하나로 조회 (Dto 적으로 조회) 실전 2편
     */
    @Query("select new com.shop.entity.cart.dto.CartDetailDto(ci.id,i.itemNm,i.price,ci.count,im.imgUrl)" +
            " from CartItem ci, ItemImg im" +
            " join ci.item i" +
            " where ci.cart.id = :cartId" +
            " and im.item.id = ci.item.id" +
            " and im.repimgYn = 'Y'" +
            " order by ci.regTime desc")
    List<CartDetailDto> findCartDetailDtoList(Long cartId);
}
