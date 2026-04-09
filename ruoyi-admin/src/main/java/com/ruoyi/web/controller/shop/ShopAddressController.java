package com.ruoyi.web.controller.shop;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.ShopAddress;
import com.ruoyi.system.service.IShopAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shop/address")
public class ShopAddressController extends BaseController {

    @Autowired
    private IShopAddressService addressService;

    /** 省市区数据：GET /shop/address/region?parentId=0 */
    @GetMapping("/region")
    public AjaxResult getRegion(@RequestParam(defaultValue = "0") Integer parentId) {
        return AjaxResult.success(addressService.getRegionsByParent(parentId));
    }

    /** 获取当前用户地址列表 */
    @GetMapping("/list")
    public AjaxResult list() {
        Long userId = SecurityUtils.getUserId();
        return AjaxResult.success(addressService.getAddressList(userId));
    }

    /** 新增地址 */
    @PostMapping
    public AjaxResult add(@RequestBody ShopAddress address) {
        Long userId = SecurityUtils.getUserId();
        addressService.addAddress(address, userId);
        return AjaxResult.success();
    }

    /** 修改地址 */
    @PutMapping
    public AjaxResult edit(@RequestBody ShopAddress address) {
        Long userId = SecurityUtils.getUserId();
        addressService.updateAddress(address, userId);
        return AjaxResult.success();
    }

    /** 删除地址 */
    @DeleteMapping("/{addressId}")
    public AjaxResult remove(@PathVariable Long addressId) {
        Long userId = SecurityUtils.getUserId();
        addressService.deleteAddress(addressId, userId);
        return AjaxResult.success();
    }

    /** 设为默认地址 */
    @PutMapping("/default/{addressId}")
    public AjaxResult setDefault(@PathVariable Long addressId) {
        Long userId = SecurityUtils.getUserId();
        addressService.setDefault(addressId, userId);
        return AjaxResult.success();
    }
}
