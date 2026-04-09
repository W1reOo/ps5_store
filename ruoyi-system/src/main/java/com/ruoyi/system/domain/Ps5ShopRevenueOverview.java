package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 商城销售额总览（付费订单）
 */
public class Ps5ShopRevenueOverview {

    /** 有效销售实付总额（status 1/2/3/5；退款申请中仍计，已退款 6 不计） */
    private BigDecimal totalRevenue;
    /** 有效销售订单笔数 */
    private Long paidOrderCount;
    /** 各游戏销量与销售额 */
    private List<Ps5GameRevenueRow> gameRows = new ArrayList<>();

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getPaidOrderCount() {
        return paidOrderCount;
    }

    public void setPaidOrderCount(Long paidOrderCount) {
        this.paidOrderCount = paidOrderCount;
    }

    public List<Ps5GameRevenueRow> getGameRows() {
        return gameRows;
    }

    public void setGameRows(List<Ps5GameRevenueRow> gameRows) {
        this.gameRows = gameRows != null ? gameRows : new ArrayList<>();
    }
}
