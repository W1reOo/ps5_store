<template>
  <div class="detail-wrap">
    <div class="detail-back" @click="$router.back()">
      <i class="el-icon-arrow-left"></i> 返回商城
    </div>

    <div v-loading="loading" class="detail-main" v-if="game">
      <div class="detail-top">
        <!-- 左侧封面 -->
        <div class="detail-left">
          <img :src="game.coverImage || defaultCover" class="cover-img" :alt="game.gameName" @error="onImgError" />
          <div class="badge-row">
            <span v-if="game.isHot === '1'" class="badge hot">HOT</span>
            <span v-if="game.isNew === '1'" class="badge new">NEW</span>
            <span class="badge lang">{{ game.language }}</span>
            <span class="badge rating">{{ game.rating }}</span>
          </div>
        </div>

        <!-- 右侧信息 -->
        <div class="detail-right">
          <div class="game-category">{{ game.categoryName }}</div>
          <h1 class="game-title">{{ game.gameName }}</h1>
          <div class="game-meta">
            <span>发行商：{{ game.publisher }}</span>
            <span>开发商：{{ game.developer }}</span>
            <span>发行日期：{{ formatDate(game.releaseDate) }}</span>
            <span>销量：{{ game.sales }}</span>
          </div>
          <div class="price-block">
            <span class="price-main">¥{{ game.price }}</span>
            <span v-if="game.originalPrice > game.price" class="price-cross">¥{{ game.originalPrice }}</span>
            <span v-if="game.originalPrice > game.price" class="discount-tag">
              {{ Math.round((1 - game.price / game.originalPrice) * 100) }}% OFF
            </span>
          </div>
          <div class="stock-info">
            <span :class="game.stock > 0 ? 'in-stock' : 'out-stock'">
              {{ game.stock > 0 ? `库存 ${game.stock} 件` : '暂无库存' }}
            </span>
          </div>

          <!-- 数量选择 -->
          <div class="qty-row">
            <span class="qty-label">数量</span>
            <el-input-number v-model="qty" :min="1" :max="game.stock || 1" size="small" />
          </div>

          <div class="btn-row">
            <el-button type="primary" size="medium" icon="el-icon-shopping-cart-2"
                       :disabled="game.stock <= 0" @click="addCart">
              加入购物车
            </el-button>
            <el-button type="warning" size="medium" icon="el-icon-s-promotion"
                       :disabled="game.stock <= 0" @click="buyNow">
              立即购买
            </el-button>
            <el-button
              size="medium"
              :icon="isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'"
              :type="isFavorite ? 'warning' : 'default'"
              plain
              @click="toggleFavorite"
            >
              {{ isFavorite ? '已收藏' : '收藏' }}
            </el-button>
          </div>

          <!-- 描述 -->
          <div class="desc-block">
            <div class="desc-title">游戏介绍</div>
            <p class="desc-text">{{ game.description }}</p>
          </div>
        </div>
      </div>

      <!-- 玩家评论 -->
      <section class="comment-section" v-loading="commentsLoading">
        <div class="comment-section-title">
          <i class="el-icon-chat-dot-round"></i> 玩家评论
          <span v-if="commentTotal > 0" class="comment-count">（主楼 {{ commentTotal }}）</span>
        </div>

        <div v-if="isLoggedIn" class="comment-form">
          <div v-if="replyTarget" class="reply-hint">
            回复 <span class="reply-name">{{ replyTarget.userName }}</span>
            <el-button type="text" size="mini" class="reply-cancel" @click="clearReply">取消</el-button>
          </div>
          <el-input
            ref="commentInput"
            v-model="commentContent"
            type="textarea"
            :rows="3"
            maxlength="500"
            show-word-limit
            :placeholder="replyTarget ? '写下你的回复…' : '分享游玩体验、购买建议，文明交流～'"
          />
          <div class="comment-form-actions">
            <el-button type="primary" size="small" :loading="commentSubmitting" @click="submitComment">
              {{ replyTarget ? '发表回复' : '发表评论' }}
            </el-button>
          </div>
        </div>
        <div v-else class="comment-login-hint">
          <router-link :to="{ path: '/login', query: { redirect: $route.fullPath } }" class="login-link">登录</router-link>
          后参与讨论
        </div>

        <div v-if="commentList.length" class="comment-list">
          <comment-node
            v-for="c in commentList"
            :key="c.commentId"
            :node="c"
            :depth="0"
            :is-logged-in="isLoggedIn"
            @reply="onReplyComment"
          />
        </div>
        <div v-else-if="!commentsLoading" class="comment-empty">暂无评论，欢迎抢先发言</div>

        <div v-if="commentTotal > commentPageSize" class="comment-pagination">
          <el-pagination
            small
            background
            layout="prev, pager, next"
            :total="commentTotal"
            :page-size="commentPageSize"
            :current-page.sync="commentPage"
            @current-change="onCommentPageChange"
          />
        </div>
      </section>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getGameDetail, addToCart, getGameComments, addGameComment, getFavoriteGameIds, addFavorite, removeFavorite } from '@/api/shop'
