package com.ecochain.ledger.web.rest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.annotation.LoginVerify;
import com.ecochain.ledger.base.BaseWebService;
import com.ecochain.ledger.constants.CodeConstant;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.constants.CookieConstant;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.model.ShopOrderGoods;
import com.ecochain.ledger.service.ShopGoodsService;
import com.ecochain.ledger.service.ShopOrderGoodsService;
import com.ecochain.ledger.service.ShopOrderInfoService;
import com.ecochain.ledger.service.ShopSupplierService;
import com.ecochain.ledger.service.SysGenCodeService;
import com.ecochain.ledger.service.UserWalletService;
import com.ecochain.ledger.util.AjaxResponse;
import com.ecochain.ledger.util.Base64;
import com.ecochain.ledger.util.DateUtil;
import com.ecochain.ledger.util.OrderGenerater;
import com.ecochain.ledger.util.RequestUtils;
import com.ecochain.ledger.util.SessionUtil;
import com.ecochain.ledger.util.StringUtil;
import com.github.pagehelper.PageInfo;

/**
 * Created by LiShuo on 2016/10/28.
 */
@RestController
@RequestMapping(value = "/api/rest/shopOrder")
public class ShopOrderInfoWebService extends BaseWebService {

    @Autowired
    private ShopOrderInfoService shopOrderInfoService;
    @Autowired
    private ShopGoodsService shopGoodsService;
    @Autowired
    private ShopOrderGoodsService shopOrderGoodsService;
   /* @Autowired
    private CacheManager cacheManager;*/
    @Autowired
    private SysGenCodeService sysGenCodeService;
   /* @Autowired
    private CalBounsService calBounsService;*/
    @Autowired
    private UserWalletService userWalletService;
   /* @Autowired
    private StoreOrderInfoService storeOrderInfoService;*/
    @Autowired
    private ShopSupplierService shopSupplierService;
    /*@Autowired
    private StoreInfoService storeInfoService;*/

    /**
     * @describe 商城&店铺取消订单
     * @author: lishuo
     * @date 2016-12-12
     */
  /*  @LoginVerify
    @RequestMapping(value = "/cancleOrder")
    
    public AjaxResponse cancleOrder(HttpServletRequest request, Page page) {
        Map<String, Object> data = new HashMap<String, Object>();
        AjaxResponse ar = new AjaxResponse();
        String type = request.getParameter("type");
        String orderNo = request.getParameter("orderNo");
        if (StringUtil.isNotEmpty(type)) {
            if (StringUtil.isNotEmpty(orderNo)) {
                //根据type去查询时shop 还是store订单 暂时商城秒杀订单不支持取消订单操作
                if ("shop".equals(type)) {
                    data = this.shopOrderInfoService.getOrderInfo(type, orderNo);
                    if (data != null && data.size() > 0 &&"1".equals(data.get("order_status").toString()) ) {
                        if ("0".equals(data.get("is_promote").toString())) {
                            if (this.shopOrderInfoService.updateCancleState(type, orderNo, "", "")) {
                                return fastReturn("取消商城订单成功！", true, "取消商城订单成功！", CodeConstant.SC_OK);
                            }
                        } else if ("1".equals(data.get("is_promote").toString())) {
                            if (this.shopOrderInfoService.updateCancleState(type, orderNo, "", data.get("is_promote").toString())) {
                                return fastReturn("取消秒杀商城订单成功！", true, "取消秒杀商城订单成功！", CodeConstant.SC_OK);
                            }
                        }else if ("2".equals(data.get("is_promote").toString())) {
                            if (this.shopOrderInfoService.updateCancleState(type, orderNo, "", data.get("is_promote").toString())) {
                                return fastReturn("取消商城人民币订单成功！", true, "取消商城人民币订单成功！", CodeConstant.SC_OK);
                            }
                        }
                    }else{
                        return fastReturn(data, true, "取消商城订单失败，订单号为  "+orderNo+" 的数据不满足要求！", CodeConstant.SYS_ERROR);
                    }
                } else if ("store".equals(type)) {
                    data = this.shopOrderInfoService.getOrderInfo(type, orderNo);
                    if (data != null && data.size() > 0 && "0".equals(data.get("order_status").toString())) {
                        //进行update 订单操作
                        if ("1".equals(data.get("is_hot").toString())) {
                            if (this.shopOrderInfoService.updateCancleState(type, orderNo, data.get("is_hot").toString(), "")) {
                                return fastReturn("取消爆款店铺订单成功！", true, "取消爆款店铺订单成功！", CodeConstant.SC_OK);
                            }
                        } else if ("0".equals(data.get("is_hot").toString())) {
                            if (this.shopOrderInfoService.updateCancleState(type, orderNo, "", "")) {
                                return fastReturn("取消店铺订单成功！", true, "取消店铺订单成功！", CodeConstant.SC_OK);
                            }
                        }
                    } else {
                        return fastReturn(data, true, "取消店铺订单失败，订单号为  "+orderNo+"  的数据不满足要求！", CodeConstant.SYS_ERROR);
                    }
                } else {
                    return fastReturn(null, false, "取消订单失败，数据库查询不到订单类型为->" + type + " 订单号为：" + orderNo + " 的订单！", CodeConstant.PARAM_ERROR);
                }
            } else {
                return fastReturn(null, false, "取消订单失败，orderNo参数为空！", CodeConstant.PARAM_ERROR);
            }
        } else {
            return fastReturn(null, false, "取消订单失败，type参数为空！", CodeConstant.PARAM_ERROR);
        }
        return ar;
    }*/

    //@LoginVerify

