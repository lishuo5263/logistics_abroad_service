package com.ecochain.ledger.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.dao.DaoSupport;
import com.ecochain.ledger.mapper.*;
import com.ecochain.ledger.model.*;
import com.ecochain.ledger.service.*;
import com.ecochain.ledger.util.Base64;
import com.ecochain.ledger.util.DateUtil;
import com.ecochain.ledger.util.HttpUtil;
import com.ecochain.ledger.util.StringUtil;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

@Component("shopOrderInfoService")
public class ShopOrderInfoServiceImpl implements ShopOrderInfoService {
    private final Logger logger = LoggerFactory.getLogger(ShopOrderInfoServiceImpl.class);
    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Autowired
    private ShopCartMapper shopCartMapper;

    @Autowired
    private ShopGoodsMapper shopGoodsMapper;

    @Autowired
    private ShopOrderInfoMapper shopOrderInfoMapper;

    @Autowired
    private ShopOrderGoodsMapper shopOrderGoodsMapper;
    @Autowired
    private BlockDataHashMapper blockDataHashMapper;
    @Autowired
    private UsersDetailsMapper usersDetailsMapper;
    @Autowired
    private AccDetailService accDetailService;
    @Autowired
    private UserWalletService userWalletService;
    @Autowired
    private ShopOrderLogisticsService shopOrderLogisticsService;
    @Autowired
    private ShopOrderGoodsService shopOrderGoodsService;
    @Autowired
    private UserAddressService userAddressService;
    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private QklLibService qklLibService;

    @Override
    public boolean updateOrderRefundStatus(String orderNo) {
        boolean res1 = shopOrderInfoMapper.updateOrderGoodsRefundStatus(orderNo);
        boolean res2 = shopOrderInfoMapper.updateOrderRefundStatus(orderNo);
        return res1 && res2;
    }

    /**
     * @return
     * @author lishuo
     * @describe 更新sotre||shop 订单状态
     */
    @Override
    public boolean updateCancleState(String type, String order_no, String isHot, String isPromote) {
        boolean res1 = false;
        boolean res2 = false;
        if ("shop".equals(type)) {
            if ("1".equals(isPromote)) {
                boolean res3 = false;
                /*res1= shopOrderInfoMapper.updateShopOrderInfoCancleStatus(order_no);
                res2=shopOrderInfoMapper.updateShopOrderGoodsCancleStatus(order_no);*/
                res3 = shopOrderInfoMapper.updateShopGoodsGoods(order_no);
                res2 = shopOrderInfoMapper.deleteShopOrderGoods(order_no);
                res1 = shopOrderInfoMapper.deleteShopOrderInfo(order_no);
                return res1 && res2 && res3;
            } else {
                res1 = shopOrderInfoMapper.updateShopOrderInfoCancleStatus(order_no);
                res2 = shopOrderInfoMapper.updateShopOrderGoodsCancleStatus(order_no);
                return res1 && res2;
            }
        } /*else if ("store".equals(type)) {
            if ("1".equals(isHot)) {
                res2 = storeOrderInfoMapper.updateStoreGoodsStock(order_no);
                res1 = storeOrderInfoMapper.deleteStoreOrderInfo(order_no);
                return res1 && res2;
            } else {
                return storeOrderInfoMapper.updateStoreOrderInfoCancleStatus(order_no);
            }
        }*/
        return false;
    }

    /**
     * @return
     * @author lishuo
     * @describe 查询sotre||shop 订单状态
     */
    @Override
    public Map getOrderInfo(String type, String orderNo) {
        Map<String, Object> map = new HashMap<>();
        if ("shop".equals(type)) {
            map = this.shopOrderInfoMapper.getOrderInfo(orderNo);
        }
        /*if ("store".equals(type)) {
            map = this.storeOrderInfoMapper.getOrderInfo(orderNo);
        }*/
        return map;
    }

    /**
     * @return
     * @author lishuo
     * @describe 获取秒杀下单不支付的订单
     */
    @Override
    public List<Map<String, Object>> getUnPayOrder() {
        return shopOrderInfoMapper.getUnPayOrder();
    }

    /**
     * @param orderNo
     * @return
     * @author lishuo
     * @describe 商城订单支付回调更新订单状态
     */
    @Override
    public boolean shopOrderPayCallBack(String orderNo) {
        if (StringUtils.isNotEmpty(orderNo)) {
            return shopOrderInfoMapper.shopOrderPayCallBack(orderNo) > 0 ? true : false;
        } else {
            return false;
        }
    }

    /**
     * @return
     * @author lishuo
     * @describe 查询用户已秒杀的订单数量
     */
    @Override
    public int querySecKillCount(Integer userId) {
        return this.shopOrderInfoMapper.querySecKillCount(userId);
    }

    /**
     * 根据goodsId查询商品SkuValue
     *
     * @return
     * @author: lishuo
     */
    @Override
    public String getSkuGoodsInfoByorderNo(String orderNo) {
        return this.shopOrderInfoMapper.getSkuGoodsInfoByorderNo(orderNo);
    }

    /**
     * @return
     * @author lishuo
     * @describe 商城定时更新订单状态&回滚超时不支付的库存
     */
    @Override
    public boolean deleteShopOrderInfo(String type, String orderNo, String skuValue) {
        boolean res1 = false;
        boolean res2 = false;
        boolean res3 = false;
        if (type.equals("SkuGoods") && StringUtils.isNotEmpty(skuValue)) {
            res1 = shopOrderInfoMapper.deleteShopOrderInfo(orderNo);
            res2 = shopOrderInfoMapper.deleteShopOrderGoods(orderNo);
            res3 = shopGoodsMapper.addSkuGoodStockByType(skuValue) > 0;
            return res1 && res2 && res3;
        } else {
            res1 = shopOrderInfoMapper.deleteShopOrderInfo(orderNo);
            res2 = shopOrderInfoMapper.deleteShopOrderGoods(orderNo);
            return res1 && res2;
        }
    }


