package com.shop.entity.cart.service;

import com.shop.entity.cart.domain.Cart;
import com.shop.entity.cart.domain.CartItem;
import com.shop.entity.cart.domain.CartItemRepository;
import com.shop.entity.cart.domain.CartRepository;
import com.shop.entity.cart.dto.CartDetailDto;
import com.shop.entity.cart.dto.CartItemDto;
import com.shop.entity.cart.dto.CartOrderDto;
import com.shop.entity.item.domain.Item;
import com.shop.entity.item.domain.ItemRepository;
import com.shop.entity.member.domain.Member;
import com.shop.entity.member.domain.MemberRepository;
import com.shop.entity.order.dto.OrderDto;
import com.shop.entity.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email){
        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());

        // 장바구니가 존재하지 않는다면 생성
        if (cart == null){
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        CartItem cartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        // 해당 상품이 장바구니에 존재하지 않는다면 생성 후 추가
        if(cartItem == null){
            cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);
        // 장바구니에 이미 존재한다면
        }else{
            cartItem.addCount(cartItem.getCount());
        }
        return cartItem.getId();
    }

    /**
     * 로그인한 유저의 장바구니안에 존재하는 상품들을 조회
     */
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){
        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());
        // 아직 장바구니가 없는 것이다. 그러므로 그냥 돌려보낸다.
        if(cart == null){
            return cartDetailDtoList;
        }

        // 장바구니 id를 가진 장바구니 아이템을 Dto로 반환해준다.
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        return cartDetailDtoList;
    }

    /**
     * 현재 로그인한 유저와 해당 장바구니 상품의 저장한 유저가 같은지 검증
     */
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        // 현재 로그인한 유저
        Member curMember = memberRepository.findByEmail(email);

        // 수량 변경 요청이 들어온 장바구니 상품의 유저
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(),savedMember.getEmail())){
            return false;
        }
        return true;
    }

    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }

    public void deleteCarItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();

        // CartOrderDto 객체를 이용하여 cartItem 객체를 조회
        // cartItem 객체에서 itemId 와 count 를 이용해 OrderDto 전달
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }
        Long orderId = orderService.orders(orderDtoList, email);

        // 주문한 장바구니 비우기
        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }
        return orderId;
    }

}
