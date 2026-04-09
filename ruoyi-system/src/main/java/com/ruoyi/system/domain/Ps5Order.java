package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class Ps5Order extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long orderId;
    private String orderNo;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payTime;

    private String expressCompany;  // 快递公司
    private String trackingNo;      // 物流单号

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deliverTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishTime;

    // 关联明细
    private List<Ps5OrderItem> items;

    // 下单时前端传入的购物车ID列表（用于清空购物车）
    private List<Long> cartIds;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getReceiverAddress() { return receiverAddress; }
    public void setReceiverAddress(String receiverAddress) { this.receiverAddress = receiverAddress; }

    public Date getPayTime() { return payTime; }
    public void setPayTime(Date payTime) { this.payTime = payTime; }

    public String getExpressCompany() { return expressCompany; }
    public void setExpressCompany(String expressCompany) { this.expressCompany = expressCompany; }

    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }

    public Date getDeliverTime() { return deliverTime; }
    public void setDeliverTime(Date deliverTime) { this.deliverTime = deliverTime; }

    public Date getFinishTime() { return finishTime; }
    public void setFinishTime(Date finishTime) { this.finishTime = finishTime; }

    public List<Ps5OrderItem> getItems() { return items; }
    public void setItems(List<Ps5OrderItem> items) { this.items = items; }

    public List<Long> getCartIds() { return cartIds; }
    public void setCartIds(List<Long> cartIds) { this.cartIds = cartIds; }
}
