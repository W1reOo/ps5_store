<template>
  <div class="account-wrap">
    <!-- 顶部导航 -->
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
          <el-button plain size="small" @click="$router.push('/shop/home')">
            <i class="el-icon-arrow-left"></i> 返回商城
          </el-button>
          <router-link to="/shop/cart">
            <el-badge :value="cartCount" :max="99" class="nav-badge">
              <el-button icon="el-icon-shopping-cart-2" size="small">购物车</el-button>
            </el-badge>
          </router-link>
          <router-link to="/shop/favorites">
            <el-badge :value="favoriteCount" :hidden="favoriteCount === 0" :max="99" class="nav-badge">
              <el-button icon="el-icon-star-off" size="small">我的收藏</el-button>
            </el-badge>
          </router-link>
          <router-link to="/shop/orders">
            <el-button icon="el-icon-document" size="small">我的订单</el-button>
          </router-link>
        </div>
      </div>
    </header>

    <div class="page-body">
      <!-- 左侧个人信息卡 -->
      <aside class="side-panel">
        <div class="profile-card">
          <div class="avatar-area">
            <div class="shop-avatar-wrap">
              <user-avatar :key="'ava-' + (avatar || '')" />
            </div>
            <div class="member-badge">
              <i class="el-icon-star-on"></i> PS5 会员
            </div>
          </div>
          <div class="user-name">{{ userNickName || userName }}</div>
          <div class="user-login">@{{ userName }}</div>
          <div class="user-since">注册于 {{ profile.createTime | shortDate }}</div>
          <div class="order-quick-stats">
            <div class="qs-item" @click="gotoOrderTab('0')">
              <div class="qs-num">{{ orderStats.pending }}</div>
              <div class="qs-label">待付款</div>
            </div>
            <div class="qs-divider"></div>
            <div class="qs-item" @click="gotoOrderTab('1')">
              <div class="qs-num">{{ orderStats.paid }}</div>
              <div class="qs-label">待发货</div>
            </div>
            <div class="qs-divider"></div>
            <div class="qs-item" @click="gotoOrderTab('2')">
              <div class="qs-num">{{ orderStats.shipped }}</div>
              <div class="qs-label">已发货</div>
            </div>
            <div class="qs-divider"></div>
            <div class="qs-item" @click="gotoOrderTab('3')">
              <div class="qs-num">{{ orderStats.done }}</div>
              <div class="qs-label">已完成</div>
            </div>
          </div>
        </div>

        <nav class="side-nav">
          <div
            v-for="item in navItems"
            :key="item.key"
            :class="['nav-item', { active: activeTab === item.key }]"
            @click="activeTab = item.key"
          >
            <i :class="item.icon"></i>
            <span>{{ item.label }}</span>
          </div>
        </nav>

        <div class="logout-area">
          <el-button type="danger" plain size="small" class="logout-btn" @click="handleLogout">
            <i class="el-icon-switch-button"></i> 退出登录
          </el-button>
        </div>
      </aside>

      <!-- 右侧内容区 -->
      <main class="main-content">

        <!-- 总览 -->
        <section v-if="activeTab === 'overview'">
          <div class="section-header">
            <div class="section-title">账号总览</div>
            <div class="section-sub">欢迎回来，{{ userNickName || userName }}！</div>
          </div>

          <div class="stat-cards">
            <div class="stat-card" @click="gotoOrderTab('0')">
              <div class="sc-icon pending"><i class="el-icon-time"></i></div>
              <div class="sc-body">
                <div class="sc-num">{{ orderStats.pending }}</div>
                <div class="sc-label">待付款</div>
              </div>
            </div>
            <div class="stat-card" @click="gotoOrderTab('1')">
              <div class="sc-icon paid"><i class="el-icon-box"></i></div>
              <div class="sc-body">
                <div class="sc-num">{{ orderStats.paid }}</div>
                <div class="sc-label">待发货</div>
              </div>
            </div>
            <div class="stat-card" @click="gotoOrderTab('2')">
              <div class="sc-icon shipped"><i class="el-icon-truck"></i></div>
              <div class="sc-body">
                <div class="sc-num">{{ orderStats.shipped }}</div>
                <div class="sc-label">配送中</div>
              </div>
            </div>
            <div class="stat-card" @click="gotoOrderTab('3')">
              <div class="sc-icon done"><i class="el-icon-circle-check"></i></div>
              <div class="sc-body">
                <div class="sc-num">{{ orderStats.done }}</div>
                <div class="sc-label">已完成</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="sc-icon total"><i class="el-icon-s-finance"></i></div>
              <div class="sc-body">
                <div class="sc-num">¥{{ orderStats.totalSpent }}</div>
                <div class="sc-label">累计消费</div>
              </div>
            </div>
          </div>

          <!-- 最近订单 -->
          <div class="block-title">最近订单</div>
          <div v-if="recentOrders.length === 0" class="empty-tip">
            <i class="el-icon-shopping-bag-1"></i>
            <p>暂无订单记录，去 <router-link to="/shop/home">商城</router-link> 选购吧！</p>
          </div>
          <div v-else class="order-list">
            <div v-for="order in recentOrders" :key="order.orderId" class="order-card">
              <div class="oc-header">
                <span class="oc-no">订单号：{{ order.orderNo }}</span>
                <el-tag :type="statusTagType(order.status)" size="mini" effect="dark">{{ statusLabel(order.status) }}</el-tag>
              </div>
              <div class="oc-items">
                <div v-for="item in order.items" :key="item.orderItemId" class="oc-item">
                  <img :src="item.coverImage || defaultCover" class="oc-cover" @error="onImgError" />
                  <div class="oc-item-info">
                    <div class="oc-game-name">{{ item.gameName }}</div>
                    <div class="oc-meta">x{{ item.quantity }}</div>
                  </div>
                  <div class="oc-price">¥{{ item.price }}</div>
                </div>
              </div>
              <div v-if="showOrderLogistics(order)" class="oc-logistics">
                <div class="ol-head"><i class="el-icon-truck"></i> 物流信息</div>
                <div v-if="order.deliverTime" class="ol-row ol-muted">发货时间：{{ order.deliverTime }}</div>
                <div v-if="order.expressCompany" class="ol-row"><span class="ol-k">快递公司</span><span class="ol-v">{{ order.expressCompany }}</span></div>
                <div v-if="order.trackingNo" class="ol-row ol-tracking">
                  <span class="ol-k">物流单号</span>
                  <span class="ol-v">{{ order.trackingNo }}</span>
                  <el-button type="text" size="mini" class="ol-copy" @click="copyTrackingNo(order.trackingNo)">复制</el-button>
                </div>
                <div v-if="!order.expressCompany && !order.trackingNo" class="ol-empty">暂无运单信息，如有疑问请联系客服</div>
              </div>
              <div class="oc-footer">
                <span class="oc-total">合计：<b>¥{{ order.totalAmount }}</b></span>
                <div class="oc-actions">
                  <el-button v-if="order.status === 0" size="mini" type="primary" @click="handlePay(order)">立即付款</el-button>
                  <el-button v-if="order.status === 0" size="mini" @click="handleCancel(order)">取消订单</el-button>
                  <el-button v-if="String(order.status) === '1'" size="mini" type="warning" plain @click="handleRefund(order)">申请退款</el-button>
                  <el-button v-if="String(order.status) === '2'" size="mini" type="success" @click="handleFinish(order)">确认收货</el-button>
                </div>
              </div>
            </div>
          </div>
          <div v-if="allOrders.length > 3" class="view-all-orders">
            <el-button type="text" @click="activeTab = 'orders'">查看全部订单 <i class="el-icon-arrow-right"></i></el-button>
          </div>
        </section>

        <!-- 我的订单 -->
        <section v-if="activeTab === 'orders'">
          <div class="section-header">
            <div class="section-title">我的订单</div>
          </div>
          <div class="order-tabs">
            <span
              v-for="t in orderTabs"
              :key="t.value"
              :class="['order-tab', { active: orderFilter === t.value }]"
              @click="orderFilter = t.value"
            >{{ t.label }}<em v-if="t.count > 0">{{ t.count }}</em></span>
          </div>

          <div v-if="filteredOrders.length === 0" class="empty-tip">
            <i class="el-icon-document"></i>
            <p>该状态下暂无订单</p>
          </div>
          <div v-else class="order-list">
            <div v-for="order in filteredOrders" :key="order.orderId" class="order-card">
              <div class="oc-header">
                <span class="oc-no">订单号：{{ order.orderNo }}</span>
                <span class="oc-date">{{ order.createTime | shortDate }}</span>
                <el-tag :type="statusTagType(order.status)" size="mini" effect="dark">{{ statusLabel(order.status) }}</el-tag>
              </div>
              <div class="oc-items">
                <div v-for="item in order.items" :key="item.orderItemId" class="oc-item">
                  <img :src="item.coverImage || defaultCover" class="oc-cover" @error="onImgError" />
                  <div class="oc-item-info">
                    <div class="oc-game-name">{{ item.gameName }}</div>
                    <div class="oc-meta">x{{ item.quantity }} · ¥{{ item.price }} / 件</div>
                  </div>
                  <div class="oc-price">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
                </div>
              </div>
              <div v-if="showOrderLogistics(order)" class="oc-logistics">
                <div class="ol-head"><i class="el-icon-truck"></i> 物流信息</div>
                <div v-if="order.deliverTime" class="ol-row ol-muted">发货时间：{{ order.deliverTime }}</div>
                <div v-if="order.expressCompany" class="ol-row"><span class="ol-k">快递公司</span><span class="ol-v">{{ order.expressCompany }}</span></div>
                <div v-if="order.trackingNo" class="ol-row ol-tracking">
                  <span class="ol-k">物流单号</span>
                  <span class="ol-v">{{ order.trackingNo }}</span>
                  <el-button type="text" size="mini" class="ol-copy" @click="copyTrackingNo(order.trackingNo)">复制</el-button>
                </div>
                <div v-if="!order.expressCompany && !order.trackingNo" class="ol-empty">暂无运单信息，如有疑问请联系客服</div>
              </div>
              <div class="oc-footer">
                <span class="oc-total">合计：<b>¥{{ order.totalAmount }}</b></span>
                <div class="oc-actions">
                  <el-button v-if="order.status === 0" size="mini" type="primary" @click="handlePay(order)">立即付款</el-button>
                  <el-button v-if="order.status === 0" size="mini" plain @click="handleCancel(order)">取消订单</el-button>
                  <el-button v-if="String(order.status) === '1'" size="mini" type="warning" plain @click="handleRefund(order)">申请退款</el-button>
                  <el-button v-if="String(order.status) === '2'" size="mini" type="success" @click="handleFinish(order)">确认收货</el-button>
                </div>
              </div>
            </div>
          </div>
        </section>

        <!-- 账号信息 -->
        <section v-if="activeTab === 'profile'">
          <div class="section-header">
            <div class="section-title">账号信息</div>
          </div>
          <div class="form-card">
            <el-form :model="profileForm" :rules="profileRules" ref="profileForm" label-width="90px">
              <el-form-item label="用户名">
                <el-input :value="profile.userName" disabled>
                  <template slot="suffix"><i class="el-icon-lock" style="color:#64748b"></i></template>
                </el-input>
                <div class="field-hint">用户名不可修改</div>
              </el-form-item>
              <el-form-item label="昵称" prop="nickName">
                <el-input v-model="profileForm.nickName" placeholder="设置你的昵称" maxlength="30" show-word-limit />
              </el-form-item>
              <el-form-item label="手机号" prop="phonenumber">
                <el-input v-model="profileForm.phonenumber" placeholder="绑定手机号" maxlength="11" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="profileForm.email" placeholder="绑定邮箱地址" />
              </el-form-item>
              <el-form-item label="性别">
                <el-radio-group v-model="profileForm.sex">
                  <el-radio label="0">男</el-radio>
                  <el-radio label="1">女</el-radio>
                  <el-radio label="2">保密</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="profileSaving" @click="saveProfile">保存修改</el-button>
                <el-button @click="resetProfile">重置</el-button>
              </el-form-item>
            </el-form>
          </div>
        </section>

        <!-- 收货地址 -->
        <section v-if="activeTab === 'address'">
          <div class="section-header" style="display:flex;align-items:center;justify-content:space-between">
            <div>
              <div class="section-title">收货地址</div>
              <div class="section-sub">管理你的常用收货地址，最多支持 10 个</div>
            </div>
            <el-button type="primary" size="small" icon="el-icon-plus" @click="openAddAddr">新增地址</el-button>
          </div>

          <div v-if="addressList.length === 0" class="empty-tip">
            <i class="el-icon-location-outline"></i>
            <p>还没有收货地址，点击右上角新增</p>
          </div>
          <div v-else class="addr-grid">
            <div v-for="addr in addressList" :key="addr.addressId" class="addr-item">
              <div class="addr-item-top">
                <span class="addr-receiver">{{ addr.receiverName }}</span>
                <span class="addr-phone">{{ addr.phone }}</span>
                <el-tag v-if="addr.isDefault === 1" size="mini" type="warning" effect="dark">默认</el-tag>
              </div>
              <div class="addr-detail">
                {{ addr.provinceName }}{{ addr.cityName }}{{ addr.districtName }}
              </div>
              <div class="addr-detail addr-extra">{{ addr.detail }}</div>
              <div class="addr-actions">
                <el-button type="text" size="mini" @click="openEditAddr(addr)">编辑</el-button>
                <el-button v-if="addr.isDefault !== 1" type="text" size="mini" @click="handleSetDefault(addr)">设为默认</el-button>
                <el-button type="text" size="mini" style="color:#ef4444" @click="handleDeleteAddr(addr)">删除</el-button>
              </div>
            </div>
          </div>
        </section>

        <!-- 安全设置 -->
        <section v-if="activeTab === 'security'">
          <div class="section-header">
            <div class="section-title">安全设置</div>
          </div>

          <div class="security-list">
            <div class="security-item">
              <div class="si-left">
                <div class="si-icon pwd"><i class="el-icon-key"></i></div>
                <div class="si-info">
                  <div class="si-title">登录密码</div>
                  <div class="si-desc">定期更换密码可以提高账号安全性</div>
                </div>
              </div>
              <el-button size="small" plain @click="showPwdDialog = true">修改密码</el-button>
            </div>

            <div class="security-item">
              <div class="si-left">
                <div class="si-icon phone"><i class="el-icon-mobile-phone"></i></div>
                <div class="si-info">
                  <div class="si-title">绑定手机</div>
                  <div class="si-desc">{{ profile.phonenumber ? maskPhone(profile.phonenumber) : '未绑定手机号' }}</div>
                </div>
              </div>
              <el-button size="small" plain @click="activeTab = 'profile'">{{ profile.phonenumber ? '更换' : '去绑定' }}</el-button>
            </div>

            <div class="security-item">
              <div class="si-left">
                <div class="si-icon email"><i class="el-icon-message"></i></div>
                <div class="si-info">
                  <div class="si-title">绑定邮箱</div>
                  <div class="si-desc">{{ profile.email ? maskEmail(profile.email) : '未绑定邮箱' }}</div>
                </div>
              </div>
              <el-button size="small" plain @click="activeTab = 'profile'">{{ profile.email ? '更换' : '去绑定' }}</el-button>
            </div>

            <div class="security-item last">
              <div class="si-left">
                <div class="si-icon login-record"><i class="el-icon-monitor"></i></div>
                <div class="si-info">
                  <div class="si-title">最近登录</div>
                  <div class="si-desc">{{ profile.loginDate | shortDate }} · {{ profile.loginIp || '未知IP' }}</div>
                </div>
              </div>
            </div>
          </div>
        </section>

      </main>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog title="修改登录密码" :visible.sync="showPwdDialog" width="420px" custom-class="shop-dialog">
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdForm" label-width="90px">
        <el-form-item label="当前密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="6-20位，字母+数字" />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="再次输入新密码" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="showPwdDialog = false">取消</el-button>
        <el-button type="primary" :loading="pwdSaving" @click="savePassword">确认修改</el-button>
      </div>
    </el-dialog>
    <!-- 新增/编辑地址弹窗 -->
    <el-dialog :title="addrDialogTitle" :visible.sync="showAddrDialog" width="500px" custom-class="shop-dialog">
      <el-form :model="addrForm" :rules="addrRules" ref="addrFormRef" label-width="80px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addrForm.receiverName" placeholder="收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="addrForm.phone" placeholder="11位手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="所在地区" prop="districtId">
          <div style="display:flex;gap:8px">
            <el-select v-model="addrForm.provinceId" placeholder="省份" @change="onProvinceChange" filterable style="flex:1">
              <el-option v-for="p in provinces" :key="p.regionId" :label="p.regionName" :value="p.regionId" />
            </el-select>
            <el-select v-model="addrForm.cityId" placeholder="城市" :disabled="!addrForm.provinceId" @change="onCityChange" filterable style="flex:1">
              <el-option v-for="c in cities" :key="c.regionId" :label="c.regionName" :value="c.regionId" />
            </el-select>
            <el-select v-model="addrForm.districtId" placeholder="区/县" :disabled="!addrForm.cityId" @change="onDistrictChange" filterable style="flex:1">
              <el-option v-for="d in districts" :key="d.regionId" :label="d.regionName" :value="d.regionId" />
            </el-select>
          </div>
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="addrForm.detail" type="textarea" :rows="2" placeholder="街道、楼栋、门牌号等" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="addrForm.remark" placeholder="选填" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addrForm.isDefaultBool" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="showAddrDialog = false">取消</el-button>
        <el-button type="primary" :loading="savingAddr" @click="saveAddr">保存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getUserProfile, updateUserProfile, updateUserPwd } from '@/api/system/user'
