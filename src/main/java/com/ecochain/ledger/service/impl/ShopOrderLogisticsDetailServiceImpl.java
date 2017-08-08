package com.ecochain.ledger.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.mapper.FabricBlockInfoMapper;
import com.ecochain.ledger.mapper.ShopOrderInfoMapper;
import com.ecochain.ledger.mapper.ShopOrderLogisticsDetailMapper;
import com.ecochain.ledger.model.FabricBlockInfo;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.model.ShopOrderLogisticsDetail;
import com.ecochain.ledger.service.ShopOrderLogisticsDetailService;
import com.ecochain.ledger.service.SysGenCodeService;
import com.ecochain.ledger.util.Base64;
import com.ecochain.ledger.util.DateUtil;
import com.ecochain.ledger.util.StringUtil;
import com.ecochain.ledger.util.UuidUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.ecochain.ledger.util.HttpTool.doPost;

/**
 * Created by Lisandro on 2017/6/1.
 */

@Component("ShopOrderLogisticsDetailService")
public class ShopOrderLogisticsDetailServiceImpl implements ShopOrderLogisticsDetailService {

    private Logger logger = Logger.getLogger(ShopOrderLogisticsDetailServiceImpl.class);

    @Autowired
    private ShopOrderLogisticsDetailMapper shopOrderLogisticsDetailMapper;

    @Autowired
    private ShopOrderInfoMapper shopOrderInfoMapper;

    @Autowired
    private FabricBlockInfoMapper fabricBlockInfoMapper;

    @Autowired
    private ShopOrderLogisticsDetailService shopOrderLogisticsDetailService;

    @Autowired
    private SysGenCodeService sysGenCodeService;

    @Override
    public Map findLogisticsInfoByNo(String logisticsNo) {
        return shopOrderLogisticsDetailMapper.findLogisticsInfoByNo(logisticsNo);
    }

    @Override
    public List<Map<String,Object>> findLogisticsInfoByNo2(String logisticsNo) {
        return shopOrderLogisticsDetailMapper.findLogisticsInfoByNo2(logisticsNo);
    }

    @Override
    public int insertSelective(ShopOrderLogisticsDetail record) {
        return shopOrderLogisticsDetailMapper.insertSelective(record);
    }
    
    @Override
    public int searchTransferLogistics(PageData pd, String versionNo) throws Exception {
        return shopOrderLogisticsDetailMapper.findLogisticsInfoByOrderNo2(pd);
    }

