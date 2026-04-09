<template>
  <div class="app-container" v-loading="loading">
    <el-card shadow="never" class="mb16">
      <div slot="header" class="panel-header">
        <span><i class="el-icon-s-finance" /> 销售额总览</span>
      </div>

      <div class="revenue-summary">
        <div class="sum-item">
          <div class="sum-value">¥{{ formatMoney(overview.totalRevenue) }}</div>
          <div class="sum-label">已付款订单销售额</div>
        </div>
        <div class="sum-item">
          <div class="sum-value">{{ overview.paidOrderCount || 0 }}</div>
          <div class="sum-label">已付款订单数</div>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="mb16">
      <div slot="header" class="panel-header">
        <span><i class="el-icon-data-analysis" /> 按游戏排行 TOP3</span>
      </div>

      <el-table :data="overview.gameRows || []" size="small" border>
        <el-table-column label="游戏ID" prop="gameId" width="90" align="center" />
        <el-table-column label="游戏名称" prop="gameName" min-width="220" show-overflow-tooltip />
        <el-table-column label="销量" prop="soldQuantity" width="110" align="right" />
        <el-table-column label="销售额" prop="revenue" min-width="140" align="right">
          <template slot-scope="scope">¥{{ formatMoney(scope.row.revenue) }}</template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-card shadow="never">
      <div slot="header" class="panel-header panel-header-row">
        <span><i class="el-icon-data-line" /> 营业额趋势</span>
        <el-date-picker
          v-model="trendRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
          :picker-options="pickerOptions"
          size="small"
          @change="onTrendRangeChange"
        />
      </div>
      <p class="trend-hint">
        按<strong>付款时间</strong>汇总到自然日（无付款时间时用下单时间、完成时间兜底）。「完成」在今日、付款在别日 → 金额记在付款日。
        请确认日期范围<strong>年份</strong>与订单一致，否则曲线会为 0。
      </p>
      <div ref="trendChart" class="trend-chart" />
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
require('echarts/theme/macarons')
import { adminGetRevenueOverview, adminGetRevenueTrend } from '@/api/shop'

export default {
  name: 'ShopRevenue',
  data() {
    return {
      loading: false,
      overview: {
        totalRevenue: 0,
        paidOrderCount: 0,
        gameRows: []
      },
      trendRange: [],
      trendChart: null,
      pickerOptions: {
        shortcuts: [
          {
            text: '近7天',
            onClick: (picker) => {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 6)
              picker.$emit('pick', [start, end])
            }
          },
          {
            text: '近30天',
            onClick: (picker) => {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 29)
              picker.$emit('pick', [start, end])
            }
          }
        ]
      }
    }
  },
  created() {
    this.initDefaultRange()
    this.load()
  },
  mounted() {
    window.addEventListener('resize', this.resizeChart)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeChart)
    if (this.trendChart) {
      this.trendChart.dispose()
      this.trendChart = null
    }
  },
  methods: {
    initDefaultRange() {
      const end = new Date()
      const start = new Date()
      start.setTime(start.getTime() - 3600 * 1000 * 24 * 6)
      this.trendRange = [this.formatDateStr(start), this.formatDateStr(end)]
    },
    formatDateStr(d) {
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
    },
    load() {
      this.loading = true
      adminGetRevenueOverview().then(res => {
        const data = res.data || {}
        this.overview = {
          totalRevenue: data.totalRevenue || 0,
          paidOrderCount: data.paidOrderCount || 0,
          gameRows: data.gameRows || []
        }
      }).finally(() => { this.loading = false })
      this.loadTrend()
    },
    onTrendRangeChange() {
      this.loadTrend()
    },
    loadTrend() {
      if (!this.trendRange || this.trendRange.length !== 2) {
        return
      }
      const [beginDate, endDate] = this.trendRange
      adminGetRevenueTrend({ beginDate, endDate }).then(res => {
        const list = res.data || []
        this.$nextTick(() => this.renderTrendChart(list, beginDate, endDate))
      })
    },
    /** 补全区间内无数据的日期为 0，保证折线连续 */
    buildSeriesData(beginDate, endDate, points) {
      const map = {}
      ;(points || []).forEach(p => {
        if (p.statDate != null) {
          map[String(p.statDate)] = Number(p.revenue || 0)
        }
      })
      const labels = []
      const values = []
      const start = new Date(beginDate + 'T00:00:00')
      const end = new Date(endDate + 'T00:00:00')
      for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
        const key = this.formatDateStr(d)
        labels.push(key.slice(5))
        values.push(map[key] != null ? map[key] : 0)
      }
      return { labels, values }
    },
    renderTrendChart(points, beginDate, endDate) {
      const el = this.$refs.trendChart
      if (!el) return
      const { labels, values } = this.buildSeriesData(beginDate, endDate, points)
      if (!this.trendChart) {
        this.trendChart = echarts.init(el, 'macarons')
      }
      this.trendChart.setOption({
        tooltip: {
          trigger: 'axis',
          formatter: (items) => {
            if (!items || !items.length) return ''
            const i = items[0]
            return `${i.axisValue}<br/>营业额：¥${Number(i.data).toFixed(2)}`
          }
        },
        grid: { left: 48, right: 24, top: 32, bottom: 40 },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: labels,
          axisLabel: { color: '#606266' }
        },
        yAxis: {
          type: 'value',
          name: '营业额(元)',
          axisLabel: {
            formatter: (v) => (v >= 1000 ? (v / 1000).toFixed(1) + 'k' : v)
          }
        },
        series: [
          {
            name: '营业额',
            type: 'line',
            smooth: true,
            data: values,
            itemStyle: { color: '#409EFF' },
            areaStyle: { color: 'rgba(64, 158, 255, 0.12)' }
          }
        ]
      }, true)
      this.resizeChart()
    },
    resizeChart() {
      if (this.trendChart) {
        this.trendChart.resize()
      }
    },
    formatMoney(v) {
      const n = Number(v || 0)
      return n.toFixed(2)
    }
  }
}
</script>

<style scoped>
.mb16 {
  margin-bottom: 16px;
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 6px;
}

.panel-header-row {
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 12px;
}

.revenue-summary {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}

.sum-item {
  flex: 1;
  min-width: 220px;
  background: #f7f8fa;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 16px;
}

.sum-value {
  font-size: 22px;
  font-weight: 700;
  color: #303133;
}

.sum-label {
  margin-top: 6px;
  font-size: 13px;
  color: #909399;
}

.trend-hint {
  margin: 0 0 12px;
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}

.trend-chart {
  width: 100%;
  height: 360px;
}
</style>
