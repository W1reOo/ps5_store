package com.ruoyi.system.domain;

import java.math.BigDecimal;

/**
 * 按游戏汇总的付费销售额（来自已付款订单明细）
 */
public class Ps5GameRevenueRow {

    private Long gameId;
    private String gameName;
    private Integer soldQuantity;
    private BigDecimal revenue;

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public Integer getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(Integer soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
