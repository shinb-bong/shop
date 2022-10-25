package com.shop.entity.item.domain;

import com.shop.entity.item.domain.ItemImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemImgRepository extends JpaRepository<ItemImg,Long> {
    List<ItemImg> findByItemIdOrderByIdAsc(Long itemId);


    // 구매내역 페이지에서 대표 이미지 조회
    ItemImg findByItemIdAndRepimgYn(Long itemId,String repimgYn);
}
