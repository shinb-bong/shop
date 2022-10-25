package com.shop.main;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

/**
 * 메인 화면 DTO
 */
@Getter @Setter
public class MainItemDto {

    private Long id;
    private String itemNm;
    private String itemDetail;
    private String imgUrl;
    private Integer price;

    @QueryProjection //Entity 객체를 DTO 객체로 바로 반환하도록 하는 어노테이션 - Compile 필요(QDto)
    public MainItemDto(Long id, String itemNm, String itemDetail, String imgUrl, Integer price) {
        this.id = id;
        this.itemNm = itemNm;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}
