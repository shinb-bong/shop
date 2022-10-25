package com.shop.entity.order.domain;

import com.shop.common.auditing.BaseEntity;
import com.shop.entity.member.domain.Member;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate; // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus; // 주문 상태

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();
    
    // Order 객체 생성 편의메소드

    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);

        for (OrderItem orderItem : orderItemList) {
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    // === 연관관계 편의 메소드
    private void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem); // 양방향이기때문에 주문 객체에 상품 객체 연결
        orderItem.setOrder(this); // 연관관계 주인이며 MANY 쪽이기 때문에 주문 객체 연결
    }

    public int getTotalPrice(){
        int totalPrice = 0;

        // 각 상품마다 TotalPrice를 구하고 모두 더함
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }


    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;

        for (OrderItem orderItem : orderItems) {
            orderItem.cancel();
        }
    }
}
