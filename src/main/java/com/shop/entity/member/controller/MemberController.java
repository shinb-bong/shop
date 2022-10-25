package com.shop.entity.member.controller;

import com.shop.entity.member.domain.Member;
import com.shop.entity.member.dto.MemberFormDto;
import com.shop.entity.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@RequestMapping("/member")
@Controller
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/new")
    public String memberForm(MemberFormDto memberFormDto,Model model){
        model.addAttribute("memberFormDto", memberFormDto);
        return "member/memberForm";
    }

    @PostMapping("/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult,
                            Model model){

        // DTO 검증을 통과 못하면
        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }
        try { // 중복성검사나 통과 못하면
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        }catch (Exception e){
            model.addAttribute("errorMessage",e.getMessage()); //아예 알림으로 처리
            return "member/memberForm";
        }
        return "redirect:/";
    }

    @GetMapping("/login")
    public String memberLogin(){
        return "member/loginForm";
    }

    @GetMapping("/login/fail")
    public String memberLonginFail(Model model){
        model.addAttribute("loginErrorMsg","아이디 또는 비밀번호를 확인해주세요.");
        return "member/loginForm";
    }
}
