package com.ruoyi.system.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 游戏评论（支持楼中楼）
 */
public class Ps5GameComment {

    private Long commentId;
    private Long gameId;
    private Long userId;
    /** 发表时快照，列表展示用 */
    private String userName;
    /** 评论者头像（来自 sys_user，列表展示用，随用户更新头像变化） */
    private String userAvatar;
    /** 父评论，null 表示主楼 */
    private Long parentId;
    /** 楼根评论 ID，同楼内一致 */
    private Long rootId;
    /** 被回复用户（楼中楼展示「回复 @xxx」） */
    private Long replyToUserId;
    private String replyToUserName;
    private String content;
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 子回复（仅接口输出使用） */
    private List<Ps5GameComment> children = new ArrayList<>();

    public Long getCommentId() { return commentId; }
    public void setCommentId(Long commentId) { this.commentId = commentId; }

    public Long getGameId() { return gameId; }
    public void setGameId(Long gameId) { this.gameId = gameId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public Long getRootId() { return rootId; }
    public void setRootId(Long rootId) { this.rootId = rootId; }

    public Long getReplyToUserId() { return replyToUserId; }
    public void setReplyToUserId(Long replyToUserId) { this.replyToUserId = replyToUserId; }

    public String getReplyToUserName() { return replyToUserName; }
    public void setReplyToUserName(String replyToUserName) { this.replyToUserName = replyToUserName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }

    public List<Ps5GameComment> getChildren() { return children; }
    public void setChildren(List<Ps5GameComment> children) { this.children = children != null ? children : new ArrayList<>(); }
}
