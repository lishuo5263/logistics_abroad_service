package com.ecochain.ledger.mapper;

import com.ecochain.ledger.model.FabricBlockInfo;

import java.util.List;

public interface FabricBlockInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FabricBlockInfo record);

    FabricBlockInfo selectByPrimaryKey(Integer id);

    List<FabricBlockInfo> selectAll();

    int updateByPrimaryKey(FabricBlockInfo record);
}