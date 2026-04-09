<template>
  <div class="orders-wrap">
    <div class="page-header">
      <div class="back-btn" @click="$router.push('/shop/home')"><i class="el-icon-arrow-left"></i> 返回商城</div>
      <h2>我的订单</h2>
    </div>

    <div v-loading="loading" class="orders-body">
      <div v-if="orders.length === 0" class="empty">
        <i class="el-icon-document"></i><p>暂无订单</p>
        <el-button type="primary" @click="$router.push('/shop/home')">去购物</el-button>
      </div>

      <div v-for="order in orders" :key="order.orderId" class="order-card">
        <div class="order-header">
          <span class="order-no">订单编号：{{ order.orderNo }}</span>
          <span :class="['order-status', `status-${order.status}`]">{{ statusMap[order.status] }}</span>
        </div>
        <div class="order-items">
          <div class="order-item" v-for="item in order.items" :key="item.itemId">
            <img :src="item.coverImage || defaultCover" class="item-img" @error="onImgError" />
            <div class="item-info">
              <div class="item-name">{{ item.gameName }}</div>
              <div class="item-meta">¥{{ item.price }} × {{ item.quantity }}</div>
            </div>
            <div class="item-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          </div>
        </div>
        <div v-if="showLogistics(order)" class="order-logistics">
          <div class="logistics-title"><i class="el-icon-truck"></i> 物流信息</div>
          <div v-if="order.deliverTime" class="logistics-row muted">发货时间：{{ order.deliverTime }}</div>
          <div v-if="order.expressCompany" class="logistics-row">
            <span class="lbl">快递公司</span>
            <span class="val">{{ order.expressCompany }}</span>
          </div>
          <div v-if="order.trackingNo" class="logistics-row">
            <span class="lbl">物流单号</span>
            <span class="val tracking">{{ order.trackingNo }}</span>
            <el-button type="text" size="mini" class="btn-copy" @click="copyTracking(order.trackingNo)">复制</el-button>
          </div>
          <div v-if="!order.expressCompany && !order.trackingNo" class="logistics-empty">
            暂无运单信息，如有疑问请联系客服
          </div>
        </div>
        <div class="order-footer">
          <span class="order-time">下单时间：{{ order.createTime }}</span>
          <div class="footer-right">
            <span class="order-amount">合计：<strong>¥{{ order.totalAmount }}</strong></span>
            <el-button v-if="order.status === '0'" type="primary" size="mini" @click="doPay(order)">立即付款</el-button>
            <el-button v-if="order.status === '0'" type="danger" size="mini" plain @click="doCancel(order)">取消订单</el-button>
            <el-button v-if="order.status === '1'" type="warning" size="mini" plain @click="doRefund(order)">申请退款</el-button>
            <el-button v-if="order.status === '2'" type="success" size="mini" @click="doFinish(order)">确认收货</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { getMyOrders, payOrder, cancelOrder, finishOrder, requestRefund } from '@/api/shop'
import { SHOP_DEFAULT_COVER, applyShopImageFallback } from '@/utils/shopImage'

export default {
  name: 'ShopOrders',
  data() {
    return {
      loading: false,
      orders: [],
      statusMap: { '0': '待付款', '1': '待发货', '2': '已发货', '3': '已完成', '4': '已取消', '5': '退款申请中', '6': '已退款' },
      defaultCover: SHOP_DEFAULT_COVER
    }
  },
  created() { this.loadOrders() },
  methods: {
    showLogistics(order) {
      const s = String(order.status)
      return s === '2' || s === '3'
    },
    copyTracking(text) {
      if (!text) return
      const done = () => this.$message.success('已复制单号')
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(done).catch(() => this.fallbackCopy(text, done))
      } else {
        this.fallbackCopy(text, done)
      }
    },
    fallbackCopy(text, done) {
      const ta = document.createElement('textarea')
      ta.value = text
      ta.style.cssText = 'position:fixed;left:-9999px'
      document.body.appendChild(ta)
      ta.select()
      try {
        document.execCommand('copy')
        done()
      } catch (e) {
        this.$message.error('复制失败，请手动复制')
      }
      document.body.removeChild(ta)
    },
    loadOrders() {
      this.loading = true
      getMyOrders().then(res => {
        this.orders = res.data || []
      }).finally(() => { this.loading = false })
    },
    doPay(order) {
      this.$confirm('确认支付此订单？', '支付确认', { type: 'warning' }).then(() => {
        payOrder(order.orderId).then(() => {
          this.$message.success('支付成功！')
          this.loadOrders()
        })
      })
    },
    doCancel(order) {
      this.$confirm('确认取消此订单？', '取消确认', { type: 'warning' }).then(() => {
        cancelOrder(order.orderId).then(() => {
          this.$message.success('订单已取消')
          this.loadOrders()
        })
      })
    },
    doFinish(order) {
      this.$confirm('确认已收到商品？确认后订单将完成。', '确认收货', { type: 'warning' }).then(() => {
        finishOrder(order.orderId).then(() => {
          this.$message.success('收货成功，订单已完成！')
          this.loadOrders()
        })
      })
    },
    doRefund(order) {
      this.$confirm('确认提交退款申请？提交后请等待后台审核。', '退款申请', { type: 'warning' }).then(() => {
        requestRefund(order.orderId).then(() => {
          this.$message.success('退款申请已提交')
          this.loadOrders()
        })
      })
    },
    onImgError(e) { applyShopImageFallback(e, this.defaultCover) }
  }
}
</script>

