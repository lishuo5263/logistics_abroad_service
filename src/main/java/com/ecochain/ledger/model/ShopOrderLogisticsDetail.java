package com.ecochain.ledger.model;

import java.util.Date;

public class ShopOrderLogisticsDetail {
    private Integer id;

    private String logisticsNo;

    private String logisticsMsg;

    private String logisticsDetailHash;

    private Date createTime;

    private Date modifyTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo == null ? null : logisticsNo.trim();
    }

    public String getLogisticsMsg() {
        return logisticsMsg;
    }

    public void setLogisticsMsg(String logisticsMsg) {
        this.logisticsMsg = logisticsMsg == null ? null : logisticsMsg.trim();
    }

    public String getLogisticsDetailHash() {
        return logisticsDetailHash;
    }

    public void setLogisticsDetailHash(String logisticsDetailHash) {
        this.logisticsDetailHash = logisticsDetailHash == null ? null : logisticsDetailHash.trim();
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
}