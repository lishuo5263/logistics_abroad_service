package com.ecochain.ledger.mapper;

import com.ecochain.ledger.model.ShopOrderGoods;
import com.ecochain.ledger.model.ShopOrderInfo;

import java.util.List;
import java.util.Map;

public interface ShopOrderInfoMapper {
    int deleteByPrimaryKey(Integer orderId);

    int insert(ShopOrderInfo record);

    int insertSelective(ShopOrderInfo record);

    ShopOrderInfo selectByPrimaryKey(Integer orderId);

    int updateByPrimaryKeySelective(ShopOrderInfo record);

    int updateByPrimaryKey(ShopOrderInfo record);

    int insertShopOrder(ShopOrderGoods shopOrderGoods);

    int shopOrderPayCallBack(String orderNo);

    List<Map<String,Object>> getUnPayOrder();

    boolean updateOrderRefundStatus(String orderNo);

    boolean updateOrderGoodsRefundStatus(String orderNo);

    Map getOrderInfo(String orderNo);

    boolean updateShopOrderInfoCancleStatus(String orderNo);

    boolean updateShopOrderGoodsCancleStatus(String orderNo);

    boolean updateOrderStatusByOrderNo(String orderNo);

    boolean updateShopGoodsGoods(String orderNo);

    int querySecKillCount(Integer userId);

    boolean deleteShopOrderInfo(String orderNo);

    boolean deleteShopOrderGoods(String orderNo);

    String getSkuGoodsInfoByorderNo(String orderNo);

    String querySimpleGoodsInfo(String orderNum);

    String queryOrderNum(String orderNum);

    boolean updateHashByOrderNo(Map updateMap);
}