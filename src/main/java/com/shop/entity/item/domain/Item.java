package com.shop.entity.item.domain;

import com.shop.common.auditing.BaseEntity;
import com.shop.common.exception.OutOfStockException;
import com.shop.entity.item.dto.ItemFormDto;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemNm;

    @Column(name ="price", nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber;

    @Lob
    @Column(nullable = false)
    private String itemDetail;

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;

    //== 상품 업데이트 로직
    // 코드 재활용 가능 , 변경 포인트 한곳
    public void updateItem(ItemFormDto itemFormDto){
        this.itemNm = itemFormDto.getItemNm();
        this.stockNumber = itemFormDto.getStock();
        this.stockNumber = itemFormDto.getStock();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
    }

    //== 연관 메소드
    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if (restStock < 0){
            throw new OutOfStockException("상품의 재고가 부족합니다. (현재 재고수량: "+ this.stockNumber + ")");
        }
        this.stockNumber = restStock;
    }

    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
    }
}
