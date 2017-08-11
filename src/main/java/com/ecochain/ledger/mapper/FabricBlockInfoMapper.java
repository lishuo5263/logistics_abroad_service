package com.ecochain.ledger.mapper;

import com.ecochain.ledger.model.FabricBlockInfo;

import java.util.List;

public interface FabricBlockInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FabricBlockInfo record);

    FabricBlockInfo selectByPrimaryKey(Integer id);

    List<FabricBlockInfo> selectAll();

    int updateByPrimaryKey(FabricBlockInfo record);
    
    List<FabricBlockInfo> getDataList10(Integer rows);
    
    FabricBlockInfo getBlockByHeight(String block_height);
    
    FabricBlockInfo getBlockByHash(String block_hash);
    
    FabricBlockInfo getBlockByFabricHash(String fabric_hash);
}