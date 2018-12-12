package com.CHY.shoppingGo.sellergoods.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.CHY.shoppingGo.mapper.*;
import com.CHY.shoppingGo.po.*;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.CHY.shoppingGo.po.TbGoodsExample.Criteria;
import com.CHY.shoppingGo.sellergoods.service.GoodsService;
import com.CHY.shoppingGo.entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;
	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbSellerMapper sellerMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;

	@Transactional
	public void updateStatus(List<Long> ids,String auditStatus){
		TbGoodsExample example = new TbGoodsExample();
		example.or().andIdIn(ids);
		List<TbGoods> goods = goodsMapper.selectByExample(example);
		for (TbGoods good : goods) {
			good.setAuditStatus(auditStatus);
			goodsMapper.updateByPrimaryKey(good);
		}
	};


	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}


	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbGoods> page=   (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	@Transactional
	public void add(Goods goods) {
		goods.getGoods().setAuditStatus("0");		//等待审批
		goods.getGoods().setIsMarketable("0");	    //初始未上架
		goodsMapper.insert(goods.getGoods());

		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
		goodsDescMapper.insert(goods.getGoodsDesc());
		insertItem(goods);
		goodsMapper.updateByPrimaryKey(goods.getGoods());
	}

	private List<String> insertItem(Goods goods) {
		List<Map> itemImages = JSONObject.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		String imageURL = null;
		if (itemImages.size()>0) {
			imageURL = (String) itemImages.get(0).get("url");
		}

		String brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId()).getName();
		String nickName = sellerMapper.getNickNameById(goods.getGoods().getSellerId());
		String title = goods.getGoods().getGoodsName();

		//此结果由manager-web 转发 search-service删除Solr信息，审核通过由相同路径添加
		List<String> itemSolrId = new ArrayList<>(goods.getItemList().size());
		for (TbItem item : goods.getItemList()) {
			if (item.getId() != null) {
				itemMapper.insert(item);
			} else {
				String spec = new String();
				if (item.getSpec() != null && item.getSpec().trim().length() > 0) {
					Map<String, String> specMap = JSONObject.parseObject(item.getSpec(), Map.class);
					for (String key : specMap.keySet()) {
						spec = spec + " " + specMap.get(key);
					}
				}
				item.setTitle(title + spec);
				item.setImage(imageURL);
				item.setCategoryid(goods.getGoods().getCategory3Id());
				item.setCategory(itemCatMapper.selectByPrimaryKey(item.getCategoryid()).getName());
				item.setGoodsId(goods.getGoods().getId());
				item.setSellerId(goods.getGoods().getSellerId());
				item.setBrand(brand);
				item.setSeller(nickName);
				item.setCreateTime(new Date());
				item.setUpdateTime(new Date());

				itemMapper.insert(item);
				itemSolrId.add(item.getId().toString());
			}
			if (item.getIsDefault().equals("1")) {
				goods.getGoods().setDefaultItemId(item.getId());
			}
		}
		return itemSolrId;
	}
	
	/**
	 * 修改
	 */
	@Override
	public List<String> update(Goods goods){
		goods.getGoods().setAuditStatus("0");
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());


		TbItemExample example = new TbItemExample();
		example.or().andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		List<String> itemSolrIds = insertItem(goods);
		goodsMapper.updateByPrimaryKey(goods.getGoods());

		return itemSolrIds;
	}


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id){
		Goods goods = new Goods();
		goods.setGoods(goodsMapper.selectByPrimaryKey(id));
		goods.setGoodsDesc(goodsDescMapper.selectByPrimaryKey(id));

		TbItemExample example = new TbItemExample();
		example.or().andGoodsIdEqualTo(id);
		goods.setItemList(itemMapper.selectByExample(example));
		return goods;
	}

	/**
	 * 批量删除
	 */
	@Override
	public List<String> delete(List<Long> ids) {
		TbGoodsExample example = new TbGoodsExample();
		example.or().andIdIn(ids);
		List<TbGoods> goods = goodsMapper.selectByExample(example);
		for (TbGoods good : goods) {
			good.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(good);
		}

		TbItemExample itemExample = new TbItemExample();
		itemExample.or().andGoodsIdIn(ids);
		List<TbItem> tbItems = itemMapper.selectByExample(itemExample);

		ArrayList<String> result = new ArrayList<>(tbItems.size());
		for (TbItem tbItem : tbItems) {
			tbItem.setStatus("3");
			itemMapper.updateByPrimaryKey(tbItem);
			result.add(tbItem.getId().toString());
		}
		return result;
	}
	
	
		@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		TbGoodsExample example=new TbGoodsExample();
		Criteria criteria = example.or().andIsDeleteIsNull();
		if (!goods.getSellerId().equals("admin")) {
			criteria.andSellerIdEqualTo(goods.getSellerId());
		}
		if(goods!=null){
			if(goods.getGoodsName()!=null && goods.getGoodsName().trim().length()>0){
				criteria.andGoodsNameLike("%"+goods.getGoodsName().trim()+"%");
			}
			if(goods.getAuditStatus()!=null){
				criteria.andAuditStatusEqualTo(goods.getAuditStatus());
			}
		}
		
		Page<TbGoods> page = (Page<TbGoods>)goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
