package com.CHY.shoppingGo.page.service.impl;

import com.CHY.shoppingGo.page.service.ItemPageService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author CHY
 * @create 2018-12-07 20:57
 */
public class RemoveHtmlListener implements MessageListener {
    @Autowired
    ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {
        try {
            itemPageService.removeHtml(JSON.parseArray(((TextMessage) message).getText(),Long.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}