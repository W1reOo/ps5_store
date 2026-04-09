<template>
  <div class="cart-wrap">
    <div class="page-header">
      <div class="back-btn" @click="$router.push('/shop/home')"><i class="el-icon-arrow-left"></i> 返回商城</div>
      <h2>购物车</h2>
    </div>

    <div v-loading="loading" class="cart-body">
      <div v-if="cartList.length === 0" class="empty-cart">
        <i class="el-icon-shopping-cart-2"></i>
        <p>购物车空空如也</p>
        <el-button type="primary" @click="$router.push('/shop/home')">去逛逛</el-button>
      </div>

      <template v-else>
        <div class="cart-list">
          <div class="cart-item" v-for="item in cartList" :key="item.cartId">
            <el-checkbox v-model="item.checked" class="item-check" @change="updateSelect" />
            <img :src="item.coverImage || defaultCover" class="item-img" @error="onImgError" />
            <div class="item-info">
              <div class="item-name" @click="$router.push(`/shop/detail/${item.gameId}`)">{{ item.gameName }}</div>
              <div class="item-price">¥{{ item.price }}</div>
            </div>
            <div class="item-qty">
              <el-input-number v-model="item.quantity" :min="1" :max="item.stock"
                               size="small" @change="(v) => updateQty(item, v)" />
            </div>
            <div class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
            <el-button type="text" icon="el-icon-delete" class="item-del" @click="removeItem(item)">删除</el-button>
          </div>
        </div>

        <div class="checkout-bar">
          <el-checkbox v-model="selectAll" @change="toggleAll">全选</el-checkbox>
          <div class="checkout-right">
            <span class="total-label">已选 {{ selectedCount }} 件</span>
            <span class="total-price">合计：<strong>¥{{ totalPrice }}</strong></span>
            <el-button type="primary" size="medium" :disabled="selectedCount === 0" @click="openCheckout">结算</el-button>
          </div>
        </div>
      </template>
    </div>

    <!-- 结算弹窗 -->
    <el-dialog title="确认订单" :visible.sync="showCheckout" width="560px" custom-class="checkout-dialog" @open="onDialogOpen">
      <div class="dialog-section-title">
        <i class="el-icon-location-outline"></i> 收货地址
        <el-button type="text" size="small" @click="showAddForm = !showAddForm" style="margin-left:8px">
          {{ showAddForm ? '收起' : '+ 新增地址' }}
        </el-button>
      </div>

      <!-- 已保存地址列表 -->
      <div v-if="savedAddresses.length > 0 && !showAddForm" class="saved-addr-list">
        <div
          v-for="addr in savedAddresses"
          :key="addr.addressId"
          :class="['addr-card', { selected: selectedAddrId === addr.addressId }]"
          @click="selectedAddrId = addr.addressId"
        >
          <div class="addr-card-top">
            <span class="addr-name">{{ addr.receiverName }}</span>
            <span class="addr-phone">{{ addr.phone }}</span>
            <el-tag v-if="addr.isDefault === 1" size="mini" type="warning" effect="dark" style="margin-left:6px">默认</el-tag>
            <i :class="selectedAddrId === addr.addressId ? 'el-icon-success addr-check-active' : 'el-icon-circle addr-check'" />
          </div>
          <div class="addr-full">{{ addr.provinceName }}{{ addr.cityName }}{{ addr.districtName }} {{ addr.detail }}</div>
        </div>
      </div>
      <div v-else-if="!showAddForm" class="no-addr-tip">还没有收货地址，请新增</div>

      <!-- 新增 / 编辑地址表单 -->
      <div v-if="showAddForm" class="addr-form-wrap">
        <el-form :model="addrForm" :rules="addrRules" ref="addrForm" label-width="80px" size="small">
          <el-form-item label="收货人" prop="receiverName">
            <el-input v-model="addrForm.receiverName" placeholder="收货人姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="addrForm.phone" placeholder="11位手机号" maxlength="11" />
          </el-form-item>
          <el-form-item label="所在地区" prop="districtId">
            <div class="region-row">
              <el-select v-model="addrForm.provinceId" placeholder="省份" @change="onProvinceChange" filterable>
                <el-option v-for="p in provinces" :key="p.regionId" :label="p.regionName" :value="p.regionId" />
              </el-select>
              <el-select v-model="addrForm.cityId" placeholder="城市" :disabled="!addrForm.provinceId" @change="onCityChange" filterable>
                <el-option v-for="c in cities" :key="c.regionId" :label="c.regionName" :value="c.regionId" />
              </el-select>
              <el-select v-model="addrForm.districtId" placeholder="区/县" :disabled="!addrForm.cityId" @change="onDistrictChange" filterable>
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
          <el-form-item>
            <el-button type="primary" size="small" :loading="savingAddr" @click="saveAddress">保存并使用</el-button>
            <el-button size="small" @click="showAddForm = false">取消</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div class="dialog-section-title" style="margin-top:16px">
        <i class="el-icon-edit-outline"></i> 备注
      </div>
      <el-input v-model="form.remark" placeholder="选填" size="small" style="margin-bottom:12px" />

      <div class="dialog-total">
        订单金额：<strong>¥{{ totalPrice }}</strong>
        <span style="color:#64748b;font-size:12px;margin-left:8px">共 {{ selectedCount }} 件</span>
      </div>

      <span slot="footer">
        <el-button @click="showCheckout = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="doSubmit">确认下单</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  getCartList, updateCartQuantity, deleteCartItem, submitOrder,
  getAddressList, addAddress, getRegions
} from '@/api/shop'
import { SHOP_DEFAULT_COVER, applyShopImageFallback } from '@/utils/shopImage'

