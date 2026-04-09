<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="请输入订单号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="订单状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
          <el-option label="待付款" value="0" />
          <el-option label="待发货" value="1" />
          <el-option label="已发货" value="2" />
          <el-option label="已完成" value="3" />
          <el-option label="已取消" value="4" />
          <el-option label="退款申请中" value="5" />
          <el-option label="已退款" value="6" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 状态快捷标签 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabChange" class="mb8">
      <el-tab-pane label="全部" name="" />
      <el-tab-pane label="待付款" name="0" />
      <el-tab-pane name="1">
        <span slot="label">待发货 <el-badge v-if="pendingCount > 0" :value="pendingCount" type="danger" /></span>
      </el-tab-pane>
      <el-tab-pane label="已发货" name="2" />
      <el-tab-pane label="已完成" name="3" />
      <el-tab-pane label="已取消" name="4" />
      <el-tab-pane label="退款申请中" name="5" />
      <el-tab-pane label="已退款" name="6" />
    </el-tabs>

    <el-table v-loading="loading" :data="orderList">
      <el-table-column label="订单号" prop="orderNo" width="200" />
      <el-table-column label="收货人" prop="receiverName" width="90" />
      <el-table-column label="收货电话" prop="receiverPhone" width="120" />
      <el-table-column label="收货地址" prop="receiverAddress" min-width="200" show-overflow-tooltip />
      <el-table-column label="金额" prop="totalAmount" width="100" align="right">
        <template slot-scope="scope">¥{{ scope.row.totalAmount }}</template>
      </el-table-column>
      <el-table-column label="状态" width="90" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.status)" size="small">{{ statusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="物流信息" width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          <span v-if="scope.row.trackingNo">
            {{ scope.row.expressCompany }} {{ scope.row.trackingNo }}
          </span>
          <span v-else style="color:#999">—</span>
        </template>
      </el-table-column>
      <el-table-column label="下单时间" prop="createTime" width="155" align="center" />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === '1'" size="mini" type="text" icon="el-icon-truck"
            style="color:#E6A23C" @click="handleDeliver(scope.row)">发货</el-button>
          <el-button v-if="scope.row.status === '5'" size="mini" type="text" icon="el-icon-refresh-right"
            style="color:#F56C6C" @click="handleApproveRefund(scope.row)">同意退款</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 发货对话框 -->
    <el-dialog title="确认发货" :visible.sync="deliverDialog.open" width="460px" append-to-body>
      <el-form ref="deliverForm" :model="deliverForm" :rules="deliverRules" label-width="90px">
        <el-form-item label="订单号">
          <span>{{ deliverDialog.orderNo }}</span>
        </el-form-item>
        <el-form-item label="快递公司" prop="expressCompany">
          <el-select v-model="deliverForm.expressCompany" placeholder="请选择快递公司" style="width:100%">
            <el-option label="顺丰速运" value="顺丰速运" />
            <el-option label="京东物流" value="京东物流" />
            <el-option label="中通快递" value="中通快递" />
            <el-option label="圆通速递" value="圆通速递" />
            <el-option label="韵达快递" value="韵达快递" />
            <el-option label="申通快递" value="申通快递" />
            <el-option label="极兔速递" value="极兔速递" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号" prop="trackingNo">
          <el-input v-model="deliverForm.trackingNo" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="deliverDialog.open = false">取 消</el-button>
        <el-button type="primary" :loading="deliverLoading" @click="submitDeliver">确认发货</el-button>
      </div>
    </el-dialog>

    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" :visible.sync="detailDialog.open" width="680px" append-to-body>
      <template v-if="detailOrder">
        <el-descriptions :column="2" border size="small" class="mb16">
          <el-descriptions-item label="订单号">{{ detailOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="statusTagType(detailOrder.status)">{{ statusLabel(detailOrder.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="收货人">{{ detailOrder.receiverName }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailOrder.receiverPhone }}</el-descriptions-item>
          <el-descriptions-item label="收货地址" :span="2">{{ detailOrder.receiverAddress }}</el-descriptions-item>
          <el-descriptions-item label="快递公司">{{ detailOrder.expressCompany || '—' }}</el-descriptions-item>
          <el-descriptions-item label="物流单号">{{ detailOrder.trackingNo || '—' }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">¥{{ detailOrder.totalAmount }}</el-descriptions-item>
          <el-descriptions-item label="付款时间">{{ detailOrder.payTime || '—' }}</el-descriptions-item>
          <el-descriptions-item label="发货时间">{{ detailOrder.deliverTime || '—' }}</el-descriptions-item>
          <el-descriptions-item label="完成时间">{{ detailOrder.finishTime || '—' }}</el-descriptions-item>
        </el-descriptions>
        <el-table :data="detailOrder.items || []" size="small" border>
          <el-table-column label="封面" width="60" align="center">
            <template slot-scope="scope">
              <el-image :src="scope.row.coverImage" style="width:40px;height:40px;border-radius:2px" fit="cover" />
            </template>
          </el-table-column>
          <el-table-column label="游戏名称" prop="gameName" />
          <el-table-column label="单价" width="90" align="right">
            <template slot-scope="scope">¥{{ scope.row.price }}</template>
          </el-table-column>
          <el-table-column label="数量" prop="quantity" width="70" align="center" />
          <el-table-column label="小计" width="100" align="right">
            <template slot-scope="scope">¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}</template>
          </el-table-column>
        </el-table>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { adminGetOrderList, adminDeliverOrder, adminApproveRefund } from '@/api/shop'
import request from '@/utils/request'

const getOrderDetail = (id) => request({ url: `/shop/order/admin/detail/${id}`, method: 'get' })
const deliverWithTracking = (orderId, data) => request({ url: `/shop/order/admin/deliver/${orderId}`, method: 'put', data })

export default {
  name: 'ShopOrder',
  data() {
    return {
      loading: false,
      total: 0,
      orderList: [],
      pendingCount: 0,
      activeTab: '',
      queryParams: { pageNum: 1, pageSize: 10, orderNo: '', status: '' },
      deliverDialog: { open: false, orderId: null, orderNo: '' },
      deliverForm: { expressCompany: '', trackingNo: '' },
      deliverRules: {
        expressCompany: [{ required: true, message: '请选择快递公司', trigger: 'change' }],
        trackingNo:     [{ required: true, message: '请输入物流单号', trigger: 'blur' }]
      },
      deliverLoading: false,
      detailDialog: { open: false },
      detailOrder: null
    }
  },
  created() {
    const q = this.$route.query.status
    if (q !== undefined && q !== null && q !== '') {
      const s = String(q)
      this.queryParams.status = s
      this.activeTab = s
    }
    this.getList()
    this.getPendingCount()
  },
  activated() {
    this.getPendingCount()
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      adminGetOrderList(this.queryParams).then(res => {
        this.orderList = res.rows || []
        this.total     = res.total || 0
        this.loading   = false
        this.getPendingCount()
      })
    },
    getPendingCount() {
      adminGetOrderList({ pageNum: 1, pageSize: 1, status: '1' }).then(res => {
        this.pendingCount = res.total || 0
      })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.$refs.queryForm.resetFields(); this.activeTab = ''; this.getList() },
    handleTabChange(tab) {
      this.queryParams.status = tab.name
      this.queryParams.pageNum = 1
      this.getList()
    },
    statusLabel(s) { return { '0': '待付款', '1': '待发货', '2': '已发货', '3': '已完成', '4': '已取消', '5': '退款申请中', '6': '已退款' }[s] || '未知' },
    statusTagType(s) { return { '0': 'info', '1': 'warning', '2': 'primary', '3': 'success', '4': 'danger', '5': 'warning', '6': 'info' }[s] || '' },
    handleDeliver(row) {
      this.deliverDialog = { open: true, orderId: row.orderId, orderNo: row.orderNo }
      this.deliverForm = { expressCompany: '', trackingNo: '' }
    },
    submitDeliver() {
      this.$refs.deliverForm.validate(valid => {
        if (!valid) return
        this.deliverLoading = true
        deliverWithTracking(this.deliverDialog.orderId, this.deliverForm).then(() => {
          this.$message.success('发货成功')
          this.deliverDialog.open = false
          this.deliverLoading = false
          this.getList()
          this.getPendingCount()
        }).catch(() => { this.deliverLoading = false })
      })
    },
    handleDetail(row) {
      getOrderDetail(row.orderId).then(res => {
        this.detailOrder = res.data
        this.detailDialog.open = true
      })
    },
    handleApproveRefund(row) {
      this.$confirm('确认同意该订单退款？同意后将回补库存并剔除销量/销售额。', '退款审核', { type: 'warning' }).then(() => {
        adminApproveRefund(row.orderId).then(() => {
          this.$message.success('已同意退款')
          this.getList()
          this.getPendingCount()
        })
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.mb16 { margin-bottom: 16px; }
</style>
