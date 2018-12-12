package com.CHY.shoppingGo.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.CHY.shoppingGo.mapper.TbSpecificationMapper;
import com.CHY.shoppingGo.mapper.TbSpecificationOptionMapper;
import com.CHY.shoppingGo.po.*;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.CHY.shoppingGo.mapper.TbTypeTemplateMapper;
import com.CHY.shoppingGo.po.TbTypeTemplateExample.Criteria;
import com.CHY.shoppingGo.sellergoods.service.TypeTemplateService;
import com.CHY.shoppingGo.entity.PageResult;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	@Autowired
	private RedisTemplate redisTemplate;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

	@Override
	public List<Map> select2TypeNameList() {
		return typeTemplateMapper.select2TypeNameList();
	};
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page=   (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
		redisUpdate(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		TbTypeTemplateExample example = new TbTypeTemplateExample();
        List<Long> idList = Arrays.asList(ids);
        example.or().andIdIn(idList);

		redisDeletes(idList,typeTemplateMapper.selectByExample(example));

		typeTemplateMapper.deleteByExample(example);
	}
	
	
		@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate!=null){			
			if(typeTemplate.getName()!=null && typeTemplate.getName().length()>0){
				criteria.andNameLike("%"+typeTemplate.getName()+"%");
			}
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);
		List<TbTypeTemplate> result = page.getResult();
		for (TbTypeTemplate template : result) {
			redisUpdate(template);
		}
		return new PageResult(page.getTotal(), result);
	}

	private void redisDeletes(List<Long> idList, List<TbTypeTemplate> typeTemplates){
        String[] deleteTypeNames = new String[typeTemplates.size()];

        for (int i = deleteTypeNames.length - 1; i >= 0; i--) {
            deleteTypeNames[i] = typeTemplates.get(i).getName();
        }
        redisTemplate.boundHashOps("categoryList").delete(deleteTypeNames);
        redisTemplate.boundHashOps("brandList").delete(idList);
        redisTemplate.boundHashOps("specList").delete(idList);
    }

    private void redisUpdate(TbTypeTemplate typeTemplate){
		ArrayList<Long> typeTemplateId = new ArrayList<>(1);
		typeTemplateId.add(typeTemplate.getId());

		List<Map> specMaps = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
		for (Map specMap : specMaps) {
			TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			example.or().andSpecIdEqualTo(Long.parseLong(specMap.get("id").toString()));
			specMap.put("options",specificationOptionMapper.selectByExample(example));
		}

		redisTemplate.boundHashOps("categoryList").put(typeTemplate.getName(),typeTemplate.getId());
		redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specMaps);
		redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),JSON.parseArray(typeTemplate.getBrandIds(), Map.class));
    }
}
