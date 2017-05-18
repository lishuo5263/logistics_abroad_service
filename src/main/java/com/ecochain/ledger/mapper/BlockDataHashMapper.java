package com.ecochain.ledger.mapper;

import com.ecochain.ledger.model.BlockDataHash;


public interface BlockDataHashMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BlockDataHash record);

    int insertSelective(BlockDataHash record);

    BlockDataHash selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BlockDataHash record);

    int updateByPrimaryKey(BlockDataHash record);
    
    boolean isExistDataHash(String dataHash);
}