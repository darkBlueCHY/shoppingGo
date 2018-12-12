package com.CHY.shoppingGo.sellergoods.service;
import java.util.List;

import com.CHY.shoppingGo.po.Goods;
import com.CHY.shoppingGo.po.TbGoods;

import com.CHY.shoppingGo.entity.PageResult;
import com.CHY.shoppingGo.po.TbItem;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {

	void updateStatus(List<Long> ids,String auditStatus);
	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(Goods goods);
	
	
	/**
	 * 修改
	 */
	public List<String> update(Goods goods);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public List<String> delete(List<Long> ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);
	
}
