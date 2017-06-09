package com.ecochain.ledger.model;


import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by LiShuo on 2016/10/28.
 */
public class ShopOrderGoods implements Serializable {
    private static final long serialVersionUID = 5848596142716836741L;
    private String activityId; //秒杀模板新增活动ID

    private Integer recId;

    private Integer isMutilPrice;

    private String shopOrderId;

    private String shopOrderNo;

    private Integer goodsId;

    private String skuInfo;

    private String isPromote; //是否火爆商品 1是 0否

    private String bussType;

    private String tradeHash;

    private String consignee;

    private String zipCode;

    private String province;

    private String city;

    private String area;

    private String mobile;

    private String address;

    private Integer skuId;

    private Integer supplierId;

    private String supplierName;

    private String goodsName;

    private String goodsSn;

    private Short goodsNumber;

    private BigDecimal payPrice;

    private String skuValue;

    private String goodsAttrId;

    private String userCode;

    private String userName;

    private String  csessionid;

    //与ShopOrderInfo 集成，加入bean 属性，在ShopOrderGoods resultMap中不配置 bean新增属性
    private String orderId;

    private String orderNo;

    private Integer userId;

    private String catId;

    private Integer addressId;

    private Integer orderStatus;

    private Integer shippingStatus;

    private Integer payStatus;

    private String goodsImg;

    private String postscript;

    private String shippingName;

    private String payName;

    private String howOos;

    private BigDecimal goodsAmount;

    private BigDecimal shippingFee;

    private BigDecimal integralMoney;

    private BigDecimal orderAmount;

    private Date addTime;

    private String createTime;

    private Date shippingTime;

    private String data;

    public String getBussType() {
        return bussType;
    }

    public void setBussType(String bussType) {
        this.bussType = bussType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getCsessionid() {
        return csessionid;
    }

    public void setCsessionid(String csessionid) {
        this.csessionid = csessionid;
    }

    public String getShopOrderNo() {
        return shopOrderNo;
    }

    public void setShopOrderNo(String shopOrderNo) {
        this.shopOrderNo = shopOrderNo;
    }

    public Integer getRecId() {
        return recId;
    }

    public void setRecId(Integer recId) {
        this.recId = recId;
    }

    public String getShopOrderId() {
        return shopOrderId;
    }

    public void setShopOrderId(String shopOrderId) {
        this.shopOrderId = shopOrderId;
    }

    public Integer getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
    }

    public String getSkuInfo() {
        return skuInfo;
    }

    public void setSkuInfo(String skuInfo) {
        this.skuInfo = skuInfo;
    }

    public Integer getSkuId() {
        return skuId;
    }

    public void setSkuId(Integer skuId) {
        this.skuId = skuId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public Integer getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Integer supplierId) {
        this.supplierId = supplierId;
    }

    public String getGoodsAttrId() {
        return goodsAttrId;
    }

    public void setGoodsAttrId(String goodsAttrId) {
        this.goodsAttrId = goodsAttrId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName == null ? null : goodsName.trim();
    }

    public String getGoodsSn() {
        return goodsSn;
    }

    public void setGoodsSn(String goodsSn) {
        this.goodsSn = goodsSn == null ? null : goodsSn.trim();
    }

    public Short getGoodsNumber() {
        return goodsNumber;
    }

    public void setGoodsNumber(Short goodsNumber) {
        this.goodsNumber = goodsNumber;
    }

    public BigDecimal getPayPrice() {
        return payPrice;
    }

    public void setPayPrice(BigDecimal payPrice) {
        this.payPrice = payPrice;
    }

    public String getSkuValue() {
        return skuValue;
    }

    public void setSkuValue(String skuValue) {
        this.skuValue = skuValue;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getShippingStatus() {
        return shippingStatus;
    }

    public void setShippingStatus(Integer shippingStatus) {
        this.shippingStatus = shippingStatus;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public String getPostscript() {
        return postscript;
    }

    public void setPostscript(String postscript) {
        this.postscript = postscript;
    }

    public String getShippingName() {
        return shippingName;
    }

    public void setShippingName(String shippingName) {
        this.shippingName = shippingName;
    }

    public String getPayName() {
        return payName;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public String getHowOos() {
        return howOos;
    }

    public void setHowOos(String howOos) {
        this.howOos = howOos;
    }

    public BigDecimal getGoodsAmount() {
        return goodsAmount;
    }

    public void setGoodsAmount(BigDecimal goodsAmount) {
        this.goodsAmount = goodsAmount;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getIntegralMoney() {
        return integralMoney;
    }

    public void setIntegralMoney(BigDecimal integralMoney) {
        this.integralMoney = integralMoney;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Date getShippingTime() {
        return shippingTime;
    }

    public void setShippingTime(Date shippingTime) {
        this.shippingTime = shippingTime;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    public String getIsPromote() {
        return isPromote;
    }

    public void setIsPromote(String isPromote) {
        this.isPromote = isPromote;
    }

    public String getTradeHash() {
        return tradeHash;
    }

    public void setTradeHash(String tradeHash) {
        this.tradeHash = tradeHash;
    }

    public Integer getIsMutilPrice() {
        return isMutilPrice;
    }

    public void setIsMutilPrice(Integer isMutilPrice) {
        this.isMutilPrice = isMutilPrice;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}