    /**
     * @param shopOrderGoods
     * @return
     * @author lishuo
     * @describe 商城订单实现
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public List<Map<String, Object>> insertShopOrder(List<ShopOrderGoods> shopOrderGoods) throws Exception {
        // 先查询用户类型再去shop_goods 取最终商品价格，shopOrderGoods记录当时购买价格，ShopOrderInfo order_amount 计算总订单价格
        logger.info("------------------------------>传递给区块链data值为："+shopOrderGoods.get(0).getData());
        ShopGoods shopGoods = null;
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        BigDecimal totalMoney = BigDecimal.ZERO;
        if (shopOrderGoods.get(0).getGoodsId() == null) {
            map.put("ErrorInsert", "订单生成失败，goodsId参数为空！");
            result.add(map);
            return result;
        }
        Integer usersType = this.usersDetailsMapper.selectUserType(shopOrderGoods.get(0).getUserId());
        if (usersType == 0 || usersType == null) { //userType  1-普通会员 2-创业会员
            map.put("ErrorInsertByUsersType", "订单生成失败，usersType参数为空无法确定购买价格！");
            result.add(map);
            return result;
        }
        if (usersType > 2) { //2以上类型的用户不可下单
            map.put("ErrorInsertNotAllowByUsersType", "订单生成失败，用户类型只能为普通会员和创业会员，商品无法购买！");
            result.add(map);
            return result;
        }
        /*if(Integer.valueOf(shopOrderGoods.get(0).getIsPromote()) ==1){
            if(usersType > 1){ //1以上类型的用户不可下单
                map.put("ErrorInsertNotAllowByUsersType","抱歉，当前账户没有购买此商品的权限，只有普通才能购买哦！");
                result.add(map);
                return result;
            }
        }

        if(Integer.valueOf(shopOrderGoods.get(0).getIsPromote()) ==0){
            if(usersType > 2){ //2以上类型的用户不可下单
                map.put("ErrorInsertNotAllowByUsersType","抱歉，当前账户没有购买此商品的权限，只有普通会员/创业会员才能购买哦！");
                result.add(map);
                return result;q
            }
        }*/

       /* logger.info("====================测试代码========start================");
        String jsonStr = HttpUtil.sendPostData("http://192.168.200.81:8332/get_new_key", "");
        JSONObject keyJsonObj = JSONObject.parseObject(jsonStr);
        PageData keyPd = new PageData();
        keyPd.put("data",Base64.getBase64((shopOrderGoods.get(0).getData())));
        keyPd.put("publicKey",keyJsonObj.getJSONObject("result").getString("publicKey"));
        keyPd.put("privateKey",keyJsonObj.getJSONObject("result").getString("privateKey"));
        System.out.println("keyPd value is ------------->"+JSON.toJSONString(keyPd));
        //2. 获取公钥签名
        String signJsonObjStr =HttpUtil.sendPostData("http://192.168.200.81:8332/send_data_for_sign", JSON.toJSONString(keyPd));
        JSONObject signJsonObj = JSONObject.parseObject(signJsonObjStr);
        Map<String, Object> paramentMap =new HashMap<String, Object>();
        paramentMap.put("publickey",keyJsonObj.getJSONObject("result").getString("publicKey"));
        paramentMap.put("data",Base64.getBase64((shopOrderGoods.get(0).getData())));
        paramentMap.put("sign",signJsonObj.getString("result"));
        String result1 = HttpUtil.sendPostData("http://192.168.200.81:8332/send_data_to_sys", JSON.toJSONString(paramentMap));
        JSONObject json = JSON.parseObject(result1);
        if(StringUtil.isNotEmpty(json.getString("result"))){
            shopOrderGoods.get(0).setTradeHash(json.getString("result"));
        }
        logger.info("====================测试代码=======end=================");*/