    @Override
    public boolean transferLogistics(PageData pd, String versionNo) throws Exception {
        String kql_url = null;
        List<PageData> codeList = sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
        for (PageData mapObj : codeList) {
            if ("QKL_URL".equals(mapObj.get("code_name"))) {
                kql_url = mapObj.get("code_value").toString();
            }
        }
        JSONObject json = null;
        pd.put("flag", "outer");
        pd.put("order_status", "11");
        pd.put("create_time", DateUtil.getCurrDateTime());
        /*logger.info("====================测试代码========start================");
        String jsonStr = HttpUtil.sendPostData("" + kql_url + "/get_new_key", "");
        JSONObject keyJsonObj = JSONObject.parseObject(jsonStr);
        PageData keyPd = new PageData();
        keyPd.put("data", Base64.getBase64((JSON.toJSONString(pd))));
        keyPd.put("publicKey", keyJsonObj.getJSONObject("result").getString("publicKey"));
        keyPd.put("privateKey", keyJsonObj.getJSONObject("result").getString("privateKey"));
        System.out.println("keyPd value is ------------->" + JSON.toJSONString(keyPd));
        //2. 获取公钥签名
        String signJsonObjStr = HttpUtil.sendPostData("" + kql_url + "/send_data_for_sign", JSON.toJSONString(keyPd));
        JSONObject signJsonObj = JSONObject.parseObject(signJsonObjStr);
        Map<String, Object> paramentMap = new HashMap<String, Object>();
        paramentMap.put("publickey", keyJsonObj.getJSONObject("result").getString("publicKey"));
        paramentMap.put("data", Base64.getBase64((JSON.toJSONString(pd))));
        paramentMap.put("sign", signJsonObj.getString("result"));
        String result1 = HttpUtil.sendPostData("" + kql_url + "/send_data_to_sys", JSON.toJSONString(paramentMap));
        json = JSON.parseObject(result1);
        if (StringUtil.isNotEmpty(json.getString("result"))) {
            pd.put("logistics_detail_hash", json.getString("result"));
        }
        ShopOrderLogisticsDetail shopOrderLogisticsDetail = new ShopOrderLogisticsDetail();
        shopOrderLogisticsDetail.setLogisticsNo(pd.getString("logistics_no"));
        shopOrderLogisticsDetail.setLogisticsMsg(pd.getString("logistics_msg"));
        shopOrderLogisticsDetail.setLogisticsDetailHash(pd.getString("logistics_detail_hash"));
        shopOrderLogisticsDetail.setCreateTime(DateUtil.fomatDateDetail(pd.getString("create_time")));
        this.shopOrderLogisticsDetailService.insertSelective(shopOrderLogisticsDetail);
        if("transferLogistics".equals(pd.getString("type"))){
            pd.put("order_no",pd.getString("shop_order_no"));
            this.shopOrderInfoMapper.updateOrderStatusByOrderNo2(pd);
        }else{
            pd.put("order_no",pd.getString("shop_order_no"));
            this.shopOrderInfoMapper.updateOrderStatusByOrderNo2(pd);
        }
        logger.info("====================测试代码=======end=================");*/
        logger.info("====================调用fabric测试代码=======start=================");
        String uuid = UuidUtil.get32UUID();
        String bussType="outerTransferLogisticss";
        String jsonInfo= Base64.getBase64(JSONObject.toJSONString(pd.toString()));
        String finalInfo =jsonInfo.replace("\n","").replace("\r","");
        StringBuffer stringBuffer = new StringBuffer("{\n" +
                "    \"fcn\":\"createObj\",\n" +
                "    \"args\":[\n" +
                "        \""+uuid+"\",\n" +
                "        \""+bussType+"\",\n" +
                "\""+finalInfo+"\"\n" +
                "    ]\n" +
                "}");
        System.out.println(JSONObject.toJSONString(pd.toString()));
        System.out.println("调用fabric给的加密数据信息为------------------>"+finalInfo);
        String fabrickInfo = doPost(kql_url+"/createObj", stringBuffer.toString());
        logger.info("====================调用fabric接口返回为=========================" + fabrickInfo);
        logger.info("====================调用fabric测试代码=======end=================");
        FabricBlockInfo fabricBlockInfo =new FabricBlockInfo();
        fabricBlockInfo.setFabricHash(Base64.getBase64(fabrickInfo)); //fabric uuid
        fabricBlockInfo.setFabricUuid(uuid); //java
        fabricBlockInfo.setHashData(jsonInfo);
        fabricBlockInfo.setFabricBussType(bussType);
        fabricBlockInfo.setCreateTime(new Date());
        fabricBlockInfoMapper.insert(fabricBlockInfo);
        logger.info("====================调用fabric接口记录DB=======success=================");
        if (StringUtil.isNotEmpty(fabrickInfo)) {
            pd.put("logistics_detail_hash", Base64.getBase64(fabrickInfo));
        }
        ShopOrderLogisticsDetail shopOrderLogisticsDetail = new ShopOrderLogisticsDetail();
        shopOrderLogisticsDetail.setLogisticsNo(pd.getString("logistics_no"));
        shopOrderLogisticsDetail.setLogisticsMsg(pd.getString("logistics_msg"));
        shopOrderLogisticsDetail.setLogisticsDetailHash(pd.getString("logistics_detail_hash"));
        shopOrderLogisticsDetail.setCreateTime(DateUtil.fomatDateDetail(pd.getString("create_time")));
        this.shopOrderLogisticsDetailService.insertSelective(shopOrderLogisticsDetail);
        if("transferLogistics".equals(pd.getString("type"))){
            pd.put("order_no",pd.getString("shop_order_no"));
            this.shopOrderInfoMapper.updateOrderStatusByOrderNo2(pd);
        }else{
            pd.put("order_no",pd.getString("shop_order_no"));
            this.shopOrderInfoMapper.updateOrderStatusByOrderNo2(pd);
        }
        return true;
    }

    @Override
    public boolean transferLogisticsWithOutBlockChain(PageData pd, String versionNo) throws Exception {  //境外物流同步国内物流
        ShopOrderLogisticsDetail shopOrderLogisticsDetail = new ShopOrderLogisticsDetail();
        shopOrderLogisticsDetail.setLogisticsNo(pd.getString("logistics_no"));
        shopOrderLogisticsDetail.setLogisticsMsg(pd.getString("logistics_msg"));
        shopOrderLogisticsDetail.setLogisticsDetailHash(pd.getString("logistics_detail_hash"));
        shopOrderLogisticsDetail.setCreateTime(DateUtil.fomatDateDetail(pd.getString("create_time")));
        this.shopOrderLogisticsDetailService.insertSelective(shopOrderLogisticsDetail);
        if(!"notUpdate".equals(pd.getString("flag"))){
            this.shopOrderInfoMapper.updateOrderStatusByOrderNo2(pd);
        }
        return true;
    }
}
