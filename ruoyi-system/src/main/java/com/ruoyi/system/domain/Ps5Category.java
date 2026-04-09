package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class Ps5Category extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Excel(name = "分类ID")
    private Long categoryId;

    @Excel(name = "分类名称")
    private String categoryName;

    private String icon;

    @Excel(name = "排序")
    private Integer sort;

    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
