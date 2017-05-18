package com.ecochain.ledger.service;

import com.ecochain.ledger.model.BlockDataHash;


public interface BlockDataHashService {
    boolean deleteByPrimaryKey(Integer id) throws Exception;;

    boolean insert(BlockDataHash record) throws Exception;;

    boolean insertSelective(BlockDataHash record) throws Exception;;

    BlockDataHash selectByPrimaryKey(Integer id) throws Exception;;

    boolean updateByPrimaryKeySelective(BlockDataHash record) throws Exception;;

    boolean updateByPrimaryKey(BlockDataHash record) throws Exception;;
    
    Integer isExistDataHash(String dataHash) throws Exception;;
}