package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Ps5Category;
import com.ruoyi.system.domain.Ps5Game;

public interface IPs5GameService {
    List<Ps5Game> selectGameList(Ps5Game game);
    Ps5Game selectGameById(Long gameId);
    int insertGame(Ps5Game game);
    int updateGame(Ps5Game game);
    int deleteGameByIds(Long[] gameIds);

    List<Ps5Category> selectCategoryList();
    Ps5Category selectCategoryById(Long categoryId);
    int insertCategory(Ps5Category category);
    int updateCategory(Ps5Category category);
    int deleteCategoryByIds(Long[] categoryIds);
    int deleteCategoryById(Long categoryId);
}
