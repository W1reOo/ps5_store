package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.system.domain.Ps5GameComment;

public interface Ps5GameCommentMapper {

    Ps5GameComment selectCommentById(Long commentId);

    /** 主楼分页（仅 parent_id IS NULL） */
    List<Ps5GameComment> selectTopLevelByGameId(Long gameId);

    /** 当前页主楼下的全部回复，按时间升序 */
    List<Ps5GameComment> selectRepliesByRootIds(@Param("gameId") Long gameId, @Param("rootIds") List<Long> rootIds);

    int insertComment(Ps5GameComment comment);

    /** 主楼插入后：root_id = comment_id */
    int updateRootSelf(Long commentId);
}
