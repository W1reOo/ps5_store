package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Ps5Category;

public interface Ps5CategoryMapper {
    List<Ps5Category> selectCategoryList(Ps5Category category);
    Ps5Category selectCategoryById(Long categoryId);
    int insertCategory(Ps5Category category);
    int updateCategory(Ps5Category category);
    int deleteCategoryByIds(Long[] categoryIds);
}