<style scoped>
.orders-wrap { min-height: 100vh; background: #0f0f1a; color: #eee; padding: 24px; }
.page-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.back-btn { color: #aaa; cursor: pointer; font-size: 14px; display: flex; align-items: center; gap: 4px; }
.back-btn:hover { color: #fff; }
.page-header h2 { margin: 0; color: #fff; }
.orders-body { max-width: 900px; margin: 0 auto; }
.empty { text-align: center; padding: 80px; color: #555; }
.empty i { font-size: 64px; display: block; margin-bottom: 16px; }
.order-card { background: #1a1a2e; border-radius: 12px; margin-bottom: 16px; overflow: hidden; border: 1px solid #2a2a4a; }
.order-header { display: flex; justify-content: space-between; align-items: center; padding: 14px 20px; border-bottom: 1px solid #2a2a4a; }
.order-no { color: #aaa; font-size: 13px; }
.order-status { font-size: 13px; font-weight: bold; padding: 2px 10px; border-radius: 12px; }
.status-0 { background: #f39c1220; color: #f39c12; }
.status-1 { background: #27ae6020; color: #27ae60; }
.status-2 { background: #2980b920; color: #2980b9; }
.status-3 { background: #8e44ad20; color: #8e44ad; }
.status-4 { background: #7f8c8d20; color: #7f8c8d; }
.status-5 { background: #e67e2220; color: #e67e22; }
.status-6 { background: #95a5a620; color: #95a5a6; }
.order-items { padding: 12px 20px; }
.order-item { display: flex; align-items: center; gap: 14px; padding: 8px 0; border-bottom: 1px solid #2a2a4a22; }
.order-item:last-child { border-bottom: none; }
.item-img { width: 64px; height: 64px; object-fit: cover; border-radius: 6px; }
.item-info { flex: 1; }
.item-name { font-size: 14px; color: #ddd; margin-bottom: 4px; }
.item-meta { font-size: 13px; color: #888; }
.item-total { color: #f39c12; font-weight: bold; }
.order-logistics {
  padding: 12px 20px;
  background: rgba(0, 0, 0, 0.2);
  border-top: 1px solid #2a2a4a;
}
.logistics-title {
  font-size: 13px;
  font-weight: 600;
  color: #5dade2;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.logistics-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px 12px;
  font-size: 13px;
  margin-top: 6px;
}
.logistics-row.muted { color: #888; font-size: 12px; margin-top: 0; }
.logistics-row .lbl { color: #888; flex-shrink: 0; }
.logistics-row .val { color: #ddd; word-break: break-all; }
.logistics-row .val.tracking { font-family: ui-monospace, monospace; letter-spacing: 0.02em; }
.btn-copy { color: #5dade2 !important; padding: 0 4px !important; }
.logistics-empty { font-size: 12px; color: #666; margin-top: 4px; }
.order-footer { display: flex; justify-content: space-between; align-items: center; padding: 12px 20px; background: #15152a; }
.order-time { font-size: 12px; color: #888; }
.footer-right { display: flex; align-items: center; gap: 12px; }
.order-amount { font-size: 14px; color: #eee; }
.order-amount strong { color: #f39c12; font-size: 18px; }
</style>
