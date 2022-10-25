package com.shop.entity.order.domain;

import com.shop.entity.item.domain.Item;
import com.shop.entity.item.domain.ItemRepository;
import com.shop.entity.item.domain.ItemSellStatus;
import com.shop.entity.member.domain.Member;
import com.shop.entity.member.domain.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
class OrderTest {

    @Autowired
    OrderRepository orderRepository;
    @Autowired ItemRepository itemRepository;
    @Autowired EntityManager em;

    @Autowired MemberRepository memberRepository;
    @Autowired OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("지연로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();
        OrderItem orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(EntityNotFoundException::new);
        log.info("OrderItem class = {}",orderItem.getOrder().getClass());
        log.info("=======");
        orderItem.getOrder().getOrderDate();
    }


    @Test
    @DisplayName("즉시 로딩 테스트")
    public void eagerLoadingTest(){
        Order order = this.createOrder();
        Long orderItem_id = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();
        OrderItem orderItem = orderItemRepository.findById(orderItem_id)
                .orElseThrow(EntityNotFoundException::new);
    }

    // CASCADE.REMOVE: 부모 Entity가 삭제 될 때 같이 삭제 되는것
    // 고아객체 제거 - 부모 Entity와의 연관관계가 끊어질 때 삭제 되는것
    @Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        Long orderItem_id = order.getOrderItems().get(0).getId();
        order.getOrderItems().remove(0); // 객체지향 중심의 DB
        em.flush(); // 실제 DB 반영

        assertEquals(Optional.empty(), orderRepository.findById(orderItem_id));

    }


    public Order createOrder(){
        Order order = new Order();
        for (int i = 0; i < 3; i++) {
            Item item = createItem();
            itemRepository.save(item);

            //  3. OrderItem 생성 및 초기화
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order); // 외래키 값 지정

            // 4. Order 에도 OrderItem 추가
            order.getOrderItems().add(orderItem);
        }
        Member member = new Member();
        memberRepository.save(member);
        order.setMember(member);
        orderRepository.save(order);
        return order;
    }

    @Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest(){
        //1. Order 생성 초기화 x
        Order order = new Order();

        for (int i = 0; i <3; i++) {

            // 2. Item 생성 및 저장 초기화
            Item item = this.createItem();
            itemRepository.save(item);

            //  3. OrderItem 생성 및 초기화
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order); // 외래키 값 지정

            // 4. Order 에도 OrderItem 추가
            order.getOrderItems().add(orderItem);
        }

        // 5. Order 저장
        orderRepository.saveAndFlush(order);
        em.clear();

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);

        assertEquals(3, savedOrder.getOrderItems().size());

    }

    public Item createItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세 설명입니다. 잘봐주세요");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

}