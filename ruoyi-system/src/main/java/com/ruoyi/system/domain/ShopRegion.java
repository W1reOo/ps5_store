package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class ShopRegion extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Excel(name = "地区ID")
    private Integer regionId;

    @Excel(name = "父级ID")
    private Integer parentId;

    @Excel(name = "地区名称")
    private String regionName;

    @Excel(name = "级别 1省 2市 3区县")
    private Integer level;

    @Excel(name = "排序")
    private Integer sortOrder;

    public Integer getRegionId() { return regionId; }
    public void setRegionId(Integer regionId) { this.regionId = regionId; }
    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }
    public String getRegionName() { return regionName; }
    public void setRegionName(String regionName) { this.regionName = regionName; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
