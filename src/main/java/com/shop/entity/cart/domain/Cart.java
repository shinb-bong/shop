package com.shop.entity.cart.domain;

import com.shop.common.auditing.BaseEntity;
import com.shop.entity.member.domain.Member;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter @Setter
@ToString
public class Cart extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "cart_id")
    private Long id;


    // 객체 관점에서는 여기에 외래키에 있는게 좋음
    // DBA 관점에서는 Member일듯...
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    //== 생성 편의 메소드==
    /**
     * 회원 한명당 1개의 장바구니를 가지므로 처음 장바구니에 상품을 담을때
     * 해당 회원의 장바구니를 생성해야한다.
     */
    public static Cart createCart(Member member){
        Cart cart = new Cart();
        cart.member = member;
        return cart;
    }

}
