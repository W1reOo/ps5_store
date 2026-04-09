package com.ruoyi.system.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class Ps5Game extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long gameId;

    @Excel(name = "游戏名称")
    private String gameName;

    private Long categoryId;

    private String categoryName;

    @Excel(name = "发行商")
    private String publisher;

    @Excel(name = "开发商")
    private String developer;

    private String description;

    private String coverImage;

    @Excel(name = "售价")
    private BigDecimal price;

    @Excel(name = "原价")
    private BigDecimal originalPrice;

    @Excel(name = "库存")
    private Integer stock;

    @Excel(name = "销量")
    private Integer sales;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date releaseDate;

    private String language;

    private String rating;

    @Excel(name = "状态", readConverterExp = "0=上架,1=下架")
    private String status;

    private String isHot;

    private String isNew;

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public String getGameName() { return gameName; }
    public void setGameName(String gameName) { this.gameName = gameName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getDeveloper() { return developer; }
    public void setDeveloper(String developer) { this.developer = developer; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Integer getSales() { return sales; }
    public void setSales(Integer sales) { this.sales = sales; }

    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getIsHot() { return isHot; }
    public void setIsHot(String isHot) { this.isHot = isHot; }

    public String getIsNew() { return isNew; }
    public void setIsNew(String isNew) { this.isNew = isNew; }
}
