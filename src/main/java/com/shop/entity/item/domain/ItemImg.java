package com.shop.entity.item.domain;

import com.shop.common.auditing.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class ItemImg extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    private String imgName; // 이미지 파일명

    private String oriImgName; // 원본 이미지 파일명
    private String imgUrl; // 이미지 조회 경로
    private String repimgYn; // 대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    //== 편의 메소드
    public void updateItemImg(String oriImgName,String imgName,String imgUrl){
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }


}
