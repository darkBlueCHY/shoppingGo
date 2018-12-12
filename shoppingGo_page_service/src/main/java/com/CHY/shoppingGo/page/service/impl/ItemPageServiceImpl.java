package com.CHY.shoppingGo.page.service.impl;

import com.CHY.shoppingGo.mapper.TbGoodsDescMapper;
import com.CHY.shoppingGo.mapper.TbGoodsMapper;
import com.CHY.shoppingGo.mapper.TbItemCatMapper;
import com.CHY.shoppingGo.mapper.TbItemMapper;
import com.CHY.shoppingGo.page.service.ItemPageService;
import com.CHY.shoppingGo.po.TbGoods;
import com.CHY.shoppingGo.po.TbItemExample;
import com.alibaba.fastjson.JSON;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CHY
 * @create 2018-12-03 23:33
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private FreeMarkerConfigurer configuration;


    @Value("${freeMarker.pageDir}")
    private String PAGEDIR;

    /**
     * @Desc 根据传递的spu商品，生成对应的详情页信息
     * @param goodsIds
     */
    public void generateHtml(List<Long> goodsIds) throws Exception {

        Configuration configuration = this.configuration.getConfiguration();
        Map<String,Object> dataModel;

        for (Long goodsId : goodsIds) {
            dataModel = new HashMap<>(6);
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);

            //向模型中添加商品 、 商品扩展 、 商品SPU JSON字符串
            dataModel.put("tbGoods",tbGoods);
            dataModel.put("tbGoodsDesc",goodsDescMapper.selectByPrimaryKey(goodsId));
            TbItemExample example = new TbItemExample();
            example.or().andGoodsIdEqualTo(goodsId).andStatusEqualTo("1");
            dataModel.put("itemList", JSON.toJSONString(itemMapper.selectByExample(example)));
            //分类数据
            dataModel.put("category1Name",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName());
            dataModel.put("category2Name",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName());
            dataModel.put("category3Name",itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName());

            Template template = configuration.getTemplate("item.ftl");
            File file = new File(PAGEDIR + goodsId + ".html");
            if (file.exists()) {
                file.delete();
            }
            Writer writer = new BufferedWriter(new FileWriter(file));
            template.process(dataModel, writer);

            writer.close();

        }
    }

    public void removeHtml(List<Long> goodsIds) {
        File dir = new File(PAGEDIR);
        for (Long goodsId : goodsIds) {
            File htmlFile = new File(dir,goodsId+".html");
            htmlFile.delete();
        }
    }
}