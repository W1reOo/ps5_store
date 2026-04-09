package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.domain.Ps5Category;
import com.ruoyi.system.domain.Ps5Game;
import com.ruoyi.system.mapper.Ps5CategoryMapper;
import com.ruoyi.system.mapper.Ps5GameMapper;
import com.ruoyi.system.service.IPs5GameService;

@Service
public class Ps5GameServiceImpl implements IPs5GameService {

    @Autowired
    private Ps5GameMapper gameMapper;

    @Autowired
    private Ps5CategoryMapper categoryMapper;

    @Override
    public List<Ps5Game> selectGameList(Ps5Game game) {
        return gameMapper.selectGameList(game);
    }

    @Override
    public Ps5Game selectGameById(Long gameId) {
        return gameMapper.selectGameById(gameId);
    }

    @Override
    public int insertGame(Ps5Game game) {
        // 销量由订单「付款成功」后自动累加，不允许在新增时手填
        game.setSales(0);
        if (game.getStatus() == null) game.setStatus("0");
        if (game.getIsHot() == null) game.setIsHot("0");
        if (game.getIsNew() == null) game.setIsNew("1");
        return gameMapper.insertGame(game);
    }

    @Override
    public int updateGame(Ps5Game game) {
        // 销量由付款自动统计，不允许通过后台编辑覆盖
        game.setSales(null);
        return gameMapper.updateGame(game);
    }

    @Override
    public int deleteGameByIds(Long[] gameIds) {
        return gameMapper.deleteGameByIds(gameIds);
    }

    @Override
    public List<Ps5Category> selectCategoryList() {
        Ps5Category cat = new Ps5Category();
        cat.setStatus("0");
        return categoryMapper.selectCategoryList(cat);
    }

    @Override
    public Ps5Category selectCategoryById(Long categoryId) {
        return categoryMapper.selectCategoryById(categoryId);
    }

    @Override
    public int insertCategory(Ps5Category category) {
        return categoryMapper.insertCategory(category);
    }

    @Override
    public int updateCategory(Ps5Category category) {
        return categoryMapper.updateCategory(category);
    }

    @Override
    public int deleteCategoryByIds(Long[] categoryIds) {
        return categoryMapper.deleteCategoryByIds(categoryIds);
    }

    @Override
    public int deleteCategoryById(Long categoryId) {
        return categoryMapper.deleteCategoryByIds(new Long[]{ categoryId });
    }
}
