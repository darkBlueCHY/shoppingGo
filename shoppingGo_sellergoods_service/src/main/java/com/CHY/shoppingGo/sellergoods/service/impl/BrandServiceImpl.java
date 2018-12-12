package com.CHY.shoppingGo.sellergoods.service.impl;

import com.CHY.shoppingGo.entity.PageResult;
import com.CHY.shoppingGo.mapper.TbBrandMapper;
import com.CHY.shoppingGo.po.TbBrand;
import com.CHY.shoppingGo.po.TbBrandExample;
import com.CHY.shoppingGo.sellergoods.service.BrandService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    TbBrandMapper brandMapper;

    @Override
    public List<Map> select2BrandList() throws Exception {
        return brandMapper.select2BrandList();
    };

    @Override
    public List<TbBrand> findAll() throws Exception {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult search(int pageNum, int pageSize, TbBrand brand) throws Exception {
        PageHelper.startPage(pageNum, pageSize);
        TbBrandExample brandExample = null;
        if (brand != null) {
            brandExample = new TbBrandExample();
            TbBrandExample.Criteria criteria = brandExample.or();
            if (brand.getName() != null && !brand.getName().trim().equals(""))
                criteria.andNameLike("%" + brand.getName() + "%");
            if (brand.getFirstChar() != null && !brand.getFirstChar().trim().equals(""))
                criteria.andFirstCharEqualTo(brand.getFirstChar());
        }
        Page<TbBrand> brandList = (Page<TbBrand>) brandMapper.selectByExample(brandExample);
        return new PageResult(brandList.getTotal(),brandList.getResult());
    }

    @Override
    public TbBrand findOne(long id) throws Exception {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(TbBrand brand) throws Exception {
        brandMapper.insert(brand);
    }

    @Override
    public void change(TbBrand brand) throws Exception {
        brandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public void delete(List<Long> ids) throws Exception {
        TbBrandExample brandExample = new TbBrandExample();
        brandExample.or().andIdIn(ids);
        brandMapper.deleteByExample(brandExample);
    }
}