    @RequestMapping(value = "insertShopOrder", method = RequestMethod.POST, consumes = "application/json")
    @ApiOperation(nickname = "insertShopOrder", value = "商城生成订单", notes = "商城生成订单！！")
    @ApiImplicitParam(name = "shopOrderGoods", value = "下单参数 列如： {\n" +
            "        \"supplierName\":\"test supplier\",\n" +
            "        \"goodsNumber\":\"3\",\n" +
            "        \"userCode\":\"999\",\n" +
            "        \"userId\":\"25918\",\n" +
            "        \"addressId\":\"774\",\n" +
            "        \"postscript\":\"ls测试\",\n" +
            "        \"shippingName\":\"货到付款\",\n" +
            "        \"howOos\":\"不要了\",\n" +
            "        \"payName\":\"weicat pay\",\n" +
            "        \"goodsId\":\"1120\",\n" +
            "        \"skuValue\":\"lstextlslsls\",\n" +
            "        \"payPrice\":\"100\",\n" +
            "        \"csessionid\":\"ZGU5YTExZDE0NTUxNDk1Njg0MTU0ODUzYzJlMDI2NTk=\",\n" +
            "\"isPromote\":\"0\",\"skuInfo\":\"{\\\"颜色\\\":\\\"黑色\\\",\\\"规格\\\":\\\"个\\\",\\\"数量\\\":1,\\\"重量\\\":\\\"100g\\\"}\"\n" +
            "    }", required = true, paramType = "body", dataType = "BlogArticleBeen")
    public synchronized AjaxResponse insertShopOrder(@RequestBody String shopOrderGoods, HttpServletRequest request) throws Exception {
        AjaxResponse ar = new AjaxResponse();
        List<ShopOrderGoods> shopOrderGood = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, ShopOrderGoods.class);
            objectMapper.configure(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            shopOrderGood = objectMapper.readValue(shopOrderGoods, javaType);
            /*String userstr = SessionUtil.getAttibuteForUser(Base64.getFromBase64(shopOrderGood.get(0).getCsessionid()));
            JSONObject user = JSONObject.parseObject(userstr);
            if(user == null || !user.containsKey("seeds")){
                return fastReturn(null, false, "下单失败，登录超时，请重新登陆！", CodeConstant.UNLOGIN);
            }
            logger.info("sessionKey中用户信息------------>"+user.toJSONString());*/
            if (!StringUtil.isNotEmpty(String.valueOf(shopOrderGood.get(0).getUserId()))) {
                return fastReturn(null, false, "订单生成失败，userId参数为空！", CodeConstant.PARAM_ERROR);
            } else if (!StringUtil.isNotEmpty(String.valueOf(shopOrderGood.get(0).getAddressId()))) {
                return fastReturn(null, false, "订单生成失败，addressId参数为空！", CodeConstant.PARAM_ERROR);
            } else if (!StringUtil.isNotEmpty(shopOrderGood.get(0).getPostscript())) {
                return fastReturn(null, false, "订单生成失败，postscript参数为空！", CodeConstant.PARAM_ERROR);
            } else if (!StringUtil.isNotEmpty(shopOrderGood.get(0).getUserCode())) {
                return fastReturn(null, false, "订单生成失败，userCode参数为空！", CodeConstant.PARAM_ERROR);
            } else if (!StringUtil.isNotEmpty(shopOrderGood.get(0).getIsPromote())) {
                return fastReturn(null, false, "订单生成失败，isPromote参数为空！", CodeConstant.PARAM_ERROR);
            }

            for (int i = 0; i < shopOrderGood.size(); i++) {
                if (Integer.valueOf(shopOrderGood.get(i).getGoodsNumber()) == 0) {
                    return fastReturn(null, false, "订单生成失败，订单商品数量不能为空！", CodeConstant.PARAM_ERROR);
                } else if (!StringUtil.isNotEmpty(shopOrderGood.get(i).getSkuInfo())) {
                    shopOrderGood.get(i).setSkuInfo("无sku信息");
                } else if (!StringUtil.isNotEmpty(String.valueOf(shopOrderGood.get(i).getGoodsNumber()))) {
                    return fastReturn(null, false, "订单生成失败，第" + i + "个商品信息中 goodsNumber参数为空！", CodeConstant.PARAM_ERROR);
                } else if (!StringUtil.isNotEmpty(String.valueOf(shopOrderGood.get(0).getGoodsId()))) {
                    return fastReturn(null, false, "订单生成失败，第" + i + "个商品信息中 goodsId参数为空！", CodeConstant.PARAM_ERROR);
                } else if (!StringUtil.isNotEmpty(String.valueOf(shopOrderGood.get(0).getIsPromote()))) {
                    return fastReturn(null, false, "订单生成失败，第" + i + "个商品信息中 idPromote参数为空！", CodeConstant.PARAM_ERROR);
                }
            }
            if (shopOrderGood != null && shopOrderGood.get(0).getCsessionid().length() >= 32) {
                if ("0".equals(shopOrderGood.get(0).getIsPromote())) {  //正常商品下单
                    logger.info("--------商城普通下单参数shopOrderGood：" + shopOrderGoods);
                    logBefore(logger, "生成用户ID为： " + shopOrderGood.get(0).getUserId() + "+的订单");
                    List<Map<String, Object>> result = new ArrayList();
                    shopOrderGood.get(0).setOrderNo(OrderGenerater.generateOrderNo(shopOrderGood.get(0).getUserCode()));
                    shopOrderGood.get(0).setOrderStatus(1);
                    shopOrderGood.get(0).setShippingFee(new BigDecimal(0));
                    shopOrderGood.get(0).setIntegralMoney(new BigDecimal(0));
                    //shopOrderGood.get(0).setTradeHash(user.getString("seeds"));
                    shopOrderGood.get(0).setData(new StringBuffer(shopOrderGoods.substring(0,shopOrderGoods.length()-1)).append(",\"orderNo\":\""+shopOrderGood.get(0).getOrderNo()+"\"").append(",\"bussType\":\"insertOrder\"}").toString());
                    result = this.shopOrderInfoService.insertShopOrder(shopOrderGood);
                    if (result.get(0).get("ErrorInsert") != null) {
                        return fastReturn(result, false, "订单生成失败，goodsId参数为空！", CodeConstant.PARAM_ERROR);
                    } else if (result.get(0).get("ErrorInsertByUsersType") != null) {
                        return fastReturn(result, false, "订单生成失败，usersType参数为空无法确定购买价格！", CodeConstant.ERROR_NO_USERTYPE);
                    } else if (result.get(0).get("ErrorInsertNotAllowByUsersType") != null) {
                        return fastReturn(result, false, "订单生成失败，用户类型只能为普通会员和创业会员，商品无法购买！", CodeConstant.ERROR_NO_PERMISSION);
                    } else if (result.get(0).get("ErrorCreateOrderByGoodsId") != null) {
                        return fastReturn(result, false, "订单生成失败，此款商品在数据库中不存在！", CodeConstant.ERROR_INFO_NOT_SAME);
                    }else if (result.get(0).get("ErrorInsertByBlockChain") != null) {
                        return fastReturn(result, false, "订单生成失败，调用区块链接口发生错误", CodeConstant.ERROR_BLOCKCHAIN);
                    }  else {
                        ar = fastReturn(result, true, "生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单成功！", CodeConstant.SC_OK);
                    }
                } /*else if ("1".equals(shopOrderGood.get(0).getIsPromote())) {  //促销商品下单
                    if (shopOrderGood.get(0).getGoodsNumber() > 1) {
                        return fastReturn(null, false, "订单生成失败，秒杀商品每次只能购买1件！", CodeConstant.ERROR_NO_REPEATSECKILLGOODS);
                    }
                    logger.info("--------商城秒殺下单参数shopOrderGood：" + shopOrderGoods);
                    List<Map<String, Object>> result = new ArrayList();
                    if (Integer.valueOf(shopOrderGood.get(0).getActivityId()) > 0) {
                        //先查询秒杀模板开启状态和开启时间和结束时间进行下单逻辑判断
                        Map activeInfo = this.shopOrderGoodsService.queryActiveInfo(shopOrderGood.get(0).getActivityId());
                        if (activeInfo != null) {
                            if (StringUtil.isNotEmpty("start_time") && StringUtil.isNotEmpty("end_time")) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                String dateString_01 = activeInfo.get("start_time").toString();
                                String dateString_02 = activeInfo.get("end_time").toString();
                                Date startTime = sdf.parse(dateString_01);
                                Date endTime = sdf.parse(dateString_02);
                                Date sysTime = sdf.parse(sdf.format(new Date()));
                                if (sysTime.compareTo(startTime) == -1) {
                                    return fastReturn(result, false, "下单失败，秒杀活动暂未开始！", CodeConstant.ERROR_SECKILL_UNOPEN);
                                } else if (sysTime.compareTo(endTime) == 1) {
                                    return fastReturn(result, false, "下单失败，秒杀活动已经结束！！", CodeConstant.ERROR_ACTIVITE_END);
                                }  else if ((sysTime.compareTo(startTime) == 1) && (sysTime.compareTo(endTime) == 0)) {
                                    return this.insertShopOrder(shopOrderGood);
                                } else if ((sysTime.compareTo(startTime) == 1) && (sysTime.compareTo(endTime) == -1)) {
                                    return this.insertShopOrder(shopOrderGood);
                                }
                            } else {
                                return fastReturn(result, false, "下单失败，秒杀活动模板配置秒杀开始时间和结束时间有误！", CodeConstant.PARAM_ERROR);
                            }
                        } else {
                            return fastReturn(result, false, "下单失败，无此秒杀活动！", CodeConstant.ERROR_NO_ACTIVITY);
                        }
                    } else {
                        return fastReturn(result, false, "下单失败，ActivityId值不正确，ActivityId值为" + shopOrderGood.get(0).getActivityId() + "！", CodeConstant.PARAM_ERROR);
                    }
                } else if ("2".equals(shopOrderGood.get(0).getIsPromote())){ //与交易所购买图书交易
                    logger.info("--------与交易所购买图书下单参数shopOrderGood：" + shopOrderGoods);
                    logBefore(logger, "生成用户ID为： " + shopOrderGood.get(0).getUserId() + "+的订单");
                    List<Map<String, Object>> result = new ArrayList();
                    shopOrderGood.get(0).setOrderNo(OrderGenerater.generateOrderNo(shopOrderGood.get(0).getUserCode()));
                    shopOrderGood.get(0).setOrderStatus(1);
                    shopOrderGood.get(0).setShippingFee(new BigDecimal(0));
                    shopOrderGood.get(0).setIntegralMoney(new BigDecimal(0));
                    result = this.shopOrderInfoService.insertShopOrder(shopOrderGood);
                    if (result.get(0).get("ErrorInsert") != null) {
                        return fastReturn(result, false, "订单生成失败，goodsId参数为空！", CodeConstant.PARAM_ERROR);
                    } else if (result.get(0).get("ErrorInsertByUsersType") != null) {
                        return fastReturn(result, false, "订单生成失败，usersType参数为空无法确定购买价格！", CodeConstant.ERROR_NO_USERTYPE);
                    } else if (result.get(0).get("ErrorInsertNotAllowByUsersType") != null) {
                        return fastReturn(result, false, "订单生成失败，用户类型只能为普通会员和创业会员，商品无法购买！", CodeConstant.ERROR_NO_PERMISSION);
                    } else if (result.get(0).get("ErrorCreateOrderByPayPrice") != null) {
                        return fastReturn(result, false, "订单生成失败,商品价格与数据库中不相符！", CodeConstant.ERROR_PAY_PRICE);
                    } else if (result.get(0).get("ErrorCreateOrderByGoodsId") != null) {
                        return fastReturn(result, false, "订单生成失败,此款商品在数据库中不存在！", CodeConstant.ERROR_INFO_NOT_SAME);
                    } else {
                        ar = fastReturn(result, true, "生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单成功！", CodeConstant.SC_OK);
                    }
                }*/else {
                    ar = fastReturn(null, false, "订单生成失败，csessionid参数为空！", CodeConstant.PARAM_ERROR);
                }
                logAfter(logger);
            } else {
                return fastReturn(null, false, "下单失败，用户未登陆！", CodeConstant.UNLOGIN);
            }
        } catch (NullPointerException e) {
            logger.debug(e.toString(), e);
            ar = fastReturn(null, false, "系统异常，生成用户ID为: " + shopOrderGood.get(0).getUserId() + "的订单失败！！", CodeConstant.PARAM_ERROR);
        } catch (Exception e) {
            logger.debug(e.toString(), e);
            ar = fastReturn(null, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
        }
        return ar;
    }

