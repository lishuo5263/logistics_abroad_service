package com.ecochain.ledger.model;

import java.util.Date;

public class BlockDataHash {
    private Integer id;

    private String dataHash;

    private String blockHash;

    private Integer blockHeight;

    private Date blockCreateTime;

    private Date dataCreateTime;

    private String bussType;

    private String sysCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataHash() {
        return dataHash;
    }

    public void setDataHash(String dataHash) {
        this.dataHash = dataHash == null ? null : dataHash.trim();
    }

    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash == null ? null : blockHash.trim();
    }

    public Integer getBlockHeight() {
        return blockHeight;
    }

    public void setBlockHeight(Integer blockHeight) {
        this.blockHeight = blockHeight;
    }

    public Date getBlockCreateTime() {
        return blockCreateTime;
    }

    public void setBlockCreateTime(Date blockCreateTime) {
        this.blockCreateTime = blockCreateTime;
    }

    public Date getDataCreateTime() {
        return dataCreateTime;
    }

    public void setDataCreateTime(Date dataCreateTime) {
        this.dataCreateTime = dataCreateTime;
    }

    public String getBussType() {
        return bussType;
    }

    public void setBussType(String bussType) {
        this.bussType = bussType == null ? null : bussType.trim();
    }

    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode == null ? null : sysCode.trim();
    }
}