package com.CHY.shoppingGo.manager.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("login")
public class LoginController {

    @RequestMapping("getLoginName")
    public Map<String, String> getLoginName(){
        Map<String, String> result = new HashMap<>();
        result.put("loginName",SecurityContextHolder.getContext().getAuthentication().getName());
        return result;
    }
}
