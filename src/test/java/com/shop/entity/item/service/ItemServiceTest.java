package com.shop.entity.item.service;

import com.shop.entity.item.domain.*;
import com.shop.entity.item.domain.ItemImgRepository;
import com.shop.entity.item.domain.ItemRepository;
import com.shop.entity.item.dto.ItemFormDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    ItemImgRepository itemImgRepository;

    /**
     * 임의로 생성한 객체를 통해 상품 저장
     */
    @Test
    @DisplayName("상품 등록 테스트")
    @WithMockUser(username = "admin",roles = "admin")
    void saveItem() throws Exception{

        ItemFormDto itemFormDto = new ItemFormDto();
        itemFormDto.setItemNm("테스트상품");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setItemDetail("테스트 상품입니다.");
        itemFormDto.setPrice(1000);
        itemFormDto.setStock(100);

        List<MultipartFile> multipartFileList = createMultipartFiles();
        Long itemId = itemService.saveItem(itemFormDto, multipartFileList);
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(itemFormDto.getItemNm(),item.getItemNm());
        assertEquals(itemFormDto.getItemSellStatus(),item.getItemSellStatus());

        assertEquals(multipartFileList.get(0).getOriginalFilename(),itemImgList.get(0).getOriImgName());
    }


    /**
     * MockMultipartFile 객체를 사용하여 가상의 이미지 파일 5개 생성
     */
    List<MultipartFile> createMultipartFiles(){
        List<MultipartFile> multipartFileList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            String path = "/Users/coodori/Desktop/Coding/spring_study/img";
            String imageName = "image" + i + ".jpg";
            MockMultipartFile multipartFile = new MockMultipartFile(path, imageName,
                    "image/jpg", new byte[]{1,2,3,4});
            multipartFileList.add(multipartFile);
        }
        return multipartFileList;
    }

}