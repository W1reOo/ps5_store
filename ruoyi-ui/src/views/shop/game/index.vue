<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="游戏名称" prop="gameName">
        <el-input v-model="queryParams.gameName" placeholder="请输入游戏名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="全部分类" clearable>
          <el-option v-for="c in categories" :key="c.categoryId" :label="c.categoryName" :value="c.categoryId" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="上架" value="0" />
          <el-option label="下架" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增商品</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="gameList">
      <el-table-column label="ID" prop="gameId" width="70" align="center" />
      <el-table-column label="封面" width="80" align="center">
        <template slot-scope="scope">
          <el-image :src="scope.row.coverImage" style="width:50px;height:50px;border-radius:4px" fit="cover" />
        </template>
      </el-table-column>
      <el-table-column label="游戏名称" prop="gameName" min-width="160" show-overflow-tooltip />
      <el-table-column label="分类" prop="categoryName" width="100" />
      <el-table-column label="价格(元)" prop="price" width="100" align="right">
        <template slot-scope="scope">¥{{ scope.row.price }}</template>
      </el-table-column>
      <el-table-column label="库存" prop="stock" width="80" align="center" />
      <el-table-column label="热门" width="70" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.isHot === '1' ? 'danger' : 'info'">{{ scope.row.isHot === '1' ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="新品" width="70" align="center">
        <template slot-scope="scope">
          <el-tag size="mini" :type="scope.row.isNew === '1' ? 'warning' : 'info'">{{ scope.row.isNew === '1' ? '是' : '否' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">{{ scope.row.status === '0' ? '上架' : '下架' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" style="color:#F56C6C" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialog.title" :visible.sync="dialog.open" width="680px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="游戏名称" prop="gameName">
              <el-input v-model="form.gameName" placeholder="请输入游戏名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%">
                <el-option v-for="c in categories" :key="c.categoryId" :label="c.categoryName" :value="c.categoryId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="语言" prop="language">
              <el-input v-model="form.language" placeholder="例如：EN / 中文" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="发行商" prop="publisher">
              <el-input v-model="form.publisher" placeholder="请输入发行商" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开发商" prop="developer">
              <el-input v-model="form.developer" placeholder="请输入开发商" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="评级" prop="rating">
              <el-input v-model="form.rating" placeholder="例如：E / T / M / 18+" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发行日期">
              <el-date-picker
                v-model="form.releaseDate"
                type="date"
                placeholder="选择日期"
                style="width:100%"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="售价(元)" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" :step="10" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="原价(元)">
              <el-input-number v-model="form.originalPrice" :min="0" :precision="2" :step="10" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="销量" help="销量由用户付款成功自动统计" style="margin-bottom:0;">
              <el-input-number v-model="form.sales" :min="0" :disabled="true" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图片">
          <!-- 模式切换 -->
          <el-radio-group v-model="coverMode" size="mini" style="margin-bottom:8px" @change="onCoverModeChange">
            <el-radio-button label="url">输入URL</el-radio-button>
            <el-radio-button label="upload">本地上传</el-radio-button>
          </el-radio-group>
          <!-- URL 输入 -->
          <el-input v-if="coverMode === 'url'" v-model="form.coverImage" placeholder="请输入图片URL" clearable />
          <!-- 本地上传 -->
          <div v-else>
            <el-upload
              action="/dev-api/common/upload"
              :headers="uploadHeaders"
              :show-file-list="false"
              :before-upload="beforeCoverUpload"
              :on-success="handleCoverSuccess"
              :on-error="handleCoverError"
              accept="image/*"
            >
              <div class="cover-upload-area">
                <img v-if="form.coverImage" :src="form.coverImage" class="cover-preview" />
                <div v-else class="cover-placeholder">
                  <i class="el-icon-plus" style="font-size:28px;color:#8c939d" />
                  <div style="font-size:12px;color:#8c939d;margin-top:4px">点击上传封面</div>
                </div>
                <div v-if="coverUploading" class="cover-uploading">
                  <i class="el-icon-loading" /> 上传中...
                </div>
              </div>
            </el-upload>
            <div style="font-size:12px;color:#999;margin-top:4px">支持 JPG/PNG/WEBP，建议尺寸 300×400，不超过 2MB</div>
          </div>
          <!-- 当前封面预览（URL模式下） -->
          <div v-if="coverMode === 'url' && form.coverImage" style="margin-top:6px">
            <el-image :src="form.coverImage" style="width:60px;height:80px;border-radius:4px;border:1px solid #eee" fit="cover">
              <div slot="error" style="width:100%;height:100%;display:flex;align-items:center;justify-content:center;background:#f5f5f5;font-size:12px;color:#999">无效</div>
            </el-image>
          </div>
        </el-form-item>
        <el-form-item label="游戏简介">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入游戏简介" />
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">上架</el-radio>
                <el-radio label="1">下架</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="热门">
              <el-switch v-model="form.isHot" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="新品">
              <el-switch v-model="form.isNew" active-value="1" inactive-value="0" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer">
        <el-button @click="dialog.open = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { adminGetGameList, adminAddGame, adminUpdateGame, adminDeleteGame, adminGetCategoryList } from '@/api/shop'
import { getToken } from '@/utils/auth'

export default {
  name: 'ShopGame',
  data() {
    return {
      loading: false,
      total: 0,
      gameList: [],
      categories: [],
      queryParams: { pageNum: 1, pageSize: 10, gameName: '', categoryId: '', status: '' },
      dialog: { open: false, title: '' },
      form: {},
      coverMode: 'url',
      coverUploading: false,
      rules: {
        gameName:   [{ required: true, message: '游戏名称不能为空', trigger: 'blur' }],
        categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
        price:      [{ required: true, message: '请输入价格', trigger: 'blur' }],
        stock:      [{ required: true, message: '请输入库存', trigger: 'blur' }],
        publisher:  [{ required: true, message: '请输入发行商', trigger: 'blur' }],
        developer:  [{ required: true, message: '请输入开发商', trigger: 'blur' }],
        language:   [{ required: true, message: '请输入语言', trigger: 'blur' }],
        rating:     [{ required: true, message: '请输入评级', trigger: 'blur' }]
      }
    }
  },
  computed: {
    uploadHeaders() {
      return { Authorization: 'Bearer ' + getToken() }
    }
  },
  created() {
    this.loadCategories()
    this.getList()
  },
  methods: {
    loadCategories() {
      adminGetCategoryList().then(res => { this.categories = res.data || [] })
    },
    getList() {
      this.loading = true
      adminGetGameList(this.queryParams).then(res => {
        this.gameList = res.rows || []
        this.total    = res.total || 0
        this.loading  = false
      })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.$refs.queryForm.resetFields()
      this.getList()
    },
    handleAdd() {
      this.form = {
        gameName: '',
        categoryId: '',
        publisher: '',
        developer: '',
        language: 'EN',
        rating: '',
        releaseDate: null,
        originalPrice: 0,
        price: 0,
        stock: 0,
        coverImage: '',
        description: '',
        status: '0',
        isHot: '0',
        isNew: '0'
      }
      this.coverMode = 'url'
      this.dialog = { open: true, title: '新增商品' }
    },
    handleEdit(row) {
      this.form = { ...row }
      this.coverMode = 'url'
      this.dialog = { open: true, title: '编辑商品' }
    },
    onCoverModeChange() {
      // 切换模式时清空封面，避免模式间残留
      this.form.coverImage = ''
    },
    beforeCoverUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isImage) { this.$message.error('只能上传图片文件'); return false }
      if (!isLt2M)  { this.$message.error('图片大小不能超过 2MB'); return false }
      this.coverUploading = true
      return true
    },
    handleCoverSuccess(res) {
      this.coverUploading = false
      if (res.code === 200) {
        this.form.coverImage = res.url
        this.$message.success('封面上传成功')
      } else {
        this.$message.error('上传失败：' + (res.msg || '未知错误'))
      }
    },
    handleCoverError() {
      this.coverUploading = false
      this.$message.error('上传失败，请检查网络或重试')
    },
    handleDelete(row) {
      this.$confirm(`确认删除"${row.gameName}"？`, '警告', { type: 'warning' }).then(() => {
        adminDeleteGame(row.gameId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const payload = { ...this.form }
        delete payload.sales
        const fn = this.form.gameId ? adminUpdateGame(payload) : adminAddGame(payload)
        fn.then(() => {
          this.$message.success(this.form.gameId ? '修改成功' : '新增成功')
          this.dialog.open = false
          this.getList()
        })
      })
    }
  }
}
</script>

<style scoped>
.cover-upload-area {
  position: relative;
  width: 120px;
  height: 160px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  transition: border-color 0.2s;
}
.cover-upload-area:hover {
  border-color: #409eff;
}
.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.cover-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.cover-uploading {
  position: absolute;
  inset: 0;
  background: rgba(0,0,0,0.5);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  gap: 4px;
}
</style>
