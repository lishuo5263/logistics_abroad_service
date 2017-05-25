package com.ecochain.ledger.service;


import com.ecochain.ledger.model.Page;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.model.ShopOrderGoods;

import java.util.List;
import java.util.Map;

public interface ShopOrderInfoService {
    /**
     * @describe:分页查询商城订单列表
     * @author: zhangchunming
     * @date: 2016年10月27日下午6:05:13
     * @param page
     * @param versionNo
     * @throws Exception
     * @return: List<PageData>
     */
    PageData listPageShopOrder(Page page, String versionNo) throws Exception;
    /**
     * @describe:分页查询订单列表
     * @author: zhangchunming
     * @date: 2017年5月17日下午1:50:31
     * @param page
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    List<PageData> listShopOrderByPage(PageData pd) throws Exception;
    /**
     * @describe:个人中心按订单状态查询数量
     * @author: zhangchunming
     * @date: 2016年10月27日下午6:05:34
     * @param user_id
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
//    PageData getShopOrderNumByStatus(Integer user_id,String versionNo) throws Exception;
    /**
     * @describe:根据不同会员类型添加不同查询条件
     * @author: zhangchunming
     * @date: 2016年11月25日上午10:51:42
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    PageData getShopOrderNumByStatus(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:查询商城订单总数
     * @author: zhangchunming
     * @date: 2016年10月28日下午2:28:57
     * @param user_id
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    Integer getShopOrderTotalNum(Integer user_id, String versionNo) throws Exception;
    /**
     * @describe:查询商城订单总数(根据用户还是供应商添加不同的查询条件)
     * @author: zhangchunming
     * @date: 2016年11月24日下午3:44:14
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: Integer
     */
    Integer getShopOrderTotalNum(PageData pd, String versionNo) throws Exception;
    /**
     * 创建订单，同时创建商品信息
     * @param ShopOrderGoods
     * @author: lishuo
     * @date: 2016年10月27日
     * @return
     */
    List<Map<String,Object>> insertShopOrder(List<ShopOrderGoods> ShopOrderGoods) throws Exception;

