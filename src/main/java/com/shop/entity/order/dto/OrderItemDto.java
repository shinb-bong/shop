package com.shop.entity.order.dto;

import com.shop.entity.order.domain.OrderItem;
import lombok.Getter;
import lombok.Setter;

/**
 * 주문 상품 정보 => 상품, 수량 , 상품 이미지, 해당 주문 금액
 */
@Getter @Setter
public class OrderItemDto {

    private String itemNm;
    private int count;
    private int orderPrice;
    private String imgUrl;

    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }
}
