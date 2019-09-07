package com.thw.solr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thw.mapper.TbItemMapper;
import com.thw.pojo.TbItem;
import com.thw.pojo.TbItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    /*
     * 查询商品数据列表,查询的必须是已经审核通过的商品列表
     *
     */
    public void importItemData(){

        TbItemExample example=new TbItemExample();
        example.createCriteria().andStatusEqualTo("1");//表示通过审核
        List<TbItem> itemList = itemMapper.selectByExample(example);
        System.out.println("===商品列表ing===");
        for (TbItem tbItem : itemList) {
            Map map = JSON.parseObject(tbItem.getSpec());
            tbItem.setSpecMap(map);
            System.out.println(tbItem.getId()+"  "+tbItem.getTitle());
        }

        //存入数据到solr内
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();

        System.out.println("===商品列表end===");
    }
    //在main函数中进行调用执行该方法
    public static void main(String[] args) {
        //1.读取加载配置文件.classpath*:spring/applicationContext*.xml / *会读取其依赖的项目内的配置文件，不加*只会读取本项目的配置文件
        ApplicationContext app = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        //2.获取sorlutil对象
        SolrUtil solrUtil = (SolrUtil) app.getBean("solrUtil");
        //3.调用方法
        solrUtil.importItemData();
    }

}