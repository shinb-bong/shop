package com.shop.entity.item.service;

import com.shop.entity.item.domain.ItemImg;
import com.shop.entity.item.domain.ItemImgRepository;
import com.shop.entity.item.image.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;
    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception{
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation,oriImgName,itemImgFile.getBytes());
            imgUrl = "/images/item/" + imgName;
        }

        // 상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName,imgName,imgUrl);
        itemImgRepository.save(itemImg);
    }

    /**
     * 상품 이미지를 수정한경우 기본 이미지 정보를 불러오고 삭제
     * 그리고 새로 들어온 이미지를 업로드
     */
    public void updateITemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{

        // 상품 이미지를 수정했다면 (없으면 추가한거니 상관없음)
        if(!itemImgFile.isEmpty()){
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId)
                    .orElseThrow(EntityNotFoundException::new);

            // 기존 이미지 파일이 존재한다면 삭제
            if(!StringUtils.isEmpty(savedItemImg.getImgName())){
                fileService.deleteFile(itemImgLocation+"/"+savedItemImg.getImgName());
            }

            String oriImgName = itemImgFile.getOriginalFilename();
            // 새로운것 업로드
            String imgName = fileService.uploadFile(itemImgLocation,
                    oriImgName, itemImgFile.getBytes());
            String imgUrl = "/images/item" + imgName;

            // 더티체킹으로 자동 수정
            savedItemImg.updateItemImg(oriImgName,imgName,imgUrl);
        }

    }

}
