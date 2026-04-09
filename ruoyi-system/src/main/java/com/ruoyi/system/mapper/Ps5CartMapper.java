package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Ps5Cart;

public interface Ps5CartMapper {
    List<Ps5Cart> selectCartByUserId(Long userId);
    Ps5Cart selectCartByUserAndGame(Long userId, Long gameId);
    Ps5Cart selectCartById(Long cartId);
    int insertCart(Ps5Cart cart);
    int updateCartQuantity(Long cartId, Integer quantity);
    int deleteCartById(Long cartId);
    int deleteCartByIds(Long[] cartIds);
    int deleteCartByUserId(Long userId);
}
