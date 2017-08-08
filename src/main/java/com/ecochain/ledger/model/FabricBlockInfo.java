package com.ecochain.ledger.model;

import java.io.Serializable;
import java.util.Date;

public class FabricBlockInfo implements Serializable {
    private Integer id;

    private String fabricHash;

    private String fabricUuid;

    private String fabricBussType;

    private String hashData;

    private Date createTime;

    private Date modifyTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public FabricBlockInfo(Integer id, String fabricHash, String fabricUuid, String fabricBussType, String hashData, Date createTime, Date modifyTime, Date updateTime) {
        this.id = id;
        this.fabricHash = fabricHash;
        this.fabricUuid = fabricUuid;
        this.fabricBussType = fabricBussType;
        this.hashData = hashData;
        this.createTime = createTime;
        this.modifyTime = modifyTime;
        this.updateTime = updateTime;
    }

    public FabricBlockInfo() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        FabricBlockInfo other = (FabricBlockInfo) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getFabricHash() == null ? other.getFabricHash() == null : this.getFabricHash().equals(other.getFabricHash()))
            && (this.getFabricUuid() == null ? other.getFabricUuid() == null : this.getFabricUuid().equals(other.getFabricUuid()))
            && (this.getFabricBussType() == null ? other.getFabricBussType() == null : this.getFabricBussType().equals(other.getFabricBussType()))
            && (this.getHashData() == null ? other.getHashData() == null : this.getHashData().equals(other.getHashData()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getModifyTime() == null ? other.getModifyTime() == null : this.getModifyTime().equals(other.getModifyTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getFabricHash() == null) ? 0 : getFabricHash().hashCode());
        result = prime * result + ((getFabricUuid() == null) ? 0 : getFabricUuid().hashCode());
        result = prime * result + ((getFabricBussType() == null) ? 0 : getFabricBussType().hashCode());
        result = prime * result + ((getHashData() == null) ? 0 : getHashData().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getModifyTime() == null) ? 0 : getModifyTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }
}