import { getMyOrders, payOrder, cancelOrder, finishOrder, requestRefund, getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress, getRegions, getCartList, getFavoriteGameIds } from '@/api/shop'
import { getToken } from '@/utils/auth'
import { SHOP_DEFAULT_COVER, applyShopImageFallback } from '@/utils/shopImage'
import UserAvatar from '@/views/system/user/profile/userAvatar.vue'

export default {
  name: 'ShopAccount',
  components: { UserAvatar },
  filters: {
    shortDate(val) {
      if (!val) return '—'
      return String(val).substring(0, 10)
    }
  },
  data() {
    const confirmPwdValidator = (rule, value, callback) => {
      if (value !== this.pwdForm.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    return {
      defaultCover: SHOP_DEFAULT_COVER,
      activeTab: 'overview',
      navItems: [
        { key: 'overview',  icon: 'el-icon-data-analysis', label: '账号总览' },
        { key: 'orders',    icon: 'el-icon-document',      label: '我的订单' },
        { key: 'address',   icon: 'el-icon-location',      label: '收货地址' },
        { key: 'profile',   icon: 'el-icon-user',          label: '账号信息' },
        { key: 'security',  icon: 'el-icon-lock',          label: '安全设置' }
      ],
      profile: {},
      profileForm: { nickName: '', phonenumber: '', email: '', sex: '2' },
      profileRules: {
        nickName: [{ max: 30, message: '昵称不超过30个字符', trigger: 'blur' }],
        email: [{ type: 'email', message: '请输入有效邮箱', trigger: 'blur' }],
        phonenumber: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入有效手机号', trigger: 'blur' }]
      },
      profileSaving: false,
      allOrders: [],
      orderFilter: 'all',
      orderTabs: [
        { label: '全部',   value: 'all',    count: 0 },
        { label: '待付款', value: '0',      count: 0 },
        { label: '待发货', value: '1',      count: 0 },
        { label: '配送中', value: '2',      count: 0 },
        { label: '已完成', value: '3',      count: 0 },
        { label: '已取消', value: '4',      count: 0 },
        { label: '退款中', value: '5',      count: 0 },
        { label: '已退款', value: '6',      count: 0 }
      ],
      // 收货地址
      addressList: [],
      showAddrDialog: false,
      addrDialogTitle: '新增地址',
      savingAddr: false,
      addrForm: { addressId: null, receiverName: '', phone: '', provinceId: null, provinceName: '', cityId: null, cityName: '', districtId: null, districtName: '', detail: '', remark: '', isDefaultBool: false },
      addrRules: {
        receiverName: [{ required: true, message: '请填写收货人', trigger: 'blur' }],
        phone: [{ required: true, message: '请填写手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
        districtId: [{ required: true, message: '请选择省市区', trigger: 'change' }],
        detail: [{ required: true, message: '请填写详细地址', trigger: 'blur' }]
      },
      provinces: [], cities: [], districts: [],
      showPwdDialog: false,
      pwdForm: { oldPassword: '', newPassword: '', confirmPassword: '' },
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, max: 20, message: '密码长度 6-20 位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          { validator: confirmPwdValidator, trigger: 'blur' }
        ]
      },
      pwdSaving: false,
      cartCount: 0,
      favoritedGameIds: []
    }
  },
  computed: {
    ...mapGetters(['avatar', 'name', 'nickName']),
    favoriteCount() {
      return this.favoritedGameIds.length
    },
    userName() { return this.name },
    userNickName() { return this.nickName },
    recentOrders() { return this.allOrders.slice(0, 3) },
    filteredOrders() {
      if (this.orderFilter === 'all') return this.allOrders
      return this.allOrders.filter(o => String(o.status) === this.orderFilter)
    },
    orderStats() {
      const o = this.allOrders
      const done = o.filter(x => String(x.status) === '3')
      const totalSpent = done.reduce((s, x) => s + parseFloat(x.totalAmount || 0), 0).toFixed(2)
      return {
        pending:  o.filter(x => String(x.status) === '0').length,
        paid:     o.filter(x => String(x.status) === '1').length,
        shipped:  o.filter(x => String(x.status) === '2').length,
        done:     done.length,
        totalSpent
      }
    }
  },
  created() {
    this.loadProfile()
    this.loadOrders()
    this.loadAddresses()
    this.loadProvinces()
    this.loadCartCount()
    this.loadFavoriteIds()
    if (this.$route.query.tab) {
      this.activeTab = this.$route.query.tab
    }
  },
  activated() {
    this.loadCartCount()
    this.loadFavoriteIds()
  },
  methods: {
    loadCartCount() {
      if (!getToken()) {
        this.cartCount = 0
        return
      }
      getCartList().then(res => {
        this.cartCount = (res.data || []).length
      }).catch(() => {
        this.cartCount = 0
      })
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
    loadProfile() {
      getUserProfile().then(res => {
        this.profile = res.data || {}
        this.profileForm = {
          nickName:    this.profile.nickName    || '',
          phonenumber: this.profile.phonenumber || '',
          email:       this.profile.email       || '',
          sex:         this.profile.sex         || '2'
        }
      })
    },
    loadOrders() {
      getMyOrders().then(res => {
        this.allOrders = res.data || []
        this.syncOrderTabCounts()
      })
    },
    syncOrderTabCounts() {
      const counts = { '0': 0, '1': 0, '2': 0, '3': 0, '4': 0, '5': 0, '6': 0 }
      this.allOrders.forEach(o => { counts[String(o.status)]++ })
      this.orderTabs.forEach(t => {
        if (t.value !== 'all') t.count = counts[t.value] || 0
      })
    },
    gotoOrderTab(status) {
      this.orderFilter = status
      this.activeTab = 'orders'
    },
    saveProfile() {
      this.$refs.profileForm.validate(valid => {
        if (!valid) return
        this.profileSaving = true
        updateUserProfile(this.profileForm).then(() => {
          this.$message.success('账号信息已更新')
          this.loadProfile()
          this.$store.dispatch('GetInfo')
        }).catch(() => {}).finally(() => { this.profileSaving = false })
      })
    },
    resetProfile() {
      this.profileForm = {
        nickName:    this.profile.nickName    || '',
        phonenumber: this.profile.phonenumber || '',
        email:       this.profile.email       || '',
        sex:         this.profile.sex         || '2'
      }
    },
    savePassword() {
      this.$refs.pwdForm.validate(valid => {
        if (!valid) return
        this.pwdSaving = true
        updateUserPwd(this.pwdForm.oldPassword, this.pwdForm.newPassword).then(() => {
          this.$message.success('密码修改成功，请重新登录')
          this.showPwdDialog = false
          setTimeout(() => this.$store.dispatch('LogOut').then(() => this.$router.push('/login')), 1200)
        }).catch(() => {}).finally(() => { this.pwdSaving = false })
      })
    },
    showOrderLogistics(order) {
      const s = String(order.status)
      return s === '2' || s === '3'
    },
    copyTrackingNo(text) {
      if (!text) return
      const ok = () => this.$message.success('已复制单号')
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(ok).catch(() => this.fallbackCopyTracking(text, ok))
      } else {
        this.fallbackCopyTracking(text, ok)
      }
    },
    fallbackCopyTracking(text, done) {
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
    handlePay(order) {
      this.$confirm(`确认支付订单 ¥${order.totalAmount}？`, '确认付款', { type: 'warning' }).then(() => {
        payOrder(order.orderId).then(() => {
          this.$message.success('支付成功！')
          this.loadOrders()
        })
      }).catch(() => {})
    },
    handleCancel(order) {
      this.$confirm('确认取消该订单？', '取消订单', { type: 'warning' }).then(() => {
        cancelOrder(order.orderId).then(() => {
          this.$message.success('订单已取消')
          this.loadOrders()
        })
      }).catch(() => {})
    },
    handleFinish(order) {
      this.$confirm('确认已收到商品？确认后订单将完成。', '确认收货', { type: 'warning' }).then(() => {
        finishOrder(order.orderId).then(() => {
          this.$message.success('收货成功，订单已完成！')
          this.loadOrders()
        })
      }).catch(() => {})
    },
    handleRefund(order) {
      this.$confirm('确认提交退款申请？提交后请等待后台审核。', '退款申请', { type: 'warning' }).then(() => {
        requestRefund(order.orderId).then(() => {
          this.$message.success('退款申请已提交')
          this.loadOrders()
        })
      }).catch(() => {})
    },
    handleLogout() {
      this.$confirm('确认退出登录？', '退出', { type: 'warning' }).then(() => {
        this.$store.dispatch('LogOut').then(() => this.$router.push('/shop/home'))
      }).catch(() => {})
    },
    statusLabel(s) {
      return ['待付款', '待发货', '配送中', '已完成', '已取消', '退款申请中', '已退款'][s] || '未知'
    },
    statusTagType(s) {
      return ['warning', 'primary', 'info', 'success', 'danger', 'warning', 'info'][s] || ''
    },
    maskPhone(p) {
      return p ? p.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2') : ''
    },
    maskEmail(e) {
      if (!e) return ''
      const [u, d] = e.split('@')
      return u.substring(0, 2) + '***@' + d
    },
    // ===== 收货地址 =====
    loadAddresses() {
      getAddressList().then(res => { this.addressList = res.data || [] }).catch(() => {})
    },
    loadProvinces() {
      getRegions(0).then(res => { this.provinces = res.data || [] })
    },
    onProvinceChange(id) {
      const p = this.provinces.find(x => x.regionId === id)
      this.addrForm.provinceName = p ? p.regionName : ''
      this.addrForm.cityId = null; this.addrForm.cityName = ''
      this.addrForm.districtId = null; this.addrForm.districtName = ''
      this.cities = []; this.districts = []
      if (id) getRegions(id).then(res => { this.cities = res.data || [] })
    },
    onCityChange(id) {
      const c = this.cities.find(x => x.regionId === id)
      this.addrForm.cityName = c ? c.regionName : ''
      this.addrForm.districtId = null; this.addrForm.districtName = ''
      this.districts = []
      if (id) getRegions(id).then(res => { this.districts = res.data || [] })
    },
    onDistrictChange(id) {
      const d = this.districts.find(x => x.regionId === id)
      this.addrForm.districtName = d ? d.regionName : ''
    },
    openAddAddr() {
      this.addrDialogTitle = '新增地址'
      this.addrForm = { addressId: null, receiverName: '', phone: '', provinceId: null, provinceName: '', cityId: null, cityName: '', districtId: null, districtName: '', detail: '', remark: '', isDefaultBool: false }
      this.cities = []; this.districts = []
      this.showAddrDialog = true
      this.$nextTick(() => { this.$refs.addrFormRef && this.$refs.addrFormRef.clearValidate() })
    },
    openEditAddr(addr) {
      this.addrDialogTitle = '编辑地址'
      this.addrForm = { ...addr, isDefaultBool: addr.isDefault === 1 }
      // 加载城市和区县
      if (addr.provinceId) getRegions(addr.provinceId).then(r => { this.cities = r.data || [] })
      if (addr.cityId) getRegions(addr.cityId).then(r => { this.districts = r.data || [] })
      this.showAddrDialog = true
    },
    saveAddr() {
      this.$refs.addrFormRef.validate(valid => {
        if (!valid) return
        this.savingAddr = true
        const payload = { ...this.addrForm, isDefault: this.addrForm.isDefaultBool ? 1 : 0 }
        const fn = payload.addressId ? updateAddress(payload) : addAddress(payload)
        fn.then(() => {
          this.$message.success('地址已保存')
          this.showAddrDialog = false
          this.loadAddresses()
        }).finally(() => { this.savingAddr = false })
      })
    },
    handleDeleteAddr(addr) {
      this.$confirm('确认删除该地址？', '提示', { type: 'warning' }).then(() => {
        deleteAddress(addr.addressId).then(() => {
          this.$message.success('已删除')
          this.loadAddresses()
        })
      }).catch(() => {})
    },
    handleSetDefault(addr) {
      setDefaultAddress(addr.addressId).then(() => {
        this.$message.success('已设为默认地址')
        this.loadAddresses()
      })
    },
    onImgError(e) { applyShopImageFallback(e, this.defaultCover) }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; }

.account-wrap {
  min-height: 100vh;
  background: #050e1f;
  color: #e2e8f0;
  font-family: 'Segoe UI', 'PingFang SC', sans-serif;
}

/* ===== 顶部导航 ===== */
.top-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(5, 14, 31, 0.95);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(148, 163, 184, 0.12);
}
.nav-inner {
  max-width: 1300px;
  margin: 0 auto;
  padding: 0 24px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.brand-box {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}
.ps-logo { height: 30px; filter: brightness(0) invert(1); }
.brand-title { color: #fff; font-size: 15px; font-weight: 700; line-height: 1.2; }
.brand-subtitle { color: #64748b; font-size: 11px; }
.nav-actions { display: flex; align-items: center; gap: 10px; }
.nav-actions .nav-badge {
  display: inline-block;
  line-height: 1;
}

/* ===== 页面主体 ===== */
.page-body {
  max-width: 1300px;
  margin: 32px auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: 24px;
  align-items: start;
}

/* ===== 左侧边栏 ===== */
.side-panel {
  position: sticky;
  top: 88px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.profile-card {
  background: rgba(15, 23, 42, 0.82);
  border: 1px solid rgba(148, 163, 184, 0.12);
  border-radius: 20px;
  padding: 28px 20px 20px;
  text-align: center;
}

.avatar-area { position: relative; display: inline-block; margin-bottom: 14px; }
/* 复用后台头像裁剪组件，尺寸与侧栏圆角风格对齐 */
.shop-avatar-wrap {
  display: inline-block;
}
.shop-avatar-wrap ::v-deep .user-info-head {
  height: 80px;
  width: 80px;
  margin: 0 auto;
  display: block;
}
.shop-avatar-wrap ::v-deep .user-info-head:hover:after {
  line-height: 80px;
  border-radius: 50%;
}
.shop-avatar-wrap ::v-deep .user-info-head .img-circle.img-lg {
  width: 80px !important;
  height: 80px !important;
  border: 3px solid #3b82f6;
  object-fit: cover;
}
.member-badge {
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);
  white-space: nowrap;
  background: linear-gradient(90deg, #f59e0b, #fbbf24);
  color: #1a1a1a;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 8px;
  border-radius: 999px;
}
.user-name { font-size: 17px; font-weight: 700; color: #fff; margin-top: 8px; }
.user-login { font-size: 12px; color: #64748b; margin-top: 4px; }
.user-since { font-size: 11px; color: #475569; margin-top: 4px; }

.order-quick-stats {
  display: flex;
  justify-content: space-around;
  align-items: center;
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(148, 163, 184, 0.1);
}
.qs-item { flex: 1; cursor: pointer; padding: 4px 0; }
.qs-item:hover .qs-num { color: #60a5fa; }
.qs-num { font-size: 18px; font-weight: 800; color: #fbbf24; }
.qs-label { font-size: 11px; color: #94a3b8; margin-top: 2px; }
.qs-divider { width: 1px; height: 30px; background: rgba(148, 163, 184, 0.15); }

.side-nav {
  background: rgba(15, 23, 42, 0.82);
  border: 1px solid rgba(148, 163, 184, 0.12);
  border-radius: 16px;
  overflow: hidden;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 20px;
  cursor: pointer;
  font-size: 14px;
  color: #94a3b8;
  transition: all 0.2s;
  border-left: 3px solid transparent;
}
.nav-item:hover { background: rgba(59, 130, 246, 0.08); color: #e2e8f0; }
.nav-item.active { background: rgba(59, 130, 246, 0.12); color: #60a5fa; border-left-color: #3b82f6; }
.nav-item i { font-size: 16px; }

.logout-area { text-align: center; }
.logout-btn { width: 100%; }

/* ===== 右侧内容 ===== */
.main-content {
  min-height: 600px;
}

.section-header { margin-bottom: 24px; }
.section-title { font-size: 22px; font-weight: 800; color: #fff; }
.section-sub { font-size: 14px; color: #64748b; margin-top: 4px; }

/* ===== 统计卡片 ===== */
.stat-cards {
  display: flex;
  gap: 14px;
  flex-wrap: wrap;
  margin-bottom: 28px;
}
.stat-card {
  flex: 1;
  min-width: 120px;
  display: flex;
  align-items: center;
  gap: 14px;
  background: rgba(15, 23, 42, 0.82);
  border: 1px solid rgba(148, 163, 184, 0.12);
  border-radius: 16px;
  padding: 18px 20px;
  cursor: pointer;
  transition: border-color 0.2s, transform 0.2s;
}
.stat-card:hover { border-color: rgba(59, 130, 246, 0.4); transform: translateY(-2px); }
.sc-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0;
}
.sc-icon.pending  { background: rgba(251, 191, 36, 0.15); color: #fbbf24; }
.sc-icon.paid     { background: rgba(59, 130, 246, 0.15); color: #60a5fa; }
.sc-icon.shipped  { background: rgba(139, 92, 246, 0.15); color: #a78bfa; }
.sc-icon.done     { background: rgba(34, 197, 94, 0.15);  color: #4ade80; }
.sc-icon.total    { background: rgba(239, 68, 68, 0.15);  color: #f87171; }
.sc-num { font-size: 22px; font-weight: 800; color: #fff; }
.sc-label { font-size: 12px; color: #64748b; margin-top: 2px; }

/* ===== 区块标题 ===== */
.block-title {
  font-size: 16px; font-weight: 700; color: #cbd5e1;
  margin-bottom: 14px;
  display: flex; align-items: center; gap: 8px;
}
.block-title::before {
  content: '';
  display: inline-block; width: 3px; height: 16px;
  background: #3b82f6; border-radius: 2px;
}

/* ===== 订单筛选 ===== */
.order-tabs {
  display: flex; flex-wrap: wrap; gap: 8px;
  margin-bottom: 20px;
}
.order-tab {
  padding: 6px 16px; border-radius: 999px; font-size: 13px;
  color: #64748b; background: rgba(30, 41, 59, 0.8); cursor: pointer;
  border: 1px solid transparent; transition: all 0.2s;
  position: relative;
}
.order-tab:hover { color: #e2e8f0; border-color: rgba(148, 163, 184, 0.2); }
.order-tab.active { background: rgba(59, 130, 246, 0.15); color: #60a5fa; border-color: rgba(59, 130, 246, 0.4); }
.order-tab em {
  display: inline-flex; align-items: center; justify-content: center;
  min-width: 16px; height: 16px; padding: 0 4px;
  background: #ef4444; color: #fff; font-size: 10px; font-style: normal;
  border-radius: 999px; margin-left: 4px;
}

/* ===== 订单列表 ===== */
.order-list { display: flex; flex-direction: column; gap: 14px; }
.order-card {
  background: rgba(15, 23, 42, 0.82);
  border: 1px solid rgba(148, 163, 184, 0.12);
  border-radius: 16px;
  overflow: hidden;
  transition: border-color 0.2s;
}
.order-card:hover { border-color: rgba(59, 130, 246, 0.3); }

.oc-header {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 18px;
  background: rgba(30, 41, 59, 0.5);
  border-bottom: 1px solid rgba(148, 163, 184, 0.08);
}
.oc-no { font-size: 13px; color: #94a3b8; flex: 1; }
.oc-date { font-size: 12px; color: #475569; }

.oc-items { padding: 14px 18px; display: flex; flex-direction: column; gap: 12px; }
.oc-item { display: flex; align-items: center; gap: 14px; }
.oc-cover { width: 54px; height: 54px; border-radius: 8px; object-fit: cover; flex-shrink: 0; background: #1e293b; }
.oc-item-info { flex: 1; }
.oc-game-name { font-size: 14px; font-weight: 600; color: #e2e8f0; }
.oc-meta { font-size: 12px; color: #64748b; margin-top: 3px; }
.oc-price { font-size: 15px; font-weight: 700; color: #fbbf24; }

.oc-logistics {
  padding: 12px 18px;
  border-top: 1px solid rgba(148, 163, 184, 0.08);
  background: rgba(15, 23, 42, 0.55);
}
.ol-head {
  font-size: 13px;
  font-weight: 600;
  color: #60a5fa;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}
.ol-row { font-size: 13px; color: #cbd5e1; display: flex; align-items: center; flex-wrap: wrap; gap: 8px 12px; margin-top: 6px; }
.ol-row.ol-muted { color: #64748b; font-size: 12px; margin-top: 0; }
.ol-k { color: #64748b; flex-shrink: 0; }
.ol-v { color: #e2e8f0; word-break: break-all; }
.ol-tracking .ol-v { font-family: ui-monospace, monospace; letter-spacing: 0.02em; }
.ol-copy { color: #60a5fa !important; padding: 0 4px !important; }
.ol-empty { font-size: 12px; color: #64748b; margin-top: 4px; }

.oc-footer {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 18px;
  border-top: 1px solid rgba(148, 163, 184, 0.08);
}
.oc-total { font-size: 14px; color: #94a3b8; }
.oc-total b { color: #fbbf24; font-size: 16px; }
.oc-actions { display: flex; gap: 8px; }

.view-all-orders { text-align: center; margin-top: 12px; }

/* ===== 空状态 ===== */
.empty-tip {
  text-align: center; padding: 60px 0;
  color: #475569;
}
.empty-tip i { font-size: 48px; }
.empty-tip p { margin-top: 14px; font-size: 14px; }
.empty-tip a { color: #60a5fa; }

/* ===== 表单卡片 ===== */
.form-card {
  background: rgba(15, 23, 42, 0.82);
  border: 1px solid rgba(148, 163, 184, 0.12);
  border-radius: 20px;
  padding: 32px;
  max-width: 520px;
}
.field-hint { font-size: 12px; color: #475569; margin-top: 4px; }

/* ===== 安全设置 ===== */
.security-list {
  background: rgba(15, 23, 42, 0.82);
  border: 1px solid rgba(148, 163, 184, 0.12);
  border-radius: 20px;
  overflow: hidden;
}
.security-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.08);
}
.security-item.last { border-bottom: none; }
.si-left { display: flex; align-items: center; gap: 16px; }
.si-icon {
  width: 44px; height: 44px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center; font-size: 20px; flex-shrink: 0;
}
.si-icon.pwd     { background: rgba(251, 191, 36, 0.15); color: #fbbf24; }
.si-icon.phone   { background: rgba(34, 197, 94, 0.15);  color: #4ade80; }
.si-icon.email   { background: rgba(59, 130, 246, 0.15); color: #60a5fa; }
.si-icon.login-record { background: rgba(139, 92, 246, 0.15); color: #a78bfa; }
.si-title { font-size: 15px; font-weight: 600; color: #e2e8f0; }
.si-desc { font-size: 13px; color: #64748b; margin-top: 3px; }

/* ===== 弹窗样式覆盖 ===== */
:deep(.shop-dialog) {
  background: #0f172a;
  border: 1px solid rgba(148, 163, 184, 0.15);
  border-radius: 20px;
}
:deep(.shop-dialog .el-dialog__header) {
  border-bottom: 1px solid rgba(148, 163, 184, 0.1);
  padding: 20px 24px;
}
:deep(.shop-dialog .el-dialog__title) { color: #fff; font-size: 16px; font-weight: 700; }
:deep(.shop-dialog .el-dialog__body) { padding: 24px; }
:deep(.shop-dialog .el-dialog__footer) {
  border-top: 1px solid rgba(148, 163, 184, 0.1);
  padding: 14px 24px;
}

/* ===== ElementUI 全局深色覆盖 ===== */
:deep(.el-input__inner) {
  background: rgba(30, 41, 59, 0.8);
  border-color: rgba(148, 163, 184, 0.2);
  color: #e2e8f0;
}
:deep(.el-input__inner:focus) { border-color: #3b82f6; }
:deep(.el-input.is-disabled .el-input__inner) { background: rgba(15, 23, 42, 0.6); color: #475569; }
:deep(.el-form-item__label) { color: #94a3b8; }
:deep(.el-radio__label) { color: #94a3b8; }
:deep(.el-radio__input.is-checked + .el-radio__label) { color: #60a5fa; }

/* ===== 地址管理 ===== */
.addr-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 14px;
}
.addr-item {
  background: rgba(15, 23, 42, 0.82);
  border: 1px solid rgba(148, 163, 184, 0.12);
  border-radius: 16px;
  padding: 18px 20px;
  transition: border-color 0.2s;
}
.addr-item:hover { border-color: rgba(59, 130, 246, 0.3); }
.addr-item-top { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.addr-receiver { font-size: 15px; font-weight: 700; color: #e2e8f0; }
.addr-phone { font-size: 13px; color: #64748b; }
.addr-detail { font-size: 13px; color: #94a3b8; line-height: 1.6; }
.addr-extra { color: #64748b; }
.addr-actions {
  margin-top: 12px;
  padding-top: 10px;
  border-top: 1px solid rgba(148, 163, 184, 0.08);
  display: flex; gap: 4px;
}

@media (max-width: 900px) {
  .page-body { grid-template-columns: 1fr; }
  .side-panel { position: static; }
  .stat-cards { gap: 10px; }
  .stat-card { min-width: calc(50% - 5px); }
}
</style>
