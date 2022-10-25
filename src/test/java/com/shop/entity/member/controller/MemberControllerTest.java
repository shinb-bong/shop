package com.shop.entity.member.controller;

import com.shop.entity.member.domain.Member;
import com.shop.entity.member.dto.MemberFormDto;
import com.shop.entity.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class MemberControllerTest {

    @Autowired
    MemberService memberService;

    //@AutoConfigureMockMvc 를 통해 서블릿 컨테이너를 목킹하여
    // 가상의 클라이언트처럼 Request 요청을 보내는 역할
    @Autowired
    private MockMvc mockMvc;

    // SecurityConfig에서 설정한 암호화 객체
    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("로그인 성공 테스트")
    public void loginSuccessTest() throws Exception{
        String email = "test@email.com";
        String password = "123456789";
        this.createMember(email,password);

        mockMvc.perform(formLogin().loginProcessingUrl("/member/login")
                .userParameter("email").user(email).password(password))
                .andExpect(SecurityMockMvcResultMatchers.authenticated());
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    public void loginFailTest() throws Exception{
        String email = "test@email.com";
        String password = "123456789";
        this.createMember(email,password);

        mockMvc.perform(formLogin().loginProcessingUrl("/member/login")
                        .userParameter("email").user(email).password("1234"))
                .andExpect(SecurityMockMvcResultMatchers.unauthenticated());
    }

    public Member createMember(String email, String password){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail(email);
        memberFormDto.setName("신봉규");
        memberFormDto.setAddress("서대문구 창천동");
        memberFormDto.setPassword(password);
        Member member = Member.createMember(memberFormDto, passwordEncoder);
        return memberService.saveMember(member);
    }

}