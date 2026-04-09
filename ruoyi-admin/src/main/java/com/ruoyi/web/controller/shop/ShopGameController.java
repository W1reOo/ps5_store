package com.ruoyi.web.controller.shop;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Ps5Category;
import com.ruoyi.system.domain.Ps5Game;
import com.ruoyi.system.service.IPs5GameService;

@RestController
@RequestMapping("/shop")
public class ShopGameController extends BaseController {

    @Autowired
    private IPs5GameService gameService;

    // ==================== 公开接口（无需登录）====================

    @GetMapping("/game/list")
    public TableDataInfo gameList(Ps5Game game) {
        game.setStatus("0");
        startPage();
        List<Ps5Game> list = gameService.selectGameList(game);
        return getDataTable(list);
    }

    @GetMapping("/game/{gameId}")
    public AjaxResult gameDetail(@PathVariable Long gameId) {
        return success(gameService.selectGameById(gameId));
    }

    @GetMapping("/category/list")
    public AjaxResult categoryList() {
        return success(gameService.selectCategoryList());
    }

    // ==================== 管理后台接口（需要权限）====================

    @PreAuthorize("@ss.hasPermi('shop:game:list')")
    @GetMapping("/admin/game/list")
    public TableDataInfo adminGameList(Ps5Game game) {
        startPage();
        List<Ps5Game> list = gameService.selectGameList(game);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('shop:game:query')")
    @GetMapping("/admin/game/{gameId}")
    public AjaxResult adminGameDetail(@PathVariable Long gameId) {
        return success(gameService.selectGameById(gameId));
    }

    @PreAuthorize("@ss.hasPermi('shop:game:add')")
    @Log(title = "游戏管理", businessType = BusinessType.INSERT)
    @PostMapping("/admin/game")
    public AjaxResult addGame(@RequestBody Ps5Game game) {
        game.setCreateBy(getUsername());
        return toAjax(gameService.insertGame(game));
    }

    @PreAuthorize("@ss.hasPermi('shop:game:edit')")
    @Log(title = "游戏管理", businessType = BusinessType.UPDATE)
    @PutMapping("/admin/game")
    public AjaxResult editGame(@RequestBody Ps5Game game) {
        game.setUpdateBy(getUsername());
        return toAjax(gameService.updateGame(game));
    }

    @PreAuthorize("@ss.hasPermi('shop:game:remove')")
    @Log(title = "游戏管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/admin/game/{gameIds}")
    public AjaxResult deleteGames(@PathVariable Long[] gameIds) {
        return toAjax(gameService.deleteGameByIds(gameIds));
    }

    // 分类管理
    @PreAuthorize("@ss.hasPermi('shop:game:list')")
    @GetMapping("/admin/category/list")
    public AjaxResult adminCategoryList() {
        return success(gameService.selectCategoryList());
    }

    @PreAuthorize("@ss.hasPermi('shop:game:add')")
    @PostMapping("/admin/category")
    public AjaxResult addCategory(@RequestBody Ps5Category category) {
        category.setCreateBy(getUsername());
        return toAjax(gameService.insertCategory(category));
    }

    @PreAuthorize("@ss.hasPermi('shop:game:edit')")
    @PutMapping("/admin/category")
    public AjaxResult editCategory(@RequestBody Ps5Category category) {
        category.setUpdateBy(getUsername());
        return toAjax(gameService.updateCategory(category));
    }

    @PreAuthorize("@ss.hasPermi('shop:game:remove')")
    @Log(title = "分类管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/admin/category/{categoryId}")
    public AjaxResult deleteCategory(@PathVariable Long categoryId) {
        return toAjax(gameService.deleteCategoryById(categoryId));
    }
}
