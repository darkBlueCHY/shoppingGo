package com.CHY.shoppingGo.search.service;

import com.CHY.shoppingGo.po.TbItem;

import java.util.List;
import java.util.Map;

/**
 * @author CHY
 * @create 2018-11-30-23:30
 */
public interface ItemSearchService {

    public Map<String,Object> search(Map searchEntity);

    public void importData(List<TbItem> tbItems);

    public void removeData(List<String> ids);
}
