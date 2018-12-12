package com.CHY.shoppingGo.sellergoods.service.impl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.CHY.shoppingGo.mapper.TbSellerMapper;
import com.CHY.shoppingGo.po.TbSeller;
import com.CHY.shoppingGo.po.TbSellerExample;
import com.CHY.shoppingGo.po.TbSellerExample.Criteria;
import com.CHY.shoppingGo.sellergoods.service.SellerService;
import com.CHY.shoppingGo.entity.PageResult;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SellerServiceImpl implements SellerService {

	@Autowired
	private TbSellerMapper sellerMapper;

	@Override
	public void updateStatus(String sellerId ,String status) {
		sellerMapper.updateStatus(sellerId, status);
	}
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeller> findAll() {
		return sellerMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeller> page=   (Page<TbSeller>) sellerMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeller seller) {
		seller.setStatus("0");
		seller.setPassword(seller.getPassword());

		sellerMapper.insert(seller);
	}

	/**
	 * 修改
	 */
	@Override
	public void update(TbSeller seller){
		sellerMapper.updateByPrimaryKey(seller);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeller findOne(String id){
		return sellerMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(String[] ids) {
		for(String id:ids){
			sellerMapper.deleteByPrimaryKey(id);
		}		
	}

		@Override
	public PageResult findPage(TbSeller seller, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		TbSellerExample example=null;

		if(seller!=null){
			example = new TbSellerExample();
			Criteria criteria = example.or();
			if(seller.getName()!=null && !seller.getName().trim().equals("")){
				criteria.andNameLike("%"+seller.getName()+"%");
			}
			if(seller.getNickName()!=null && !seller.getNickName().trim().equals("")){
				criteria.andNickNameLike("%"+seller.getNickName()+"%");
			}
			if(seller.getStatus()!=null){
				criteria.andStatusEqualTo(seller.getStatus());
			}
		}

		Page<TbSeller> page= (Page<TbSeller>)sellerMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
}
