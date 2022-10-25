package com.shop.entity.order.service;

import com.shop.entity.item.domain.Item;
import com.shop.entity.item.domain.ItemRepository;
import com.shop.entity.item.domain.ItemSellStatus;
import com.shop.entity.member.domain.Member;
import com.shop.entity.member.domain.MemberRepository;
import com.shop.entity.order.domain.Order;
import com.shop.entity.order.domain.OrderItem;
import com.shop.entity.order.domain.OrderRepository;
import com.shop.entity.order.domain.OrderStatus;
import com.shop.entity.order.dto.OrderDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("주문 테스트")
    public void order(){
        Item item = saveItem();
        Member member = savedMember();

        // 상품 상세페이지 넘어오는 값 세팅
        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());

        // 주문 객체 저장
        Long orderId = orderService.order(orderDto, member.getEmail());

        em.flush();
        em.clear();

        // 저장된 주문 객체 조회
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        // 1.DB에 저장된 주문 객체에서 주문 상품 추출(1개)
        List<OrderItem> orderItems = order.getOrderItems();

        // 2. 위에서 만든 주문 상품 총 가격(1개)
        int totalPrice = orderDto.getCount() * item.getPrice();

        // 1 ==2
        assertEquals(totalPrice, order.getTotalPrice());
    }

    @Test
    @DisplayName("주문 취소 테스트")
    public void cancelOrder(){
        Item item = saveItem();
        Member member = savedMember();

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());

        // 주문 객체 저장
        Long orderId = orderService.order(orderDto, member.getEmail());

        // 주문된 객체를 조회한 뒤에 주문 취소
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        orderService.cancelOrder(orderId);

        // 주문 상태가 CANCEL 수량 100 복구
        assertEquals(OrderStatus.CANCEL, order.getOrderStatus());
        assertEquals(100,item.getStockNumber());
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