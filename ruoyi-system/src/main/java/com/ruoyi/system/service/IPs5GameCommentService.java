package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.Ps5GameComment;

public interface IPs5GameCommentService {

    /** 仅主楼（供 PageHelper 分页） */
    List<Ps5GameComment> selectTopLevelByGameId(Long gameId);

    /** 为本页主楼填充楼中楼 children */
    void attachReplies(List<Ps5GameComment> tops, Long gameId);

    int insertComment(Long gameId, String content, Long userId, String displayName, Long parentId);
}
