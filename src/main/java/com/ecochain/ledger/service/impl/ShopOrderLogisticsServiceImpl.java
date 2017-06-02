package com.ecochain.ledger.service.impl;

import java.util.List;

import com.ecochain.ledger.dao.DaoSupport;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.ShopOrderLogisticsService;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Component("shopOrderLogisticsService")
public class ShopOrderLogisticsServiceImpl implements ShopOrderLogisticsService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;
    @Override
    public boolean deleteById(Integer id, String versionNo) throws Exception {
        return (Integer)dao.delete("com.ecochain.ledger.mapper.ShopOrderLogisticsMapper.deleteById", id)>0;
    }

    @Override
    public boolean insert(PageData pd, String versionNo) throws Exception {
        return (Integer)dao.save("com.ecochain.ledger.mapper.ShopOrderLogisticsMapper.insert", pd)>0;
    }

    @Override
    public boolean insertSelective(PageData pd, String versionNo) throws Exception {
        return (Integer)dao.save("com.ecochain.ledger.mapper.ShopOrderLogisticsMapper.insertSelective", pd)>0;
    }

    @Override
    public PageData selectById(Integer id, String versionNo) throws Exception {
        return (PageData)dao.findForObject("com.ecochain.ledger.mapper.ShopOrderLogisticsMapper.selectById", id);
    }

    @Override
    public boolean updateByIdSelective(PageData pd, String versionNo) throws Exception {
        return (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderLogisticsMapper.updateByIdSelective", pd)>0;
    }

    @Override
    public boolean updateById(PageData pd, String versionNo) throws Exception {
        return (Integer)dao.update("com.ecochain.ledger.mapper.ShopOrderLogisticsMapper.updateById", pd)>0;
    }

    @Override
    public List<PageData> getLogistics(PageData pd) throws Exception {
        return (List<PageData>)dao.findForList("com.ecochain.ledger.mapper.ShopOrderLogisticsMapper.getLogistics", pd);
    }
}
