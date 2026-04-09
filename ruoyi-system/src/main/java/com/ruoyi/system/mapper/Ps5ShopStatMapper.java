package com.ruoyi.system.mapper;

import java.math.BigDecimal;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.Ps5DailyRevenuePoint;
import com.ruoyi.system.domain.Ps5GameRevenueRow;

public interface Ps5ShopStatMapper {

    BigDecimal selectTotalPaidRevenue();

    Long selectPaidOrderCount();

    List<Ps5GameRevenueRow> selectRevenueByGameTop3();

    List<Ps5DailyRevenuePoint> selectDailyRevenue(@Param("beginDate") String beginDate, @Param("endDate") String endDate);
}
