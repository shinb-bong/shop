package com.shop.entity.order.controller;

import com.shop.entity.order.dto.OrderDto;
import com.shop.entity.order.dto.OrderHistDto;
import com.shop.entity.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 *  비동기 방식으로 Json 데이터를 주고 받음
 *  입력값에 문제가 있을시 ResponseEntity 객체에 담아서 반환
 *  현재 로그인한 유저 정보를 담고 있는 Principal 객체에서 유저 email 추출
 *  정상적으로 동작시 생성된 주문 객체의 Id 와 상태코드 200 반환
 */
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 스프링 시큐리티를 사용할 경우 CSRF 토큰 값을 자동으로 주고 받지만
     * AJAX 통신 할 경우 직접 코드를 구현해야함.
     */
    @PostMapping("/order")
    @ResponseBody
    public ResponseEntity order(@RequestBody @Valid OrderDto orderDto,
                                BindingResult bindingResult, Principal principal){
        if (bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            // 오류 바디 반환 및 상태 반환
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        String email = principal.getName(); // 해당 부분 공부하기
        Long orderId;

        try {
            orderId = orderService.order(orderDto,email);
        } catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }

    @GetMapping(value = {"/orders","/orders/{page}"})
    public String orderHist(@PathVariable("page") Optional<Integer> page,
                            Principal principal, Model model){
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);

        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders",orderHistDtoList);
        model.addAttribute("page",pageable.getPageNumber());
        model.addAttribute("maxPage",5);

        return "order/orderHist";
    }

    @PostMapping("/order/{orderId}/cancel")
    @ResponseBody
    public ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId,
                                      Principal principal){
        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId,HttpStatus.OK);
    }
}
