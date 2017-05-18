package com.ecochain.ledger.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ecochain.ledger.dao.DaoSupport;
import com.ecochain.ledger.model.BlockDataHash;
import com.ecochain.ledger.service.BlockDataHashService;

@Component("blockDataHashService")
public class BlockDataHashServiceImpl implements BlockDataHashService {

    @Resource(name = "daoSupport")
    private DaoSupport dao;
    @Override
    public boolean deleteByPrimaryKey(Integer id) throws Exception {
        return (Integer)dao.delete("com.ecochain.ledger.mapper.BlockDataHashMapper.deleteByPrimaryKey", id)>0;
    }

    @Override
    public boolean insert(BlockDataHash record) throws Exception {
        return (Integer)dao.save("com.ecochain.ledger.mapper.BlockDataHashMapper.insert", record)>0;
    }

    @Override
    public boolean insertSelective(BlockDataHash record) throws Exception {
        return (Integer)dao.save("com.ecochain.ledger.mapper.BlockDataHashMapper.insertSelective", record)>0;
    }

    @Override
    public BlockDataHash selectByPrimaryKey(Integer id) throws Exception {
        return (BlockDataHash)dao.findForObject("com.ecochain.ledger.mapper.BlockDataHashMapper.selectByPrimaryKey", id);
    }

    @Override
    public boolean updateByPrimaryKeySelective(BlockDataHash record) throws Exception {
        return (Integer)dao.update("com.ecochain.ledger.mapper.BlockDataHashMapper.updateByPrimaryKeySelective", record)>0;
    }

    @Override
    public boolean updateByPrimaryKey(BlockDataHash record) throws Exception {
        return (Integer)dao.update("com.ecochain.ledger.mapper.BlockDataHashMapper.updateByPrimaryKey", record)>0;
    }

    @Override
    public boolean isExistDataHash(String dataHash) throws Exception {
        return (Integer)dao.findForObject("com.ecochain.ledger.mapper.BlockDataHashMapper.isExistDataHash", dataHash)>0;
    }

}
