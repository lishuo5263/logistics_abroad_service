package com.ecochain.ledger.mapper;

import com.ecochain.ledger.model.shopOrderLogisticsDetail;

public interface shopOrderLogisticsDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(shopOrderLogisticsDetail record);

    int insertSelective(shopOrderLogisticsDetail record);

    shopOrderLogisticsDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(shopOrderLogisticsDetail record);

    int updateByPrimaryKey(shopOrderLogisticsDetail record);
}