    /**
     * 秒杀订单公共方法
     *
     * @param shopOrderGood
     * @return
     * @author lishuo
     * @date 2017-1-6 15:46:58
     */
   /* private synchronized AjaxResponse insertShopOrder(List<ShopOrderGoods> shopOrderGood) throws Exception {
        String SecKillState = "false";
        boolean redisCategoryKeyFlag = false;
        List<Map<String, Object>> result = new ArrayList();
        List<PageData> codeList = this.sysGenCodeService.findByGroupCode("SECKILL_STATUS", Constant.VERSION_NO);
        for (Map<String, Object> codeMap1 : codeList) {
            if ("SECKILL_STATUS".equals(codeMap1.get("code_name"))) {
                SecKillState = codeMap1.get("code_value").toString();
            }
        }
        if ("true".equals(SecKillState)) {
            //2016年12月17日 最新规则每人不限制场地一天只能秒杀一个商品
            int secKillCount = this.shopOrderInfoService.querySecKillCount(shopOrderGood.get(0).getUserId());
            if (secKillCount < 1) {
            *//*
            *1.查询Redis看是否有库存缓存key，无---->查SQL set库存key， 有直接2
            *2.按userId&goodsId查询此用户当天是否购买过此商品，无----->缓存key值-1
            *3.下单前查询此商品的库存， if库存量 >0  进行库存-1， 可进行下单
            *4.下单若失败，对Redis缓存key值进行+1 db库存+1
            *5 TODO 此规则未做多库存商品减库存逻辑
            *//*
                redisCategoryKeyFlag = cacheManager.isExist(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId());//秒杀商品等同立即购买1订单1个商品
                if (redisCategoryKeyFlag) { //该秒杀商品有库存
                    if ((Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) > 0) {
                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) - 1, 60 * 60 * 24);
                        Integer buyConut = this.shopOrderGoodsService.queryGoodsByCount(shopOrderGood.get(0).getUserId(), shopOrderGood.get(0).getGoodsId());
                        if (buyConut == 0) {
                            String type = "del";
                            Integer goodStock = this.shopGoodsService.queryGoodsStock(shopOrderGood.get(0).getGoodsId());
                            if (goodStock > 0) {
                                boolean flag = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                if (flag) {
                                    try {  //执行秒杀下单操做
                                        type = "add";
                                        shopOrderGood.get(0).setOrderStatus(1);
                                        shopOrderGood.get(0).setShippingFee(new BigDecimal(0));
                                        shopOrderGood.get(0).setIntegralMoney(new BigDecimal(0));
                                        shopOrderGood.get(0).setOrderNo(OrderGenerater.generateOrderNo(shopOrderGood.get(0).getUserCode()));
                                        result = this.shopOrderInfoService.insertShopOrder(shopOrderGood);
                                        if (result.get(0).get("ErrorInsert") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，goodsId参数为空！", CodeConstant.PARAM_ERROR);
                                        } else if (result.get(0).get("ErrorInsertByUsersType") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，usersType参数为空无法确定购买价格！", CodeConstant.ERROR_NO_USERTYPE);
                                        } else if (result.get(0).get("ErrorInsertNotAllowByUsersType") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，用户类型只能为普通会员和创业会员，商品无法购买！", CodeConstant.ERROR_NO_PERMISSION);
                                        } else if (result != null && result.size() > 0) {
                                            return fastReturn(result, true, "生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单成功！", CodeConstant.SC_OK);
                                        } else {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                        }
                                    } catch (Exception e) {
                                        type = "add";
                                        this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                        logger.info(e.toString(), e);
                                        return fastReturn(null, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                    }
                                } else {
                                    type = "add";
                                    boolean addGoodStockResult = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                    if (addGoodStockResult) {
                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                        return fastReturn(null, false, "减库存时发生异常，订单生成失败！", CodeConstant.SYS_ERROR);
                                    }
                                }
                            } else {
                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                                return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                            }
                        } else {
                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                            return fastReturn(null, false, "订单生成失败，用户不能重复秒杀同款商品！", CodeConstant.SYS_HOTGOODSREPEATKILL_ERROR);
                        }
                    } else {
                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                        return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                    }
                } else {
                    String goodsStock = this.shopGoodsService.getGoodsStock(String.valueOf(shopOrderGood.get(0).getGoodsId())); //从数据库获取秒杀商品个数并设置缓存
                    if (Integer.valueOf(goodsStock) > 0) {
                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), Integer.valueOf(goodsStock) - 1, 60 * 60 * 24);
                        Integer buyConut = this.shopOrderGoodsService.queryGoodsByCount(shopOrderGood.get(0).getUserId(), shopOrderGood.get(0).getGoodsId());
                        if (buyConut == 0) {
                            String type = "del";
                            Integer goodStock = this.shopGoodsService.queryGoodsStock(shopOrderGood.get(0).getGoodsId());
                            if (goodStock > 0) {
                                boolean flag = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                if (flag) {
                                    try {  //执行秒杀下单操做
                                        type = "add";
                                        shopOrderGood.get(0).setOrderStatus(1);
                                        shopOrderGood.get(0).setShippingFee(new BigDecimal(0));
                                        shopOrderGood.get(0).setIntegralMoney(new BigDecimal(0));
                                        shopOrderGood.get(0).setOrderNo(OrderGenerater.generateOrderNo(shopOrderGood.get(0).getUserCode()));
                                        result = this.shopOrderInfoService.insertShopOrder(shopOrderGood);
                                        if (result.get(0).get("ErrorInsert") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，goodsId参数为空！", CodeConstant.PARAM_ERROR);
                                        } else if (result.get(0).get("ErrorInsertByUsersType") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，usersType参数为空无法确定购买价格！", CodeConstant.ERROR_NO_USERTYPE);
                                        } else if (result.get(0).get("ErrorInsertNotAllowByUsersType") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，用户类型只能为普通会员和创业会员，商品无法购买！", CodeConstant.ERROR_NO_PERMISSION);
                                        } else if (result != null && result.size() > 0) {
                                            return fastReturn(result, true, "生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单成功！", CodeConstant.SC_OK);
                                        } else {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                        }
                                    } catch (Exception e) {
                                        type = "add";
                                        this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                        logger.info(e.toString(), e);
                                        return fastReturn(null, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                    }
                                } else {
                                    type = "add";
                                    boolean addGoodStockResult = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                    if (addGoodStockResult) {
                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                        return fastReturn(null, false, "减库存时发生异常，订单生成失败！", CodeConstant.SYS_ERROR);
                                    }
                                }
                            } else {
                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                                return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                            }
                        } else {
                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                            return fastReturn(null, false, "订单生成失败，用户不能重复秒杀同款商品！", CodeConstant.SYS_HOTGOODSREPEATKILL_ERROR);
                        }
                    } else {
                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                        return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                    }
                }
            } else {
                return fastReturn(secKillCount, false, "订单生成失败，用户每次秒杀活动只能购买一件商品！", CodeConstant.ERROR_NO_REPEATSECKILL);
            }
        } else {
            *//*
            *  ！！不限制用户每场秒杀商品数量但仅每个秒杀商品只能购买一次！！
            *1.查询Redis看是否有库存缓存key，无---->查SQL set库存key， 有直接2
            *2.按userId&goodsId查询此用户当天是否购买过此商品，无----->缓存key值-1
            *3.下单前查询此商品的库存， if库存量 >0  进行库存-1， 可进行下单
            *4.下单若失败，对Redis缓存key值进行+1 db库存+1
            *5.秒杀减库存减少多SKU的库存
            *//*
            if (shopOrderGood.get(0).getIsMutilPrice() != null) {   //SKU秒杀商品下单逻辑
                if (shopOrderGood.get(0).getIsMutilPrice() == 1) { //多价格减库存下单
                    if (StringUtils.isNotEmpty(shopOrderGood.get(0).getSkuValue())) {
                        redisCategoryKeyFlag = cacheManager.isExist(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId());//秒杀商品等同立即购买1订单1个商品
                        if (redisCategoryKeyFlag) { //该秒杀商品有库存
                            Map skuGoods = this.shopGoodsService.getSkuGoodsInfo(shopOrderGood.get(0).getSkuValue()); //从数据库获取秒杀商品个数并设置缓存
                            if (skuGoods != null && Integer.valueOf(skuGoods.get("gmp_goods_id").toString()).intValue() == shopOrderGood.get(0).getGoodsId()) {
                                if ((Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) > 0) {
                                    cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) - 1, 60 * 60 * 24);
                                    Integer buyConut = this.shopOrderGoodsService.queryGoodsByCount(shopOrderGood.get(0).getUserId(), shopOrderGood.get(0).getGoodsId());
                                    if (buyConut == 0) {
                                        String type = "del";
                                        //Integer goodStock=this.shopGoodsService.queryMutityGoodsStock(shopOrderGood.get(0).getSkuValue());
                                        if (skuGoods.get("gmp_stock") != null && Integer.valueOf(skuGoods.get("gmp_stock").toString()) > 0) {
                                            boolean flag = this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                            if (flag) {
                                                try {  //执行秒杀下单操做
                                                    type = "add";
                                                    shopOrderGood.get(0).setOrderStatus(1);
                                                    shopOrderGood.get(0).setShippingFee(new BigDecimal(0));
                                                    shopOrderGood.get(0).setIntegralMoney(new BigDecimal(0));
                                                    shopOrderGood.get(0).setOrderNo(OrderGenerater.generateOrderNo(shopOrderGood.get(0).getUserCode()));
                                                    result = this.shopOrderInfoService.insertShopOrder(shopOrderGood);
                                                    if (result.get(0).get("ErrorInsert") != null) {
                                                        this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                        return fastReturn(result, false, "订单生成失败，goodsId参数为空！", CodeConstant.PARAM_ERROR);
                                                    } else if (result.get(0).get("ErrorInsertByUsersType") != null) {
                                                        this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                        return fastReturn(result, false, "订单生成失败，usersType参数为空无法确定购买价格！", CodeConstant.ERROR_NO_USERTYPE);
                                                    } else if (result.get(0).get("ErrorInsertNotAllowByUsersType") != null) {
                                                        this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                        return fastReturn(result, false, "订单生成失败，用户类型只能为普通会员和创业会员，商品无法购买！", CodeConstant.ERROR_NO_PERMISSION);
                                                    } else if (result != null && result.size() > 0) {
                                                        return fastReturn(result, true, "生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单成功！", CodeConstant.SC_OK);
                                                    } else {
                                                        this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                        return fastReturn(result, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                                    }
                                                } catch (Exception e) {
                                                    type = "add";
                                                    this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                    cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                    logger.info(e.toString(), e);
                                                    return fastReturn(null, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                                }
                                            } else {
                                                type = "add";
                                                boolean addGoodStockResult = this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                if (addGoodStockResult) {
                                                    cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                    return fastReturn(null, false, "减库存时发生异常，订单生成失败！", CodeConstant.SYS_ERROR);
                                                }
                                            }
                                        } else {
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                                            return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                                        }
                                    } else {
                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                        return fastReturn(null, false, "订单生成失败，用户不能重复秒杀同款商品！", CodeConstant.SYS_HOTGOODSREPEATKILL_ERROR);
                                    }
                                } else {
                                    cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                                    return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                                }
                            } else {
                                return fastReturn(null, false, "订单生成失败，下单商品属性与数据库不一致！", CodeConstant.ERROR_SKU_NOT_SAME);
                            }
                        } else {
                            Map skuGoods = this.shopGoodsService.getSkuGoodsInfo(shopOrderGood.get(0).getSkuValue()); //从数据库获取秒杀商品个数并设置缓存
                            if (skuGoods != null && Integer.valueOf(skuGoods.get("gmp_goods_id").toString()).intValue() == shopOrderGood.get(0).getGoodsId()) {
                                if (Integer.valueOf(skuGoods.get("gmp_stock").toString()) > 0) {
                                    cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), Integer.valueOf(skuGoods.get("gmp_stock").toString()) - 1, 60 * 60 * 24);
                                    Integer buyConut = this.shopOrderGoodsService.queryGoodsByCount(shopOrderGood.get(0).getUserId(), shopOrderGood.get(0).getGoodsId());
                                    if (buyConut == 0) {
                                        String type = "del";
                                        //this.shopGoodsService.queryMutityGoodsStock(shopOrderGood.get(0).getSkuValue());
                                        if (skuGoods.get("gmp_stock") != null && Integer.valueOf(skuGoods.get("gmp_stock").toString()) > 0) {
                                            boolean flag = this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                            if (flag) {
                                                try {  //执行秒杀下单操做
                                                    type = "add";
                                                    shopOrderGood.get(0).setOrderStatus(1);
                                                    shopOrderGood.get(0).setShippingFee(new BigDecimal(0));
                                                    shopOrderGood.get(0).setIntegralMoney(new BigDecimal(0));
                                                    shopOrderGood.get(0).setOrderNo(OrderGenerater.generateOrderNo(shopOrderGood.get(0).getUserCode()));
                                                    result = this.shopOrderInfoService.insertShopOrder(shopOrderGood);
                                                    if (result.get(0).get("ErrorInsert") != null) {
                                                        this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                        return fastReturn(result, false, "订单生成失败，goodsId参数为空！", CodeConstant.PARAM_ERROR);
                                                    } else if (result.get(0).get("ErrorInsertByUsersType") != null) {
                                                        this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                        return fastReturn(result, false, "订单生成失败，usersType参数为空无法确定购买价格！", CodeConstant.ERROR_NO_USERTYPE);
                                                    } else if (result.get(0).get("ErrorInsertNotAllowByUsersType") != null) {
                                                        this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                        return fastReturn(result, false, "订单生成失败，用户类型只能为普通会员和创业会员，商品无法购买！", CodeConstant.ERROR_NO_PERMISSION);
                                                    } else if (result != null && result.size() > 0) {
                                                        return fastReturn(result, true, "生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单成功！", CodeConstant.SC_OK);
                                                    } else {
                                                        this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                        return fastReturn(result, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                                    }
                                                } catch (Exception e) {
                                                    type = "add";
                                                    this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                    cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                    logger.info(e.toString(), e);
                                                    return fastReturn(null, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                                }
                                            } else {
                                                type = "add";
                                                boolean addGoodStockResult = this.shopGoodsService.updateMutityGoodStockByType(type, shopOrderGood.get(0).getSkuValue());
                                                if (addGoodStockResult) {
                                                    cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                    return fastReturn(null, false, "减库存时发生异常，订单生成失败！", CodeConstant.SYS_ERROR);
                                                }
                                            }
                                        } else {
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                                            return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                                        }
                                    } else {
                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                        return fastReturn(null, false, "订单生成失败，用户不能重复秒杀同款商品！", CodeConstant.SYS_HOTGOODSREPEATKILL_ERROR);
                                    }
                                } else {
                                    cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                                    return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                                }
                            } else {
                                return fastReturn(null, false, "订单生成失败，下单商品属性与数据库不一致！", CodeConstant.ERROR_SKU_NOT_SAME);
                            }
                        }
                    } else {
                        return fastReturn(result, false, "订单生成失败，多价格skuValue参数为空！", CodeConstant.PARAM_ERROR);
                    }
                }else {   //普通秒杀下单逻辑
                    redisCategoryKeyFlag = cacheManager.isExist(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId());//秒杀商品等同立即购买1订单1个商品
                    if (redisCategoryKeyFlag) { //该秒杀商品有库存
                        if ((Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) > 0) {
                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) - 1, 60 * 60 * 24);
                            Integer buyConut = this.shopOrderGoodsService.queryGoodsByCount(shopOrderGood.get(0).getUserId(), shopOrderGood.get(0).getGoodsId());
                            if (buyConut == 0) {
                                String type = "del";
                                Integer goodStock = this.shopGoodsService.queryGoodsStock(shopOrderGood.get(0).getGoodsId());
                                if (goodStock > 0) {
                                    boolean flag = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                    if (flag) {
                                        try {  //执行秒杀下单操做
                                            type = "add";
                                            shopOrderGood.get(0).setOrderStatus(1);
                                            shopOrderGood.get(0).setShippingFee(new BigDecimal(0));
                                            shopOrderGood.get(0).setIntegralMoney(new BigDecimal(0));
                                            shopOrderGood.get(0).setOrderNo(OrderGenerater.generateOrderNo(shopOrderGood.get(0).getUserCode()));
                                            result = this.shopOrderInfoService.insertShopOrder(shopOrderGood);
                                            if (result.get(0).get("ErrorInsert") != null) {
                                                this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                return fastReturn(result, false, "订单生成失败，goodsId参数为空！", CodeConstant.PARAM_ERROR);
                                            } else if (result.get(0).get("ErrorInsertByUsersType") != null) {
                                                this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                return fastReturn(result, false, "订单生成失败，usersType参数为空无法确定购买价格！", CodeConstant.ERROR_NO_USERTYPE);
                                            } else if (result.get(0).get("ErrorInsertNotAllowByUsersType") != null) {
                                                this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                return fastReturn(result, false, "订单生成失败，用户类型只能为普通会员和创业会员，商品无法购买！", CodeConstant.ERROR_NO_PERMISSION);
                                            } else if (result != null && result.size() > 0) {
                                                return fastReturn(result, true, "生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单成功！", CodeConstant.SC_OK);
                                            } else {
                                                this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                return fastReturn(result, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                            }
                                        } catch (Exception e) {
                                            type = "add";
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            logger.info(e.toString(), e);
                                            return fastReturn(null, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                        }
                                    } else {
                                        type = "add";
                                        boolean addGoodStockResult = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                        if (addGoodStockResult) {
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(null, false, "减库存时发生异常，订单生成失败！", CodeConstant.SYS_ERROR);
                                        }
                                    }
                                } else {
                                    cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                                    return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                                }
                            } else {
                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                return fastReturn(null, false, "订单生成失败，用户不能重复秒杀同款商品！", CodeConstant.SYS_HOTGOODSREPEATKILL_ERROR);
                            }
                        } else {
                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                            return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                        }
                    } else {
                        String goodsStock = this.shopGoodsService.getGoodsStock(String.valueOf(shopOrderGood.get(0).getGoodsId())); //从数据库获取秒杀商品个数并设置缓存
                        if (Integer.valueOf(goodsStock) > 0) {
                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), Integer.valueOf(goodsStock) - 1, 60 * 60 * 24);
                            Integer buyConut = this.shopOrderGoodsService.queryGoodsByCount(shopOrderGood.get(0).getUserId(), shopOrderGood.get(0).getGoodsId());
                            if (buyConut == 0) {
                                String type = "del";
                                Integer goodStock = this.shopGoodsService.queryGoodsStock(shopOrderGood.get(0).getGoodsId());
                                if (goodStock > 0) {
                                    boolean flag = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                    if (flag) {
                                        try {  //执行秒杀下单操做
                                            type = "add";
                                            shopOrderGood.get(0).setOrderStatus(1);
                                            shopOrderGood.get(0).setShippingFee(new BigDecimal(0));
                                            shopOrderGood.get(0).setIntegralMoney(new BigDecimal(0));
                                            shopOrderGood.get(0).setOrderNo(OrderGenerater.generateOrderNo(shopOrderGood.get(0).getUserCode()));
                                            result = this.shopOrderInfoService.insertShopOrder(shopOrderGood);
                                            if (result.get(0).get("ErrorInsert") != null) {
                                                this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                return fastReturn(result, false, "订单生成失败，goodsId参数为空！", CodeConstant.PARAM_ERROR);
                                            } else if (result.get(0).get("ErrorInsertByUsersType") != null) {
                                                this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                return fastReturn(result, false, "订单生成失败，usersType参数为空无法确定购买价格！", CodeConstant.ERROR_NO_USERTYPE);
                                            } else if (result.get(0).get("ErrorInsertNotAllowByUsersType") != null) {
                                                this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                return fastReturn(result, false, "订单生成失败，用户类型只能为普通会员和创业会员，商品无法购买！", CodeConstant.ERROR_NO_PERMISSION);
                                            } else if (result != null && result.size() > 0) {
                                                return fastReturn(result, true, "生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单成功！", CodeConstant.SC_OK);
                                            } else {
                                                this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                                return fastReturn(result, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                            }
                                        } catch (Exception e) {
                                            type = "add";
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            logger.info(e.toString(), e);
                                            return fastReturn(null, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                        }
                                    } else {
                                        type = "add";
                                        boolean addGoodStockResult = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                        if (addGoodStockResult) {
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(null, false, "减库存时发生异常，订单生成失败！", CodeConstant.SYS_ERROR);
                                        }
                                    }
                                } else {
                                    cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                                    return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                                }
                            } else {
                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                return fastReturn(null, false, "订单生成失败，用户不能重复秒杀同款商品！", CodeConstant.SYS_HOTGOODSREPEATKILL_ERROR);
                            }
                        } else {
                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                            return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                        }
                    }
                }
            } else {   //普通秒杀下单逻辑(APP端调用)
                redisCategoryKeyFlag = cacheManager.isExist(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId());//秒杀商品等同立即购买1订单1个商品
                if (redisCategoryKeyFlag) { //该秒杀商品有库存
                    if ((Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) > 0) {
                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) - 1, 60 * 60 * 24);
                        Integer buyConut = this.shopOrderGoodsService.queryGoodsByCount(shopOrderGood.get(0).getUserId(), shopOrderGood.get(0).getGoodsId());
                        if (buyConut == 0) {
                            String type = "del";
                            Integer goodStock = this.shopGoodsService.queryGoodsStock(shopOrderGood.get(0).getGoodsId());
                            if (goodStock > 0) {
                                boolean flag = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                if (flag) {
                                    try {  //执行秒杀下单操做
                                        type = "add";
                                        shopOrderGood.get(0).setOrderStatus(1);
                                        shopOrderGood.get(0).setShippingFee(new BigDecimal(0));
                                        shopOrderGood.get(0).setIntegralMoney(new BigDecimal(0));
                                        shopOrderGood.get(0).setOrderNo(OrderGenerater.generateOrderNo(shopOrderGood.get(0).getUserCode()));
                                        result = this.shopOrderInfoService.insertShopOrder(shopOrderGood);
                                        if (result.get(0).get("ErrorInsert") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，goodsId参数为空！", CodeConstant.PARAM_ERROR);
                                        } else if (result.get(0).get("ErrorInsertByUsersType") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，usersType参数为空无法确定购买价格！", CodeConstant.ERROR_NO_USERTYPE);
                                        } else if (result.get(0).get("ErrorInsertNotAllowByUsersType") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，用户类型只能为普通会员和创业会员，商品无法购买！", CodeConstant.ERROR_NO_PERMISSION);
                                        } else if (result != null && result.size() > 0) {
                                            return fastReturn(result, true, "生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单成功！", CodeConstant.SC_OK);
                                        } else {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                        }
                                    } catch (Exception e) {
                                        type = "add";
                                        this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                        logger.info(e.toString(), e);
                                        return fastReturn(null, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                    }
                                } else {
                                    type = "add";
                                    boolean addGoodStockResult = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                    if (addGoodStockResult) {
                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                        return fastReturn(null, false, "减库存时发生异常，订单生成失败！", CodeConstant.SYS_ERROR);
                                    }
                                }
                            } else {
                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                                return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                            }
                        } else {
                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                            return fastReturn(null, false, "订单生成失败，用户不能重复秒杀同款商品！", CodeConstant.SYS_HOTGOODSREPEATKILL_ERROR);
                        }
                    } else {
                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                        return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                    }
                } else {
                    String goodsStock = this.shopGoodsService.getGoodsStock(String.valueOf(shopOrderGood.get(0).getGoodsId())); //从数据库获取秒杀商品个数并设置缓存
                    if (Integer.valueOf(goodsStock) > 0) {
                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), Integer.valueOf(goodsStock) - 1, 60 * 60 * 24);
                        Integer buyConut = this.shopOrderGoodsService.queryGoodsByCount(shopOrderGood.get(0).getUserId(), shopOrderGood.get(0).getGoodsId());
                        if (buyConut == 0) {
                            String type = "del";
                            Integer goodStock = this.shopGoodsService.queryGoodsStock(shopOrderGood.get(0).getGoodsId());
                            if (goodStock > 0) {
                                boolean flag = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                if (flag) {
                                    try {  //执行秒杀下单操做
                                        type = "add";
                                        shopOrderGood.get(0).setOrderStatus(1);
                                        shopOrderGood.get(0).setShippingFee(new BigDecimal(0));
                                        shopOrderGood.get(0).setIntegralMoney(new BigDecimal(0));
                                        shopOrderGood.get(0).setOrderNo(OrderGenerater.generateOrderNo(shopOrderGood.get(0).getUserCode()));
                                        result = this.shopOrderInfoService.insertShopOrder(shopOrderGood);
                                        if (result.get(0).get("ErrorInsert") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，goodsId参数为空！", CodeConstant.PARAM_ERROR);
                                        } else if (result.get(0).get("ErrorInsertByUsersType") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，usersType参数为空无法确定购买价格！", CodeConstant.ERROR_NO_USERTYPE);
                                        } else if (result.get(0).get("ErrorInsertNotAllowByUsersType") != null) {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "订单生成失败，用户类型只能为普通会员和创业会员，商品无法购买！", CodeConstant.ERROR_NO_PERMISSION);
                                        } else if (result != null && result.size() > 0) {
                                            return fastReturn(result, true, "生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单成功！", CodeConstant.SC_OK);
                                        } else {
                                            this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                            return fastReturn(result, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                        }
                                    } catch (Exception e) {
                                        type = "add";
                                        this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                        logger.info(e.toString(), e);
                                        return fastReturn(null, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
                                    }
                                } else {
                                    type = "add";
                                    boolean addGoodStockResult = this.shopGoodsService.updateGoodStockByType(type, shopOrderGood.get(0).getGoodsId());
                                    if (addGoodStockResult) {
                                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                                        return fastReturn(null, false, "减库存时发生异常，订单生成失败！", CodeConstant.SYS_ERROR);
                                    }
                                }
                            } else {
                                cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                                return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                            }
                        } else {
                            cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), (Integer) cacheManager.get(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId()) + 1, 60 * 60 * 24);
                            return fastReturn(null, false, "订单生成失败，用户不能重复秒杀同款商品！", CodeConstant.SYS_HOTGOODSREPEATKILL_ERROR);
                        }
                    } else {
                        cacheManager.set(RedisConstantUtil.SHOPHOTGOODS + shopOrderGood.get(0).getGoodsId(), -1, 60 * 60 * 24);
                        return fastReturn(null, false, "订单生成失败，此款秒杀商品已无库存！", CodeConstant.SYS_HOTGOODSKILL_ERROR);
                    }
                }
            }
        }
        return fastReturn(null, false, "系统异常，生成用户ID为：" + shopOrderGood.get(0).getUserId() + "的订单失败！", CodeConstant.SYS_ERROR);
    }*/

