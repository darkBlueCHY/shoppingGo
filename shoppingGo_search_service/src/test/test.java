import com.CHY.shoppingGo.po.Item;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Crotch;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
/**
 * @author CHY
 * @create 2018-11-30-17:48
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-service.xml")
public class test {
    public static final String COLLECTION="shoppingGo";

    @Autowired
    SolrTemplate solrTemplate;
    @Test
    public void test (){
//        TbItem item = new TbItem();
//        item.setId(1l);
//        item.setBrand("华为");
//        item.setCategory("手机");
//        item.setGoodsId(1l);
//        item.setSeller("华为官方旗舰店");
//        item.setTitle("华为MATE2000未来科技");
//        item.setPrice(new BigDecimal(29999.999));

        try {
//            solrTemplate.saveBean(COLLECTION,new Item().transfrom(item));
//            Optional<Item> optional = solrTemplate.getById(COLLECTION, 1l, Item.class);
//            Item item = optional.get();
//            System.out.println(item);
//            ArrayList<Long> array = new ArrayList<>(1);
            UpdateResponse updateResponse = solrTemplate.deleteByIds(COLLECTION, "1");
            solrTemplate.commit(COLLECTION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void test2(){
        Query query = new SimpleQuery("*:*");

        Crotch contains = new Criteria("item_keywords").contains("华为")
                .and("item_brand").contains("华为");


        query.addCriteria(contains);
        query.setOffset(0L);
        query.setRows(10);

        Page<Item> pages = solrTemplate.query(COLLECTION, query, Item.class);
        System.out.println("总记录数:  " +  pages.getTotalElements());
        for (Item item : pages.getContent()) {
            System.out.println(item);
        }
    }
}
