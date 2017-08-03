package com.ecochain.ledger.mapper;

import java.util.List;
import java.util.Map;

import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.model.ShopOrderLogisticsDetail;

public interface ShopOrderLogisticsDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ShopOrderLogisticsDetail record);

    int insertSelective(ShopOrderLogisticsDetail record);

    ShopOrderLogisticsDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ShopOrderLogisticsDetail record);

    int updateByPrimaryKey(ShopOrderLogisticsDetail record);

    Map findLogisticsInfoByNo(String logisticsNo);

    List<Map<String,Object>> findLogisticsInfoByNo2(String logisticsNo);

    Map findLogisticsInfoByOrderNo(String orderNo);
    
    int findLogisticsInfoByOrderNo2(PageData pd);

}