package com.ecochain.ledger.service;

import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.model.ShopOrderLogisticsDetail;

import java.util.List;
import java.util.Map;

/**
 * Created by Lisandro on 2017/6/1.
 */
public interface ShopOrderLogisticsDetailService {


    Map findLogisticsInfoByNo(String logisticsNo);

    List<Map<String,Object>> findLogisticsInfoByNo2(String logisticsNo);

    int insertSelective(ShopOrderLogisticsDetail record);

    boolean transferLogistics(PageData pd, String versionNo) throws Exception;

    boolean transferLogisticsWithOutBlockChain(PageData pd, String versionNo) throws Exception;
}
