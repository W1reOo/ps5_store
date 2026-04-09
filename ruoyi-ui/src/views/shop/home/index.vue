<template>
  <div class="shop-wrap">
    <header class="top-nav">
      <div class="nav-inner">
        <div class="brand-box" @click="$router.push('/shop/home')">
          <svg class="ps-logo" viewBox="0 0 120 40" xmlns="http://www.w3.org/2000/svg">
            <text x="2" y="30" font-family="Arial Black,sans-serif" font-size="32" font-weight="900" fill="white" letter-spacing="-1">PS5</text>
            <rect x="68" y="6" width="3" height="28" rx="1.5" fill="#0070d1"/>
            <rect x="74" y="2" width="3" height="32" rx="1.5" fill="#0070d1"/>
            <rect x="80" y="8" width="3" height="24" rx="1.5" fill="#0070d1"/>
            <rect x="86" y="14" width="3" height="18" rx="1.5" fill="#0070d1"/>
          </svg>
          <div>
            <div class="brand-title">PS5 游戏光盘商城</div>
            <div class="brand-subtitle">正版现货 · 次世代体验</div>
          </div>
        </div>
        <div class="nav-actions">
          <el-input
            v-model="query.gameName"
            placeholder="搜索《战神》《蜘蛛侠2》《最终幻想》..."
            prefix-icon="el-icon-search"
            clearable
            @keyup.enter.native="loadGames"
            @clear="loadGames"
            class="nav-search"
          />
          <router-link to="/shop/cart">
            <el-badge :value="cartCount" :max="99" class="cart-badge">
              <el-button icon="el-icon-shopping-cart-2">购物车</el-button>
            </el-badge>
          </router-link>
          <router-link to="/shop/orders">
            <el-button icon="el-icon-document">我的订单</el-button>
          </router-link>
          <el-button type="primary" @click="handleAccountAction">{{ hasToken ? '账号中心' : '登录下单' }}</el-button>
        </div>
      </div>
    </header>

    <section class="hero-section">
      <div class="hero-bg"></div>
      <div class="page-container hero-content">
        <div class="hero-left">
          <div class="hero-badge">PS5 DISC STORE 1.0</div>
          <h1 class="hero-title">把热门 PS5 光盘，一次买齐</h1>
          <p class="hero-desc">
            覆盖动作冒险、RPG、体育、格斗、赛车等多个品类，正版现货、极速发货，支持购物车下单与完整订单流转。
          </p>
          <div class="hero-actions">
            <el-button type="primary" size="medium" @click="scrollToGoods">立即选购</el-button>
            <el-button size="medium" plain @click="focusHot">查看热门榜单</el-button>
          </div>
          <div class="hero-stats">
            <div class="stat-card">
              <span class="stat-num">{{ total }}</span>
              <span class="stat-label">在售游戏</span>
            </div>
            <div class="stat-card">
              <span class="stat-num">{{ categories.length }}</span>
              <span class="stat-label">精选分类</span>
            </div>
            <div class="stat-card">
              <span class="stat-num">{{ hotGames.length }}</span>
              <span class="stat-label">热门推荐</span>
            </div>
          </div>
        </div>

        <div class="hero-right" v-if="heroGame">
          <div class="featured-card" @click="goDetail(heroGame.gameId)">
            <div class="featured-cover">
              <img :src="heroGame.coverImage || defaultCover" :alt="heroGame.gameName" @error="onImgError" />
            </div>
            <div class="featured-info">
              <div class="featured-label">本周主推</div>
              <div class="featured-name">{{ heroGame.gameName }}</div>
              <div class="featured-meta">{{ heroGame.categoryName }} · 销量 {{ heroGame.sales }}</div>
              <div class="featured-price-row">
                <span class="featured-price">¥{{ heroGame.price }}</span>
                <span v-if="heroGame.originalPrice > heroGame.price" class="featured-old">¥{{ heroGame.originalPrice }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="page-container feature-strip">
      <div class="feature-item">
        <i class="el-icon-medal"></i>
        <div>
          <div class="feature-title">正版光盘</div>
          <div class="feature-text">精选 PS5 热门大作，支持长期版本迭代</div>
        </div>
      </div>
      <div class="feature-item">
        <i class="el-icon-truck"></i>
        <div>
          <div class="feature-title">快速发货</div>
          <div class="feature-text">下单后支持订单流转，发货状态可追踪</div>
        </div>
      </div>
      <div class="feature-item">
        <i class="el-icon-s-data"></i>
        <div>
          <div class="feature-title">真实数据</div>
          <div class="feature-text">商品、购物车、订单全部落库，不是静态页面</div>
        </div>
      </div>
    </section>

    <section class="page-container hot-section" v-if="hotGames.length">
      <div class="section-head">
        <div>
          <div class="section-kicker">HOT PICKS</div>
          <h2 class="section-title">热门榜单</h2>
        </div>
        <el-button type="text" @click="setTag('isHot', '1')">查看更多热门</el-button>
      </div>
      <div class="hot-grid">
        <div v-for="game in hotGames" :key="game.gameId" class="hot-card" @click="goDetail(game.gameId)">
          <div class="hot-rank">TOP {{ hotGames.indexOf(game) + 1 }}</div>
          <img :src="game.coverImage || defaultCover" class="hot-image" @error="onImgError" />
          <div class="hot-info">
            <div class="hot-name">{{ game.gameName }}</div>
            <div class="hot-meta">{{ game.categoryName }} · 销量 {{ game.sales }}</div>
            <div class="hot-price">¥{{ game.price }}</div>
          </div>
        </div>
      </div>
    </section>

    <section class="page-container goods-section" ref="goodsSection">
      <div class="section-head">
        <div>
          <div class="section-kicker">DISC CATALOG</div>
          <h2 class="section-title">全站商品</h2>
        </div>
        <div class="section-actions">
          <el-button type="primary" icon="el-icon-search" @click="loadGames">搜索</el-button>
        </div>
      </div>

      <div class="goods-layout">
        <aside class="filter-panel">
          <div class="panel-title">分类筛选</div>
          <div class="category-pills">
            <span :class="['pill-item', { active: !currentCategory }]" @click="filterCategory(null)">全部游戏</span>
            <span
              v-for="cat in categories"
              :key="cat.categoryId"
              :class="['pill-item', { active: currentCategory === cat.categoryId }]"
              @click="filterCategory(cat.categoryId)"
            >
              {{ cat.categoryName }}
            </span>
          </div>

          <div class="panel-title">精选标签</div>
          <div class="tag-bar">
            <el-tag :type="!query.isHot && !query.isNew ? 'primary' : 'info'" @click="setTag(null, null)" class="tag-item">全部</el-tag>
            <el-tag :type="query.isHot === '1' ? 'primary' : 'info'" @click="setTag('isHot', '1')" class="tag-item">热门</el-tag>
            <el-tag :type="query.isNew === '1' ? 'primary' : 'info'" @click="setTag('isNew', '1')" class="tag-item">新品</el-tag>
          </div>

          <div class="panel-box">
            <div class="mini-title">商城说明</div>
            <ul class="tips-list">
              <li>首页和详情页可免登录浏览</li>
              <li>购物车、下单、订单管理需要登录</li>
              <li>账号中心可查看全部订单状态</li>
            </ul>
          </div>
        </aside>

        <section class="goods-main">
          <div v-loading="loading" class="game-grid">
            <div v-for="game in games" :key="game.gameId" class="game-card" @click="goDetail(game.gameId)">
              <div class="card-cover">
                <img :src="game.coverImage || defaultCover" :alt="game.gameName" @error="onImgError" />
                <div class="card-tags">
                  <span v-if="game.isHot === '1'" class="tag-hot">HOT</span>
                  <span v-if="game.isNew === '1'" class="tag-new">NEW</span>
                </div>
              </div>
              <div class="card-info">
                <div class="card-category">{{ game.categoryName }}</div>
                <div class="card-name" :title="game.gameName">{{ game.gameName }}</div>
                <div class="card-meta">
                  <span>{{ game.publisher || '官方发行' }}</span>
                  <span>库存 {{ game.stock }}</span>
                </div>
                <div class="card-price">
                  <span class="price-now">¥{{ game.price }}</span>
                  <span v-if="game.originalPrice > game.price" class="price-old">¥{{ game.originalPrice }}</span>
                </div>
                <div class="card-bottom">
                  <span class="sales-text">销量 {{ game.sales }}</span>
                  <el-button
                    type="primary"
                    size="mini"
                    icon="el-icon-shopping-cart-2"
                    @click.stop="addCart(game)"
                    :disabled="game.stock <= 0"
                  >
                    {{ game.stock > 0 ? '加入购物车' : '暂无库存' }}
                  </el-button>
                </div>
              </div>
            </div>
            <div v-if="!loading && games.length === 0" class="empty-tip">
              <i class="el-icon-box"></i>
              <p>暂无相关游戏</p>
            </div>
          </div>

          <div class="pagination-wrap">
            <el-pagination
              background
              layout="prev, pager, next, total"
              :total="total"
              :page-size="query.pageSize"
              :current-page="query.pageNum"
              @current-change="handlePageChange"
            />
          </div>
        </section>
      </div>
    </section>

    <footer class="shop-footer">
      <div class="page-container footer-inner">
        <div>
          <div class="footer-title">PS5 游戏光盘商城</div>
          <div class="footer-text">基于若依框架构建的前后端一体化商城 v1.0</div>
        </div>
        <div class="footer-links">
          <span @click="$router.push('/shop/home')">商城首页</span>
          <span @click="$router.push('/shop/cart')">购物车</span>
          <span @click="$router.push('/shop/orders')">我的订单</span>
          <span @click="$router.push('/shop/account')">账号中心</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { getGameList, getCategoryList, addToCart, getCartList } from '@/api/shop'
import { getToken } from '@/utils/auth'
import { SHOP_DEFAULT_COVER, applyShopImageFallback } from '@/utils/shopImage'

export default {
  name: 'ShopHome',
  data() {
    return {
      loading: false,
      games: [],
      total: 0,
      categories: [],
      currentCategory: null,
      cartCount: 0,
      query: { gameName: '', pageNum: 1, pageSize: 12, isHot: '', isNew: '' },
      defaultCover: SHOP_DEFAULT_COVER
    }
  },
  computed: {
    hasToken() {
      return !!getToken()
    },
    heroGame() {
      return this.games[0] || null
    },
    hotGames() {
      return this.games.filter(item => item.isHot === '1').slice(0, 4)
    }
  },
  created() {
    this.loadCategories()
    this.loadGames()
    this.loadCartCount()
  },
  methods: {
    loadCategories() {
      getCategoryList().then(res => {
        this.categories = res.data || []
      })
    },
    loadGames() {
      this.loading = true
      const params = { ...this.query }
      if (this.currentCategory) params.categoryId = this.currentCategory
      getGameList(params).then(res => {
        this.games = res.rows || []
        this.total = res.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    loadCartCount() {
      if (!this.hasToken) {
        this.cartCount = 0
        return
      }
      getCartList().then(res => {
        this.cartCount = (res.data || []).length
      }).catch(() => {
        this.cartCount = 0
      })
    },
    filterCategory(id) {
      this.currentCategory = id
      this.query.pageNum = 1
      this.loadGames()
    },
    setTag(key, val) {
      this.query.isHot = ''
      this.query.isNew = ''
      if (key) this.query[key] = val
      this.query.pageNum = 1
      this.loadGames()
    },
    handlePageChange(page) {
      this.query.pageNum = page
      this.loadGames()
    },
    goDetail(gameId) {
      this.$router.push(`/shop/detail/${gameId}`)
    },
    addCart(game) {
      if (!this.hasToken) {
        this.$message.warning('请先登录再加入购物车')
        return
      }
      addToCart({ gameId: game.gameId, quantity: 1 }).then(() => {
        this.$message.success('已加入购物车')
        this.loadCartCount()
      })
    },
    handleAccountAction() {
      if (this.hasToken) {
        this.$router.push('/shop/account')
        return
      }
      this.$router.push('/login?redirect=%2Fshop%2Faccount')
    },
    scrollToGoods() {
      const el = this.$refs.goodsSection
      if (el && el.scrollIntoView) {
        el.scrollIntoView({ behavior: 'smooth', block: 'start' })
      }
    },
    focusHot() {
      this.setTag('isHot', '1')
      this.$nextTick(() => {
        window.scrollTo({ top: 560, behavior: 'smooth' })
      })
    },
    onImgError(e) {
      applyShopImageFallback(e, this.defaultCover)
    }
  }
}
</script>

<style scoped>
.shop-wrap {
  min-height: 100vh;
  background:
    radial-gradient(circle at top right, rgba(37, 99, 235, 0.18), transparent 28%),
    linear-gradient(180deg, #07111f 0%, #0b1220 35%, #0f172a 100%);
  color: #e5e7eb;
  font-family: 'Microsoft YaHei', sans-serif;
}

.page-container {
  width: min(1380px, calc(100% - 48px));
  margin: 0 auto;
}

.top-nav {
  position: sticky;
  top: 0;
  z-index: 20;
  backdrop-filter: blur(12px);
  background: rgba(7, 17, 31, 0.82);
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
}

.nav-inner {
  width: min(1380px, calc(100% - 48px));
  margin: 0 auto;
  min-height: 76px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.brand-box {
  display: flex;
  align-items: center;
  gap: 14px;
  cursor: pointer;
}

.ps-logo {
  height: 40px;
  filter: brightness(0) invert(1);
}

.brand-title {
  font-size: 18px;
  font-weight: 700;
  color: #fff;
}

.brand-subtitle {
  font-size: 12px;
  color: #94a3b8;
  margin-top: 2px;
}

.nav-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.nav-search {
  width: 360px;
}

.hero-section {
  position: relative;
  overflow: hidden;
  padding: 52px 0 24px;
}

.hero-bg {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(120deg, rgba(29, 78, 216, 0.22) 0%, rgba(37, 99, 235, 0.04) 40%, transparent 100%),
    radial-gradient(circle at left center, rgba(59, 130, 246, 0.18), transparent 25%);
  pointer-events: none;
}

.hero-content {
  position: relative;
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  gap: 26px;
  align-items: center;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
  color: #bfdbfe;
  background: rgba(37, 99, 235, 0.18);
  border: 1px solid rgba(96, 165, 250, 0.22);
}

.hero-title {
  margin: 18px 0 14px;
  font-size: 48px;
  line-height: 1.15;
  color: #fff;
  font-weight: 800;
}

.hero-desc {
  max-width: 700px;
  color: #94a3b8;
  font-size: 16px;
  line-height: 1.9;
}

.hero-actions {
  margin-top: 24px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-stats {
  display: flex;
  gap: 14px;
  margin-top: 28px;
  flex-wrap: wrap;
}

.stat-card {
  min-width: 134px;
  padding: 18px 20px;
  border-radius: 18px;
  background: rgba(15, 23, 42, 0.72);
  border: 1px solid rgba(148, 163, 184, 0.12);
  box-shadow: 0 20px 50px rgba(2, 6, 23, 0.24);
}

.stat-num {
  display: block;
  font-size: 28px;
  font-weight: 800;
  color: #fff;
}

.stat-label {
  display: block;
  margin-top: 8px;
  font-size: 13px;
  color: #94a3b8;
}

.featured-card {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0;
  min-height: 360px;
  border-radius: 24px;
  overflow: hidden;
  background: linear-gradient(145deg, rgba(15, 23, 42, 0.95), rgba(30, 41, 59, 0.88));
  border: 1px solid rgba(148, 163, 184, 0.12);
  box-shadow: 0 24px 80px rgba(2, 6, 23, 0.35);
  cursor: pointer;
}

.featured-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.featured-info {
  padding: 28px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.featured-label {
  font-size: 12px;
  letter-spacing: 1.2px;
  color: #60a5fa;
  margin-bottom: 12px;
}

.featured-name {
  font-size: 28px;
  line-height: 1.3;
  color: #fff;
  font-weight: 800;
}

.featured-meta {
  margin-top: 12px;
  color: #94a3b8;
  font-size: 14px;
}

.featured-price-row {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.featured-price {
  color: #fbbf24;
  font-size: 34px;
  font-weight: 800;
}

.featured-old {
  color: #64748b;
  font-size: 16px;
  text-decoration: line-through;
}

.feature-strip {
  margin-top: 24px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px 22px;
  border-radius: 18px;
  background: rgba(15, 23, 42, 0.72);
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.feature-item i {
  font-size: 26px;
  color: #60a5fa;
}

.feature-title {
  color: #fff;
  font-size: 15px;
  font-weight: 700;
}

.feature-text {
  margin-top: 4px;
  color: #94a3b8;
  font-size: 13px;
}

.hot-section,
.goods-section {
  margin-top: 34px;
}

.section-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  margin-bottom: 18px;
}

.section-kicker {
  color: #60a5fa;
  font-size: 12px;
  letter-spacing: 1.4px;
  margin-bottom: 6px;
}

.section-title {
  margin: 0;
  color: #fff;
  font-size: 28px;
  font-weight: 800;
}

.hot-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 18px;
}

.hot-card {
  padding: 14px;
  border-radius: 20px;
  background: rgba(15, 23, 42, 0.72);
  border: 1px solid rgba(148, 163, 184, 0.12);
  cursor: pointer;
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

.hot-card:hover,
.game-card:hover {
  transform: translateY(-4px);
  border-color: rgba(96, 165, 250, 0.36);
  box-shadow: 0 18px 44px rgba(2, 6, 23, 0.28);
}

.hot-rank {
  display: inline-flex;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(239, 68, 68, 0.16);
  color: #fca5a5;
  font-size: 12px;
  margin-bottom: 12px;
}

.hot-image {
  width: 100%;
  height: 260px;
  object-fit: cover;
  object-position: center top;
  border-radius: 14px;
}

.hot-info {
  margin-top: 12px;
}

.hot-name {
  color: #fff;
  font-size: 16px;
  font-weight: 700;
}

.hot-meta {
  margin-top: 6px;
  color: #94a3b8;
  font-size: 13px;
}

.hot-price {
  margin-top: 12px;
  color: #fbbf24;
  font-size: 22px;
  font-weight: 800;
}

.goods-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 22px;
}

.filter-panel {
  align-self: start;
  position: sticky;
  top: 94px;
  padding: 22px;
  border-radius: 20px;
  background: rgba(15, 23, 42, 0.72);
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.panel-title {
  color: #fff;
  font-size: 15px;
  font-weight: 700;
  margin-bottom: 14px;
}

.category-pills {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.pill-item {
  display: inline-flex;
  align-items: center;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(30, 41, 59, 0.9);
  color: #cbd5e1;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pill-item.active,
.pill-item:hover {
  background: linear-gradient(90deg, #2563eb, #3b82f6);
  color: #fff;
}

.tag-bar {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag-item {
  cursor: pointer;
}

.panel-box {
  margin-top: 22px;
  padding: 16px;
  border-radius: 16px;
  background: rgba(30, 41, 59, 0.75);
}

.mini-title {
  color: #fff;
  font-size: 14px;
  font-weight: 700;
  margin-bottom: 12px;
}

.tips-list {
  padding-left: 18px;
  margin: 0;
  color: #94a3b8;
  font-size: 13px;
  line-height: 1.9;
}

.game-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 18px;
  min-height: 220px;
}

.game-card {
  background: rgba(15, 23, 42, 0.78);
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
  border: 1px solid rgba(148, 163, 184, 0.12);
}

.card-cover {
  position: relative;
  padding-top: 133.33%;
  background: #111827;
}

.card-cover img {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-tags {
  position: absolute;
  top: 10px;
  left: 10px;
  display: flex;
  gap: 6px;
}

.tag-hot,
.tag-new {
  padding: 3px 8px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  color: #fff;
}

.tag-hot {
  background: #ef4444;
}

.tag-new {
  background: #22c55e;
}

.card-info {
  padding: 16px;
}

.card-category {
  font-size: 12px;
  color: #60a5fa;
  margin-bottom: 8px;
}

.card-name {
  font-size: 16px;
  font-weight: 700;
  color: #fff;
  line-height: 1.5;
  min-height: 48px;
}

.card-meta {
  margin-top: 10px;
  display: flex;
  justify-content: space-between;
  gap: 10px;
  color: #94a3b8;
  font-size: 12px;
}

.card-price {
  margin-top: 14px;
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price-now {
  font-size: 22px;
  font-weight: 800;
  color: #fbbf24;
}

.price-old {
  font-size: 13px;
  color: #64748b;
  text-decoration: line-through;
}

.card-bottom {
  margin-top: 14px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.sales-text {
  font-size: 12px;
  color: #94a3b8;
}

.empty-tip {
  grid-column: 1 / -1;
  text-align: center;
  padding: 60px;
  color: #64748b;
  font-size: 16px;
}

.empty-tip i {
  font-size: 48px;
  display: block;
  margin-bottom: 12px;
}

.pagination-wrap {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.shop-footer {
  margin-top: 42px;
  border-top: 1px solid rgba(148, 163, 184, 0.12);
  background: rgba(2, 6, 23, 0.5);
}

.footer-inner {
  min-height: 88px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.footer-title {
  color: #fff;
  font-size: 16px;
  font-weight: 700;
}

.footer-text {
  margin-top: 4px;
  color: #94a3b8;
  font-size: 13px;
}

.footer-links {
  display: flex;
  gap: 18px;
  flex-wrap: wrap;
  color: #cbd5e1;
  font-size: 13px;
}

.footer-links span {
  cursor: pointer;
}

.footer-links span:hover {
  color: #60a5fa;
}

@media (max-width: 1200px) {
  .hero-content,
  .goods-layout,
  .hot-grid,
  .feature-strip {
    grid-template-columns: 1fr;
  }

  .filter-panel {
    position: static;
  }
}

@media (max-width: 768px) {
  .page-container,
  .nav-inner {
    width: calc(100% - 24px);
  }

  .nav-search {
    width: 100%;
  }

  .hero-title {
    font-size: 34px;
  }

  .featured-card {
    grid-template-columns: 1fr;
  }

  .card-bottom,
  .section-head,
  .footer-inner {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
