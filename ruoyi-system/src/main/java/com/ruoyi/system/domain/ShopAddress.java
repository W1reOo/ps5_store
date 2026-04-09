package com.ruoyi.system.domain;

import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class ShopAddress extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @Excel(name = "地址ID")
    private Long addressId;

    @Excel(name = "用户ID")
    private Long userId;

    @Excel(name = "收货人")
    private String receiverName;

    @Excel(name = "联系电话")
    private String phone;

    @Excel(name = "省份ID")
    private Integer provinceId;

    @Excel(name = "省份名称")
    private String provinceName;

    @Excel(name = "城市ID")
    private Integer cityId;

    @Excel(name = "城市名称")
    private String cityName;

    @Excel(name = "区县ID")
    private Integer districtId;

    @Excel(name = "区县名称")
    private String districtName;

    @Excel(name = "详细地址")
    private String detail;

    @Excel(name = "备注")
    private String remark;

    @Excel(name = "是否默认 0否1是")
    private Integer isDefault;

    public Long getAddressId() { return addressId; }
    public void setAddressId(Long addressId) { this.addressId = addressId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getProvinceId() { return provinceId; }
    public void setProvinceId(Integer provinceId) { this.provinceId = provinceId; }
    public String getProvinceName() { return provinceName; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }
    public Integer getCityId() { return cityId; }
    public void setCityId(Integer cityId) { this.cityId = cityId; }
    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }
    public Integer getDistrictId() { return districtId; }
    public void setDistrictId(Integer districtId) { this.districtId = districtId; }
    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }
    public String getDetail() { return detail; }
    public void setDetail(String detail) { this.detail = detail; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Integer getIsDefault() { return isDefault; }
    public void setIsDefault(Integer isDefault) { this.isDefault = isDefault; }

    /** 拼接完整地址字符串 */
    public String getFullAddress() {
        return (provinceName == null ? "" : provinceName)
             + (cityName == null ? "" : cityName)
             + (districtName == null ? "" : districtName)
             + (detail == null ? "" : detail);
    }
}