const emptyAddrForm = () => ({
  receiverName: '', phone: '',
  provinceId: null, provinceName: '', cityId: null, cityName: '',
  districtId: null, districtName: '', detail: '', remark: '', isDefaultBool: false
})

export default {
  name: 'ShopCart',
  data() {
    return {
      loading: false, submitting: false, savingAddr: false,
      showCheckout: false, showAddForm: false,
      cartList: [],
      selectAll: true,
      form: { remark: '' },
      defaultCover: SHOP_DEFAULT_COVER,
      // 省市区
      provinces: [], cities: [], districts: [],
      // 收货地址
      savedAddresses: [],
      selectedAddrId: null,
      addrForm: emptyAddrForm(),
      addrRules: {
        receiverName: [{ required: true, message: '请填写收货人', trigger: 'blur' }],
        phone: [
          { required: true, message: '请填写手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        districtId: [{ required: true, message: '请选择省市区', trigger: 'change' }],
        detail: [{ required: true, message: '请填写详细地址', trigger: 'blur' }]
      }
    }
  },
  computed: {
    selectedItems() { return this.cartList.filter(i => i.checked) },
    selectedCount() { return this.selectedItems.length },
    totalPrice() {
      return this.selectedItems.reduce((s, i) => s + i.price * i.quantity, 0).toFixed(2)
    },
    selectedAddr() {
      return this.savedAddresses.find(a => a.addressId === this.selectedAddrId)
    }
  },
  created() { this.loadCart(); this.loadProvinces() },
  methods: {
    loadCart() {
      this.loading = true
      getCartList().then(res => {
        this.cartList = (res.data || []).map(i => ({ ...i, checked: true }))
        this.selectAll = true
      }).finally(() => { this.loading = false })
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
    openCheckout() {
      this.showCheckout = true
      this.showAddForm = false
    },
    onDialogOpen() {
      getAddressList().then(res => {
        this.savedAddresses = res.data || []
        // 默认选中默认地址
        const def = this.savedAddresses.find(a => a.isDefault === 1)
        this.selectedAddrId = def ? def.addressId : (this.savedAddresses[0] ? this.savedAddresses[0].addressId : null)
        if (this.savedAddresses.length === 0) this.showAddForm = true
      }).catch(() => {
        // 未登录时 savedAddresses 为空
        this.savedAddresses = []
        this.showAddForm = true
      })
    },
    saveAddress() {
      this.$refs.addrForm.validate(valid => {
        if (!valid) return
        this.savingAddr = true
        const payload = {
          ...this.addrForm,
          isDefault: this.addrForm.isDefaultBool ? 1 : 0
        }
        addAddress(payload).then(res => {
          this.$message.success('地址已保存')
          this.showAddForm = false
          getAddressList().then(r => {
            this.savedAddresses = r.data || []
            // 选中刚新增的
            if (res.data) this.selectedAddrId = res.data
            else {
              const last = this.savedAddresses[this.savedAddresses.length - 1]
              if (last) this.selectedAddrId = last.addressId
            }
          })
          this.addrForm = emptyAddrForm()
          this.cities = []; this.districts = []
        }).finally(() => { this.savingAddr = false })
      })
    },
    updateSelect() { this.selectAll = this.cartList.every(i => i.checked) },
    toggleAll(val) { this.cartList.forEach(i => { i.checked = val }) },
    updateQty(item, val) { updateCartQuantity({ cartId: item.cartId, quantity: val }) },
    removeItem(item) {
      this.$confirm('确认删除该商品？', '提示', { type: 'warning' }).then(() => {
        deleteCartItem(item.cartId).then(() => {
          this.cartList = this.cartList.filter(i => i.cartId !== item.cartId)
          this.$message.success('已删除')
        })
      })
    },
    doSubmit() {
      if (!this.selectedAddr) {
        this.$message.warning('请选择或新增收货地址')
        return
      }
      const addr = this.selectedAddr
      this.submitting = true
      const payload = {
        receiverName:    addr.receiverName,
        receiverPhone:   addr.phone,
        receiverAddress: addr.provinceName + addr.cityName + addr.districtName + addr.detail,
        remark:          this.form.remark,
        cartIds:  this.selectedItems.map(i => i.cartId),
        items:    this.selectedItems.map(i => ({ gameId: i.gameId, quantity: i.quantity }))
      }
      submitOrder(payload).then(() => {
        this.$message.success('下单成功！')
        this.showCheckout = false
        this.$router.push('/shop/orders')
      }).catch(e => {
        this.$message.error(e.message || '下单失败')
      }).finally(() => { this.submitting = false })
    },
    onImgError(e) { applyShopImageFallback(e, this.defaultCover) }
  }
}
</script>

<style scoped>
.cart-wrap { min-height: 100vh; background: #050e1f; color: #e2e8f0; padding: 28px 32px; font-family: 'Segoe UI','PingFang SC',sans-serif; }
.page-header { display: flex; align-items: center; gap: 16px; margin-bottom: 24px; }
.back-btn { color: #64748b; cursor: pointer; font-size: 14px; display: flex; align-items: center; gap: 4px; transition: color .2s; }
.back-btn:hover { color: #60a5fa; }
.page-header h2 { margin: 0; font-size: 22px; color: #fff; }
.cart-body { max-width: 960px; margin: 0 auto; }

.empty-cart { text-align: center; padding: 80px; color: #475569; }
.empty-cart i { font-size: 64px; display: block; margin-bottom: 16px; }

.cart-list { background: rgba(15,23,42,.85); border-radius: 16px; overflow: hidden; margin-bottom: 16px; border: 1px solid rgba(148,163,184,.1); }
.cart-item { display: flex; align-items: center; gap: 16px; padding: 16px 20px; border-bottom: 1px solid rgba(148,163,184,.08); }
.cart-item:last-child { border-bottom: none; }
.item-img { width: 80px; height: 80px; object-fit: cover; border-radius: 10px; flex-shrink: 0; }
.item-info { flex: 1; }
.item-name { font-size: 15px; color: #e2e8f0; cursor: pointer; margin-bottom: 6px; }
.item-name:hover { color: #60a5fa; }
.item-price { color: #fbbf24; font-size: 15px; }
.item-subtotal { width: 90px; text-align: right; color: #fbbf24; font-weight: bold; font-size: 16px; }
.item-del { color: #ef4444; padding: 0 8px; }

.checkout-bar { display: flex; justify-content: space-between; align-items: center; background: rgba(15,23,42,.85); border-radius: 16px; padding: 16px 20px; border: 1px solid rgba(148,163,184,.1); }
.checkout-right { display: flex; align-items: center; gap: 20px; }
.total-label { color: #94a3b8; font-size: 14px; }
.total-price { font-size: 15px; color: #e2e8f0; }
.total-price strong { color: #fbbf24; font-size: 22px; }

/* ===== 弹窗 ===== */
:deep(.checkout-dialog) { background: #0f172a; border: 1px solid rgba(148,163,184,.15); border-radius: 20px; }
:deep(.checkout-dialog .el-dialog__header) { border-bottom: 1px solid rgba(148,163,184,.1); padding: 18px 24px; }
:deep(.checkout-dialog .el-dialog__title) { color: #fff; font-size: 16px; font-weight: 700; }
:deep(.checkout-dialog .el-dialog__body) { padding: 20px 24px; }
:deep(.checkout-dialog .el-dialog__footer) { border-top: 1px solid rgba(148,163,184,.1); padding: 12px 24px; }
:deep(.checkout-dialog .el-input__inner),
:deep(.checkout-dialog .el-textarea__inner) { background: rgba(30,41,59,.8); border-color: rgba(148,163,184,.2); color: #e2e8f0; }
:deep(.checkout-dialog .el-form-item__label) { color: #94a3b8; }
:deep(.checkout-dialog .el-select .el-input__inner) { background: rgba(30,41,59,.8); color: #e2e8f0; }
:deep(.checkout-dialog .el-select-dropdown) { background: #1e293b; border-color: rgba(148,163,184,.2); }
:deep(.checkout-dialog .el-select-dropdown__item) { color: #cbd5e1; }
:deep(.checkout-dialog .el-select-dropdown__item.hover) { background: rgba(59,130,246,.15); }

.dialog-section-title { font-size: 13px; font-weight: 700; color: #60a5fa; margin-bottom: 12px; display: flex; align-items: center; gap: 6px; }

/* 已保存地址 */
.saved-addr-list { display: flex; flex-direction: column; gap: 8px; margin-bottom: 4px; max-height: 240px; overflow-y: auto; }
.addr-card { background: rgba(30,41,59,.7); border: 1px solid rgba(148,163,184,.12); border-radius: 12px; padding: 12px 16px; cursor: pointer; transition: border-color .2s; position: relative; }
.addr-card.selected { border-color: #3b82f6; background: rgba(59,130,246,.1); }
.addr-card-top { display: flex; align-items: center; gap: 10px; margin-bottom: 6px; }
.addr-name { font-weight: 700; color: #e2e8f0; }
.addr-phone { color: #94a3b8; font-size: 13px; }
.addr-full { font-size: 13px; color: #94a3b8; }
.addr-check { position: absolute; right: 14px; top: 50%; transform: translateY(-50%); font-size: 18px; color: #334155; }
.addr-check-active { position: absolute; right: 14px; top: 50%; transform: translateY(-50%); font-size: 18px; color: #3b82f6; }
.no-addr-tip { color: #475569; font-size: 13px; margin-bottom: 8px; }

/* 地址表单 */
.addr-form-wrap { background: rgba(15,23,42,.6); border-radius: 12px; padding: 16px; border: 1px solid rgba(148,163,184,.1); margin-bottom: 4px; }
.region-row { display: flex; gap: 8px; }
.region-row .el-select { flex: 1; }

.dialog-total { text-align: right; font-size: 15px; color: #94a3b8; margin-top: 8px; }
.dialog-total strong { color: #fbbf24; font-size: 20px; margin-left: 4px; }
</style>
