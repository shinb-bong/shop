package com.shop.entity.item.dto;

import com.shop.entity.item.domain.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * 상품 조회 Dto
 * 상품 등록일
 * 상품 판매 상태
 * 상품명 or 상품 등록자 아이디
 */
@Getter @Setter
public class ItemSearchDto {
    private String searchDateType;
    private ItemSellStatus searchSellStatus;
    private String searchBy;
    // 찾아야하는 물품명
    private String searchQuery = "";
}
