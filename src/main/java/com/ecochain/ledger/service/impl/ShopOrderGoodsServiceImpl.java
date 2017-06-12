package com.ecochain.ledger.service.impl;

import com.ecochain.ledger.dao.DaoSupport;
import com.ecochain.ledger.mapper.ShopGoodsMapper;
import com.ecochain.ledger.mapper.ShopOrderGoodsMapper;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.ShopOrderGoodsService;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.Map;

/**
 * Created by LiShuo on 2016/10/28.
 */
@Component("shopOrderGoodsService")
public class ShopOrderGoodsServiceImpl implements ShopOrderGoodsService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;

    @Resource
    private ShopGoodsMapper shopGoodsMapper;

    @Resource
    private ShopOrderGoodsMapper shopOrderGoodsMapper;

    @Override
    public boolean updateLogistics(PageData pd, String versionNo) throws Exception {
        return (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderGoodsMapper.updateLogistics", pd)>0;
    }

    @Override
    public PageData getOrderGoods(PageData pd, String versionNo) throws Exception {
        return (PageData)dao.findForObject("com.ecochain.ledger.mapper.ShopOrderGoodsMapper.getOrderGoods", pd);
    }

    @Override
    public boolean updateOrderGoodsStatus(PageData pd, String versionNo) throws Exception {
        return (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderGoodsMapper.updateOrderGoodsStatus", pd)>0;
    }

    @Override
    public Integer queryGoodsByCount(Integer userId, Integer goodsId) {
        return shopOrderGoodsMapper.queryGoodsByCount(userId,goodsId);
    }

    @Override
    public Map queryActiveInfo(String activityId) {
        return shopOrderGoodsMapper.queryActiveInfo(activityId);
    }

    @Override
    public PageData getOrderGoodsAndUserInfoById(String rec_id) throws Exception {
        return (PageData)dao.findForObject("com.ecochain.ledger.mapper.ShopOrderGoodsMapper.getOrderGoodsAndUserInfoById", rec_id);
    }

    @Override
    public String getOneGoodsNameByOrderNo(String shop_order_no) throws Exception {
        return (String)dao.findForObject("com.ecochain.ledger.mapper.ShopOrderGoodsMapper.getOneGoodsNameByOrderNo", shop_order_no);
    }

    @Override
    public boolean updateOrderGoodsStatusByHash(PageData pd, String versionNo) throws Exception {
        return (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderGoodsMapper.updateOrderGoodsStatusByHash", pd)>0;
    }

    @Override
    public String getStateByOrderNo(String shop_order_no) throws Exception {
        return (String)dao.findForObject("com.ecochain.ledger.mapper.ShopOrderGoodsMapper.getStateByOrderNo", shop_order_no);
    }
}
