package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Ps5Cart;

public interface IPs5CartService {
    List<Ps5Cart> selectCartByUserId(Long userId);
    int addToCart(Long userId, Long gameId, Integer quantity);
    int updateCartQuantity(Long cartId, Integer quantity);
    int deleteCartById(Long cartId);
    int deleteCartByIds(Long[] cartIds);
}
