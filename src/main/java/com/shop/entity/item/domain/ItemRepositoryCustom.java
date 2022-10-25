package com.shop.entity.item.domain;


import com.shop.entity.item.domain.Item;
import com.shop.entity.item.dto.ItemSearchDto;
import com.shop.main.MainItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Querydsl + Spring Data Jpa
 * 1. 사용자 정의 인터페이스 작성
 * 2. 구현체 작성
 * 3. Spring Data Jpa 리포지토리에서 사용자 정의 인터페이스 상속
 */
public interface ItemRepositoryCustom {
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
