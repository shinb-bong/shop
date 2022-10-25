package com.shop.entity.item.dto;

import com.shop.entity.item.domain.Item;
import com.shop.entity.item.domain.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ItemFormDto {

    private Long id;

    @NotBlank(message = "상품명은 필수입니다.")
    private String itemNm;

    @NotNull(message = "가격은 필수 입력 값입니다.")
    private Integer price;

    @NotBlank(message = "상품 상세는 필수 입력값 입니다.")
    private String itemDetail;

    @NotNull(message = "재고는 필수 입력 값입니다.")
    private Integer stock;

    private ItemSellStatus itemSellStatus;

    // 이미 저장된 정보를 돌려보낼때 필요함
    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();

    // 수정할때 사용
    private List<Long> itemImgIds = new ArrayList<>();

    private static ModelMapper modelMapper = new ModelMapper();


    // DTO -> Entity
    public Item createItem(){
        return modelMapper.map(this, Item.class);
    }

    // Entity -> DTO
    public static ItemFormDto of(Item item){
        return modelMapper.map(item, ItemFormDto.class);
    }
}
