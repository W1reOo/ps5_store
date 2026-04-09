package com.ruoyi.web.controller.shop;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.system.domain.Ps5GameComment;
import com.ruoyi.system.service.IPs5GameCommentService;

@RestController
@RequestMapping("/shop/comment")
public class ShopCommentController extends BaseController {

    @Autowired
    private IPs5GameCommentService commentService;

    /**
     * 分页仅针对主楼；随后拉取本页主楼下的全部回复并组装 children 树
     */
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam(required = false) Long gameId) {
        if (gameId == null) {
            return getDataTable(Collections.emptyList());
        }
        startPage();
        List<Ps5GameComment> tops = commentService.selectTopLevelByGameId(gameId);
        TableDataInfo data = getDataTable(tops);
        commentService.attachReplies(tops, gameId);
        return data;
    }

    /**
     * 发表评论（需登录）；可选 parentId 表示回复某条（楼中楼）
     */
    @PostMapping
    public AjaxResult add(@RequestBody Map<String, Object> body) {
        if (body == null || !body.containsKey("gameId") || !body.containsKey("content")) {
            return error("参数不完整");
        }
        Long gameId = Long.valueOf(body.get("gameId").toString());
        String content = body.get("content") != null ? body.get("content").toString() : "";
        Long parentId = null;
        if (body.containsKey("parentId") && body.get("parentId") != null && !"".equals(String.valueOf(body.get("parentId")))) {
            parentId = Long.valueOf(body.get("parentId").toString());
        }
        LoginUser lu = getLoginUser();
        Long userId = lu.getUserId();
        String displayName = resolveDisplayName(lu.getUser());
        return toAjax(commentService.insertComment(gameId, content, userId, displayName, parentId));
    }

    private static String resolveDisplayName(SysUser user) {
        if (user == null) {
            return "用户";
        }
        String nick = user.getNickName();
        if (nick != null && !nick.trim().isEmpty()) {
            return nick.trim();
        }
        return user.getUserName() != null ? user.getUserName() : "用户";
    }
}
