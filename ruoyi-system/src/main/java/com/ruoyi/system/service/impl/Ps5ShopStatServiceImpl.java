package com.ruoyi.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.system.domain.Ps5DailyRevenuePoint;
import com.ruoyi.system.domain.Ps5GameRevenueRow;
import com.ruoyi.system.domain.Ps5ShopRevenueOverview;
import com.ruoyi.system.mapper.Ps5ShopStatMapper;
import com.ruoyi.system.service.IPs5ShopStatService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商城统计实现
 */
@Service
public class Ps5ShopStatServiceImpl implements IPs5ShopStatService {

    @Autowired
    private Ps5ShopStatMapper statMapper;

    @Override
    public Ps5ShopRevenueOverview getRevenueOverview() {
        Ps5ShopRevenueOverview overview = new Ps5ShopRevenueOverview();

        BigDecimal total = statMapper.selectTotalPaidRevenue();
        Long paidCount = statMapper.selectPaidOrderCount();
        List<Ps5GameRevenueRow> rows = statMapper.selectRevenueByGameTop3();

        overview.setTotalRevenue(total);
        overview.setPaidOrderCount(paidCount);
        overview.setGameRows(rows);
        return overview;
    }

    @Override
    public List<Ps5DailyRevenuePoint> getDailyRevenue(String beginDate, String endDate) {
        return statMapper.selectDailyRevenue(beginDate, endDate);
    }
}

