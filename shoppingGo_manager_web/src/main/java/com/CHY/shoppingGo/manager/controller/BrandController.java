package com.CHY.shoppingGo.manager.controller;

import com.CHY.shoppingGo.entity.PageResult;
import com.CHY.shoppingGo.entity.Result;
import com.CHY.shoppingGo.po.TbBrand;
import com.CHY.shoppingGo.sellergoods.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("brand")
public class BrandController {
    @Reference
    BrandService brandService;

    @RequestMapping("select2BrandList")
    public List<Map> select2BrandList() throws Exception{
        return brandService.select2BrandList();
    }

    @RequestMapping("findAll")
    public List<TbBrand> findAll() throws Exception {
        return brandService.findAll();
    }

    @RequestMapping("search")
    public PageResult search(@RequestParam("page") int pageNum,@RequestParam("rows") int pageSize, @RequestBody(required = false) TbBrand brand) throws Exception {
        return brandService.search(pageNum, pageSize, brand);
    }

    @RequestMapping("findOne")
    public TbBrand findOne(Long id) throws Exception {
        return brandService.findOne(id);
    }

    @RequestMapping("add")
    public Result add(@RequestBody TbBrand brand) throws Exception {
        Result result = null;
        try {
            brandService.add(brand);
            result = new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"添加失败");
        } finally {
            return result;
        }
    }

    @RequestMapping("update")
    public Result update(@RequestBody TbBrand brand) throws Exception {
        Result result = null;
        try {
            brandService.change(brand);
            result = new Result(true,"修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            result = new Result(false,"修改失败");
        } finally {
            return result;
        }
    }

    @RequestMapping("delete")
    public Result delete(@RequestBody List<Long> ids) throws Exception {
        try {
            System.out.println(ids);
            brandService.delete(ids);
            System.out.println(new Result(true,"删除成功"));
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(new Result(false,"删除失败"));
            return new Result(false,"删除失败");
        } finally {
        }
    }
}