import { getToken } from '@/utils/auth'
import { SHOP_DEFAULT_COVER, applyShopImageFallback } from '@/utils/shopImage'
import CommentNode from './CommentNode.vue'

export default {
  name: 'ShopDetail',
  components: { CommentNode },
  data() {
    return {
      loading: false,
      game: null,
      qty: 1,
      defaultCover: SHOP_DEFAULT_COVER,
      commentsLoading: false,
      commentList: [],
      commentTotal: 0,
      commentPage: 1,
      commentPageSize: 10,
      commentContent: '',
      commentSubmitting: false,
      replyTarget: null,
      favoritedGameIds: []
    }
  },
  computed: {
    ...mapGetters(['token']),
    isLoggedIn() {
      return !!this.token
    },
    isFavorite() {
      if (!this.game) return false
      const id = Number(this.game.gameId)
      return this.favoritedGameIds.some(x => Number(x) === id)
    }
  },
  created() {
    this.loadGame()
  },
  methods: {
    loadGame() {
      this.loading = true
      getGameDetail(this.$route.params.gameId).then(res => {
        this.game = res.data
        this.commentPage = 1
        this.loadComments()
        this.loadFavoriteIds()
      }).finally(() => { this.loading = false })
    },
    loadFavoriteIds() {
      if (!getToken()) {
        this.favoritedGameIds = []
        return
      }
      getFavoriteGameIds()
        .then(res => {
          this.favoritedGameIds = res.data || []
        })
        .catch(() => {
          this.favoritedGameIds = []
        })
    },
    toggleFavorite() {
      if (!getToken()) {
        this.$message.warning('请先登录后再收藏')
        this.$router.push({ path: '/login', query: { redirect: this.$route.fullPath } })
        return
      }
      const gid = this.game.gameId
      if (this.isFavorite) {
        removeFavorite(gid).then(() => {
          this.$message.success('已取消收藏')
          this.favoritedGameIds = this.favoritedGameIds.filter(x => Number(x) !== Number(gid))
        })
      } else {
        addFavorite({ gameId: gid }).then(() => {
          this.$message.success('已加入收藏')
          if (!this.favoritedGameIds.some(x => Number(x) === Number(gid))) {
            this.favoritedGameIds = [...this.favoritedGameIds, gid]
          }
        })
      }
    },
    loadComments() {
      if (!this.game) return
      this.commentsLoading = true
      getGameComments({
        gameId: this.game.gameId,
        pageNum: this.commentPage,
        pageSize: this.commentPageSize
      }).then(res => {
        this.commentList = res.rows || []
        this.commentTotal = res.total || 0
      }).finally(() => { this.commentsLoading = false })
    },
    onCommentPageChange(page) {
      this.commentPage = page
      this.loadComments()
    },
    onReplyComment(node) {
      this.replyTarget = node
      this.$nextTick(() => {
        const ref = this.$refs.commentInput
        if (ref && ref.$refs && ref.$refs.textarea) {
          ref.$refs.textarea.focus()
        }
      })
    },
    clearReply() {
      this.replyTarget = null
    },
    submitComment() {
      const text = (this.commentContent || '').trim()
      if (!text) {
        this.$message.warning('请输入评论内容')
        return
      }
      if (!getToken()) {
        this.$message.warning('请先登录')
        return
      }
      const payload = { gameId: this.game.gameId, content: text }
      if (this.replyTarget) {
        payload.parentId = this.replyTarget.commentId
      }
      this.commentSubmitting = true
      addGameComment(payload).then(() => {
        this.$message.success(this.replyTarget ? '回复成功' : '发表成功')
        this.commentContent = ''
        this.replyTarget = null
        this.commentPage = 1
        this.loadComments()
      }).finally(() => { this.commentSubmitting = false })
    },
    formatDate(d) { return d ? d.substring(0, 10) : '-' },
    checkLogin() {
      if (!getToken()) { this.$message.warning('请先登录'); return false }
      return true
    },
    addCart() {
      if (!this.checkLogin()) return
      addToCart({ gameId: this.game.gameId, quantity: this.qty }).then(() => {
        this.$message.success('已加入购物车 🎮')
      })
    },
    buyNow() {
      if (!this.checkLogin()) return
      addToCart({ gameId: this.game.gameId, quantity: this.qty }).then(() => {
        this.$router.push('/shop/cart')
      })
    },
    onImgError(e) { applyShopImageFallback(e, this.defaultCover) }
  }
}
</script>

