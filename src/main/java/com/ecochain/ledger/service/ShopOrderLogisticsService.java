package com.ecochain.ledger.service;

import java.util.List;

import com.ecochain.ledger.model.PageData;

public interface ShopOrderLogisticsService {

    boolean deleteById(Integer id,String versionNo) throws Exception;

    boolean insert(PageData pd,String versionNo) throws Exception;

    boolean insertSelective(PageData pd,String versionNo) throws Exception;

    PageData selectById(Integer id,String versionNo) throws Exception;

    boolean updateByIdSelective(PageData pd,String versionNo) throws Exception;

    boolean updateById(PageData pd,String versionNo) throws Exception;
    
    List<PageData> getLogistics(PageData pd) throws Exception;
}
