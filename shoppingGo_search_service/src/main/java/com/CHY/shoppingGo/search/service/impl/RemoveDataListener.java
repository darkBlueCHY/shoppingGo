package com.CHY.shoppingGo.search.service.impl;

import com.CHY.shoppingGo.search.service.ItemSearchService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author CHY
 * @create 2018-12-07 18:09
 */
public class RemoveDataListener implements MessageListener {
    @Autowired
    ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        try {
            itemSearchService.removeData(JSONObject.parseArray(((TextMessage) message).getText(), String.class));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
