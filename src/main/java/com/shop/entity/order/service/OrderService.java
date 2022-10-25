package com.shop.entity.order.service;

import com.shop.entity.item.domain.Item;
import com.shop.entity.item.domain.ItemImg;
import com.shop.entity.item.domain.ItemImgRepository;
import com.shop.entity.item.domain.ItemRepository;
import com.shop.entity.member.domain.Member;
import com.shop.entity.member.domain.MemberRepository;
import com.shop.entity.order.domain.Order;
import com.shop.entity.order.domain.OrderItem;
import com.shop.entity.order.domain.OrderRepository;
import com.shop.entity.order.dto.OrderDto;
import com.shop.entity.order.dto.OrderHistDto;
import com.shop.entity.order.dto.OrderItemDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository; // 상품을 불러와서 재고변경
    private final MemberRepository memberRepository; // 멤버 조회
    private final OrderRepository orderRepository; // 주문 객체 저장

    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String email){
        Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByEmail(email);

        // orderItemList는 DB관련이 아니니 따로 빼서 설정하고 파라미터화
        List<OrderItem> orderItemList = new ArrayList<>();

        // Order.createOrderItem => static 메소드
        OrderItem orderItem = OrderItem.createdOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        // Order.createOrder -> static 메소드
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);

        return order.getId();
    }

    /**
     * 주문 목록 조회하는 메소드
     */
    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable){
        List<Order> orders = orderRepository.findOrders(email, pageable);
        Long totalCount = orderRepository.countOrder(email);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository
                        .findByItemIdAndRepimgYn(orderItem.getItem().getId(), "Y");
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }
            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos,pageable,totalCount);
    }

    /**
     * 상품을 주문한 유저와 주문 취소를 요청한 유저가 동일한지 검증(조회)
     */
    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email){

        // 주문 취소 요청 유저
        Member curMember = memberRepository.findByEmail(email);

        // 상품을 주문한 유저
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMEmber = order.getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMEmber.getEmail())){
            return false;
        }
        return true;
    }

    /**
     * 변경 감지 취소
     */
    public void cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        order.cancelOrder();
    }

    /**
     * 장바구니 페이지에서 전달 받은 구매상품들로 주문을 생성
     */
    public Long orders(List<OrderDto> orderDtoList, String email){

        // 로그인한 유저 조회
        Member member = memberRepository.findByEmail(email);

        // orderDto 객체를 통해 item객체와 count 를 얻어낸뒤 OrderItem 객체 생성
        List<OrderItem> orderItemList = new ArrayList<>();
        for (OrderDto orderDto : orderDtoList) {
            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);
            OrderItem orderItem = OrderItem.createdOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }
        // Order.createOrder -> static 메소드
        Order order = Order.createOrder(member, orderItemList);
        orderRepository.save(order);
        return order.getId();
    }
}