<style scoped>
.detail-wrap { min-height: 100vh; background: #0f0f1a; color: #eee; padding: 24px; }
.detail-back { color: #aaa; cursor: pointer; margin-bottom: 20px; font-size: 14px; display: inline-flex; align-items: center; gap: 4px; }
.detail-back:hover { color: #fff; }
.detail-main { max-width: 1100px; margin: 0 auto; }
.detail-top { display: flex; gap: 40px; flex-wrap: wrap; }
.detail-left { flex-shrink: 0; width: 360px; }
.cover-img { width: 100%; border-radius: 16px; box-shadow: 0 12px 40px rgba(0,0,0,0.6); }
.badge-row { display: flex; gap: 8px; margin-top: 12px; flex-wrap: wrap; }
.badge { padding: 3px 10px; border-radius: 6px; font-size: 12px; font-weight: bold; }
.badge.hot { background: #e74c3c; color: #fff; }
.badge.new { background: #27ae60; color: #fff; }
.badge.lang { background: #2980b9; color: #fff; }
.badge.rating { background: #8e44ad; color: #fff; }
.detail-right { flex: 1; min-width: 300px; }
.game-category { color: #888; font-size: 13px; margin-bottom: 8px; }
.game-title { font-size: 28px; font-weight: bold; color: #fff; margin: 0 0 16px; }
.game-meta { display: flex; flex-wrap: wrap; gap: 12px; font-size: 13px; color: #aaa; margin-bottom: 20px; }
.price-block { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.price-main { font-size: 36px; font-weight: bold; color: #f39c12; }
.price-cross { font-size: 18px; color: #666; text-decoration: line-through; }
.discount-tag { background: #e74c3c; color: #fff; padding: 2px 8px; border-radius: 6px; font-size: 13px; }
.stock-info { margin-bottom: 20px; }
.in-stock { color: #27ae60; font-size: 14px; }
.out-stock { color: #e74c3c; font-size: 14px; }
.qty-row { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; }
.qty-label { color: #aaa; font-size: 14px; }
.btn-row { display: flex; gap: 12px; margin-bottom: 28px; }
.desc-block { border-top: 1px solid #2a2a4a; padding-top: 20px; }
.desc-title { font-size: 16px; font-weight: bold; margin-bottom: 10px; color: #ccc; }
.desc-text { font-size: 14px; color: #aaa; line-height: 1.8; white-space: pre-line; }

/* 评论区 */
.comment-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #2a2a4a;
}
.comment-section-title {
  font-size: 18px;
  font-weight: bold;
  color: #e0e0e0;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.comment-count { font-size: 14px; font-weight: normal; color: #888; }
.comment-form { margin-bottom: 20px; }
.comment-form >>> .el-textarea__inner {
  background: #1a1a2e;
  border-color: #3a3a5a;
  color: #eee;
}
.comment-form-actions { margin-top: 10px; text-align: right; }
.comment-login-hint {
  padding: 14px 16px;
  background: #1a1a2e;
  border-radius: 8px;
  border: 1px dashed #3a3a5a;
  color: #888;
  font-size: 14px;
  margin-bottom: 20px;
}
.comment-login-hint .login-link { color: #5dade2; font-weight: 600; }
.reply-hint {
  font-size: 13px;
  color: #888;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.reply-name { color: #5dade2; font-weight: 600; }
.reply-cancel { padding: 0 !important; color: #999 !important; }
.comment-list { display: flex; flex-direction: column; gap: 14px; }
.comment-empty { text-align: center; color: #666; font-size: 14px; padding: 24px; }
.comment-pagination { margin-top: 16px; display: flex; justify-content: center; }
.comment-pagination >>> .el-pagination.is-background .el-pager li:not(.disabled).active { background-color: #409eff; }
</style>
