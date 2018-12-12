package com.CHY.shoppingGo.search.controller;

import com.CHY.shoppingGo.search.service.ItemSearchService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author CHY
 * @create 2018-12-01 8:55
 */

@RestController
@RequestMapping("itemSearch")
public class ItemSearchController {
    @Reference
    ItemSearchService itemSearchService;

    @RequestMapping("search")
    public Map<String,Object> search(@RequestBody Map searchEntity){
        return itemSearchService.search(searchEntity);
    }
}
