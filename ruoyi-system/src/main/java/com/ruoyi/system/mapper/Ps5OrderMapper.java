package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.Ps5Order;
import com.ruoyi.system.domain.Ps5OrderItem;

public interface Ps5OrderMapper {
    List<Ps5Order> selectOrderList(Ps5Order order);
    List<Ps5Order> selectOrderListByUserId(Long userId);
    Ps5Order selectOrderById(Long orderId);
    Ps5Order selectOrderByNo(String orderNo);
    int insertOrder(Ps5Order order);
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") String status);
    /** 待付款 -> 待发货，必须写入 pay_time */
    int payOrder(@Param("orderId") Long orderId);
    /** 已付款 -> 退款申请中 */
    int requestRefund(@Param("orderId") Long orderId, @Param("userId") Long userId);
    /** 退款申请中 -> 已退款 */
    int approveRefund(@Param("orderId") Long orderId);
    int deliverOrderWithTracking(Ps5Order order);
    int insertOrderItem(Ps5OrderItem item);
    List<Ps5OrderItem> selectOrderItemsByOrderId(Long orderId);
}
