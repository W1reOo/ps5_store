<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true">
      <el-form-item label="分类名称" prop="categoryName">
        <el-input v-model="queryParams.categoryName" placeholder="请输入分类名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="categoryList">
      <el-table-column label="ID" prop="categoryId" width="80" align="center" />
      <el-table-column label="分类名称" prop="categoryName" />
      <el-table-column label="排序" prop="orderNum" width="80" align="center" />
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
            {{ scope.row.status === '0' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" width="160" align="center" />
      <el-table-column label="操作" width="150" align="center">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" style="color:#F56C6C" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="dialog.title" :visible.sync="dialog.open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="排序" prop="orderNum">
          <el-input-number v-model="form.orderNum" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialog.open = false">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { adminGetCategoryList, adminAddCategory } from '@/api/shop'
import request from '@/utils/request'

const updateCategory = (data) => request({ url: '/shop/admin/category', method: 'put', data })
const deleteCategory = (id) => request({ url: `/shop/admin/category/${id}`, method: 'delete' })

export default {
  name: 'ShopCategory',
  data() {
    return {
      loading: false,
      total: 0,
      categoryList: [],
      queryParams: { pageNum: 1, pageSize: 20, categoryName: '' },
      dialog: { open: false, title: '' },
      form: {},
      rules: {
        categoryName: [{ required: true, message: '分类名称不能为空', trigger: 'blur' }]
      }
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      adminGetCategoryList().then(res => {
        this.categoryList = (res.data || []).filter(c =>
          !this.queryParams.categoryName || c.categoryName.includes(this.queryParams.categoryName)
        )
        this.total = this.categoryList.length
        this.loading = false
      })
    },
    handleQuery() { this.getList() },
    resetQuery() { this.queryParams.categoryName = ''; this.getList() },
    handleAdd() {
      this.form = { categoryName: '', orderNum: 0, status: '0' }
      this.dialog = { open: true, title: '新增分类' }
    },
    handleEdit(row) {
      this.form = { ...row }
      this.dialog = { open: true, title: '编辑分类' }
    },
    handleDelete(row) {
      this.$confirm(`确认删除分类"${row.categoryName}"？`, '警告', { type: 'warning' }).then(() => {
        deleteCategory(row.categoryId).then(() => {
          this.$message.success('删除成功')
          this.getList()
        })
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const fn = this.form.categoryId ? updateCategory(this.form) : adminAddCategory(this.form)
        fn.then(() => {
          this.$message.success(this.form.categoryId ? '修改成功' : '新增成功')
          this.dialog.open = false
          this.getList()
        })
      })
    }
  }
}
</script>
