<template>
  <div class="thread-node">
    <div class="thread-node-card" :class="{ 'is-reply': depth > 0 }">
      <div v-if="node.replyToUserName" class="reply-to">
        回复 <span class="at">@{{ node.replyToUserName }}</span>
      </div>
      <div class="comment-meta">
        <div class="comment-user-block">
          <img class="comment-avatar" :src="avatarSrc" alt="" @error="onAvatarError" />
          <span class="comment-user">{{ node.userName }}</span>
        </div>
        <span class="comment-time">{{ node.createTime }}</span>
      </div>
      <div class="comment-body">{{ node.content }}</div>
      <div v-if="isLoggedIn" class="comment-actions">
        <el-button type="text" size="mini" @click="$emit('reply', node)">回复</el-button>
      </div>
    </div>
    <div v-if="node.children && node.children.length" class="thread-children">
      <comment-node
        v-for="ch in node.children"
        :key="ch.commentId"
        :node="ch"
        :depth="depth + 1"
        :is-logged-in="isLoggedIn"
        @reply="$emit('reply', $event)"
      />
    </div>
  </div>
</template>

<script>
import CommentNode from './CommentNode.vue'
import defAva from '@/assets/images/profile.jpg'
import { isHttp, isEmpty } from '@/utils/validate'

export default {
  name: 'CommentNode',
  components: { CommentNode },
  props: {
    node: { type: Object, required: true },
    depth: { type: Number, default: 0 },
    isLoggedIn: { type: Boolean, default: false }
  },
  computed: {
    avatarSrc() {
      const a = this.node.userAvatar
      if (!a || isEmpty(String(a).trim())) return defAva
      if (isHttp(a)) return a
      return process.env.VUE_APP_BASE_API + a
    }
  },
  methods: {
    onAvatarError(e) {
      e.target.src = defAva
    }
  }
}
</script>

<style scoped>
.thread-node { width: 100%; }
.thread-node-card {
  padding: 12px 14px;
  background: #1a1a2e;
  border-radius: 10px;
  border: 1px solid #2a2a4a;
}
.thread-node-card.is-reply {
  background: #15152a;
  border-color: #333355;
}
.reply-to {
  font-size: 12px;
  color: #888;
  margin-bottom: 6px;
}
.reply-to .at { color: #5dade2; font-weight: 600; }
.comment-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
  flex-wrap: wrap;
  gap: 8px;
}
.comment-user-block {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}
.comment-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  flex-shrink: 0;
  border: 2px solid rgba(93, 173, 226, 0.35);
  background: #0f0f1a;
}
.thread-node-card.is-reply .comment-avatar {
  width: 32px;
  height: 32px;
}
.comment-user { font-weight: 600; color: #5dade2; font-size: 14px; }
.comment-time { font-size: 12px; color: #666; }
.comment-body {
  font-size: 14px;
  color: #ccc;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
.comment-actions { margin-top: 6px; }
.thread-children {
  margin-top: 10px;
  padding-left: 12px;
  border-left: 2px solid #2a3a5a;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
</style>
