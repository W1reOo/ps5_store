package com.ruoyi.system.service;

import com.ruoyi.system.domain.ShopAddress;
import com.ruoyi.system.domain.ShopRegion;
import java.util.List;

public interface IShopAddressService {
    /** 查询省/市/区 列表（根据父ID） */
    List<ShopRegion> getRegionsByParent(Integer parentId);

    /** 获取当前用户的收货地址列表 */
    List<ShopAddress> getAddressList(Long userId);

    /** 新增收货地址 */
    int addAddress(ShopAddress address, Long userId);

    /** 修改收货地址 */
    int updateAddress(ShopAddress address, Long userId);

    /** 删除收货地址 */
    int deleteAddress(Long addressId, Long userId);

    /** 设置默认地址 */
    int setDefault(Long addressId, Long userId);
}
