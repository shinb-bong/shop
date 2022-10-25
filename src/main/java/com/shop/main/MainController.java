package com.shop.main;

import com.shop.entity.item.dto.ItemSearchDto;
import com.shop.entity.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final ItemService itemService;

    @GetMapping("/")
    public String main(ItemSearchDto itemSearchDto,
                       Optional<Integer> page,
                       Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 6);
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        model.addAttribute("items",items);
        model.addAttribute("itemSearchDto",itemSearchDto);
        // 하단에 보여줄 페이지 번호의 최대 개수 설정
        model.addAttribute("maxPage",5);
        return "main";
    }
}
