package com.ecochain.ledger.model;


import java.util.Date;

public class FabricBlockInfo {
    private Integer id;

    private String fabricBlockHash;
    
    private String fabricBlockHeight;

    private String fabricHash;

    private String fabricUuid;

    private String fabricBussType;

    private String hashData;

    private Date createTime;

    private Date modifyTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFabricBlockHash() {
        return fabricBlockHash;
    }

    public void setFabricBlockHash(String fabricBlockHash) {
        this.fabricBlockHash = fabricBlockHash;
    }
    
    public String getFabricBlockHeight() {
        return fabricBlockHeight;
    }

    public void setFabricBlockHeight(String fabricBlockHeight) {
        this.fabricBlockHeight = fabricBlockHeight == null ? null : fabricBlockHeight.trim();
    }

    public String getFabricHash() {
        return fabricHash;
    }

    public void setFabricHash(String fabricHash) {
        this.fabricHash = fabricHash == null ? null : fabricHash.trim();
    }

    public String getFabricUuid() {
        return fabricUuid;
    }

    public void setFabricUuid(String fabricUuid) {
        this.fabricUuid = fabricUuid == null ? null : fabricUuid.trim();
    }

    public String getFabricBussType() {
        return fabricBussType;
    }

    public void setFabricBussType(String fabricBussType) {
        this.fabricBussType = fabricBussType == null ? null : fabricBussType.trim();
    }

    public String getHashData() {
        return hashData;
    }

    public void setHashData(String hashData) {
        this.hashData = hashData == null ? null : hashData.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "FabricBlockInfo [id=" + id + ", fabricBlockHeight=" + fabricBlockHeight + ", fabricHash=" + fabricHash + ", fabricUuid=" + fabricUuid
                + ", fabricBussType=" + fabricBussType + ", hashData=" + hashData + ", createTime=" + createTime + ", modifyTime=" + modifyTime
                + ", updateTime=" + updateTime + "]";
    }
    
    
}