package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Ps5Order;

public interface IPs5OrderService {
    List<Ps5Order> selectOrderList(Ps5Order order);
    List<Ps5Order> selectMyOrders(Long userId);
    Ps5Order selectOrderById(Long orderId);
    Ps5Order submitOrder(Ps5Order order, Long userId, String username);
    int payOrder(Long orderId);
    int deliverOrder(Long orderId);
    int deliverOrderWithTracking(Long orderId, String expressCompany, String trackingNo);
    int cancelOrder(Long orderId);
    int finishOrder(Long orderId, Long userId);
    int requestRefund(Long orderId, Long userId);
    int approveRefund(Long orderId);
}
