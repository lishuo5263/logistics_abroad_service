package com.ecochain.ledger.web.rest;


import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.annotation.LoginVerify;
import com.ecochain.ledger.base.BaseWebService;
import com.ecochain.ledger.constants.CodeConstant;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.constants.CookieConstant;
import com.ecochain.ledger.mapper.ShopOrderInfoMapper;
import com.ecochain.ledger.model.Page;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.ShopOrderLogisticsDetailService;
import com.ecochain.ledger.util.AjaxResponse;
import com.ecochain.ledger.util.DateUtil;
import com.ecochain.ledger.util.RequestUtils;
import com.ecochain.ledger.util.SessionUtil;
import com.ecochain.ledger.util.StringUtil;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiShuo on 2017/06/01.
 */
@RestController
@RequestMapping(value = "/api/rest/logistics")
public class ShopOrderLogisticsDetailWebService extends BaseWebService {

    private Logger logger = Logger.getLogger(ShopOrderLogisticsDetailWebService.class);

    @Autowired
    private ShopOrderLogisticsDetailService shopOrderLogisticsDetailService;

    @Autowired
    private ShopOrderInfoMapper shopOrderInfoMapper;

    /**
     * 物流转货(调区块链)
     */
    @LoginVerify
    @GetMapping("/transferLogistics")
    @ApiOperation(nickname = "transferLogistics", value = "物流转货", notes = "物流转货！！")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "CSESSIONID", value = "CSESSIONID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "logistics_no", value = "物流单号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "logistics_msg", value = "物流信息 ", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "业务类型 转货传值为 transferLogistics ，新增物流信息随意传值 ", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse transferLogistics(HttpServletRequest request, Page page) {
        PageData pd = new PageData();
        pd = this.getPageData();
        page.setPd(pd);
        AjaxResponse ar = new AjaxResponse();
        Map<String, Object> map = new HashMap<String, Object>();
        
        if(pd.getString("logistics_msg").contains("?")){
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.PARAM_ERROR);
            ar.setMessage("请勿重复提交！");
            return ar;
        }
        try {
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            pd.remove("CSESSIONID");//add by zhangchunming
            pd.put("user_name", user.getString("user_name"));
            pd.put("create_time", DateUtil.getCurrDateTime());
            String logisticsNo = (String) pd.get("logistics_no");
            String type = pd.getString("type");
            if (!StringUtil.isNotEmpty(logisticsNo)) {
                return fastReturn(null, false, "接口参数logistics_no异常，物流转货失败！", CodeConstant.PARAM_ERROR);
            }
            map = shopOrderLogisticsDetailService.findLogisticsInfoByNo(logisticsNo);//查询物流信息
            if (map.containsKey("shop_order_no") && map.containsKey("logistics_hash")) {
                //修改发货状态 && 更新物流详情
                pd.put("logistics_hash", map.containsKey("logistics_hash") == true ? map.get("logistics_hash") : "");
                pd.put("type", type);
                pd.put("logistics_no", logisticsNo);
                pd.put("shop_order_no", map.get("shop_order_no").toString());
                pd.put("bussType", "outerTransferLogisticss");
                if(this.shopOrderLogisticsDetailService.searchTransferLogistics(pd, Constant.VERSION_NO) == 0){
                    this.shopOrderLogisticsDetailService.transferLogistics(pd, Constant.VERSION_NO);
                    return fastReturn(map, true, "物流转货成功！", CodeConstant.SC_OK);
                }
            } else {
                return fastReturn(null, false, "接口参数异常，物流转货失败！", CodeConstant.PARAM_ERROR);
            }
        } catch (ClassCastException e) {
            logger.debug(e.toString(), e);
            ar = fastReturn(null, false, "接口参数类型异常，物流转货失败！", CodeConstant.PARAM_ERROR);
        } catch (NullPointerException e) {
            logger.debug(e.toString(), e);
            ar = fastReturn(null, false, "接口参数异常，物流转货失败！", CodeConstant.PARAM_ERROR);
        } catch (Exception e) {
            logger.debug(e.toString(), e);
            ar = fastReturn(null, false, "系统异常，物流转货失败！", CodeConstant.SYS_ERROR);
        }
        return ar;
    }

    /*
     * 物流转货数据同步(不调区块链)
     */
    @GetMapping("/transferLogisticsWithOutBlockChain")
    @ApiOperation(nickname = "transferLogisticsWithOutBlockChain", value = "物流转货不调区块链", notes = "物流转货不调区块链！！")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "logistics_no", value = "物流单号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "logistics_msg", value = "物流信息", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "create_time", value = "创建时间", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "hash", value = "hash", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "shop_order_no", value = "订单号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "type", required = true, paramType = "query", dataType = "String"),
    })
    public AjaxResponse transferLogisticsWithOutBlockChain(HttpServletRequest request, Page page) {
        PageData pd = new PageData();
        pd = this.getPageData();
        page.setPd(pd);
        AjaxResponse ar = new AjaxResponse();
        try {
            if (!StringUtil.isNotEmpty(pd.getString("logistics_no")) && !StringUtil.isNotEmpty(pd.getString("logistics_msg")) && !StringUtil.isNotEmpty(pd.getString("hash")) && !StringUtil.isNotEmpty(pd.getString("create_time")) && !StringUtil.isNotEmpty(pd.getString("shop_order_no")) ) {
                return fastReturn(null, false, "接口参数logistics_no异常，物流转货失败！", CodeConstant.PARAM_ERROR);
            }
            pd.put("logistics_detail_hash",pd.getString("hash"));
            this.shopOrderLogisticsDetailService.transferLogisticsWithOutBlockChain(pd, Constant.VERSION_NO);
            return fastReturn(true, true, "物流转货成功！", CodeConstant.SC_OK);
        } catch (ClassCastException e) {
            logger.debug(e.toString(), e);
            ar = fastReturn(null, false, "接口参数类型异常，物流转货失败！", CodeConstant.PARAM_ERROR);
        } catch (NullPointerException e) {
            logger.debug(e.toString(), e);
            ar = fastReturn(null, false, "接口参数异常，物流转货失败！", CodeConstant.PARAM_ERROR);
        } catch (Exception e) {
            logger.debug(e.toString(), e);
            ar = fastReturn(null, false, "系统异常，物流转货失败！", CodeConstant.SYS_ERROR);
        }
        return ar;
    }
    
    public static void main(String[] args) {
        String str = "???��?????��??1";
        if(str.contains("?")){
            System.out.println("5545");
        }
    }
}
