package com.shop.entity.cart.controller;

import com.shop.entity.cart.dto.CartDetailDto;
import com.shop.entity.cart.dto.CartItemDto;
import com.shop.entity.cart.dto.CartOrderDto;
import com.shop.entity.cart.service.CartService;
import com.shop.entity.member.domain.Member;
import com.shop.entity.member.service.customPricipal.AuthMember;
import com.shop.entity.member.service.customPricipal.UserMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CartController {

    private final CartService cartService;

    @PostMapping(value = "/cart")
    @ResponseBody
    public ResponseEntity cart(@RequestBody @Valid CartItemDto cartItemDto, BindingResult bindingResult,  @AuthMember Member member){
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        Long cartItemId;

        try {
            cartItemId = cartService.addCart(cartItemDto, member.getName());
        } catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(cartItemId,HttpStatus.OK);
    }

    @GetMapping("/cart")
    public String orderHist(Principal principal, Model model){
        List<CartDetailDto> cartDetailDtoList = cartService.getCartList(principal.getName());

        model.addAttribute("cartItems",cartDetailDtoList);
        return "cart/cartList";
    }

    /**
     * 장바구니 상품 갯수 수정
     * 0으로 지정할시 에러메세지
     */
    @PatchMapping("/cartItem/{cartItemId}")
    @ResponseBody
    public ResponseEntity updateCartItem(@PathVariable("cartItemId") Long cartItemId,
                                         int count, @AuthenticationPrincipal UserMember userMember){
        if (count < 0){
            return new ResponseEntity<String>("최소 1개 이상 담아주세요.", HttpStatus.BAD_REQUEST);
        } else if (!cartService.validateCartItem(cartItemId, userMember.getMember().getName())){
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        cartService.updateCartItemCount(cartItemId,count);
        return new ResponseEntity<Long>(cartItemId,HttpStatus.OK);
    }

    @DeleteMapping("/cartItem/{cartItemId}")
    @ResponseBody
    public ResponseEntity deleteCartItem(@PathVariable("cartItemId") Long cartItemId,
                                         Principal principal){
        if (!cartService.validateCartItem(cartItemId, principal.getName())) {
            return new ResponseEntity<String>("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        cartService.deleteCarItem(cartItemId);
        return new ResponseEntity<Long>(cartItemId,HttpStatus.OK);
    }

    @PostMapping("/cart/orders")
    @ResponseBody
    public ResponseEntity orderCartItem(@RequestBody CartOrderDto cartOrderDto, Principal principal){

        List<CartOrderDto> cartOrderDtoList = cartOrderDto.getCartOrderDtoList();
        if (cartOrderDtoList == null || cartOrderDtoList.size() == 0 ){
            return new ResponseEntity<String>("주문할 상품을 선택해주세요.", HttpStatus.BAD_REQUEST);
        }

        // 장바구니 주문 상품들을 각각 검증

        for (CartOrderDto cartOrder : cartOrderDtoList) {
            if(!cartService.validateCartItem(cartOrder.getCartItemId(), principal.getName())){
                return new ResponseEntity<String>("주문 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        }
        Long orderId = cartService.orderCartItem(cartOrderDtoList, principal.getName());
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }
}
