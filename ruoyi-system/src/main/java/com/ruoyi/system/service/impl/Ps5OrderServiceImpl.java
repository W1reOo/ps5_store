package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.system.domain.Ps5Game;
import com.ruoyi.system.domain.Ps5Order;
import com.ruoyi.system.domain.Ps5OrderItem;
import com.ruoyi.system.mapper.Ps5CartMapper;
import com.ruoyi.system.mapper.Ps5GameMapper;
import com.ruoyi.system.mapper.Ps5OrderMapper;
import com.ruoyi.system.service.IPs5OrderService;

@Service
public class Ps5OrderServiceImpl implements IPs5OrderService {

    private static final AtomicInteger SEQ = new AtomicInteger(0);

    @Autowired
    private Ps5OrderMapper orderMapper;

    @Autowired
    private Ps5GameMapper gameMapper;

    @Autowired
    private Ps5CartMapper cartMapper;

    @Override
    public List<Ps5Order> selectOrderList(Ps5Order order) {
        List<Ps5Order> list = orderMapper.selectOrderList(order);
        list.forEach(o -> o.setItems(orderMapper.selectOrderItemsByOrderId(o.getOrderId())));
        return list;
    }

    @Override
    public List<Ps5Order> selectMyOrders(Long userId) {
        List<Ps5Order> list = orderMapper.selectOrderListByUserId(userId);
        list.forEach(o -> o.setItems(orderMapper.selectOrderItemsByOrderId(o.getOrderId())));
        return list;
    }

    @Override
    public Ps5Order selectOrderById(Long orderId) {
        Ps5Order order = orderMapper.selectOrderById(orderId);
        if (order != null) {
            order.setItems(orderMapper.selectOrderItemsByOrderId(orderId));
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Ps5Order submitOrder(Ps5Order order, Long userId, String username) {
        // 生成订单号
        String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", SEQ.incrementAndGet() % 10000);
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setCreateBy(username);

        // 计算总金额 & 构建明细
        BigDecimal total = BigDecimal.ZERO;
        for (Ps5OrderItem item : order.getItems()) {
            Ps5Game game = gameMapper.selectGameById(item.getGameId());
            if (game == null || !"0".equals(game.getStatus())) {
                throw new RuntimeException("游戏 [" + item.getGameId() + "] 不存在或已下架");
            }
            item.setGameName(game.getGameName());
            item.setCoverImage(game.getCoverImage());
            item.setPrice(game.getPrice());
            total = total.add(game.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

            // 防超卖：以条件更新影响行数为准（UPDATE ... WHERE stock >= quantity）
            int affected = gameMapper.updateGameStock(game.getGameId(), item.getQuantity());
            if (affected == 0) {
                throw new RuntimeException("游戏 [" + game.getGameName() + "] 库存不足，请刷新后重试");
            }
        }
        order.setTotalAmount(total);

        // 保存订单
        orderMapper.insertOrder(order);
        for (Ps5OrderItem item : order.getItems()) {
            item.setOrderId(order.getOrderId());
            orderMapper.insertOrderItem(item);
        }

        // 清空选中的购物车条目
        if (order.getCartIds() != null && !order.getCartIds().isEmpty()) {
            cartMapper.deleteCartByIds(order.getCartIds().toArray(new Long[0]));
        }
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int payOrder(Long orderId) {
        int rows = orderMapper.payOrder(orderId);
        if (rows == 0) {
            Ps5Order o = orderMapper.selectOrderById(orderId);
            if (o == null) {
                throw new RuntimeException("订单不存在");
            }
            throw new RuntimeException("订单状态不允许付款（仅待付款可支付）");
        }

        // 付款成功后才累加该游戏销量
        List<Ps5OrderItem> items = orderMapper.selectOrderItemsByOrderId(orderId);
        for (Ps5OrderItem item : items) {
            gameMapper.updateGameSales(item.getGameId(), item.getQuantity());
        }
        return rows;
    }

    @Override
    public int deliverOrder(Long orderId) {
        return orderMapper.updateOrderStatus(orderId, "2");
    }

    @Override
    public int deliverOrderWithTracking(Long orderId, String expressCompany, String trackingNo) {
        Ps5Order order = new Ps5Order();
        order.setOrderId(orderId);
        order.setExpressCompany(expressCompany);
        order.setTrackingNo(trackingNo);
        return orderMapper.deliverOrderWithTracking(order);
    }

    @Override
    public int finishOrder(Long orderId, Long userId) {
        Ps5Order order = orderMapper.selectOrderById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (!"2".equals(order.getStatus())) {
            throw new RuntimeException("只有已发货的订单才能确认收货");
        }
        return orderMapper.updateOrderStatus(orderId, "3");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int requestRefund(Long orderId, Long userId) {
        Ps5Order order = orderMapper.selectOrderById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        int rows = orderMapper.requestRefund(orderId, userId);
        if (rows == 0) {
            throw new RuntimeException("当前订单状态不允许申请退款（仅已付款可申请）");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approveRefund(Long orderId) {
        Ps5Order order = orderMapper.selectOrderById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!"5".equals(order.getStatus())) {
            throw new RuntimeException("仅退款申请中的订单可处理退款");
        }

        // 退款通过：库存回补 + 销量回退（从已付款统计中剔除）
        List<Ps5OrderItem> items = orderMapper.selectOrderItemsByOrderId(orderId);
        for (Ps5OrderItem item : items) {
            gameMapper.updateGameStock(item.getGameId(), -item.getQuantity());
            gameMapper.updateGameSales(item.getGameId(), -item.getQuantity());
        }

        int rows = orderMapper.approveRefund(orderId);
        if (rows == 0) {
            throw new RuntimeException("退款处理失败，请刷新后重试");
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelOrder(Long orderId) {
        Ps5Order order = orderMapper.selectOrderById(orderId);
        if (order == null) return 0;
        if ("2".equals(order.getStatus()) || "3".equals(order.getStatus()) || "5".equals(order.getStatus()) || "6".equals(order.getStatus())) {
            throw new RuntimeException("当前订单状态无法取消");
        }
        // 回滚库存
        List<Ps5OrderItem> items = orderMapper.selectOrderItemsByOrderId(orderId);
        boolean paid = "1".equals(order.getStatus());
        for (Ps5OrderItem item : items) {
            gameMapper.updateGameStock(item.getGameId(), -item.getQuantity());
            // 仅在已付款状态下回滚销量
            if (paid) {
                gameMapper.updateGameSales(item.getGameId(), -item.getQuantity());
            }
        }
        return orderMapper.updateOrderStatus(orderId, "4");
    }
}
