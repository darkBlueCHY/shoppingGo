package com.CHY.shoppingGo.sellergoods.service;
import java.util.List;
import java.util.Map;

import com.CHY.shoppingGo.po.TbSpecificationOption;

import com.CHY.shoppingGo.entity.PageResult;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationOptionService {

	List<Map> select2OptionList();
	/**
	 * 返回全部列表
	 * @return
	 */
	List<TbSpecificationOption> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	PageResult findPage(int pageNum, int pageSize);
	
	
	/**
	 * 增加
	*/
	void add(TbSpecificationOption specificationOption);
	
	
	/**
	 * 修改
	 */
	void update(TbSpecificationOption specificationOption);
	

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSpecificationOption findOne(Long id);
	
	
	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSpecificationOption specificationOption, int pageNum, int pageSize);
	
}
