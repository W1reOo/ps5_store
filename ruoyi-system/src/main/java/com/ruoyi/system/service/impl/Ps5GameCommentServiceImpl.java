package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.domain.Ps5Game;
import com.ruoyi.system.domain.Ps5GameComment;
import com.ruoyi.system.mapper.Ps5GameCommentMapper;
import com.ruoyi.system.mapper.Ps5GameMapper;
import com.ruoyi.system.service.IPs5GameCommentService;

@Service
public class Ps5GameCommentServiceImpl implements IPs5GameCommentService {

    @Autowired
    private Ps5GameCommentMapper commentMapper;

    @Autowired
    private Ps5GameMapper gameMapper;

    @Override
    public List<Ps5GameComment> selectTopLevelByGameId(Long gameId) {
        return commentMapper.selectTopLevelByGameId(gameId);
    }

    @Override
    public void attachReplies(List<Ps5GameComment> tops, Long gameId) {
        if (tops == null || tops.isEmpty()) {
            return;
        }
        List<Long> rootIds = tops.stream().map(Ps5GameComment::getCommentId).collect(Collectors.toList());
        List<Ps5GameComment> replies = commentMapper.selectRepliesByRootIds(gameId, rootIds);
        buildTree(tops, replies);
    }

    private void buildTree(List<Ps5GameComment> tops, List<Ps5GameComment> replies) {
        Map<Long, Ps5GameComment> map = new LinkedHashMap<>();
        for (Ps5GameComment t : tops) {
            t.setChildren(new ArrayList<>());
            map.put(t.getCommentId(), t);
        }
        replies.sort(Comparator.comparing(Ps5GameComment::getCreateTime));
        for (Ps5GameComment r : replies) {
            r.setChildren(new ArrayList<>());
            map.put(r.getCommentId(), r);
            Ps5GameComment parent = map.get(r.getParentId());
            if (parent != null) {
                parent.getChildren().add(r);
            }
        }
    }

    @Override
    public int insertComment(Long gameId, String content, Long userId, String displayName, Long parentId) {
        if (content == null) {
            throw new ServiceException("评论内容不能为空");
        }
        String text = content.trim();
        if (text.isEmpty()) {
            throw new ServiceException("评论内容不能为空");
        }
        if (text.length() > 500) {
            throw new ServiceException("评论内容不超过500字");
        }
        Ps5Game game = gameMapper.selectGameById(gameId);
        if (game == null || !"0".equals(game.getStatus())) {
            throw new ServiceException("游戏不存在或已下架");
        }
        String name = displayName != null && !displayName.isEmpty() ? displayName : "用户";

        if (parentId == null) {
            Ps5GameComment c = new Ps5GameComment();
            c.setGameId(gameId);
            c.setUserId(userId);
            c.setUserName(name);
            c.setParentId(null);
            c.setRootId(null);
            c.setReplyToUserId(null);
            c.setReplyToUserName("");
            c.setContent(text);
            int n = commentMapper.insertComment(c);
            if (n > 0 && c.getCommentId() != null) {
                commentMapper.updateRootSelf(c.getCommentId());
            }
            return n;
        }

        Ps5GameComment parent = commentMapper.selectCommentById(parentId);
        if (parent == null || !"0".equals(parent.getStatus())) {
            throw new ServiceException("被回复的评论不存在");
        }
        if (!gameId.equals(parent.getGameId())) {
            throw new ServiceException("参数错误");
        }
        Long root = parent.getParentId() == null
                ? parent.getCommentId()
                : (parent.getRootId() != null ? parent.getRootId() : parent.getCommentId());

        Ps5GameComment c = new Ps5GameComment();
        c.setGameId(gameId);
        c.setUserId(userId);
        c.setUserName(name);
        c.setParentId(parentId);
        c.setRootId(root);
        c.setReplyToUserId(parent.getUserId());
        c.setReplyToUserName(parent.getUserName() != null ? parent.getUserName() : "");
        c.setContent(text);
        return commentMapper.insertComment(c);
    }
}
