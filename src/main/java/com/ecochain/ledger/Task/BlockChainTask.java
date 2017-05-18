package com.ecochain.ledger.Task;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.service.BlockDataHashService;
import com.ecochain.ledger.service.ShopOrderInfoService;
import com.ecochain.ledger.util.Base64;
import com.ecochain.ledger.util.HttpTool;
import com.ecochain.ledger.util.StringUtil;

/**
 * Created by Lisandro on 2017/5/17.
 */
@Component
//@EnableScheduling
public class BlockChainTask {

    @Value("${server.port}")
    private String servicePort;

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    private ShopOrderInfoService shopOrderInfoService;
    @Autowired
    private BlockDataHashService blockDataHashService;

    private Logger logger = Logger.getLogger(BlockChainTask.class);

//    @Scheduled(fixedDelay=10000)
    public void scheduler()throws  Exception {
        /**
         * 1.需要调用区块链接口查出当日增量的hash数据
         * 2.然后json解析出hash数据中的data字段
         * 3.取data字段中每个调用区块链接口存入的bussType 进行业务判断后，调用自身系统相对应的接口方法同步数据
         */
       logger.info(">>>>>>>>>>>>> Scheduled  Execute Interface ServiceName:   " +serviceName +" ServicePort:  " +servicePort);
        String getToDayBlockInfo = HttpTool.doPost("http://192.168.200.85:8332/GetDataList", "100");
        JSONObject toDayBlockInfo = JSONObject.parseObject(getToDayBlockInfo);
        for (Object resultMsg : toDayBlockInfo.getJSONArray("result")) {
            JSONObject resultInfo = (JSONObject) resultMsg;
            if (StringUtil.isNotEmpty(resultInfo.getString("data"))) {
                JSONObject data  =JSONObject.parseObject(Base64.getFromBase64(resultInfo.getString("data")));
                String hash = resultInfo.getString("hash");
                /*if(Integer.valueOf(shopOrderInfoService.queryOrderNum(toDayBlockInfoo.getString("shop_order_no"))) > 0){
                    continue;
                }else if("insertOrder".equals(toDayBlockInfoo.getString("bussType"))){
                    HttpTool.doPost("http://localhost:"+servicePort+"/"+serviceName+"/api/rest/shopOrder/insertShopOrder", "insertOrder"); //insertOrder 此处值应为给区块链的data值
                    continue;
                }else if("deliverGoods".equals(toDayBlockInfoo.getString("bussType"))){
                    HttpTool.doPost("http://localhost:"+servicePort+"/"+serviceName+"/deliverGoods?shop_order_no=170517112233972312999&goods_id=1120&logistics_no=111&logistics_name=22", "deliverGoods"); //deliverGoods 此处值应为给区块链的data值
                    continue;
                }else if("payNow".equals(toDayBlockInfoo.getString("bussType"))){
                    HttpTool.doPost("http://localhost:"+servicePort+"/"+serviceName+"/api/rest/shopOrder/payNow", "insertOrder"); //insertOrder 此处值应为给区块链的data值
                    continue;
                }else{
                    continue;
                }*/
                if(!blockDataHashService.isExistDataHash(hash)){
                    /*if("insertOrder".equals(data.getString("bussType"))){
                        HttpTool.doPost("http://localhost:"+servicePort+"/"+serviceName+"/api/rest/shopOrder/insertShopOrder", "insertOrder"); //insertOrder 此处值应为给区块链的data值
                    }else if("deliverGoods".equals(data.getString("bussType"))){
                        HttpTool.doPost("http://localhost:"+servicePort+"/"+serviceName+"/deliverGoods?shop_order_no=170517112233972312999&goods_id=1120&logistics_no=111&logistics_name=22", "deliverGoods"); //deliverGoods 此处值应为给区块链的data值
                    }else if("payNow".equals(data.getString("bussType"))){
                        HttpTool.doPost("http://localhost:"+servicePort+"/"+serviceName+"/api/rest/shopOrder/payNow", JSON.toJSONString(data)); //insertOrder 此处值应为给区块链的data值
                    }*/
                    
                    if("payNow".equals(data.getString("bussType"))){
                        HttpTool.doPost("http://localhost:"+servicePort+"/"+serviceName+"/api/rest/shopOrder/payNow", JSON.toJSONString(data)); //insertOrder 此处值应为给区块链的data值
                    }
                }
            }
        }
    }

}
