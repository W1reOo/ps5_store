package com.ruoyi.system.domain;

import java.math.BigDecimal;

/**
 * 按日营业额（有效销售订单）
 */
public class Ps5DailyRevenuePoint {

    /** 统计日 yyyy-MM-dd */
    private String statDate;
    private BigDecimal revenue;

    public String getStatDate() {
        return statDate;
    }

    public void setStatDate(String statDate) {
        this.statDate = statDate;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
