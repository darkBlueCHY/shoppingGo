package com.CHY.shoppingGo.shop.controller;

import com.CHY.shoppingGo.sellergoods.service.SellerService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("login")
public class LoginController {
    @Reference
    private SellerService sellerService;

    @RequestMapping("getLoginName")
    public Map<String, String> getLoginName(){
        Map result = new HashMap();
        result.put("loginName",sellerService.findOne(SecurityContextHolder.getContext().getAuthentication().getName()).getNickName());
        return result;
    }
}
