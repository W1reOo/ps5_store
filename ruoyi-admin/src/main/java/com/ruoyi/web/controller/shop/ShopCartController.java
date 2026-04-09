package com.ruoyi.web.controller.shop;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.Ps5Cart;
import com.ruoyi.system.service.IPs5CartService;

@RestController
@RequestMapping("/shop/cart")
public class ShopCartController extends BaseController {

    @Autowired
    private IPs5CartService cartService;

    @GetMapping("/list")
    public AjaxResult list() {
        List<Ps5Cart> list = cartService.selectCartByUserId(getUserId());
        return success(list);
    }

    @PostMapping("/add")
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        Long gameId = Long.valueOf(body.get("gameId").toString());
        Integer quantity = body.containsKey("quantity") ? Integer.valueOf(body.get("quantity").toString()) : 1;
        return toAjax(cartService.addToCart(getUserId(), gameId, quantity));
    }

    @PutMapping("/update")
    public AjaxResult update(@RequestBody Map<String, Object> body) {
        Long cartId = Long.valueOf(body.get("cartId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        if (quantity <= 0) {
            return toAjax(cartService.deleteCartById(cartId));
        }
        return toAjax(cartService.updateCartQuantity(cartId, quantity));
    }

    @DeleteMapping("/{cartId}")
    public AjaxResult delete(@PathVariable Long cartId) {
        return toAjax(cartService.deleteCartById(cartId));
    }
}
