package com.CHY.shoppingGo.sellergoods.service;

import com.CHY.shoppingGo.entity.PageResult;
import com.CHY.shoppingGo.po.TbBrand;
import java.util.List;
import java.util.Map;

public interface BrandService {
    List<Map> select2BrandList() throws Exception;

    List<TbBrand> findAll() throws Exception;

    PageResult search(int pageNum, int pageSize, TbBrand brand) throws Exception;

    TbBrand findOne(long id) throws Exception;

    void add(TbBrand brand) throws Exception;

    void change(TbBrand brand) throws Exception;

    void delete(List<Long> ids) throws Exception;
}
