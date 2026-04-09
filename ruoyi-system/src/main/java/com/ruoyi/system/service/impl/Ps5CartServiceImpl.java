package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.Ps5Cart;
import com.ruoyi.system.mapper.Ps5CartMapper;
import com.ruoyi.system.service.IPs5CartService;

@Service
public class Ps5CartServiceImpl implements IPs5CartService {

    @Autowired
    private Ps5CartMapper cartMapper;

    @Override
    public List<Ps5Cart> selectCartByUserId(Long userId) {
        return cartMapper.selectCartByUserId(userId);
    }

    @Override
    public int addToCart(Long userId, Long gameId, Integer quantity) {
        Ps5Cart existing = cartMapper.selectCartByUserAndGame(userId, gameId);
        if (existing != null) {
            return cartMapper.updateCartQuantity(existing.getCartId(), existing.getQuantity() + quantity);
        }
        Ps5Cart cart = new Ps5Cart();
        cart.setUserId(userId);
        cart.setGameId(gameId);
        cart.setQuantity(quantity);
        return cartMapper.insertCart(cart);
    }

    @Override
    public int updateCartQuantity(Long cartId, Integer quantity) {
        return cartMapper.updateCartQuantity(cartId, quantity);
    }

    @Override
    public int deleteCartById(Long cartId) {
        return cartMapper.deleteCartById(cartId);
    }

    @Override
    public int deleteCartByIds(Long[] cartIds) {
        return cartMapper.deleteCartByIds(cartIds);
    }
}
