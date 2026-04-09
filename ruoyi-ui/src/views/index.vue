<template>
  <div class="app-container ps5-dashboard" v-loading="loading">
    <div class="dash-hero">
      <div class="hero-text">
        <h2>PS5 商城控制台</h2>
        <p class="hero-sub">
          欢迎回来，<strong>{{ displayName }}</strong>。在此查看经营概况，并快速进入商品与订单管理。
        </p>
      </div>
    </div>

    <el-row :gutter="16" class="stat-row">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-games" @click.native="go('/shop/game')">
          <div class="stat-icon"><i class="el-icon-s-goods" /></div>
          <div class="stat-body">
            <div class="stat-value">{{ gameTotal }}</div>
            <div class="stat-label">上架游戏</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-cat" @click.native="go('/shop/category')">
          <div class="stat-icon"><i class="el-icon-menu" /></div>
          <div class="stat-body">
            <div class="stat-value">{{ categoryCount }}</div>
            <div class="stat-label">商品分类</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-order" @click.native="go('/shop/order')">
          <div class="stat-icon"><i class="el-icon-s-order" /></div>
          <div class="stat-body">
            <div class="stat-value">{{ orderTotal }}</div>
            <div class="stat-label">订单总数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card stat-ship" :class="{ warn: pendingShip > 0 }" @click.native="goOrderPending">
          <div class="stat-icon"><i class="el-icon-truck" /></div>
          <div class="stat-body">
            <div class="stat-value">{{ pendingShip }}</div>
            <div class="stat-label">待发货</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="lower-row">
      <el-col :xs="24" :lg="14">
        <el-card shadow="never" class="panel-card">
          <div slot="header" class="panel-header">
            <span><i class="el-icon-s-operation" /> 快捷入口</span>
          </div>
          <div class="quick-actions">
            <el-button type="primary" plain icon="el-icon-menu" @click="go('/shop/category')">商品分类</el-button>
            <el-button type="primary" plain icon="el-icon-s-goods" @click="go('/shop/game')">游戏管理</el-button>
            <el-button type="primary" plain icon="el-icon-s-order" @click="go('/shop/order')">订单管理</el-button>
            <el-button type="primary" plain icon="el-icon-s-finance" @click="go('/shop/revenue')">销售额统计</el-button>
            <el-button type="success" plain icon="el-icon-monitor" @click="openShopFront">预览商城前台</el-button>
          </div>
          <p class="hint-text">预览商城将在新标签页打开，便于核对上架效果与前台展示。</p>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="never" class="panel-card">
          <div slot="header" class="panel-header">
            <span><i class="el-icon-info" /> 运营提示</span>
          </div>
          <ul class="tips-list">
            <li>待付款、待发货订单请及时在「订单管理」中处理。</li>
            <li>发货时请填写快递公司与物流单号，用户将在订单页查看物流信息。</li>
            <li>游戏详情页支持玩家评论与楼中楼回复，可在前台商品页体验。</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { adminGetGameList, adminGetCategoryList, adminGetOrderList } from '@/api/shop'

export default {
  name: 'Index',
  data() {
    return {
      loading: false,
      gameTotal: 0,
      categoryCount: 0,
      orderTotal: 0,
      pendingShip: 0
    }
  },
  computed: {
    ...mapGetters(['name', 'nickName']),
    displayName() {
      return (this.nickName && String(this.nickName).trim()) || this.name || '管理员'
    }
  },
  created() {
    this.loadStats()
  },
  methods: {
    go(path) {
      this.$router.push(path)
    },
    goOrderPending() {
      this.$router.push({ path: '/shop/order', query: { status: '1' } })
    },
    openShopFront() {
      const url = this.$router.resolve({ path: '/shop/home' }).href
      window.open(url, '_blank')
    },
    loadStats() {
      this.loading = true
      const done = () => { this.loading = false }
      Promise.all([
        adminGetGameList({ pageNum: 1, pageSize: 1 }).then(res => {
          this.gameTotal = res.total != null ? res.total : 0
        }),
        adminGetCategoryList().then(res => {
          const list = res.data || []
          this.categoryCount = list.length
        }),
        adminGetOrderList({ pageNum: 1, pageSize: 1 }).then(res => {
          this.orderTotal = res.total != null ? res.total : 0
        }),
        adminGetOrderList({ pageNum: 1, pageSize: 1, status: '1' }).then(res => {
          this.pendingShip = res.total != null ? res.total : 0
        })
      ]).catch(() => {
        this.$message.warning('部分统计数据加载失败，请确认已登录且具有商城管理权限')
      }).finally(done)
    }
  }
}
</script>

<style scoped lang="scss">
.ps5-dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.dash-hero {
  padding: 20px 24px;
  margin-bottom: 20px;
  border-radius: 8px;
  background: linear-gradient(135deg, #1e3a5f 0%, #2c5282 45%, #1a365d 100%);
  color: #fff;
  box-shadow: 0 4px 14px rgba(30, 58, 95, 0.35);

  h2 {
    margin: 0 0 8px;
    font-size: 22px;
    font-weight: 600;
    letter-spacing: 0.5px;
  }

  .hero-sub {
    margin: 0;
    font-size: 14px;
    opacity: 0.92;
    line-height: 1.6;

    strong {
      font-weight: 600;
    }
  }
}

.stat-row {
  margin-bottom: 16px;
}

.stat-card {
  cursor: pointer;
  border-radius: 8px;
  transition: transform 0.15s ease;
  display: flex;
  align-items: stretch;
  min-height: 96px;

  &:hover {
    transform: translateY(-2px);
  }

  ::v-deep .el-card__body {
    display: flex;
    align-items: center;
    padding: 16px;
    width: 100%;
  }

  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    color: #fff;
    flex-shrink: 0;
    margin-right: 14px;
  }

  .stat-body {
    flex: 1;
    min-width: 0;
  }

  .stat-value {
    font-size: 26px;
    font-weight: 700;
    line-height: 1.2;
    color: #303133;
  }

  .stat-label {
    font-size: 13px;
    color: #909399;
    margin-top: 4px;
  }

  &.stat-games .stat-icon {
    background: linear-gradient(145deg, #409eff, #66b1ff);
  }
  &.stat-cat .stat-icon {
    background: linear-gradient(145deg, #67c23a, #95d475);
  }
  &.stat-order .stat-icon {
    background: linear-gradient(145deg, #e6a23c, #f3c77a);
  }
  &.stat-ship .stat-icon {
    background: linear-gradient(145deg, #909399, #b1b3b8);
  }
  &.stat-ship.warn .stat-icon {
    background: linear-gradient(145deg, #f56c6c, #f89898);
  }
}

.lower-row {
  margin-top: 4px;
}

.panel-card {
  border-radius: 8px;
  min-height: 200px;

  .panel-header {
    font-weight: 600;
    color: #303133;

    i {
      margin-right: 6px;
      color: #409eff;
    }
  }
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 12px;
}

.hint-text {
  margin: 0;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  color: #606266;
  font-size: 13px;
  line-height: 1.85;

  li {
    margin-bottom: 6px;
  }
}
</style>