       /* String tradeResult=qklLibService.sendDataToSys(shopOrderGoods.get(0).getTradeHash(), Base64.getBase64(shopOrderGoods.get(0).getData()));//此时TradeHash值为seeds
        JSONObject json = JSON.parseObject(tradeResult);
        if(StringUtil.isNotEmpty(json.getString("result"))&&!json.getString("result").contains("failure")){
            shopOrderGoods.get(0).setTradeHash(json.getString("result"));
        }else{
            map.put("ErrorInsertByBlockChain","订单生成失败，调用区块链接口发生错误！");
            result.add(map);
            return result;
        }*/
        if ("0".equals(shopOrderGoods.get(0).getIsPromote())) { //商城普通订单
            for (int i = 0; i < shopOrderGoods.size(); i++) {
                if (StringUtils.isNotEmpty(String.valueOf(shopOrderGoods.get(i).getGoodsId()))) {
                    shopGoods = this.shopGoodsMapper.selectByPrimaryKey(shopOrderGoods.get(i).getGoodsId());
                    if (shopGoods != null) {
                        shopOrderGoods.get(i).setPayPrice(shopGoods.getGoodsPrice());
                        shopOrderGoods.get(0).setPayName("三界石");
                    }else {
                        map.put("ErrorCreateOrderByGoodsId", "订单生成失败，此款商品在数据库中不存在！");
                        result.add(map);
                        return result;
                    }
                /*if(usersType==1){   //暂时不判断会员类型统一使用 goodsPrice
                    shopOrderGoods.get(i).setPayPrice(shopGoods.getGoodsPrice());
                }else if(usersType==2){
                    shopOrderGoods.get(i).setPayPrice(shopGoods.getVipPrice());
                }*/
                }
                list.add(String.valueOf(shopGoods.getGoodsId()));  // 组装用于删除购物车的订单号
                list2.add(shopOrderGoods.get(i).getSkuValue());  // 组装用于删除购物车sku信息的订单
                shopOrderGoods.get(i).setShopOrderNo(shopOrderGoods.get(0).getOrderNo());
                shopOrderGoods.get(i).setShopOrderId("222");
                shopOrderGoods.get(i).setCatId(String.valueOf(shopGoods.getCatId()));
                shopOrderGoods.get(i).setUserId(shopOrderGoods.get(0).getUserId());
                shopOrderGoods.get(i).setGoodsSn(shopGoods.getGoodsSn());
                shopOrderGoods.get(i).setGoodsImg(shopGoods.getGoodsImg());
                shopOrderGoods.get(i).setGoodsName(shopGoods.getGoodsName());
                shopOrderGoods.get(i).setSupplierId(Integer.valueOf(shopGoods.getSupplierId()));
                totalMoney = totalMoney.add(shopOrderGoods.get(i).getPayPrice().multiply(new BigDecimal(shopOrderGoods.get(i).getGoodsNumber())));
            }
        } else if ("1".equals(shopOrderGoods.get(0).getIsPromote())) {  //秒杀订单
            if (StringUtils.isNotEmpty(String.valueOf(shopOrderGoods.get(0).getGoodsId()))) {
                shopGoods = this.shopGoodsMapper.selectByPrimaryKey2(shopOrderGoods.get(0).getGoodsId());
                if (shopGoods != null) {
                    shopOrderGoods.get(0).setPayPrice(shopGoods.getGoodsPrice());
                    shopOrderGoods.get(0).setPayName("三界石");
                }else {
                    map.put("ErrorCreateOrderByGoodsId", "订单生成失败，此款商品在数据库中不存在！");
                    result.add(map);
                    return result;
                }
                /*if(usersType==1){   //暂时不判断会员类型统一使用 goodsPrice
                    shopOrderGoods.get(i).setPayPrice(shopGoods.getGoodsPrice());
                }else if(usersType==2){
                    shopOrderGoods.get(i).setPayPrice(shopGoods.getVipPrice());
                }*/
            }
            list.add(String.valueOf(shopGoods.getGoodsId()));  // 组装用于删除购物车的订单号
            list2.add(shopOrderGoods.get(0).getSkuValue());  // 组装用于删除购物车sku信息的订单
            shopOrderGoods.get(0).setShopOrderNo(shopOrderGoods.get(0).getOrderNo());
            shopOrderGoods.get(0).setShopOrderId("222");
            shopOrderGoods.get(0).setCatId(String.valueOf(shopGoods.getCatId()));
            shopOrderGoods.get(0).setUserId(shopOrderGoods.get(0).getUserId());
            shopOrderGoods.get(0).setGoodsSn(shopGoods.getGoodsSn());
            shopOrderGoods.get(0).setGoodsImg(shopGoods.getGoodsImg());
            shopOrderGoods.get(0).setGoodsName(shopGoods.getGoodsName());
            shopOrderGoods.get(0).setSupplierId(Integer.valueOf(shopGoods.getSupplierId()));
            totalMoney = totalMoney.add(shopOrderGoods.get(0).getPayPrice().multiply(new BigDecimal(shopOrderGoods.get(0).getGoodsNumber())));
        } else if ("2".equals(shopOrderGoods.get(0).getIsPromote())) { //与交易所购买图书交易
            if (StringUtils.isNotEmpty(String.valueOf(shopOrderGoods.get(0).getGoodsId()))) {
                shopGoods = this.shopGoodsMapper.selectByPrimaryKey(shopOrderGoods.get(0).getGoodsId());
                if (shopGoods != null) {
                    if (shopOrderGoods.get(0).getPayPrice().compareTo(shopGoods.getGoodsPrice().multiply(new BigDecimal(shopOrderGoods.get(0).getGoodsNumber())))==0 ) {
                        shopOrderGoods.get(0).setPayPrice(shopGoods.getGoodsPrice());
                        shopOrderGoods.get(0).setPayName("人民币");
                    } else {
                        map.put("ErrorCreateOrderByPayPrice", "订单生成失败，商品价格与数据库中不相符！");
                        result.add(map);
                        return result;
                    }
                } else {
                    map.put("ErrorCreateOrderByGoodsId", "订单生成失败，此款商品在数据库中不存在！");
                    result.add(map);
                    return result;
                }
            }
            list.add(String.valueOf(shopGoods.getGoodsId()));  // 组装用于删除购物车的订单号
            list2.add(shopOrderGoods.get(0).getSkuValue());  // 组装用于删除购物车sku信息的订单
            shopOrderGoods.get(0).setShopOrderNo(shopOrderGoods.get(0).getOrderNo());
            shopOrderGoods.get(0).setShopOrderId("222");
            shopOrderGoods.get(0).setCatId(String.valueOf(shopGoods.getCatId()));
            shopOrderGoods.get(0).setUserId(shopOrderGoods.get(0).getUserId());
            shopOrderGoods.get(0).setGoodsSn(shopGoods.getGoodsSn());
            shopOrderGoods.get(0).setGoodsImg(shopGoods.getGoodsImg());
            shopOrderGoods.get(0).setGoodsName(shopGoods.getGoodsName());
            shopOrderGoods.get(0).setSupplierId(Integer.valueOf(shopGoods.getSupplierId()));
            totalMoney = totalMoney.add(shopOrderGoods.get(0).getPayPrice().multiply(new BigDecimal(shopOrderGoods.get(0).getGoodsNumber())));
        }
        map.put("list", list);
        map.put("list2", list2);
        map.put("userId", shopOrderGoods.get(0).getUserId());
        this.shopOrderGoodsMapper.insert(shopOrderGoods);//新增商品信息
        shopOrderGoods.get(0).setGoodsAmount(totalMoney);
        shopOrderGoods.get(0).setOrderAmount(totalMoney);
        PageData pd = userAddressService.getOneAddressById(String.valueOf(shopOrderGoods.get(0).getAddressId()), "1.00");
        shopOrderGoods.get(0).setConsignee(pd.getString("consignee"));
        shopOrderGoods.get(0).setZipCode(pd.getString("zipcode"));
        shopOrderGoods.get(0).setProvince(pd.getString("province"));
        shopOrderGoods.get(0).setAddress(pd.getString("address"));
        shopOrderGoods.get(0).setMobile(pd.getString("mobile"));
        shopOrderGoods.get(0).setCity(pd.getString("city"));
        shopOrderGoods.get(0).setArea(pd.getString("area"));
        this.shopOrderInfoMapper.insertShopOrder(shopOrderGoods.get(0));//创建订单信息
        updateOrderIdByOrderNo(shopOrderGoods.get(0).getOrderNo(), "");
        //this.shopCartMapper.batchDeleteMyCart(map);//批量删除生成订单的购物车记录
        map.remove("list");
        map.remove("list2");
        map.put("orderNum", shopOrderGoods.get(0).getOrderNo());
        map.put("orderAmount", shopOrderGoods.get(0).getOrderAmount());
        result.add(map);
        return result;
    }

    @Override
    public PageData listPageShopOrder(Page page, String versionNo) throws Exception {
        List<PageData> list = (List<PageData>) dao.findForList("com.ecochain.ledger.mapper.ShopOrderInfoMapper.listPageShopOrder", page);
        PageData tpd = new PageData();
        tpd.put("list", list);
        tpd.put("page", page);
        return tpd;
    }
    

    @Override
    public List<PageData> listShopOrderByPage(PageData pd) throws Exception{
        if (pd.getPage() != null && pd.getRows() != null) {
            PageHelper.startPage(pd.getPage(), pd.getRows());
        }
        List<PageData> list = (List<PageData>)dao.findForList("com.ecochain.ledger.mapper.ShopOrderInfoMapper.listShopOrderByPage", pd);
        return list;
    }

    /* @Override
     public PageData getShopOrderNumByStatus(Integer user_id, String versionNo) throws Exception {
         return (PageData)dao.findForObject("com.ecochain.ledger.mapper.ShopOrderInfoMapper.getShopOrderStatus", user_id);
     }*/
    @Override
    public Integer getShopOrderTotalNum(Integer user_id, String versionNo) throws Exception {
        return (Integer) dao.findForObject("com.ecochain.ledger.mapper.ShopOrderInfoMapper.getShopOrderTotalNum", user_id);
    }

    @Override
    public PageData selectById(Integer order_id, String versionNo) throws Exception {
        return (PageData) dao.findForObject("com.ecochain.ledger.mapper.ShopOrderInfoMapper.selectById", order_id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean confirmReceipt(PageData pd, String versionNo) throws Exception {
        logger.info("--------确认收货更新订单状态、物流状态---------start---------");
        //扣除账户冻结余额
        PageData userWallet = new PageData();
        userWallet.put("froze_wlbi_amnt", pd.getString("other_amnt"));
        userWallet.put("user_id", pd.get("user_id"));
        boolean userWalletResult = userWalletService.updateSub(userWallet, versionNo);
        logger.info("-------商城确认收货-------扣除客户冻结三界通userWalletResult：" + userWalletResult);
        /*boolean addMoneyToSupplier = false;
        if(userWalletResult){
          //供应商加钱
            PageData userWallet1  = new PageData();
            userWallet1.put("shop_order_no", pd.getString("shop_order_no"));
//            userWallet1.put("rate", pd.getString("rate"));
            addMoneyToSupplier = userWalletService.addMoneyToSupplier(userWallet1, versionNo);
            logger.info("------确认收货-----给供应商增加人民币-----结果addMoneyToSupplier："+addMoneyToSupplier);
            if(!addMoneyToSupplier){
                logger.error("------商城确认收货-------给供应商增加人民币 失败-----------");
            }
        }*/
        if (userWalletResult) {
            //更新订单状态
            PageData orderInfo = new PageData();
            orderInfo.put("order_id", String.valueOf(pd.get("order_id")));
           /* orderInfo.put("user_id", pd.get("user_id"));
            orderInfo.put("state", "6");//完成交易*/
//            orderInfo.put("order_status", "3");
            boolean orderResult = updateOrderConflag(orderInfo, versionNo);
            logger.info("--------确认收货更新订单状态、物流状态--------orderResult总结果：" + orderResult);
            /*boolean orderResult = (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.updateOrderStatus", orderInfo)>0;
            boolean orderGoodsResult = (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.updateOrderGoodsStatus", orderInfo)>0;
            logger.info("--------确认收货更新订单状态、物流状态---------(orderResult&&orderGoodsResult)结果："+(orderResult&&orderGoodsResult));*/
            /*boolean accDetailResult = false;
            if((orderResult&&userWalletResult)){
                PageData accDetail = new PageData();
                accDetail.put("user_id", pd.get("user_id"));
                accDetail.put("acc_no", "05");
                accDetail.put("wlbi_amnt", String.valueOf(pd.get("wlbi_amnt")));
                accDetail.put("user_type", pd.getString("user_type"));
                accDetail.put("caldate", DateUtil.getCurrDateTime());
                accDetail.put("cntflag", "1");
                accDetail.put("status", "4");
                accDetail.put("otherno", pd.getString("otherno"));
                accDetail.put("other_amnt", pd.getString("other_amnt"));
                accDetail.put("other_source", "商城兑换");
                accDetail.put("operator", pd.getString("operator"));
                accDetailResult = accDetailService.insertSelective(accDetail, Constant.VERSION_NO);
                logger.info("--------确认收货插用户入账户流水---------accDetailResult结果："+accDetailResult);
                //商城确认消费插入供应商账户流水
                boolean addSupplierSalesAchievement = accDetailService.addSupplierSalesAchievement(pd.getString("shop_order_no"), Constant.VERSION_NO);
                logger.info("--------确认收货插入供应商账户流水---------addSupplierSalesAchievement结果："+addSupplierSalesAchievement);
            }
            logger.info("--------确认收货更新订单状态、物流状态---------end(accDetailResult&&orderResult)总结果："+(accDetailResult&&orderResult));*/
            //商城确认消费插入供应商账户流水
            boolean addSupplierSalesAchievement = accDetailService.addSupplierSalesAchievement(pd.getString("shop_order_no"), Constant.VERSION_NO);
            logger.info("--------确认收货插入供应商账户流水---------addSupplierSalesAchievement结果：" + addSupplierSalesAchievement);
            logger.info("--------确认收货更新订单状态、物流状态---------end---------");
            return orderResult;
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deliverGoods(PageData pd, String versionNo) throws Exception {
        /*String tradeResult=qklLibService.sendDataToSys(pd.getString("seeds"), Base64.getBase64(JSONObject.toJSON(pd.toString()).toString()));
        JSONObject json = JSON.parseObject(tradeResult);
        if(StringUtil.isNotEmpty(json.getString("result"))&&!json.getString("result").contains("failure")){
            pd.put("logistics_hash",json.getString("result"));
        }*/
        logger.info("====================测试代码========start================");
        String jsonStr = HttpUtil.sendPostData("http://192.168.200.81:8332/get_new_key", "");
        JSONObject keyJsonObj = JSONObject.parseObject(jsonStr);
        PageData keyPd = new PageData();
        keyPd.put("data",Base64.getBase64((pd.toString())));
        keyPd.put("publicKey",keyJsonObj.getJSONObject("result").getString("publicKey"));
        keyPd.put("privateKey",keyJsonObj.getJSONObject("result").getString("privateKey"));
        System.out.println("keyPd value is ------------->"+JSON.toJSONString(keyPd));
        //2. 获取公钥签名
        String signJsonObjStr =HttpUtil.sendPostData("http://192.168.200.81:8332/send_data_for_sign", JSON.toJSONString(keyPd));
        JSONObject signJsonObj = JSONObject.parseObject(signJsonObjStr);
        Map<String, Object> paramentMap =new HashMap<String, Object>();
        paramentMap.put("publickey",keyJsonObj.getJSONObject("result").getString("publicKey"));
        paramentMap.put("data",Base64.getBase64((pd.toString())));
        paramentMap.put("sign",signJsonObj.getString("result"));
        String result1 = HttpUtil.sendPostData("http://192.168.200.81:8332/send_data_to_sys", JSON.toJSONString(paramentMap));
        JSONObject json = JSON.parseObject(result1);
        if(StringUtil.isNotEmpty(json.getString("result"))){
            pd.put("logistics_hash",json.getString("result"));
        }
        logger.info("====================测试代码=======end=================");
        BlockDataHash blockDataHash =new BlockDataHash();
        blockDataHash.setBussType("deliverGoods");
        blockDataHash.setDataHash( json.getString("result"));
        blockDataHash.setBlockCreateTime(new Date());
        this.blockDataHashMapper.insert(blockDataHash);
        //添加物流信息
        shopOrderLogisticsService.insertSelective(pd, Constant.VERSION_NO);
        //修改订单商品关联表信息（添加物流单号及修改发货状态）
        shopOrderGoodsService.updateLogistics(pd, Constant.VERSION_NO);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean payNow(PageData pd, String versionNo) throws Exception {
        
        logger.info("-------------商城支付-----------start------------");
        pd.put("bussTyte", "payNow");//添加业务类型
        //从账户余额扣钱到冻结余额中
        if(userWalletService.payNowBySJT(pd, Constant.VERSION_NO)){
            /*//修改订单状态为已支付
            PageData shopOrder = new PageData();
            shopOrder.put("user_id", pd.get("user_id"));
            shopOrder.put("order_id", pd.getString("order_id"));
            shopOrder.put("order_status", "2");
            shopOrder.put("pay_time", DateUtil.getCurrDateTime());
            if(!updateShopOrderStatus(shopOrder, versionNo)){
                logger.error("--------商城支付-------updateShopOrderStatus------更新商城订单状态  失败");
            }
            //修改订单商品关联表状态为已支付
            PageData shopOrderGoods = new PageData();
            shopOrderGoods.put("user_id", pd.get("user_id"));
            shopOrderGoods.put("shop_order_id", pd.getString("order_id"));
            shopOrderGoods.put("state", "2");
            if(!shopOrderGoodsService.updateOrderGoodsStatus(shopOrderGoods, versionNo)){
                logger.error("--------商城支付-------shopOrderGoodsService.updateOrderGoodsStatus------更新商城订单商品关联表状态  失败");
            }*/
            
            PageData accDetail = new PageData();
            accDetail.put("user_id", pd.get("user_id"));
            accDetail.put("user_type", pd.getString("user_type"));
            accDetail.put("acc_no", "05");
            pd.put("acc_no", "05");//进区块链
            accDetail.put("wlbi_amnt", String.valueOf(pd.get("order_amount")));
            accDetail.put("future_currency", String.valueOf(pd.get("order_amount")));//区块链保存数据用
            accDetail.put("user_type", pd.getString("user_type"));
            /*accDetail.put("caldate", DateUtil.getCurrDateTime());
            accDetail.put("cntflag", "1");
            accDetail.put("status", "4");*/
            accDetail.put("status", "5");//5-审核中，6-成功，7失败
            pd.put("status", "5");//进区块链
            accDetail.put("otherno", pd.getString("order_no"));
            accDetail.put("other_amnt", String.valueOf(pd.get("order_amount")));
            accDetail.put("other_source", "商城兑换");
            pd.put("other_source", "商城兑换");//进区块链
            accDetail.put("operator", pd.getString("operator"));
            String good_name = shopOrderGoodsService.getOneGoodsNameByOrderNo(pd.getString("shop_order_no"));
            accDetail.put("remark1", good_name);
            pd.put("remark1", good_name);//进区块链
            accDetail.put("create_time", DateUtil.getCurrDateTime());
            pd.put("create_time", DateUtil.getCurrDateTime());//进区块链
            pd.put("order_status", "2");//进区块链
            
            
            /*logger.info("====================生产掉动态库代码========start================");
            String seedsStr = pd.getString("seeds");
            logger.info("seeds="+seedsStr);
            String hash = qklLibService.sendDataToSys(seedsStr, accDetail);
            accDetail.put("hash", hash); 
            pd.put("trade_hash", hash); 
            logger.info("====================生产掉动态库代码=======end=================");*/
            
            logger.info("====================测试代码========start================");
            String jsonStr = HttpUtil.sendPostData("http://192.168.200.81:8332/get_new_key", "");
            JSONObject keyJsonObj = JSONObject.parseObject(jsonStr);
            PageData keyPd = new PageData();
            keyPd.put("data",Base64.getBase64((JSON.toJSONString(pd))));
            keyPd.put("publicKey",keyJsonObj.getJSONObject("result").getString("publicKey"));
            keyPd.put("privateKey",keyJsonObj.getJSONObject("result").getString("privateKey"));
            System.out.println("keyPd value is ------------->"+JSON.toJSONString(keyPd));
            //2. 获取公钥签名
            String signJsonObjStr =HttpUtil.sendPostData("http://192.168.200.81:8332/send_data_for_sign",JSON.toJSONString(keyPd));
            JSONObject signJsonObj = JSONObject.parseObject(signJsonObjStr);
            Map<String, Object> paramentMap =new HashMap<String, Object>();
            paramentMap.put("publickey",keyJsonObj.getJSONObject("result").getString("publicKey"));
            paramentMap.put("data",Base64.getBase64((JSON.toJSONString(pd))));
            paramentMap.put("sign",signJsonObj.getString("result"));
            String result = HttpUtil.sendPostData("http://192.168.200.81:8332/send_data_to_sys", JSON.toJSONString(paramentMap));
            JSONObject json = JSON.parseObject(result);
            if(StringUtil.isNotEmpty(json.getString("result"))){
                accDetail.put("hash", json.getString("result")); 
                pd.put("trade_hash", json.getString("result")); 
            }
            logger.info("====================测试代码=======end=================");
            
            
            
            
            boolean accDetailResult = accDetailService.insertSelective(accDetail, Constant.VERSION_NO);
            logger.info("--------商城兑换插入账户流水---------accDetailResult结果："+accDetailResult);
            
            PageData tshopOrder = new PageData();
            tshopOrder.put("order_no", pd.getString("order_no"));
            tshopOrder.put("trade_hash", pd.getString("trade_hash"));
            tshopOrder.put("order_status", "10");//支付处理中
            boolean updateOrderHashResult = updateOrderHashByOrderNo(tshopOrder);
            logger.info("--------商城兑换订单更新hash值---------updateOrderHashResult结果："+updateOrderHashResult);
            //解锁订单
            boolean unLockOrderByOrderNo = unLockOrderByOrderNo(pd);
            logger.info("支付订单解锁结果unLockOrderByOrderNo："+unLockOrderByOrderNo);
            
            logger.info("-------------商城支付-----------end------------");
            return true;
        }else{
            logger.error("--------商城支付-------userWalletService.payNowBySJT------从账户余额扣钱到冻结余额中  失败");
        }
        
        return false;
    }
    
    @Override
    public boolean updateShopOrderStatus(PageData pd, String versionNo) throws Exception {
        return (Integer) dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.updateShopOrderStatus", pd) > 0;
    }

    @Override
    public List<PageData> getGoodsByOrderId(PageData pd, String versionNo) throws Exception {
        return (List<PageData>) dao.findForList("com.ecochain.ledger.mapper.ShopOrderInfoMapper.getGoodsByOrderId", pd);
    }

    @Override
    public PageData getShopOrderByOrderNo(PageData pd, String versionNo) throws Exception {
        return (PageData) dao.findForObject("com.ecochain.ledger.mapper.ShopOrderInfoMapper.getShopOrderByOrderNo", pd);
    }

    @Override
    public boolean updateOrderIdByOrderNo(String order_no, String versionNo) throws Exception {
        return (Integer) dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.updateOrderIdByOrderNo", order_no) > 0;
    }

    @Override
    public List<PageData> getSupplierByUserId(String user_id, String versionNo) throws Exception {
        return (List<PageData>) dao.findForList("com.qkl.wlsc.provider.dao.ShopSupplierMapper.getSupplierByUserId", user_id);
    }

    @Override
    public boolean isLastShopOrder(PageData pd, String versionNo) throws Exception {
        return (Integer) dao.findForObject("com.ecochain.ledger.mapper.ShopOrderInfoMapper.isLastShopOrder", pd) > 1;
    }

    @Override
    public boolean updateStateByOrderNo(PageData pd, String versionNo) throws Exception {
        return (Integer) dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.updateStateByOrderNo", pd) > 0;
    }

    @Override
    public List<PageData> getShopOrderListByConflag(String conflag, String versionNo) throws Exception {
        return (List<PageData>) dao.findForList("com.ecochain.ledger.mapper.ShopOrderInfoMapper.getShopOrderListByConflag", conflag);
    }

    @Override
    public boolean updateOrderConflag(PageData pd, String versionNo) throws Exception {
        return (Integer) dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.updateOrderConflag", pd) > 0;
    }

    @Override
    public Integer getShopOrderTotalNum(PageData pd, String versionNo) throws Exception {
        return (Integer) dao.findForObject("com.ecochain.ledger.mapper.ShopOrderInfoMapper.getShopOrderTotalNum", pd);
    }

    @Override
    public PageData getShopOrderNumByStatus(PageData pd, String versionNo) throws Exception {
        return (PageData) dao.findForObject("com.ecochain.ledger.mapper.ShopOrderInfoMapper.getShopOrderNumByStatus", pd);
    }

    @Override
    public PageData getOneSupplierByUserId(String user_id, String versionNo) throws Exception {
        return (PageData) dao.findForObject("com.ecochain.ledger.mapper.ShopOrderInfoMapper.getOneSupplierByUserId", user_id);
    }

    @Override
    public PageData getShopOrderNumByStatusForSupplier(PageData pd,String versionNo) throws Exception {
        return (PageData)dao.findForObject("com.ecochain.ledger.mapper.ShopOrderInfoMapper.getShopOrderNumByStatusForSupplier", pd);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean refundGoods(PageData pd) throws Exception {
        PageData shopOrderGoods = shopOrderGoodsService.getOrderGoodsAndUserInfoById(pd.getString("rec_id"));
       /* boolean lastRefundGoods = this.isLastRefundGoods(rec_id);
        boolean refundResult = false;
        if(lastRefundGoods){//如果前面的商品都是支付后关闭的，需修改订单状态为关闭，有一个商品状态确认收货，订单状态都改为已完成
            refundResult = this.refundLastGoods(rec_id);
        }else{
            refundResult = (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.refundGoods", rec_id)>0;
        }*/
        boolean refundResult = (Integer) dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.refundGoods", pd) > 0;
        logger.info("**********商城订单退款结果refundResult：" + refundResult + "*****************");
        if (refundResult) {
            PageData accDetail = new PageData();
            accDetail.put("user_id", shopOrderGoods.get("user_id"));
            /*if("11".equals(pd.getString("state"))){//退款
                accDetail.put("acc_no", "32");
                accDetail.put("other_source", "商城退款");
            }else if("13".equals(pd.getString("state"))){//退货
                accDetail.put("acc_no", "34");
                accDetail.put("other_source", "商城退货");
            }*/
            accDetail.put("acc_no", "32");
            accDetail.put("other_source", "商城退款");
            accDetail.put("wlbi_amnt", String.valueOf(shopOrderGoods.get("pay_price")));
            accDetail.put("user_type", shopOrderGoods.getString("user_type"));
            accDetail.put("caldate", DateUtil.getCurrDateTime());
            accDetail.put("cntflag", "1");
            accDetail.put("status", "4");
            accDetail.put("otherno", shopOrderGoods.getString("shop_order_no"));
            accDetail.put("other_amnt", String.valueOf(shopOrderGoods.get("pay_price")));
            accDetail.put("operator", shopOrderGoods.getString("mobile_phone"));
            accDetail.put("remark1", shopOrderGoods.getString("goods_name"));
            boolean accDetailResult = accDetailService.insertSelective(accDetail, Constant.VERSION_NO);
            logger.info("--------商城退款退货插入账户流水---------accDetailResult结果：" + accDetailResult);
        }
        return refundResult;
    }

    @Override
    public boolean refundLastGoods(String rec_id) throws Exception {
        return (Integer) dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.refundLastGoods", rec_id) > 0;
    }

    @Override
    public boolean isLastRefundGoods(String rec_id) throws Exception {
        return (Integer) dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.isLastRefundGoods", rec_id) > 0;
    }


    @Override
    public boolean applyAndRefuseRefundGoods(PageData pd) throws Exception {
        return (Integer) dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.applyAndRefuseRefundGoods", pd) > 0;
    }

    @Override
    public PageData getShopOrderAndUserInfo(PageData pd) throws Exception {
        return (PageData) dao.findForObject("com.ecochain.ledger.mapper.ShopOrderInfoMapper.getShopOrderAndUserInfo", pd);
    }

    @Override
    public boolean shopReceipt(PageData pd, String versionNo) throws Exception {
        logger.info("--------shopReceipt确认收货更新订单状态、物流状态---------start---------");
        //扣除账户冻结余额
        PageData userWallet = new PageData();
        userWallet.put("froze_wlbi_amnt", pd.getString("other_amnt"));
        userWallet.put("user_id", pd.get("user_id"));
        boolean userWalletResult = userWalletService.updateSub(userWallet, versionNo);
        logger.info("-------shopReceipt商城确认收货-------扣除客户冻结三界通userWalletResult：" + userWalletResult);
        /*boolean addMoneyToSupplier = false;
        if(userWalletResult){
          //供应商加钱
            PageData userWallet1  = new PageData();
            userWallet1.put("shop_order_no", pd.getString("shop_order_no"));
//            userWallet1.put("rate", pd.getString("rate"));
            addMoneyToSupplier = userWalletService.addMoneyToSupplier(userWallet1, versionNo);
            logger.info("------确认收货-----给供应商增加人民币-----结果addMoneyToSupplier："+addMoneyToSupplier);
            if(!addMoneyToSupplier){
                logger.error("------商城确认收货-------给供应商增加人民币 失败-----------");
            }
        }*/
        if (userWalletResult) {
            //更新订单状态
            PageData orderInfo = new PageData();
            orderInfo.put("order_id", String.valueOf(pd.get("order_id")));
            orderInfo.put("user_id", pd.get("user_id"));
            orderInfo.put("state", "6");//完成交易
            orderInfo.put("order_status", "3");
            boolean orderResult = updateShopReceipt(orderInfo);
            logger.info("--------shopReceipt确认收货更新订单状态、物流状态--------orderResult总结果：" + orderResult);
            /*boolean accDetailResult = false;
            if((orderResult&&userWalletResult)){
                PageData accDetail = new PageData();
                accDetail.put("user_id", pd.get("user_id"));
                accDetail.put("acc_no", "05");
                accDetail.put("wlbi_amnt", String.valueOf(pd.get("wlbi_amnt")));
                accDetail.put("user_type", pd.getString("user_type"));
                accDetail.put("caldate", DateUtil.getCurrDateTime());
                accDetail.put("cntflag", "1");
                accDetail.put("status", "4");
                accDetail.put("otherno", pd.getString("otherno"));
                accDetail.put("other_amnt", pd.getString("other_amnt"));
                accDetail.put("other_source", "商城兑换");
                accDetail.put("operator", pd.getString("operator"));
                accDetailResult = accDetailService.insertSelective(accDetail, Constant.VERSION_NO);
                logger.info("--------确认收货插用户入账户流水---------accDetailResult结果："+accDetailResult);
                //商城确认消费插入供应商账户流水
                boolean addSupplierSalesAchievement = accDetailService.addSupplierSalesAchievement(pd.getString("shop_order_no"), Constant.VERSION_NO);
                logger.info("--------确认收货插入供应商账户流水---------addSupplierSalesAchievement结果："+addSupplierSalesAchievement);
            }
            logger.info("--------确认收货更新订单状态、物流状态---------end(accDetailResult&&orderResult)总结果："+(accDetailResult&&orderResult));*/
            //商城确认消费插入供应商账户流水
            boolean addSupplierSalesAchievement = accDetailService.addSupplierSalesAchievement(pd.getString("shop_order_no"), Constant.VERSION_NO);
            logger.info("--------shopReceipt确认收货插入供应商账户流水---------addSupplierSalesAchievement结果：" + addSupplierSalesAchievement);
            logger.info("--------shopReceipt确认收货更新订单状态、物流状态---------end---------");
            return orderResult;
        }
        return false;
    }

    @Override
    public boolean updateShopReceipt(PageData pd) throws Exception {
        return (Integer) dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.updateShopReceipt", pd) > 0;
    }

    @Override
    public String querySimpleGoodsInfo(String orderNum) {
        return this.shopOrderInfoMapper.querySimpleGoodsInfo(orderNum);
    }

    @Override
    public boolean updateShopOrderStatusInfo(PageData pageData, String s) throws Exception {
        return (Integer) dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.updateShopOrderStatusInfo", pageData) > 0;
    }



    @Override
    public boolean payNowByRMB(PageData pd,String versionNo) throws Exception {
        logger.info("--------商城支付-------payNowByRMB------start------pd value is" +pd.toString());

        //支付成功修改订单状态
        PageData payOrder = new PageData();
        payOrder.put("pay_no", pd.getString("pay_no"));
        payOrder.put("bank_tradeno", pd.getString("bank_tradeno"));
        payOrder.put("bank_tradestatus", pd.getString("bank_tradestatus"));
        payOrder.put("confirm_time", DateUtil.getCurrDateTime());
        payOrder.put("status", "1");//交易成功
        boolean orderResult = payOrderService.updateStatusByPayNo(payOrder, versionNo);
        logger.info("------------商城支付---payNowByRMB----------更新订单orderResult："+orderResult+",参数payOrder.toString():"+payOrder.toString());

        //修改订单状态为已支付
        PageData shopOrder = new PageData();
        shopOrder.put("user_id", pd.get("user_id"));
        shopOrder.put("order_id", pd.getString("order_id"));
        shopOrder.put("order_status", "2");
        shopOrder.put("pay_time", DateUtil.getCurrDateTime());
        if(!updateShopOrderStatus(shopOrder, versionNo)){
            logger.error("--------商城支付-------updateShopOrderStatus------更新商城订单状态  失败");
        }
        //修改订单商品关联表状态为已支付
        PageData shopOrderGoods = new PageData();
        shopOrderGoods.put("user_id", pd.get("user_id"));
        shopOrderGoods.put("shop_order_id", pd.getString("order_id"));
//        shopOrderGoods.put("goods_id", pd.getString("goods_id"));
        shopOrderGoods.put("state", "2");
        if(!shopOrderGoodsService.updateOrderGoodsStatus(shopOrderGoods, versionNo)){
            logger.error("--------商城支付-------shopOrderGoodsService.updateOrderGoodsStatus------更新商城订单商品关联表状态  失败");
        }
        /**
         * add by lishuo
         * 更新商品销量（正常商品会有多个商品需更新，秒杀商品更新一个）
         * 2016年12月29日10:57:42
         */
        PageData shopGoods = new PageData();
        int isPromote = this.shopGoodsMapper.getOrderType(pd.getString("order_no"));//根据订单号查询订单类型
        logger.info("--------商城支付更新商品销量-------------start------");
        if(isPromote ==0 || isPromote ==2){ //2为支付人民币商品
            List<Map<String,Object>> updateList = this.shopGoodsMapper.getBuyCountByOrderNo(pd.getString("order_no"));//根据订单号获取订单的商品ID个数
            Map<Object ,Object> map =new HashMap<>();
            map.put("updateList",updateList);
            map.put("goods_number",updateList.get(0).get("goods_number"));
            logger.info("--------商城支付更新商品销量-------------start------即将更新的参数为：" +map.toString());
            if(!this.shopGoodsMapper.updateShopGoodsSales(map)){
                logger.error("--------商城支付-------shopOrderGoodsService.updateShopGoodsSales------更新商城商品销量  失败");
            }
        }else if(isPromote ==1){
            List updateList = this.shopGoodsMapper.getBuyCountByOrderNo(pd.getString("order_no"));//根据订单号获取订单的商品ID个数
            Map<Object ,Object> map =new HashMap<>();
            map.put("updateList",updateList);
            if(!this.shopGoodsMapper.updateShopHotGoodsSales(map)){
                logger.error("--------商城支付-------shopOrderGoodsService.updateShopGoodsSales------更新商城商品销量  失败");
            }
        }
        logger.info("--------商城支付更新商品销量-------------end------");
        PageData accDetail = new PageData();
        accDetail.put("user_id", pd.get("user_id"));
        accDetail.put("acc_no", "05");
        accDetail.put("rmb_amnt", String.valueOf(pd.get("order_amount")));
        accDetail.put("user_type", pd.getString("user_type"));
        accDetail.put("caldate", DateUtil.getCurrDateTime());
        accDetail.put("cntflag", "1");
        accDetail.put("status", "4");
        accDetail.put("otherno", pd.getString("order_no"));
        accDetail.put("other_amnt", String.valueOf(pd.get("order_amount")));
        accDetail.put("other_source", "商城兑换");
        accDetail.put("operator", pd.getString("operator"));
        String good_name = shopOrderGoodsService.getOneGoodsNameByOrderNo(pd.getString("order_no"));
        accDetail.put("remark1", good_name);
        boolean accDetailResult = accDetailService.insertSelective(accDetail, Constant.VERSION_NO);
        logger.info("--------商城兑换插入账户流水---------accDetailResult结果："+accDetailResult);
        logger.info("-------------商城支付-----------end------------");
        return true;
    }

    @Override
    public boolean confirmReceiptByRMB(PageData pd, String versionNo) throws Exception {
        logger.info("--------confirmReceiptByRMB---确认收货更新订单状态、物流状态---------start---------");
        //更新订单状态
        PageData orderInfo = new PageData();
        orderInfo.put("order_id", String.valueOf(pd.get("order_id")));
        boolean orderResult = updateOrderConflag(orderInfo, versionNo);
        logger.info("--------confirmReceiptByRMB---确认收货更新订单状态、物流状态--------orderResult总结果："+orderResult);
        //商城确认消费插入供应商账户流水
        /*boolean addSupplierSalesAchievement = accDetailService.addSupplierSalesAchievement(pd.getString("shop_order_no"), Constant.VERSION_NO);
        logger.info("--------confirmReceiptByRMB---确认收货插入供应商账户流水---------addSupplierSalesAchievement结果："+addSupplierSalesAchievement);
        logger.info("--------confirmReceiptByRMB---确认收货更新订单状态、物流状态---------end---------");*/
        return orderResult;
    }

    @Override
    public boolean updateShopOrderStatusByHash(PageData pd, String versionNo) throws Exception {
        return (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.updateShopOrderStatusByHash", pd)>0;
    }
    
    @Override
    public boolean lockOrderByOrderNo(PageData pd) throws Exception {
        return (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.lockOrderByOrderNo", pd)>0;
    }

    @Override
    public boolean unLockOrderByOrderNo(PageData pd) throws Exception {
        return (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.unLockOrderByOrderNo", pd)>0;
    }

    @Override
    public boolean updateOrderHashByOrderNo(PageData pd) throws Exception {
        return (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderInfoMapper.updateOrderHashByOrderNo", pd)>0;
    }

    /**
     * @param orderNum
     * @describe:根据订单号查询商品简要信息
     * @author: Lisandro
     * @date: 2017年5月2日14:53:36
     * @return: String
     */
    @Override
    public String queryOrderNum(String orderNum) {
        return this.shopOrderInfoMapper.queryOrderNum(orderNum);
    }
}
