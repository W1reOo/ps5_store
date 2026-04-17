package com.ruoyi.web.controller.shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.Ps5ShopRevenueOverview;
import com.ruoyi.system.service.IPs5ShopStatService;

/**
 * 销售额统计
 */
@RestController
@RequestMapping("/shop")
public class ShopRevenueController extends BaseController {

    @Autowired
    private IPs5ShopStatService statService;

    @PreAuthorize("@ss.hasRole('shop_owner') and @ss.hasPermi('shop:revenue:list')")
    @Log(title = "销售额统计", businessType = BusinessType.OTHER)
    @GetMapping("/admin/revenue/overview")
    public AjaxResult overview() {
        Ps5ShopRevenueOverview data = statService.getRevenueOverview();
        return success(data);
    }

    /**
     * 按日营业额（付款日），用于趋势图
     */
    @PreAuthorize("@ss.hasRole('shop_owner') and @ss.hasPermi('shop:revenue:list')")
    @GetMapping("/admin/revenue/trend")
    public AjaxResult trend(@RequestParam String beginDate, @RequestParam String endDate) {
        if (StringUtils.isEmpty(beginDate) || StringUtils.isEmpty(endDate)) {
            return error("请选择开始与结束日期");
        }
        return success(statService.getDailyRevenue(beginDate, endDate));
    }
}

