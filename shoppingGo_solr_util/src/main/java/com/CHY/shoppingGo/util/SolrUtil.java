package com.CHY.shoppingGo.util;

import com.CHY.shoppingGo.mapper.TbItemMapper;
import com.CHY.shoppingGo.po.Item;
import com.CHY.shoppingGo.po.TbItem;
import com.CHY.shoppingGo.po.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author CHY
 * @create 2018-11-30-22:48
 */
@Component
public class SolrUtil {
    public static final String COLLECTION="shoppingGo";

    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private SolrTemplate solrTemplate;

    public void importData(){
        TbItemExample example = new TbItemExample();
        example.or().andStatusEqualTo("1");
        Optional<List<TbItem>> tbItemsOptional = Optional.ofNullable(itemMapper.selectByExample(example));
        List<Item> itemList = tbItemsOptional.orElse(new ArrayList<>())
                .stream().map( tbItem -> new Item().transfrom(tbItem)).collect(Collectors.toList());

        solrTemplate.saveBeans(COLLECTION,itemList);
        solrTemplate.commit(COLLECTION);
    }

    public void clear(){
        SimpleQuery query = new SimpleQuery("*:*");
        solrTemplate.delete(COLLECTION,query);
        solrTemplate.commit(COLLECTION);
    }

    public void query(){
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        query.addCriteria(new Criteria("item_keywords").is("华为"));

        HighlightOptions options = new HighlightOptions();
        options.addField("item_title");
        options.setSimplePrefix("<em style='color:red'>");
        options.setSimplePostfix("</em>");
        query.setHighlightOptions(options);
        HighlightPage<Item> page = solrTemplate.queryForHighlightPage(COLLECTION,query, Item.class);

        System.out.println(page.getClass().getName());
    }

    public Item findOne(Long id){
        return solrTemplate.getById(COLLECTION, id, Item.class).get();
    }

    public static void main(String[] args) {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        SolrUtil solrUtil = (SolrUtil) ac.getBean("solrUtil");
        solrUtil.clear();
        ac.close();
    }
}
