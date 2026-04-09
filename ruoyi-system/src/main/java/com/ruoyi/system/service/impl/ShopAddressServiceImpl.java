package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.ShopAddress;
import com.ruoyi.system.domain.ShopRegion;
import com.ruoyi.system.mapper.ShopAddressMapper;
import com.ruoyi.system.mapper.ShopRegionMapper;
import com.ruoyi.system.service.IShopAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ShopAddressServiceImpl implements IShopAddressService {

    @Autowired
    private ShopRegionMapper regionMapper;

    @Autowired
    private ShopAddressMapper addressMapper;

    @Override
    public List<ShopRegion> getRegionsByParent(Integer parentId) {
        return regionMapper.selectByParentId(parentId);
    }

    @Override
    public List<ShopAddress> getAddressList(Long userId) {
        return addressMapper.selectByUserId(userId);
    }

    @Override
    @Transactional
    public int addAddress(ShopAddress address, Long userId) {
        address.setUserId(userId);
        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }
        // 如果是第一条地址或设为默认，先清除其他默认
        if (Integer.valueOf(1).equals(address.getIsDefault())) {
            addressMapper.clearDefault(userId);
        }
        // 若当前用户没有地址，自动设为默认
        if (addressMapper.selectByUserId(userId).isEmpty()) {
            address.setIsDefault(1);
        }
        return addressMapper.insert(address);
    }

    @Override
    @Transactional
    public int updateAddress(ShopAddress address, Long userId) {
        address.setUserId(userId);
        if (Integer.valueOf(1).equals(address.getIsDefault())) {
            addressMapper.clearDefault(userId);
        }
        return addressMapper.update(address);
    }

    @Override
    public int deleteAddress(Long addressId, Long userId) {
        ShopAddress existing = addressMapper.selectById(addressId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return 0;
        }
        return addressMapper.deleteById(addressId);
    }

    @Override
    @Transactional
    public int setDefault(Long addressId, Long userId) {
        ShopAddress existing = addressMapper.selectById(addressId);
        if (existing == null || !existing.getUserId().equals(userId)) {
            return 0;
        }
        addressMapper.clearDefault(userId);
        return addressMapper.setDefault(addressId);
    }
}
