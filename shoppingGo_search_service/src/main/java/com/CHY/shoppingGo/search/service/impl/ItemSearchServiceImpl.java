package com.CHY.shoppingGo.search.service.impl;
import com.CHY.shoppingGo.po.Item;
import com.CHY.shoppingGo.po.TbItem;
import com.CHY.shoppingGo.search.service.ItemSearchService;
import com.CHY.utils.EncoderUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

/**
 * @author CHY
 * @create 2018-11-30 23:37
 */

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
    public static final String COLLECTION="shoppingGo";
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchEntity) {
        Map<String,Object> result = new HashMap<>();
        if (searchEntity.get("keywords") == null || searchEntity.get("keywords").equals("")) {
            return result;
        }
        searchEntity.put("keywords",((String) searchEntity.get("keywords")).replaceAll(" ", ""));
        searchHighLightItemList(searchEntity, result);
        extractCategory(searchEntity, result);

        String categoryName = null;
        if (searchEntity.get("category") != null && !searchEntity.get("category").equals("")) {
            categoryName = (String) searchEntity.get("category");
        } else {
            if (((List<String>) result.get("categoryList")).size() > 0){
                categoryName = ((List<String>)result.get("categoryList")).get(0);
            }
        }
        if (categoryName != null) getInfoFormRedis(categoryName, result);


        return result;
    }


    private void searchHighLightItemList(Map searchEntity,Map<String,Object> result){
        //设置高亮"<em style='color:red'>"  "</em>"
        Criteria criteria = new Criteria("item_keywords").is(searchEntity.get("keywords"));
        HighlightQuery query = new SimpleHighlightQuery(criteria);
        HighlightOptions options = new HighlightOptions();
        options.setSimplePrefix("<em style='color:red'>");
        options.setSimplePostfix("</em>");
        options.addField("item_title");

        query.setHighlightOptions(options);
        //设置分页
        if (searchEntity.get("pageNumber") != null) {
            query.setOffset((((int)searchEntity.get("pageNumber")) - 1) * 10L);
        } else {
            query.setOffset(0L);
        }
        query.setRows(10);
        //添加过滤器 分类+品牌+spec
        addFieldFilter(searchEntity, query, "category");
        addFieldFilter(searchEntity, query, "brand");
        if (searchEntity.get("price") != null && ((String) searchEntity.get("price")).length() > 0 ) {
            String[] prices = ((String) searchEntity.get("price")).split("-");
            FilterQuery priceFilter = new SimpleFilterQuery();
            priceFilter.addCriteria(new Criteria("item_price").greaterThan(Double.parseDouble(prices[0])));
            if (!prices[1].equals("*")) {
                priceFilter.addCriteria(new Criteria("item_price").lessThan(Double.parseDouble(prices[1])));
            };
            query.addFilterQuery(priceFilter);
        }
        if (searchEntity.get("spec") != null && ((JSONObject) searchEntity.get("spec")).size() > 0 ) {
            for (String specName : ((JSONObject) searchEntity.get("spec")).keySet()) {
                FilterQuery specFilter = new SimpleFilterQuery();
                specFilter.addCriteria(new Criteria("item_spec_"+EncoderUtils.MD5Bas64EncodeNoTail(specName)).is(((JSONObject) searchEntity.get("spec")).get(specName)));
                query.addFilterQuery(specFilter);
            }
        }
        //添加Order orderBy SearchEntity.orderBy  排序正反 SearchEntity.orderWay [asc,desc]
        if (searchEntity.get("orderBy") != null && !"".equals(searchEntity.get("orderBy"))) {
            if (searchEntity.get("orderBy").equals("price") || searchEntity.get("orderBy").equals("updatetime")) {
                query.addSort(createSort(searchEntity));
            }
        }

        HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(COLLECTION, query, Item.class);
        List<HighlightEntry<Item>> highlightEntries = highlightPage.getHighlighted();
        for (HighlightEntry<Item> highlightEntry : highlightEntries) {
            for (HighlightEntry.Highlight highlight : highlightEntry.getHighlights()) {
                if (highlight.getField().getName().equals("item_title")) {
                    highlightEntry.getEntity().setTitle(highlight.getSnipplets().get(0));
                }
            }
        }
        result.put("total",highlightPage.getTotalElements());
        result.put("itemList",highlightPage.getContent());
        result.put("totalPages",highlightPage.getTotalPages());
    }
    private Sort createSort(Map searchEntity){
        Sort sort = Sort.by("item_" + searchEntity.get("orderBy"));
        if (((String)searchEntity.get("orderWay")).toLowerCase(Locale.ENGLISH).equals("desc")) {
            sort = sort.descending();
        }
        return sort;
    }

    private void addFieldFilter(Map searchEntity, HighlightQuery query, String fieldName) {
        if (searchEntity.get(fieldName) != null && !"".equals(searchEntity.get(fieldName))) {
            FilterQuery catFilter = new SimpleFilterQuery();
            catFilter.addCriteria(new Criteria("item_"+fieldName).is(searchEntity.get(fieldName)));
            query.addFilterQuery(catFilter);
        }
    }


    private void extractCategory(Map searchEntity,Map<String,Object> result){
        Query query = new SimpleQuery(new Criteria("item_keywords").is(searchEntity.get("keywords")));
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        groupOptions.setOffset(0);
        groupOptions.setLimit(20);
        query.setGroupOptions(groupOptions);
        GroupPage<Item> page = solrTemplate.queryForGroupPage(COLLECTION, query, Item.class);

        result.put("categoryList", getList(page, "item_category"));
    }

    private List<String> getList(GroupPage<Item> page, String fieldName) {
        List<String> resultList = new ArrayList<>();
        GroupResult<Item> Result = page.getGroupResult(fieldName);
        Page<GroupEntry<Item>> pages = Result.getGroupEntries();
        List<GroupEntry<Item>> entries = pages.getContent();
        for (GroupEntry<Item> entry : entries) {
            resultList.add(entry.getGroupValue());
        }
        return resultList;
    }

    //提取redis中的brand spec信息
    private void getInfoFormRedis(String typeName, Map<String, Object> result) {
        Object typeId = redisTemplate.boundHashOps("categoryList").get(typeName);
        result.put("brandList",redisTemplate.boundHashOps("brandList").get(typeId));
        result.put("specList",redisTemplate.boundHashOps("specList").get(typeId));
    }

    public void importData(List<TbItem> tbItems){
        if (tbItems.size()>0) {
            ArrayList<Item> itemList = new ArrayList(tbItems.size());
            for (int index = 0; index < tbItems.size(); index++) {
                itemList.add(new Item().transfrom(tbItems.get(index)));
            }
            solrTemplate.saveBeans(COLLECTION, itemList);
            solrTemplate.commit(COLLECTION);
        }
    }

    @Override
    public void removeData(List<String> ids) {
        if (ids.size()>0) {
            solrTemplate.deleteByIds(COLLECTION, ids);
            solrTemplate.commit(COLLECTION);
        }
    }
}