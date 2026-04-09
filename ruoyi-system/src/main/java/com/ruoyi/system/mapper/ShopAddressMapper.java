package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ShopAddress;
import java.util.List;

public interface ShopAddressMapper {
    List<ShopAddress> selectByUserId(Long userId);
    ShopAddress selectById(Long addressId);
    int insert(ShopAddress address);
    int update(ShopAddress address);
    int deleteById(Long addressId);
    /** 清除用户所有默认标记 */
    int clearDefault(Long userId);
    /** 设置某条地址为默认 */
    int setDefault(Long addressId);
}
