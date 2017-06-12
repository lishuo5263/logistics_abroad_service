package com.ecochain.ledger.service;

import java.util.Map;

import com.ecochain.ledger.model.PageData;

/**
 * Created by LiShuo on 2016/10/28.
 */
public interface ShopOrderGoodsService {
    /**
     * @describe:发货更新物流信息
     * @author: zhangchunming
     * @date: 2016年11月9日下午8:24:50
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateLogistics(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:查询商品关联表
     * @author: zhangchunming
     * @date: 2016年11月9日下午8:53:04
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    PageData getOrderGoods(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:更新物流状态
     * @author: zhangchunming
     * @date: 2016年11月10日下午8:53:07
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateOrderGoodsStatus(PageData pd,String versionNo) throws Exception;
    /**
     * @describe:根据hash更新订单商品状态
     * @author: zhangchunming
     * @date: 2017年3月20日下午4:38:29
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateOrderGoodsStatusByHash(PageData pd,String versionNo) throws Exception;

    /**
     * 查询当前用户购买促销商品次数
     * @param userId
     * @param goodsId
     * @return
     */
    Integer queryGoodsByCount(Integer userId,Integer goodsId);

    /**
     * 查询秒杀模板信息
     * @author lishuo
     * @date2017-1-5 16:57:38
     * @return
     */
    Map queryActiveInfo(String activityId);

    /**
     * @describe:查询商城订单商品关联信息
     * @author: zhangchunming
     * @date: 2016年12月27日下午12:01:01
     * @param rec_id
     * @throws Exception
     * @return: PageData
     */
    PageData getOrderGoodsAndUserInfoById(String rec_id)throws Exception;
    /**
     * @describe:根据一个订单号查询一个商品
     * @author: zhangchunming
     * @date: 2017年1月13日下午6:03:14
     * @param shop_order_no
     * @throws Exception
     * @return: PageData
     */
    String getOneGoodsNameByOrderNo(String shop_order_no)throws Exception;
    /**
     * @describe:根据订单号查询订单商品状态
     * @author: zhangchunming
     * @date: 2017年6月12日下午5:49:28
     * @param shop_order_no
     * @throws Exception
     * @return: String
     */
    String getStateByOrderNo(String shop_order_no) throws Exception;
 }