    /**
     * @param request
     * @describe:分页查询订单列表
     * @author: zhangchunming
     * @date: 2016年10月27日上午11:46:28
     * @return: AjaxResponse
     */
    @LoginVerify
    @PostMapping("/listPageShopOrder")
    @ApiOperation(nickname = "查询订单列表", value = "查询订单列表", notes = "查询订单列表！")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "CSESSIONID", value = "会话token", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse listPageShopOrder(HttpServletRequest request) {
        AjaxResponse ar = new AjaxResponse();
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            PageData pd = new PageData();
            pd = this.getPageData();
            //获取用户信息
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            
            /*//供应商List
            List<PageData> supplierList = null;
            //供应商IDList
            List<Integer> supplierIdList = new ArrayList<Integer>();*/
            PageData oneSupplier = null;
            if ("4".equals(user.getString("user_type"))) {//供应商
                //根据user_id查询供应商信息
//                supplierList = shopOrderInfoService.getSupplierByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
                oneSupplier = shopOrderInfoService.getOneSupplierByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
                if (oneSupplier == null) {
                    logger.error("--------查询商城订单列表-----------根据user_id查找不到供应商信息！");
                    ar.setSuccess(false);
                    ar.setMessage("查找不到供应商信息！");
                    ar.setErrorCode(CodeConstant.NO_EXISTS);
                    return ar;
                }
                /*for(PageData supplier:supplierList){
                    supplierIdList.add((Integer)supplier.get("id"));
                }
                pd.put("supplierList", supplierList);*/
                pd.put("supplier_id", String.valueOf(oneSupplier.get("id")));
            } else {
                pd.put("user_id", String.valueOf(user.get("id")));
                pd.put("user_type", String.valueOf(user.getString("user_type")));
            }
            
            List<PageData> shopOrderList = shopOrderInfoService.listShopOrderByPage(pd);//查询所有订单
            if (shopOrderList != null && shopOrderList.size()>0) {
                    List<String> orderIdList = new ArrayList<String>();
                    for (PageData shopOrder : shopOrderList) {
                        orderIdList.add(String.valueOf(shopOrder.get("order_id")));
                    }
                    PageData shopOrder = new PageData();
                    if ("4".equals(user.getString("user_type"))) {//供应商
//                        shopOrder.put("supplierIdList", supplierIdList);
                        shopOrder.put("supplier_id", String.valueOf(oneSupplier.get("id")));
                    } else {
                        shopOrder.put("user_id", String.valueOf(user.get("id")));
                    }
                    shopOrder.put("orderIdList", orderIdList);
                    List<PageData> shopGoods = shopOrderInfoService.getGoodsByOrderId(shopOrder, Constant.VERSION_NO);

                    if (shopGoods != null && shopGoods.size() > 0) {
                        for (PageData newShopOrder : shopOrderList) {
                            List<PageData> shopGoodsList = new ArrayList<PageData>();
                            for (PageData newShopGoods : shopGoods) {
                                if (newShopGoods.getString("shop_order_id").equals(String.valueOf(newShopOrder.get("order_id")))) {
                                    shopGoodsList.add(newShopGoods);
                                }
                            }
                            newShopOrder.put("shopGoodsList", shopGoodsList);
                        }
                    }
            }
            data.put("pageInfo", new PageInfo<PageData>(shopOrderList));
            ar.setData(data);
            ar.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
        }
        return ar;
    }

    /**
     * @param request
     * @describe:按状态查询商城订单数量
     * @author: zhangchunming
     * @date: 2016年10月28日下午1:10:13
     * @return: AjaxResponse
     */
    @LoginVerify
    @RequestMapping(value = "/getShopOrderNumByStatus", method = RequestMethod.POST)

    public AjaxResponse getShopOrderNumByStatus(HttpServletRequest request) {
        AjaxResponse ar = new AjaxResponse();
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            PageData pd = new PageData();
            /*pd = this.getPageData();*/
            /*String key = RequestUtils.getCookieValueByKey(CookieConstant.CSESSIONID, request, response);
            String userstr = SessionUtil.getAttibuteForUser(key);*/
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            if ("1".equals(user.getString("user_type")) || "2".equals(user.getString("user_type"))) {//普通会员、创业会员
                pd.put("user_id", String.valueOf(user.get("id")));
                PageData shopOrderNum = shopOrderInfoService.getShopOrderNumByStatus(pd, Constant.VERSION_NO);
                data.put("shopOrderNum", shopOrderNum);
            } else if ("4".equals(user.getString("user_type"))) {
                PageData supplier = shopOrderInfoService.getOneSupplierByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
                if (supplier == null) {
                    logger.error("--------根据用户信息查不到对应的供应商信息");
                    data.put("shopOrderNum", null);
                } else {
                    pd.put("supplier_id", String.valueOf(supplier.get("id")));
//                    PageData shopOrderNum = shopOrderInfoService.getShopOrderNumByStatus(pd, Constant.VERSION_NO);
                    PageData shopOrderNum = shopOrderInfoService.getShopOrderNumByStatusForSupplier(pd,Constant.VERSION_NO);
                    data.put("shopOrderNum", shopOrderNum);
                }
            } else {
                data.put("shopOrderNum", null);
            }
            ar.setData(data);
            ar.setMessage("查询成功！");
            ar.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
        }
        return ar;
    }

    /**
     * @param request
     * @describe:查询订单总数
     * @author: zhangchunming
     * @date: 2016年10月28日下午2:40:23
     * @return: AjaxResponse
     */
    @LoginVerify
    @RequestMapping(value = "/getOrderTotalNum", method = RequestMethod.POST)

    public AjaxResponse getOrderTotalNum(HttpServletRequest request) {
        AjaxResponse ar = new AjaxResponse();
        Map<String, Object> data = new HashMap<String, Object>();
        try {
            PageData pd = new PageData();
            /*String key = RequestUtils.getCookieValueByKey(CookieConstant.CSESSIONID, request, response);
            String userstr = SessionUtil.getAttibuteForUser(key);*/
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            String user_id = String.valueOf(user.get("id"));
//            Integer shopOrderTotalNum = shopOrderInfoService.getShopOrderTotalNum(user_id, Constant.VERSION_NO);
            if ("4".equals(user.getString("user_type"))) {//供应商
                PageData supplier = shopSupplierService.getSupplierByUserId(user_id, Constant.VERSION_NO);
                if (supplier == null) {
                    logger.error("----------根据用户信息查不到对应的供应商信息---------------");
                    data.put("shopOrderTotalNum", null);
                } else {
                    pd.put("supplier_id", String.valueOf(supplier.get("id")));
                    Integer shopOrderTotalNum = shopOrderInfoService.getShopOrderTotalNum(pd, Constant.VERSION_NO);
                    data.put("shopOrderTotalNum", shopOrderTotalNum);
                }

            } /*else if ("3".equals(user.getString("user_type"))) { //店铺
                Integer storeId = storeInfoService.getStoreIdByUserId(user_id, Constant.VERSION_NO);
                if (storeId == null) {
                    logger.error("----------根据用户信息查不到对应的店铺信息---------------");
                    data.put("storeOrderTotalNum", null);
                } else {
                    pd.put("store_id", storeId);
                   *//* Integer storeOrderTotalNum = storeOrderInfoService.getStoreOrderTotalNum(pd, Constant.VERSION_NO);
                    data.put("storeOrderTotalNum", storeOrderTotalNum);*//*
                }
            }*/ else {//普通会员、创业会员
                pd.put("user_id", user_id);
                Integer shopOrderTotalNum = shopOrderInfoService.getShopOrderTotalNum(pd, Constant.VERSION_NO);
                //Integer storeOrderTotalNum = storeOrderInfoService.getStoreOrderTotalNum(pd, Constant.VERSION_NO);
                data.put("shopOrderTotalNum", shopOrderTotalNum);
               // data.put("storeOrderTotalNum", storeOrderTotalNum);
            }
            ar.setData(data);
            ar.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
        }
        return ar;
    }

    /**
     * @param request
     * @describe:确认收货,更改订单状态
     * @author: zhangchunming
     * @date: 2016年11月08日下午17:23:50
     * @return: AjaxResponse
     */
   /* @LoginVerify
    @RequestMapping(value="/confirmReceipt", method=RequestMethod.POST)

    public AjaxResponse confirmReceipt(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        try {
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            PageData pd = new PageData();
            pd = this.getPageData();
            pd.put("user_id", user.getInteger("id"));
            String  order_no = pd.getString("order_no");
            if(StringUtil.isEmpty(order_no)){
                ar.setSuccess(false);
                ar.setMessage("订单号不能为空");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if(StringUtil.isEmpty(pd.getString("goods_id"))){
                ar.setSuccess(false);
                ar.setMessage("商品ID不能为空");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
//            PageData shopOrder = shopOrderInfoService.selectById(Integer.valueOf(order_id), Constant.VERSION_NO);
            pd.put("shop_order_no", order_no);
            PageData orderGoods = shopOrderGoodsService.getOrderGoods(pd, Constant.VERSION_NO);
            if(orderGoods==null){
                ar.setSuccess(false);
                ar.setMessage("订单不存在");
                ar.setErrorCode(CodeConstant.ORDER_NO_EXISTS);
                return ar;
            }
            boolean lastShopOrder = shopOrderInfoService.isLastShopOrder(pd, Constant.VERSION_NO);
            if(lastShopOrder){
                boolean updateStateByOrderNo = shopOrderInfoService.updateStateByOrderNo(pd, Constant.VERSION_NO);
                if(updateStateByOrderNo){
                    ar.setSuccess(true);
                    ar.setMessage("确认收货成功！");
                    return ar;
                }else{
                    ar.setSuccess(false);
                    ar.setMessage("确认收货失败");
                    ar.setErrorCode(CodeConstant.UPDATE_FAIL);
                }
            }else{
                PageData shopOrder = shopOrderInfoService.getShopOrderByOrderNo(pd, Constant.VERSION_NO);
                pd.put("user_type", user.getString("user_type"));
                pd.put("wlbi_amnt", shopOrder.getString("order_amount"));//应付款金额
                pd.put("other_amnt", shopOrder.getString("order_amount"));//应付款金额
                pd.put("otherno", shopOrder.getString("order_no"));//业务订单号
                pd.put("operator", user.getString("mobile_phone"));

//                pd.put("shop_order_no", shopOrder.getString("order_no"));
                //获取三界行情
                BigDecimal sanhq =new BigDecimal(0);//这里需要获取SANHQ数据
                String Sanexrate="";
               if(cacheManager.isExist(RedisConstantUtil.R8EXCHANGERATE)){
                   Sanexrate=(String)cacheManager.get(RedisConstantUtil.R8EXCHANGERATE);
                    logAfter(logger);
                }else{
                   Sanexrate =R8ExChangeController.getSanExrate().equals("")?"0":R8ExChangeController.getSanExrate();
                   try{
                       if(StringUtil.isNotEmpty(Sanexrate)){
                           cacheManager.set(RedisConstantUtil.R8EXCHANGERATE,Sanexrate,WlscConstants.WLSC_R8EXCHANGE_RATE_SECOND);
                       }
                       sanhq =new BigDecimal(Sanexrate);//这里需要获取SANHQ数据
                   }catch(Exception e){
                      String codeName = "SANEXRATE";
                      List<PageData> codeList =sysGenCodeService.findByGroupCode("SANEXRATE", Constant.VERSION_NO);
                      for(PageData code:codeList){
                          if(codeName.equals(code.get("code_name"))){
                              Sanexrate = code.get("code_value").toString();
                          }
                      }
                       sanhq =new BigDecimal(Sanexrate);
                   }
                }
                pd.put("sanhq",sanhq);

//                pd.put("shop_order_no", shopOrder.getString("order_no"));

                //商品成本价及供应商拿到的价格，无需获取费率
                String  rate = "";
                List<PageData> codeList =sysGenCodeService.findByGroupCode("LIMIT_RATE", Constant.VERSION_NO);
                for(PageData code:codeList){
                    if("EXCHANGE_RATE".equals(code.get("code_name"))){
                        rate = code.get("code_value").toString();
                    }
                }
                pd.put("rate", rate);
                boolean updateCheckStatus = shopOrderInfoService.confirmReceipt(pd, Constant.VERSION_NO);
                if(updateCheckStatus){
                    logger.info("------确认收货计算奖励-----------start---------------");
                    PageData consume = new PageData();
                    consume.put("order_no", pd.getString("order_no"));
                    consume.put("sanhq", pd.getString("sanhq"));
                    consume.put("shop_order_no", pd.getString("shop_order_no"));
                    if("1".equals(pd.getString("user_type"))){//普通会员消费奖励计算\
                        if(calBounsService.calBounsMallConsumePt(consume, Constant.VERSION_NO)){
                            logger.error("calBounsService.calBounsMallConsumePt fail !普通会员消费奖励计算接口，商城  失败！");
                        }
                    }else if("2".equals(pd.getString("user_type"))){
                        if(calBounsService.calBounsMallConsumeVip(consume, Constant.VERSION_NO)){
                            logger.error("calBounsService.calBounsMallConsumeVip fail !创业会员消费奖励计算接口 ,商城   失败！");
                        }
                    }
                    if(calBounsService.calBounsProviderSales(consume, Constant.VERSION_NO)){
                        logger.error("calBounsService.calBounsProviderSales fail !供应商销售业绩奖励计算接口 推荐奖励   失败！");
                    }
                    logger.info("------确认收货计算奖励-----------end---------------");
                    ar.setSuccess(true);
                    ar.setMessage("确认收货成功");
                    return ar;
                }else{
                    ar.setSuccess(false);
                    ar.setMessage("确认收货失败");
                    ar.setErrorCode(CodeConstant.UPDATE_FAIL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
        }
        return ar;
    }*/

