package com.ruoyi.web.controller.shop;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Ps5Order;
import com.ruoyi.system.service.IPs5OrderService;

@RestController
@RequestMapping("/shop/order")
public class ShopOrderController extends BaseController {

    @Autowired
    private IPs5OrderService orderService;

    // ==================== 用户接口 ====================

    @GetMapping("/my")
    public AjaxResult myOrders() {
        List<Ps5Order> list = orderService.selectMyOrders(getUserId());
        return success(list);
    }

    @GetMapping("/{orderId}")
    public AjaxResult detail(@PathVariable Long orderId) {
        Ps5Order order = orderService.selectOrderById(orderId);
        if (order == null || !order.getUserId().equals(getUserId())) {
            return error("订单不存在");
        }
        return success(order);
    }

    @Log(title = "提交订单", businessType = BusinessType.INSERT)
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody Ps5Order order) {
        try {
            Ps5Order result = orderService.submitOrder(order, getUserId(), getUsername());
            return success(result);
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PutMapping("/pay/{orderId}")
    public AjaxResult pay(@PathVariable Long orderId) {
        try {
            return toAjax(orderService.payOrder(orderId));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PutMapping("/cancel/{orderId}")
    public AjaxResult cancel(@PathVariable Long orderId) {
        try {
            return toAjax(orderService.cancelOrder(orderId));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PutMapping("/finish/{orderId}")
    public AjaxResult finish(@PathVariable Long orderId) {
        try {
            return toAjax(orderService.finishOrder(orderId, getUserId()));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    @PutMapping("/refund/request/{orderId}")
    public AjaxResult requestRefund(@PathVariable Long orderId) {
        try {
            return toAjax(orderService.requestRefund(orderId, getUserId()));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }

    // ==================== 管理后台接口 ====================

    @PreAuthorize("@ss.hasPermi('shop:order:list')")
    @GetMapping("/admin/list")
    public TableDataInfo adminList(Ps5Order order) {
        startPage();
        List<Ps5Order> list = orderService.selectOrderList(order);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('shop:order:list')")
    @GetMapping("/admin/detail/{orderId}")
    public AjaxResult adminDetail(@PathVariable Long orderId) {
        Ps5Order order = orderService.selectOrderById(orderId);
        if (order == null) {
            return error("订单不存在");
        }
        return success(order);
    }

    @PreAuthorize("@ss.hasPermi('shop:order:deliver')")
    @Log(title = "订单发货", businessType = BusinessType.UPDATE)
    @PutMapping("/admin/deliver/{orderId}")
    public AjaxResult deliver(@PathVariable Long orderId,
                              @RequestBody(required = false) java.util.Map<String, String> body) {
        if (body != null && body.containsKey("trackingNo")) {
            return toAjax(orderService.deliverOrderWithTracking(
                    orderId,
                    body.getOrDefault("expressCompany", ""),
                    body.get("trackingNo")));
        }
        return toAjax(orderService.deliverOrder(orderId));
    }

    @PreAuthorize("@ss.hasPermi('shop:order:refund')")
    @Log(title = "订单退款", businessType = BusinessType.UPDATE)
    @PutMapping("/admin/refund/approve/{orderId}")
    public AjaxResult approveRefund(@PathVariable Long orderId) {
        try {
            return toAjax(orderService.approveRefund(orderId));
        } catch (Exception e) {
            return error(e.getMessage());
        }
    }
}
