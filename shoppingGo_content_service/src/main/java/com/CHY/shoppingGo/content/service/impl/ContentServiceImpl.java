package com.CHY.shoppingGo.content.service.impl;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.CHY.shoppingGo.content.service.ContentService;
import com.CHY.shoppingGo.entity.PageResult;
import com.CHY.shoppingGo.mapper.TbContentMapper;
import com.CHY.shoppingGo.po.TbContent;
import com.CHY.shoppingGo.po.TbContentExample;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class ContentServiceImpl implements ContentService {
	@Autowired
	RedisTemplate template;
	@Autowired
	private TbContentMapper contentMapper;

	@Override
    public List<TbContent> findContentByCatId(Long catId){
		List<TbContent> contentList = (List<TbContent>) template.boundHashOps("contentList").get(catId);
		if (contentList == null) {
			TbContentExample example = new TbContentExample();
			example.or().andCategoryIdEqualTo(catId);
			contentList = contentMapper.selectByExample(example);
			template.boundHashOps("contentList").put(catId,contentList);
		}
		return contentList;
    };
	/**
	 * 查询全部
	 */
	@Override
	public List<TbContent> findAll() {
		return contentMapper.selectByExample(null);
	}

	/**
	 * 按分页查询
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbContent> page= (Page<TbContent>) contentMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbContent content) {
		contentMapper.insert(content);
		template.boundHashOps("contentList").delete(content.getCategoryId());
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbContent content){
		Long originCatId = contentMapper.selectByPrimaryKey(content.getId()).getCategoryId();
		contentMapper.updateByPrimaryKey(content);
		template.boundHashOps("contentList").delete(originCatId);
		if (originCatId != content.getCategoryId()) {
			template.boundHashOps("contentList").delete(content.getCategoryId());
		}
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbContent findOne(Long id){
		return contentMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(List<Long> ids) {
		TbContentExample example = new TbContentExample();
		example.or().andIdIn(ids);
		List<TbContent> contentList = contentMapper.selectByExample(example);
		HashSet<Long> contentCatSet = new HashSet();
		for (TbContent content : contentList) {
			contentCatSet.add(content.getCategoryId());
		}

		contentMapper.deleteByExample(example);
		template.boundHashOps("contentList").delete(contentCatSet);
	}
	
	
		@Override
	public PageResult findPage(TbContent content, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbContentExample example=new TbContentExample();
            TbContentExample.Criteria criteria = example.or();

            if(content!=null){
						if(content.getTitle()!=null && content.getTitle().length()>0){
				criteria.andTitleLike("%"+content.getTitle()+"%");
			}
			if(content.getUrl()!=null && content.getUrl().length()>0){
				criteria.andUrlLike("%"+content.getUrl()+"%");
			}
			if(content.getPic()!=null && content.getPic().length()>0){
				criteria.andPicLike("%"+content.getPic()+"%");
			}
			if(content.getStatus()!=null && content.getStatus().length()>0){
				criteria.andStatusLike("%"+content.getStatus()+"%");
			}
	
		}
		
		Page<TbContent> page= (Page<TbContent>)contentMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}
	
}
