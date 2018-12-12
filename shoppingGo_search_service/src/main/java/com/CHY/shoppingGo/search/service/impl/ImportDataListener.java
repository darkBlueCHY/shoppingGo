package com.CHY.shoppingGo.search.service.impl;

import com.CHY.shoppingGo.po.TbItem;
import com.CHY.shoppingGo.search.service.ItemSearchService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @author CHY
 * @create 2018-12-07 17:50
 */
public class ImportDataListener implements MessageListener {
    @Autowired
    ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        try {
            itemSearchService.importData(JSONObject.parseArray(((TextMessage) message).getText(), TbItem.class));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
