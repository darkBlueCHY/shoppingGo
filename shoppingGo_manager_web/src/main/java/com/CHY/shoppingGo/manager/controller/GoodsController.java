package com.CHY.shoppingGo.manager.controller;

import com.CHY.shoppingGo.entity.PageResult;
import com.CHY.shoppingGo.entity.Result;
import com.CHY.shoppingGo.po.Goods;
import com.CHY.shoppingGo.po.TbGoods;
import com.CHY.shoppingGo.po.TbItem;
import com.CHY.shoppingGo.sellergoods.service.GoodsService;
import com.CHY.shoppingGo.sellergoods.service.ItemService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.ArrayList;
import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	@Reference
	private ItemService itemService;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private Destination itemSearchImportData;
	@Autowired
	private Destination itemSearchRemoveData;
	@Autowired
	private Destination itemPageGenerateHtml;
	@Autowired
	private Destination itemPageRemoveHtml;

	@RequestMapping("updateStatus")
	public Result updateStatus(@RequestBody List<Long> ids,String auditStatus){
		try {
			goodsService.updateStatus(ids, auditStatus);
			if (auditStatus.equals("1")) {
				List<TbItem> tbItems = itemService.findItemByGoodsId(ids, auditStatus);
				if (tbItems.size() > 0) {
					//将商品信息发送给activeMQ, search_service会取出并创造索引
					jmsTemplate.send(itemSearchImportData, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							return session.createTextMessage(JSON.toJSONString(tbItems));
						}
					});
					//生成商品信息的静态页面
				}
				jmsTemplate.send(itemPageGenerateHtml, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {

						return session.createTextMessage(JSON.toJSONString(ids));
					}
				});
			}
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll() {
		return goodsService.findAll();
	}

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows) {
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods) {
		try {
			goods.getGoods().setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改商品信息,商品需要重新审核
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods) {

		try {//itemSearchRemoveData
			jmsTemplate.send(itemSearchRemoveData, new MessageCreator(){
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(JSON.toJSONString(goodsService.update(goods)));
				}
			});
			jmsTemplate.send(itemPageRemoveHtml, new MessageCreator(){
				@Override
				public Message createMessage(Session session) throws JMSException {
					ArrayList<Long> array = new ArrayList<>(1);
					array.add(goods.getGoods().getId());
					return session.createTextMessage(JSON.toJSONString(array));
				}
			});
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id) {
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody List<Long> ids){
		try {
			jmsTemplate.send(itemSearchRemoveData, new MessageCreator(){
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(JSON.toJSONString(goodsService.delete(ids)));
				}
			});
			jmsTemplate.send(itemPageRemoveHtml, new MessageCreator(){
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(JSON.toJSONString(ids));
				}
			});

			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
		/**
	 * 查询+分页
	 * @param goods
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int rows  ){
		goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
		return goodsService.findPage(goods, page, rows);
	}

	/**
	 * 生成Goods详情页
	 * @param goodsId 要生成的Goods的id
	 * @return	返回添加事件状态
	 */
	@RequestMapping("/generateHtml")
	public Result generateHtml(Long goodsId) {
		try {
			ArrayList array = new ArrayList(1);
			array.add(goodsId);
			jmsTemplate.send(itemPageGenerateHtml, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createTextMessage(JSON.toJSONString(array));
				}
			});
			return new Result(true ,"生成成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false ,"生成失败");
		}
	}
}
