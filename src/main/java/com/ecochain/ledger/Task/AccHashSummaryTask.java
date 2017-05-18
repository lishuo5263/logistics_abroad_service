package com.ecochain.ledger.Task;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.AccDetailService;
import com.ecochain.ledger.service.SysGenCodeService;
import com.ecochain.ledger.util.DateUtil;
import com.ecochain.ledger.util.HttpUtil;
import com.ecochain.ledger.util.Logger;
import com.ecochain.ledger.util.StringUtil;
/**
 * @author zhangchunming
 * @描述：定时账户汇总
 */
@Component
//@EnableScheduling
public class AccHashSummaryTask {
    private Logger logger = Logger.getLogger(AccHashSummaryTask.class);
	@Autowired
	private AccDetailService accDetailService;
	@Autowired
	private SysGenCodeService sysGenCodeService;
	/**
	 * 日期：2016-11-12<br>
	 * 版本：v1.0<br>
	 * 描述：summary 每12秒汇总一次
	 * 创建日期：2016-11-12 下午18:27:20 <br>
	 * 创建人 zhangchunming<br>
	 */
//	@Scheduled(cron="0/12 * *  * * ?")//配置时间表达式，每12秒执行一次任务
//	@Scheduled(fixedDelay=12000)  ////配置时间表达式，每12秒执行一次任务
	public void summary() {
	    logger.debug("---------------------------------------------定时器hash账户汇总------start------"+DateUtil.getCurrDateTime()+"---------------------------------------");
	    try {
	        List<String> tradeHashlist = new ArrayList<String>();
//            List<String> rechargeHashlist = new ArrayList<String>();
            List<PageData> hashList = accDetailService.getHashList(null, Constant.VERSION_NO);
            
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            String kql_url ="";
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            for(PageData hashMap:hashList){
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("\"").append(hashMap.getString("hash")).append("\"");
                
                String jsonStr = HttpUtil.sendPostData(kql_url+"/get_data_from_sys", stringBuffer.toString());
                JSONObject jsonObject = JSONObject.parseObject(jsonStr);
                String result = jsonObject.getString("result");
                if(StringUtil.isNotEmpty(result)){//生成区块成功
                    if("05".equals(hashMap.getString("acc_no"))){//商城兑换
                        tradeHashlist.add(hashMap.getString("hash"));
                    }
                    
                    /*else if("06".equals(hashMap.getString("acc_no"))){//积分充值
                        rechargeHashlist.add(hashMap.getString("hash"));
                    }*/
                }
            }
            if(tradeHashlist != null && tradeHashlist.size() > 0){//商城兑换
                PageData pd = new PageData();
                pd.put("hashList", tradeHashlist);
                boolean updatePayByHash = accDetailService.updatePayByHash(pd, Constant.VERSION_NO);
                logger.info("------------------------------定时器hash账户汇总修改统计标志updatePayByHash:"+updatePayByHash);
            }
            
            /*if(rechargeHashlist != null && rechargeHashlist.size() > 0){//积分充值
                PageData pd = new PageData();
                pd.put("hashList", rechargeHashlist);
                boolean updateRechargeByHash = accDetailService.updateRechargeByHash(pd, Constant.VERSION_NO);
                logger.info("------------------------------定时器hash账户汇总修改统计标志updateRechargeByHash:"+updateRechargeByHash);
            }*/
            
            
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("----定时器账户hash汇总异常---------e.getMessage():"+e.getMessage());
        }
	    logger.debug("---------------------------------------------定时器hash账户汇总------end----------"+DateUtil.getCurrDateTime()+"-----------------------------------");
	}
	public static void main(String[] args) {
        PageData pd = new PageData();
        pd.put("str", "123456.00");
        System.out.println(pd.getString("str"));
        System.out.println(DateUtil.getCurrDateTime());
    }
}
