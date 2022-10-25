package com.shop.entity.item.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tymeleaf")
public class ThymeleafExController {

    @GetMapping("ex07")
    public String thymeleafEx(){
        return "thymeleafEx/thymeleafEx07";
    }
}
