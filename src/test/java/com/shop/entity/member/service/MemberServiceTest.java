package com.shop.entity.member.service;

import com.shop.entity.member.domain.Member;
import com.shop.entity.member.dto.MemberFormDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입 테스트")
    public void savedMemberTest(){
        Member member = createMember();
        Member savedMember = memberService.saveMember(member);

        assertEquals(member.getEmail(),savedMember.getEmail());
        assertEquals(member.getName(),savedMember.getName());
        assertEquals(member.getAddress(),savedMember.getAddress());
    }

    @Test
    @DisplayName("중복 회원가입 테스트")
    void validateDuplicateMember(){
        Member member1 = createMember();
        Member member2 = createMember();
        memberService.saveMember(member1);

        Throwable e =assertThrows(IllegalStateException.class, () ->{
            memberService.saveMember(member2);
        });

        assertEquals("이미 가입된 회원입니다.", e.getMessage());
    }



    // Controller에서 post로올때 MemberFormDto로 받을예정
    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("신봉규");
        memberFormDto.setAddress("서대문구 창천동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto,passwordEncoder);
    }



}