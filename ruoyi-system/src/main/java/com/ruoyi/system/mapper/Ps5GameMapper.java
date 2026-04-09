package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Ps5Game;

public interface Ps5GameMapper {
    List<Ps5Game> selectGameList(Ps5Game game);
    Ps5Game selectGameById(Long gameId);
    int insertGame(Ps5Game game);
    int updateGame(Ps5Game game);
    int deleteGameByIds(Long[] gameIds);
    int updateGameStock(Long gameId, int quantity);
    int updateGameSales(Long gameId, int quantity);
}
