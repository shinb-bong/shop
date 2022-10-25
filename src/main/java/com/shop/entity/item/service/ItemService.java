package com.shop.entity.item.service;

import com.shop.entity.item.domain.Item;
import com.shop.entity.item.domain.ItemImg;
import com.shop.entity.item.domain.ItemImgRepository;
import com.shop.entity.item.domain.ItemRepository;
import com.shop.entity.item.dto.ItemFormDto;
import com.shop.entity.item.dto.ItemImgDto;
import com.shop.entity.item.dto.ItemSearchDto;
import com.shop.main.MainItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;

    private final ItemImgRepository itemImgRepository;


    // Item을 저장하기 위해 경로와 ItemFormDto 를 처리
    public Long saveItem(ItemFormDto itemFormDto,
                         List<MultipartFile> itemImgFileList) throws Exception{
        //상품 등록
        Item item = itemFormDto.createItem();
        itemRepository.save(item);

        // 이미지 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            // 추가정보를 가지고 있는 객체
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);
            // 첫번째 사진은 대표 사진으로 설정
            if (i ==0){
                itemImg.setRepimgYn("Y");
            } else{
                itemImg.setRepimgYn("N");
            }
            itemImgService.saveItemImg(itemImg,itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId){
        // id를 기반으로 상품 이미지 엔티티 객체 가져옴
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        // 상품 이미지 Dto 객체를 담을 그릇 생성
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();

        // 상품 이미지 엔티티 객체를 상품 이미지 DTO 객체로 변환
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }
        // 상품 id 기반으로 상품 엔티티 객체 가져옴
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // 상품 엔티티 객체를 DTO객체로 변환
        ItemFormDto itemFormDto = ItemFormDto.of(item);
        itemFormDto.setItemImgDtoList(itemImgDtoList);
        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto,
                           List<MultipartFile> itemImgFileList) throws Exception{

        // 상품 수정(변경 감지)
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);

        // 상품 이미지 수정
        List<Long> itemImgIds = itemFormDto.getItemImgIds();
        for (int i = 0; i < itemImgFileList.size(); i++) {
            itemImgService.updateITemImg(itemImgIds.get(i),itemImgFileList.get(i));
        }
        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable){
        return itemRepository.getAdminItemPage(itemSearchDto,pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto,Pageable pageable){
        return itemRepository.getMainItemPage(itemSearchDto,pageable);
    }
}
