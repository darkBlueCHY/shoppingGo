package com.CHY.shoppingGo.page.service;

import java.io.IOException;
import java.util.List;

/**
 * @author CHY
 * @create 2018-12-03 23:31
 */
public interface ItemPageService {


    /**
     * @Desc 根据传递的spu商品，生成对应的详情页信息
     * @param goodsIds
     */
    public void generateHtml(List<Long> goodsIds) throws Exception;

    public void removeHtml(List<Long> goodsIds) throws Exception;
}
