package com.CHY.shoppingGo.user.controller;
import java.util.List;
import java.util.Optional;

import com.CHY.shoppingGo.entity.PageResult;
import com.CHY.shoppingGo.entity.Result;
import com.CHY.shoppingGo.po.TbUser;
import com.CHY.shoppingGo.user.service.UserService;
import com.CHY.utils.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.dubbo.config.annotation.Reference;


/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/user")
public class UserController {
	@Reference
	private UserService userService;

	@RequestMapping("sendShortMessage")
	public Result sendShortMessage(String phoneNumber){
		if (PhoneFormatCheckUtils.isPhoneLegal(phoneNumber)) {
			try {
				userService.sendShortMessage(phoneNumber);
			} catch (Exception e) {
				return new Result(false,"短信发送失败");
			}
			return new Result(true,"短信已发送");
		} else {
			return new Result(false,"请输入正确的手机号");
		}
	}

	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbUser> findAll(){			
		return userService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult  findPage(int page,int rows){			
		return userService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param user
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(String code, @RequestBody TbUser user){
		try {
			Optional<String> codeOptional = Optional.ofNullable(code);
		    if (codeOptional.orElse("").equals(""))
				return new Result(false,"请输入验证码");
			if (userService.add(code,user)) {
				return new Result(true, "增加成功");
			} else {
				return new Result(false, "验证码错误，请重新输入");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param user
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbUser user){
		try {
			userService.update(user);
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
	public TbUser findOne(Long id){
		return userService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long [] ids){
		try {
			userService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
		/**
	 * 查询+分页
	 * @param user
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbUser user, int page, int rows  ){
		return userService.findPage(user, page, rows);		
	}
	
}