//    @LoginVerify
    /*@RequestMapping(value = "/confirmReceipt", method = RequestMethod.POST)

    public AjaxResponse confirmReceipt(HttpServletRequest request) {
        AjaxResponse ar = new AjaxResponse();
        try {
            *//*String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);*//*
            PageData pd = new PageData();
            pd = this.getPageData();
//            pd.put("user_id", String.valueOf(user.get("id")));
            String order_no = pd.getString("order_no");
            if (StringUtil.isEmpty(order_no)) {
                ar.setSuccess(false);
                ar.setMessage("订单号不能为空");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            PageData shopOrder = shopOrderInfoService.getShopOrderAndUserInfo(pd);
            if (shopOrder == null) {
                ar.setSuccess(false);
                ar.setMessage("订单不存在");
                ar.setErrorCode(CodeConstant.ORDER_NO_EXISTS);
                return ar;
            }
            //获取三界行情
            BigDecimal sanhq = new BigDecimal(0);//这里需要获取SANHQ数据
            String Sanexrate = "";
            if (cacheManager.isExist(RedisConstantUtil.R8EXCHANGERATE)) {
                Sanexrate = (String) cacheManager.get(RedisConstantUtil.R8EXCHANGERATE);
            } else {
                Sanexrate = R8ExChangeController.getSanExrate().equals("") ? "0" : R8ExChangeController.getSanExrate();
                try {
                    sanhq = new BigDecimal(Sanexrate);//这里需要获取SANHQ数据
                    if (StringUtil.isNotEmpty(Sanexrate)) {
                        cacheManager.set(RedisConstantUtil.R8EXCHANGERATE, Sanexrate, WlscConstants.WLSC_R8EXCHANGE_RATE_SECOND);
                    }
                } catch (Exception e) {
                    String codeName = "SANEXRATE";
                    List<PageData> codeList = sysGenCodeService.findByGroupCode("SANEXRATE", Constant.VERSION_NO);
                    for (PageData code : codeList) {
                        if (codeName.equals(code.get("code_name"))) {
                            Sanexrate = code.get("code_value").toString();
                        }
                    }
                    sanhq = new BigDecimal(Sanexrate);
                }
            }
            pd.put("sanhq", sanhq);
            pd.put("user_id", String.valueOf(shopOrder.get("user_id")));
            pd.put("user_type", shopOrder.getString("user_type"));
            pd.put("wlbi_amnt", String.valueOf(shopOrder.get("order_amount")));//应付款金额
            pd.put("other_amnt", String.valueOf(shopOrder.get("order_amount")));//应付款金额
            pd.put("otherno", shopOrder.getString("order_no"));//业务订单号
            pd.put("operator", shopOrder.getString("mobile_phone"));
            pd.put("shop_order_no", shopOrder.getString("order_no"));
            pd.put("order_no", shopOrder.getString("order_no"));
            pd.put("order_id", String.valueOf(shopOrder.get("order_id")));
            boolean shopReceiptResult = shopOrderInfoService.shopReceipt(pd, Constant.VERSION_NO);
            if (shopReceiptResult) {
                logger.info("------确认收货计算奖励-----------start---------------");
                PageData consume = new PageData();
                consume.put("order_no", pd.getString("order_no"));
                consume.put("sanhq", String.valueOf(pd.get("sanhq")));
                consume.put("shop_order_no", pd.getString("shop_order_no"));
                if ("1".equals(pd.getString("user_type"))) {//普通会员消费奖励计算\
                    if (calBounsService.calBounsMallConsumePt(consume, Constant.VERSION_NO)) {
                        logger.error("calBounsService.calBounsMallConsumePt fail !普通会员消费奖励计算接口，商城  失败！");
                    }
                } else if ("2".equals(pd.getString("user_type"))) {
                    if (calBounsService.calBounsMallConsumeVip(consume, Constant.VERSION_NO)) {
                        logger.error("calBounsService.calBounsMallConsumeVip fail !创业会员消费奖励计算接口 ,商城   失败！");
                    }
                }
                if (calBounsService.calBounsProviderSales(consume, Constant.VERSION_NO)) {
                    logger.error("calBounsService.calBounsProviderSales fail !供应商销售业绩奖励计算接口 推荐奖励   失败！");
                }
                logger.info("------确认收货计算奖励-----------end---------------");
                ar.setMessage("操作成功！");
                ar.setSuccess(true);
                return ar;
            } else {
                ar.setMessage("操作失败");
                ar.setSuccess(false);
                ar.setErrorCode(CodeConstant.UPDATE_FAIL);
                return ar;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
        }
        return ar;
    }
*/
    //@LoginVerify
    @RequestMapping(value = "/deliverGoods", method = RequestMethod.GET)
    @ApiOperation(nickname = "deliverGoods", value = "物流确认发货", notes = "物流确认发货！！")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "shop_order_no", value = "订单号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "goods_id", value = "商品ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "logistics_no", value = "物流单号", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "logistics_name", value = "物流名称", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "CSESSIONID", value = "CSESSIONID", required = false, paramType = "query", dataType = "String"),
    })
    public AjaxResponse deliverGoods(HttpServletRequest request) {
        AjaxResponse ar = new AjaxResponse();
        try {
            /*String key = RequestUtils.getCookieValueByKey(CookieConstant.CSESSIONID, request, response);
            String userstr = SessionUtil.getAttibuteForUser(key);*/


            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            logger.info("sessionKey中用户信息------------>"+userstr);
            JSONObject user = JSONObject.parseObject(userstr);
            PageData pd = new PageData();
            pd = this.getPageData();
            if (StringUtil.isEmpty(pd.getString("shop_order_no"))) {
                ar.setSuccess(false);
                ar.setMessage("订单号不能为空");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if (StringUtil.isEmpty(pd.getString("goods_id"))) {
                ar.setSuccess(false);
                ar.setMessage("商品ID不能为空");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if (StringUtil.isEmpty(pd.getString("logistics_no"))) {
                ar.setSuccess(false);
                ar.setMessage("快递单号不能为空");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if (StringUtil.isEmpty(pd.getString("logistics_name"))) {
                ar.setSuccess(false);
                ar.setMessage("快递公司不能为空");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            PageData shopOrder = shopOrderGoodsService.getOrderGoods(pd, Constant.VERSION_NO);
            if (shopOrder == null) {
                ar.setMessage("商品订单关联表中不存在该条信息！");
                ar.setSuccess(false);
                ar.setErrorCode(CodeConstant.NO_EXISTS);
            }
            //pd.put("seeds",user.get("seeds"));
            pd.put("rec_id", shopOrder.get("rec_id"));
            pd.put("shop_order_id", shopOrder.get("shop_order_id"));
            pd.put("createtime",new Date());
            pd.put("logistics_code", pd.getString("logistics_no").toLowerCase());
            pd.put("state", "3");//发货
            pd.put("bussType","deliverGoods");
            boolean deliverGoods = shopOrderInfoService.deliverGoods(pd, Constant.VERSION_NO);
            if (deliverGoods) {
                ar.setSuccess(true);
                ar.setMessage("发货成功");
                return ar;
            }
            ar.setSuccess(false);
            ar.setMessage("发货失败");
            ar.setErrorCode(CodeConstant.UPDATE_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
        }
        return ar;
    }

    /**
     * @param request
     * @describe:立即支付
     * @author: zhangchunming
     * @date: 2016年11月9日下午9:59:50
     * @return: AjaxResponse
     */
    @LoginVerify
    @PostMapping("/payNow")
    @ApiOperation(nickname = "立即支付", value = "立即支付", notes = "立即支付！")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "CSESSIONID", value = "会话token", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "order_no", value = "订单号", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "order_amount", value = "付款金额", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse payNow(HttpServletRequest request) {
        Map<String, Object> data = new HashMap<String, Object>();
        AjaxResponse ar = new AjaxResponse();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            pd.put("user_id", String.valueOf(user.get("id")));
            pd.put("seeds", user.getString("seeds"));
            pd.put("user_type", String.valueOf(user.getString("user_type")));
            pd.put("operator", String.valueOf(user.getString("account")));
            /*if(StringUtil.isEmpty(pd.getString("order_id"))){
                ar.setSuccess(false);
                ar.setMessage("订单ID不能为空");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }*/
            if (StringUtil.isEmpty(pd.getString("order_no"))) {
                ar.setSuccess(false);
                ar.setMessage("订单号不能为空");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            /*if(StringUtil.isEmpty(pd.getString("order_amount"))){
                ar.setSuccess(false);
                ar.setMessage("付款金额不能为空");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }*/
//            PageData shopOrderInfo = shopOrderInfoService.selectById(Integer.valueOf(pd.getString("order_id")), Constant.VERSION_NO);
            PageData shopOrderInfo = shopOrderInfoService.getShopOrderByOrderNo(pd, Constant.VERSION_NO);
            if ("2".equals(shopOrderInfo.getString("order_status"))) {//已支付
                ar.setSuccess(false);
                ar.setMessage("已支付");
                ar.setErrorCode(CodeConstant.pay_finish);
                return ar;
            }
            if (StringUtil.isNotEmpty(shopOrderInfo.getString("trade_hash"))||"1".equals(shopOrderInfo.getString("is_lock"))) {//已支付
                ar.setSuccess(false);
                ar.setMessage("交易正在处理，请勿重复支付！");
                ar.setErrorCode(CodeConstant.pay_finish);
                return ar;
            }
            
            if ("1".equals(String.valueOf(shopOrderInfo.get("is_promote"))) && "4".equals(shopOrderInfo.getString("order_status"))) {//订单自动取消
                ar.setSuccess(false);
                ar.setMessage("未在规定时间内支付，订单自动取消！");
                ar.setErrorCode(CodeConstant.ERROR_CALCEL);
                return ar;
            } else if ("4".equals(shopOrderInfo.getString("order_status"))) {//订单自动取消
                ar.setSuccess(false);
                ar.setMessage("订单已取消！");
                ar.setErrorCode(CodeConstant.ERROR_CALCEL);
                return ar;
            }

            PageData userWallet = userWalletService.getWalletByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
            String future_currency = String.valueOf(userWallet.get("future_currency"));
            if (new BigDecimal(String.valueOf(shopOrderInfo.get("order_amount"))).compareTo(new BigDecimal(future_currency)) > 0) {
                ar.setSuccess(false);
                ar.setMessage("积分余额不足，请充值");
                ar.setErrorCode(CodeConstant.BALANCE_NOT_ENOUGH);
                return ar;
            }
            pd.put("order_no", shopOrderInfo.getString("order_no"));
            pd.put("order_id", String.valueOf(shopOrderInfo.get("order_id")));
            pd.put("order_amount", shopOrderInfo.get("order_amount"));
            pd.put("mobile_phone", user.getString("mobile_phone"));

            pd.put("shop_order_no", shopOrderInfo.getString("order_no"));

            //商品表里有供应商有专门价格无需查询兑换费率
           /* String  rate = "";
            List<PageData> codeList =sysGenCodeService.findByGroupCode("LIMIT_RATE", Constant.VERSION_NO);
            for(PageData code:codeList){
                if("EXCHANGE_RATE".equals(code.get("code_name"))){
                    rate = code.get("code_value").toString();
                }
            }
            if(StringUtil.isEmpty(rate)){

            }
            pd.put("rate", rate);*/
            //锁定订单
            boolean lockOrderByOrderNo = shopOrderInfoService.lockOrderByOrderNo(pd);
            logger.info("支付订单锁定结果lockOrderByOrderNo："+lockOrderByOrderNo);
            
            boolean payNow = shopOrderInfoService.payNow(pd, Constant.VERSION_NO);
            if (payNow) {
                ar.setSuccess(true);
                ar.setMessage("交易处理中...请前往账单查看兑换结果");
                data.put("order_no", pd.getString("order_no"));
                data.put("order_amount", shopOrderInfo.get("order_amount"));
                data.put("pay_time", DateUtil.getCurrDateTime());
                ar.setData(data);
                return ar;
            }
            ar.setSuccess(false);
            ar.setMessage("支付失败");
            ar.setErrorCode(CodeConstant.UPDATE_FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            //解锁订单
            try {
                boolean unLockOrderByOrderNo = shopOrderInfoService.unLockOrderByOrderNo(pd);
                logger.info("支付订单解锁结果unLockOrderByOrderNo："+unLockOrderByOrderNo);
            } catch (Exception e1) {
                e1.printStackTrace();
                ar.setSuccess(false);
                ar.setMessage("网络繁忙，请稍候重试！");
                ar.setErrorCode(CodeConstant.SYS_ERROR);
            }
        }
        return ar;
    }

    /**
     * @param request
     * @describe:根据订单号查询订单信息
     * @author: zhangchunming
     * @date: 2016年11月14日下午3:39:04
     * @return: AjaxResponse
     */
    @LoginVerify
    @RequestMapping(value = "/getShopOrderByOrderNo", method = RequestMethod.POST)

    public AjaxResponse getShopOrderByOrderNo(HttpServletRequest request) {
        Map<String, Object> data = new HashMap<String, Object>();
        AjaxResponse ar = new AjaxResponse();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            pd.put("user_id", String.valueOf(user.get("id")));
            if (StringUtil.isEmpty(pd.getString("order_no"))) {
                ar.setSuccess(false);
                ar.setMessage("请输入订单号");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            PageData shopOrderInfo = shopOrderInfoService.getShopOrderByOrderNo(pd, Constant.VERSION_NO);
            data.put("shopOrderInfo", shopOrderInfo);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
        }
        return ar;
    }

    /**
     * @param request
     * @describe:商品退货关闭订单
     * @author: zhangchunming
     * @date: 2016年12月26日下午4:06:47
     * @return: AjaxResponse
     */
    /*@RequestMapping(value = "/refundGoods", method = RequestMethod.POST)
    
    public AjaxResponse refundGoods(HttpServletRequest request) {
        Map<String, Object> data = new HashMap<String, Object>();
        AjaxResponse ar = new AjaxResponse();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if (StringUtil.isEmpty(pd.getString("rec_id"))) {
                ar.setSuccess(false);
                ar.setMessage("请输入订单商品关联表ID！");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if (StringUtil.isEmpty(pd.getString("state"))) {
                ar.setSuccess(false);
                ar.setMessage("请输入订单状态！");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if (!"2".equals(pd.getString("state")) || !"3".equals(pd.getString("state")) || !"10".equals(pd.getString("state")) || !"11".equals(pd.getString("state")) || !"12".equals(pd.getString("state")) || !"13".equals(pd.getString("state"))) {
                ar.setSuccess(false);
                ar.setMessage("订单状态输入有误！");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            boolean refundResult = false;
            //申请或拒绝退款退货
            if ("2".equals(pd.getString("state")) || "3".equals(pd.getString("state")) || "10".equals(pd.getString("state")) || "12".equals(pd.getString("state"))) {
                refundResult = shopOrderInfoService.applyAndRefuseRefundGoods(pd);
                if (refundResult) {
                    ar.setSuccess(true);
                    ar.setMessage("操作成功！");
                    return ar;
                } else {
                    ar.setErrorCode(CodeConstant.UPDATE_FAIL);
                    ar.setSuccess(false);
                    ar.setMessage("操作失败！");
                    return ar;
                }
                *//*if("10".equals(pd.getString("state"))){//一键退款
                    if(refundResult){
                        ar.setSuccess(true);
                        ar.setMessage("操作成功！"); 
                        return ar;
                    }else{
                        ar.setErrorCode(CodeConstant.UPDATE_FAIL);
                        ar.setSuccess(false);
                        ar.setMessage("操作失败！"); 
                        return ar;
                    }
                }else if("12".equals(pd.getString("state"))){//一键退货
                    if(refundResult){
                        ar.setSuccess(true);
                        ar.setMessage("操作成功！"); 
                        return ar;
                    }else{
                        ar.setErrorCode(CodeConstant.UPDATE_FAIL);
                        ar.setSuccess(false);
                        ar.setMessage("操作失败！"); 
                        return ar;
                    }
                }*//*
            }
            //同意退款退货
            if ("11".equals(pd.getString("state")) || "13".equals(pd.getString("state"))) {
                refundResult = shopOrderInfoService.refundGoods(pd);
                if ("11".equals(pd.getString("state"))) {//一键退款
                    if (refundResult) {
                        ar.setSuccess(true);
                        ar.setMessage("退款成功！");
                        return ar;
                    } else {
                        ar.setErrorCode(CodeConstant.UPDATE_FAIL);
                        ar.setSuccess(false);
                        ar.setMessage("退款失败！");
                        return ar;
                    }
                } else if ("13".equals(pd.getString("state"))) {//一键退货
                    if (refundResult) {
                        ar.setSuccess(true);
                        ar.setMessage("退货成功！");
                        return ar;
                    } else {
                        ar.setErrorCode(CodeConstant.UPDATE_FAIL);
                        ar.setSuccess(false);
                        ar.setMessage("退货失败！");
                        return ar;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
        }
        return ar;
    }*/

    public static void main(String[] args) {
        System.out.println(Integer.valueOf("2147483647"));
    }
}
