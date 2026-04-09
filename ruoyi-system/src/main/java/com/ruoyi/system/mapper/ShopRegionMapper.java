package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.ShopRegion;
import java.util.List;

public interface ShopRegionMapper {
    /** 按父级ID查询子地区 */
    List<ShopRegion> selectByParentId(Integer parentId);
    /** 按ID查询单个地区 */
    ShopRegion selectById(Integer regionId);
}
