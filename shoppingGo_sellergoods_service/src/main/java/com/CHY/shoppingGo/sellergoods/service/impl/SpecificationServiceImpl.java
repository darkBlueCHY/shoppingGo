package com.CHY.shoppingGo.sellergoods.service.impl;
import java.util.List;
import java.util.Map;

import com.CHY.shoppingGo.mapper.TbSpecificationOptionMapper;
import com.CHY.shoppingGo.po.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.CHY.shoppingGo.mapper.TbSpecificationMapper;
import com.CHY.shoppingGo.sellergoods.service.SpecificationService;
import com.CHY.shoppingGo.entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SpecificationServiceImpl implements SpecificationService {
	@Autowired
	private TbSpecificationMapper specificationMapper;
	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;

	@Override
	public List<Specification> findSpecificationByIds(List<Long> ids){
		return specificationMapper.findSpecificationByIds(ids);
	}

	@Override
	public List<Map> select2SpecificationList(){
		return specificationMapper.select2SpecificationList();
	}
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSpecification> findAll() {
		return specificationMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSpecification> page=   (Page<TbSpecification>) specificationMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	@Transactional
	public void add(Specification specification) throws Exception {
		TbSpecification tbSpecification = specification.getSpecification();
		specificationMapper.insert(tbSpecification);
		for (TbSpecificationOption tbSpecificationOption : specification.getSpecificationOptionList()) {
			tbSpecificationOption.setSpecId(tbSpecification.getId());
			specificationOptionMapper.insert(tbSpecificationOption);
		}
	}

	/**
	 * 修改
	 */
	@Override
	@Transactional
	public void update(Specification specification) throws Exception{
		specificationMapper.updateByPrimaryKey(specification.getSpecification());
			//查询原有options
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
			example.or().andSpecIdEqualTo(specification.getSpecification().getId());
		List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);

		for (TbSpecificationOption optionNew : specification.getSpecificationOptionList()) {
			if (optionNew.getId() == null) {
				optionNew.setSpecId(specification.getSpecification().getId());
				specificationOptionMapper.insert(optionNew);
			} else {
				for (TbSpecificationOption optionOld : options) {
					if (optionNew.getId().equals(optionOld.getId())) {
						if (!optionNew.equals(optionOld)) {
							specificationOptionMapper.updateByPrimaryKey(optionNew);
						}
						options.remove(optionOld);
						break ;
					}
				}
			}
		}
	//从数据库查询出来的列表中还未找到相同id的部分是需要删除的部分
		for (TbSpecificationOption option : options) {
			specificationOptionMapper.deleteByPrimaryKey(option.getId());
		}
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	@Transactional
	public Specification findOne(Long id) {
		Specification specification = new Specification();
		//查询规格
		specification.setSpecification(specificationMapper.selectByPrimaryKey(id));
		//查询规格选项
		TbSpecificationOptionExample example = new TbSpecificationOptionExample();
		example.or().andSpecIdEqualTo(id);
		specification.setSpecificationOptionList(specificationOptionMapper.selectByExample(example));
		return specification;
	}

	/**
	 * 批量删除
	 */
	@Override
	@Transactional
	public void delete(List<Long> ids) throws Exception{
		//删除所有option
		TbSpecificationOptionExample specificationOptionExample = new TbSpecificationOptionExample();
		specificationOptionExample.or().andSpecIdIn(ids);
		specificationOptionMapper.deleteByExample(specificationOptionExample);
		//删除模板
		TbSpecificationExample specificationExample = new TbSpecificationExample();
		specificationExample.or().andIdIn(ids);
		specificationMapper.deleteByExample(specificationExample);
	}
	
	
		@Override
	public PageResult findPage(TbSpecification specification, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		TbSpecificationExample example = null;
		if(specification!=null){
			String specName = specification.getSpecName();
			if(specName!=null && !specName.trim().equals("")){
				example=new TbSpecificationExample();
				example.or().andSpecNameLike("%"+specName+"%");
			}
		}
		Page<TbSpecification> page= (Page<TbSpecification>)specificationMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
