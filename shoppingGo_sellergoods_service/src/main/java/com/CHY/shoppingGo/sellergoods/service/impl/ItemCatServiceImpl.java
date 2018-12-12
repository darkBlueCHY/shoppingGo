package com.CHY.shoppingGo.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.List;
import com.CHY.shoppingGo.mapper.TbTypeTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.CHY.shoppingGo.mapper.TbItemCatMapper;
import com.CHY.shoppingGo.po.TbItemCat;
import com.CHY.shoppingGo.po.TbItemCatExample;
import com.CHY.shoppingGo.po.TbItemCatExample.Criteria;
import com.CHY.shoppingGo.sellergoods.service.ItemCatService;
import com.CHY.shoppingGo.entity.PageResult;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;

	@Override
	public List<TbItemCat> findByParentId(Long parentId){
		TbItemCatExample example = new TbItemCatExample();
		example.or().andParentIdEqualTo(parentId);
		List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(example);
		for (TbItemCat itemCat : tbItemCats) {
			itemCat.setTypeName(typeTemplateMapper.findNameById(itemCat.getTypeId()));
		}
		return tbItemCats;
	}
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbItemCat> page=   (Page<TbItemCat>) itemCatMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(id);
		itemCat.setTypeName(typeTemplateMapper.findNameById(itemCat.getTypeId()));
		return itemCat;
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(List<Long> ids) {
		List<Long> deleteIds = new ArrayList();
		while (! ids.isEmpty()) {
			deleteIds.addAll(ids);
			ids = itemCatMapper.findIdByParentId(ids);
		}
		TbItemCatExample example = new TbItemCatExample();
		example.or().andIdIn(deleteIds);
		itemCatMapper.deleteByExample(example);
	}
	
	
		@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();
		
		if(itemCat!=null){			
						if(itemCat.getName()!=null && itemCat.getName().length()>0){
				criteria.andNameLike("%"+itemCat.getName()+"%");
			}
		}
		
		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
}
