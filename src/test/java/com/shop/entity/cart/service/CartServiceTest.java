package com.shop.entity.cart.service;

import com.shop.entity.cart.domain.CartItem;
import com.shop.entity.cart.domain.CartItemRepository;
import com.shop.entity.cart.dto.CartItemDto;
import com.shop.entity.item.domain.Item;
import com.shop.entity.item.domain.ItemRepository;
import com.shop.entity.item.domain.ItemSellStatus;
import com.shop.entity.member.domain.Member;
import com.shop.entity.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CartServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CartService cartService;

    @Autowired
    CartItemRepository cartItemRepository;

    @Test
    @DisplayName("장바구니 담기 테스트")
    public void addCart(){
        Member member = savedMember();
        Item item = saveItem();

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCount(5);
        cartItemDto.setItemId(item.getId());

        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(item.getId(), cartItem.getItem().getId());
        assertEquals(cartItemDto.getCount(),cartItem.getCount());

    }



    public Item saveItem(){
        Item item = new Item();
        item.setItemNm("테스트상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);

        return itemRepository.save(item);
    }

    public Member savedMember(){
        Member member = new Member();
        member.setEmail("test@naver11.com");
        return memberRepository.save(member);
    }

}