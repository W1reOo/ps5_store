package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Ps5DailyRevenuePoint;
import com.ruoyi.system.domain.Ps5ShopRevenueOverview;

/**
 * 商城统计（销售额等）
 */
public interface IPs5ShopStatService {

    Ps5ShopRevenueOverview getRevenueOverview();

    List<Ps5DailyRevenuePoint> getDailyRevenue(String beginDate, String endDate);
}