    /**
     * 订单回掉更新订单状态
     * @param orderNo
     * @return
     * @author: lishuo
     * @date: 2016年11月3日
     */
    boolean shopOrderPayCallBack(String orderNo);
    /**
     * @describe:查询商城订单详情
     * @author: zhangchunming
     * @date: 2016年11月8日下午5:32:02
     * @param id
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    PageData selectById(Integer order_id, String versionNo) throws Exception;
    /**
     * @describe:确认收货
     * @author: zhangchunming
     * @date: 2016年11月8日下午6:36:16
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean confirmReceipt(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:确认收货
     * @author: zhangchunming
     * @date: 2016年11月8日下午6:36:16
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean shopReceipt(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:发货
     * @author: zhangchunming
     * @date: 2016年11月9日上午10:50:18
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean deliverGoods(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:立即支付
     * @author: zhangchunming
     * @date: 2016年11月9日下午10:03:05
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean payNow(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:修改商城订单状态
     * @author: zhangchunming
     * @date: 2016年11月10日下午8:44:23
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateShopOrderStatus(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:通过hash更新hash状态
     * @author: zhangchunming
     * @date: 2017年3月20日下午5:04:14
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateShopOrderStatusByHash(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:根据订单号查询所有商品
     * @author: zhangchunming
     * @date: 2016年11月11日下午2:21:55
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: List<PageData>
     */
    List<PageData> getGoodsByOrderId(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:根据订单号查询商城订单
     * @author: zhangchunming
     * @date: 2016年11月12日下午6:22:10
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    PageData getShopOrderByOrderNo(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:根据订单号更新订单商品关联表订单Id
     * @author: zhangchunming
     * @date: 2016年11月13日下午4:24:06
     * @param order_no
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateOrderIdByOrderNo(String order_no, String versionNo) throws Exception;

    /**
     *
     * @describe:根据订单号更新订单状态等信息
     * @author: Lisandro
     * @date: 2017年5月2日17:15:25
     * @param pd
     * @param versionNo
     * @return
     * @throws Exception
     */
    boolean updateShopOrderStatusInfo(PageData pd, String versionNo) throws Exception;

    /**
     * @describe:根据用户ID查询供应商信息
     * @author: zhangchunming
     * @date: 2016年11月14日下午12:38:22
     * @param user_id
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    List<PageData> getSupplierByUserId(String user_id, String versionNo) throws Exception;
    /**
     * @describe:根据user_id查询供应商
     * @author: zhangchunming
     * @date: 2016年11月25日上午11:11:05
     * @param user_id
     * @param versionNo
     * @throws Exception
     * @return: PageData
     */
    PageData getOneSupplierByUserId(String user_id, String versionNo) throws Exception;
    /**
     * @describe: 判断是否为最后一个订单商品
     * @author: zhangchunming
     * @date: 2016年11月20日下午7:54:08
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean isLastShopOrder(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:根据订单号更新订单商品状态
     * @author: zhangchunming
     * @date: 2016年11月20日下午7:56:51
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateStateByOrderNo(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:根据统计标志查询商城订单列表
     * @author: zhangchunming
     * @date: 2016年11月21日下午1:05:53
     * @param conflag
     * @param versionNo
     * @throws Exception
     * @return: List<PageData>
     */
    List<PageData> getShopOrderListByConflag(String conflag, String versionNo) throws Exception;
    /**
     * @describe:定时器确认收货后修改统计标志
     * @author: zhangchunming
     * @date: 2016年11月21日下午1:29:14
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean updateOrderConflag(PageData pd, String versionNo) throws Exception;

    /**
     * @describe:获取秒杀商品不支付的订单
     * @author: lishuo
     * @date: 2016年12月5日11:30:21
     * @return: List
     */
    List<Map<String,Object>> getUnPayOrder();

    /**
     * 定时更新后更新订单恢复库存状态
     * @author: lishuo
     * @date: 2016年12月5日17:40:46
     * @param orderNo
     * @return
     */
    boolean updateOrderRefundStatus(String orderNo);
    /**
     * @describe:查询供应商订单数
     * @author: zhangchunming
     * @date: 2016年12月10日上午11:10:09
     * @param pd
     * @throws Exception
     * @return: PageData
     */
    PageData getShopOrderNumByStatusForSupplier(PageData pd, String versionNo) throws Exception;

    /**
     * 定时更新后更新订单恢复库存状态
     * @author: lishuo
     * @date: 2016年12月5日17:40:46
     * @return
    */
    Map getOrderInfo(String type, String orderNo);

    /**
     * 取消订单，修改订单状态
     * @author: lishuo
     * @date: 2016年12月5日17:40:46
     * @return
     */
    boolean updateCancleState(String type, String orderNo, String isHot, String isPromote);

    /**
     * 删除秒杀不支付订单
     * @author: lishuo
     * @date: 2017年1月4日17:40:41
     * @return
     */
    boolean  deleteShopOrderInfo(String type, String orderNo, String skuValue);

    /**
     * 根据订单号查询商品SkuValue
     * @author: lishuo
     * @return
     */
    String getSkuGoodsInfoByorderNo (String orderNo);

    /**
     * 查询用户已秒杀的订单数量
     * @author: lishuo
     * @date: 2016年12月17日10:16:41
     * @return
     */
    int querySecKillCount(Integer userId);
    /**
     * @describe:退款退货处理
     * @author: zhangchunming
     * @date: 2016年12月26日下午3:33:34
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean refundGoods(PageData pd)throws Exception;
    /**
     * @describe:全部商品退货
     * @author: zhangchunming
     * @date: 2016年12月26日下午3:51:16
     * @param rec_id
     * @throws Exception
     * @return: boolean
     */
    boolean refundLastGoods(String rec_id)throws Exception;
    /**
     * @describe:判断是否最后一个退换（即前面的商品都退货）
     * @author: zhangchunming
     * @date: 2016年12月26日下午4:09:03
     * @param rec_id
     * @throws Exception
     * @return: boolean
     */
    boolean isLastRefundGoods(String rec_id)throws Exception;
    /**
     * @describe:申请或拒绝退款退货
     * @author: zhangchunming
     * @date: 2016年12月28日下午6:05:44
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean applyAndRefuseRefundGoods(PageData pd)throws Exception;

    /**
     * @describe:根据订单号及商品id查询订单及用户信息
     * @author: zhangchunming
     * @date: 2017年1月9日下午4:24:59
     * @param pd
     * @throws Exception
     * @return: PageData
     */
    PageData getShopOrderAndUserInfo(PageData pd)throws Exception;
    /**
     * @describe:确认收货
     * @author: zhangchunming
     * @date: 2017年1月9日下午4:44:26
     * @param pd
     * @throws Exception
     * @return: PageData
     */
    boolean updateShopReceipt(PageData pd)throws Exception;
    /**
     * @describe:更新hash值
     * @author: zhangchunming
     * @date: 2017年3月17日下午5:54:13
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean updateOrderHashByOrderNo(PageData pd)throws Exception;
    /**
     * @describe:根据订单号锁定订单
     * @author: zhangchunming
     * @date: 2017年3月22日下午5:01:28
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean lockOrderByOrderNo(PageData pd)throws Exception;
    /**
     * @describe:根据订单号解锁订单
     * @author: zhangchunming
     * @date: 2017年3月22日下午5:01:57
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean unLockOrderByOrderNo(PageData pd)throws Exception;

    /**
     * @describe:商城人民币支付
     * @author: zhangchunming
     * @date: 2017年5月2日上午11:49:02
     * @param pd
     * @throws Exception
     * @return: boolean
     */
    boolean payNowByRMB(PageData pd, String versionNo) throws Exception;
    /**
     * @describe:人民币支付-确认收货
     * @author: zhangchunming
     * @date: 2017年5月2日下午3:50:35
     * @param pd
     * @param versionNo
     * @throws Exception
     * @return: boolean
     */
    boolean confirmReceiptByRMB(PageData pd, String versionNo) throws Exception;

    /**
     * @describe:根据订单号查询商品简要信息
     * @author: Lisandro
     * @date: 2017年5月2日14:53:36
     * @return: String
     */
    String querySimpleGoodsInfo(String orderNum);

    /**
     * @describe:根据订单号查询商品简要信息
     * @author: Lisandro
     * @date: 2017年5月2日14:53:36
     * @return: String
     */
    String queryOrderNum(String orderNum);

    boolean updateHashByOrderNo(Map updateMap);
}
