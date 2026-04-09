<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="brand-side">
      <div class="brand-content">
        <div class="brand-logo" @click="goShop">
          <svg viewBox="0 0 120 40" xmlns="http://www.w3.org/2000/svg" class="ps-svg">
            <text x="2" y="30" font-family="Arial Black,sans-serif" font-size="32" font-weight="900" fill="white" letter-spacing="-1">PS5</text>
            <rect x="68" y="6" width="3" height="28" rx="1.5" fill="#0070d1"/>
            <rect x="74" y="2" width="3" height="32" rx="1.5" fill="#0070d1"/>
            <rect x="80" y="8" width="3" height="24" rx="1.5" fill="#0070d1"/>
            <rect x="86" y="14" width="3" height="18" rx="1.5" fill="#0070d1"/>
          </svg>
        </div>
        <h1 class="brand-title">PS5 游戏光盘商城</h1>
        <p class="brand-desc">海量正版光盘 · 次日达 · 售后无忧</p>
        <ul class="brand-feats">
          <li><i class="el-icon-success"></i> 10,000+ 正版游戏光盘</li>
          <li><i class="el-icon-success"></i> 支持购物车下单 · 订单全程追踪</li>
          <li><i class="el-icon-success"></i> 多地商家入驻 · 竞价更实惠</li>
          <li><i class="el-icon-success"></i> 账号中心管理订单与收货地址</li>
        </ul>
        <el-button class="back-btn" @click="goShop">
          <i class="el-icon-arrow-left"></i> 返回商城浏览
        </el-button>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="form-side">
      <div class="form-card">
        <!-- Tab 切换 -->
        <div class="tab-bar">
          <div :class="['tab-item', { active: tab === 'login' }]" @click="switchTab('login')">账号登录</div>
          <div :class="['tab-item', { active: tab === 'register' }]" @click="switchTab('register')">注册账号</div>
        </div>

        <!-- ========== 账号登录 ========== -->
        <el-form v-if="tab === 'login'" ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="账号（用户名）"
              prefix-icon="el-icon-user" class="dark-input" @keyup.enter.native="doLogin" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="密码"
              prefix-icon="el-icon-lock" class="dark-input" show-password @keyup.enter.native="doLogin" />
          </el-form-item>
          <el-form-item v-if="captchaEnabled" prop="code">
            <div class="captcha-row">
              <el-input v-model="loginForm.code" placeholder="验证码"
                prefix-icon="el-icon-key" class="dark-input" maxlength="4"
                @keyup.enter.native="doLogin" />
              <img :src="codeUrl" class="captcha-img" @click="getCode" title="点击刷新" />
            </div>
          </el-form-item>
          <el-form-item>
            <el-checkbox v-model="loginForm.rememberMe" class="dark-check">记住我</el-checkbox>
          </el-form-item>
          <el-button type="primary" :loading="loading" class="submit-btn" @click="doLogin">
            {{ loading ? '登录中...' : '立即登录' }}
          </el-button>
        </el-form>

        <!-- ========== 注册账号 ========== -->
        <el-form v-if="tab === 'register'" ref="regForm" :model="regForm" :rules="regRules" class="login-form">
          <el-form-item prop="username">
            <el-input v-model="regForm.username" placeholder="用户名（字母/数字，4-20位）"
              prefix-icon="el-icon-user" class="dark-input" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="regForm.nickName" placeholder="昵称（选填）"
              prefix-icon="el-icon-s-custom" class="dark-input" maxlength="20" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="regForm.password" type="password" placeholder="密码（至少6位）"
              prefix-icon="el-icon-lock" class="dark-input" show-password />
          </el-form-item>
          <el-form-item prop="confirmPwd">
            <el-input v-model="regForm.confirmPwd" type="password" placeholder="确认密码"
              prefix-icon="el-icon-lock" class="dark-input" show-password
              @blur="$refs.regForm.validateField('confirmPwd')"
              @keyup.enter.native="doRegister" />
          </el-form-item>
          <el-form-item v-if="captchaEnabled" prop="code">
            <div class="captcha-row">
              <el-input v-model="regForm.code" placeholder="验证码"
                prefix-icon="el-icon-key" class="dark-input" maxlength="4" @keyup.enter.native="doRegister" />
              <img :src="codeUrl" class="captcha-img" @click="getCode" title="点击刷新" />
            </div>
          </el-form-item>
          <el-button type="primary" :loading="loading" class="submit-btn" @click="doRegister">
            {{ loading ? '注册中...' : '立即注册' }}
          </el-button>
        </el-form>

        <div class="form-footer">© 2025 PS5 游戏光盘商城</div>
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'
import { getCodeImg } from '@/api/login'
import { register } from '@/api/login'
import { setToken } from '@/utils/auth'

export default {
  name: "Login",
  data() {
    return {
      tab: 'login',
      codeUrl: "",
      captchaEnabled: true,
      loading: false,
      redirect: undefined,

      loginForm: { username: "", password: "", code: "", uuid: "", rememberMe: false },
      loginRules: {
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        code:     [{ required: true, message: '请输入验证码', trigger: 'blur' }]
      },

      regForm: { username: "", nickName: "", password: "", confirmPwd: "", code: "", uuid: "" },
      regRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: ['blur', 'change'] },
          { min: 4, max: 20, message: '用户名长度需在4-20位之间', trigger: ['blur', 'change'] },
          { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: ['blur', 'change'] }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: ['blur', 'change'] },
          { min: 6, max: 20, message: '密码长度需在6-20位之间', trigger: ['blur', 'change'] }
        ],
        confirmPwd: [
          { required: true, message: '请再次输入密码', trigger: ['blur', 'change'] },
          {
            validator: (rule, value, callback) => {
              if (!value) { callback(new Error('请再次输入密码')) }
              else if (value !== this.regForm.password) { callback(new Error('两次输入的密码不一致')) }
              else { callback() }
            },
            trigger: ['blur', 'change']
          }
        ],
        code: [{ required: true, message: '请输入验证码', trigger: ['blur', 'change'] }]
      }
    }
  },
  watch: {
    $route: {
      handler(route) { this.redirect = route.query && route.query.redirect },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    switchTab(t) {
      this.tab = t
      this.getCode()
    },

    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
          this.regForm.uuid   = res.uuid
        }
      })
    },

    getCookie() {
      const username   = Cookies.get("username")
      const password   = Cookies.get("password")
      const rememberMe = Cookies.get("rememberMe")
      this.loginForm.username   = username   === undefined ? "" : username
      this.loginForm.password   = password   === undefined ? "" : decrypt(password)
      this.loginForm.rememberMe = rememberMe === undefined ? false : Boolean(rememberMe)
    },

    doLogin() {
      this.$refs.loginForm.validate(valid => {
        if (!valid) return
        this.loading = true
        const { username, password, rememberMe, code, uuid } = this.loginForm
        if (rememberMe) {
          Cookies.set("username",   username,          { expires: 30 })
          Cookies.set("password",   encrypt(password), { expires: 30 })
          Cookies.set("rememberMe", rememberMe,        { expires: 30 })
        } else {
          Cookies.remove("username")
          Cookies.remove("password")
          Cookies.remove("rememberMe")
        }
        this.$store.dispatch("Login", { username, password, code, uuid, rememberMe }).then(() => {
          // 统一跳 /index，由 permission.js 根据角色决定最终去哪里
          this.$router.replace({ path: '/index' })
        }).catch(() => {
          this.loading = false
          this.getCode()
        })
      })
    },

    doRegister() {
      const { username, password, confirmPwd, code } = this.regForm
      // 手动逐项校验，确保提示必定弹出
      if (!username) {
        this.$message.warning('请输入用户名'); return
      }
      if (username.length < 4 || username.length > 20) {
        this.$message.warning('用户名长度需在4-20位之间'); return
      }
      if (!/^[a-zA-Z0-9_]+$/.test(username)) {
        this.$message.warning('用户名只能包含字母、数字和下划线'); return
      }
      if (!password) {
        this.$message.warning('请输入密码'); return
      }
      if (password.length < 6) {
        this.$message.warning('密码长度不能少于6位'); return
      }
      if (password.length > 20) {
        this.$message.warning('密码长度不能超过20位'); return
      }
      if (!confirmPwd) {
        this.$message.warning('请再次输入密码'); return
      }
      if (password !== confirmPwd) {
        this.$message.warning('两次输入的密码不一致'); return
      }
      if (!code) {
        this.$message.warning('请输入验证码'); return
      }
      // 触发表单字段红色高亮（视觉反馈）
      this.$refs.regForm.validate(() => {})
      this.loading = true
      const { nickName, uuid } = this.regForm
      register({ username, password, nickName, code, uuid }).then(() => {
        this.$message.success('注册成功，请登录')
        this.tab = 'login'
        this.getCode()
        this.loading = false
      }).catch(() => {
        this.loading = false
        this.getCode()
      })
    },

    goShop() { this.$router.push("/shop/home") }
  }
}
</script>

<style scoped>
* { box-sizing: border-box; margin: 0; padding: 0; }

.login-page {
  display: flex;
  min-height: 100vh;
  background: #050e1f;
  font-family: 'Segoe UI', 'PingFang SC', sans-serif;
}

/* ===== 左侧品牌 ===== */
.brand-side {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #050e1f 0%, #0a1628 50%, #0d1f3c 100%);
  padding: 60px 40px;
  position: relative;
  overflow: hidden;
}
.brand-side::before {
  content: '';
  position: absolute;
  width: 500px; height: 500px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(0,112,209,.15) 0%, transparent 70%);
  top: -100px; left: -100px;
}
.brand-content { position: relative; max-width: 440px; }
.brand-logo { cursor: pointer; margin-bottom: 24px; }
.ps-svg { width: 140px; height: 46px; }
.brand-title { font-size: 34px; font-weight: 900; color: #fff; margin-bottom: 10px; line-height: 1.2; }
.brand-desc { font-size: 15px; color: #60a5fa; margin-bottom: 32px; }
.brand-feats { list-style: none; display: flex; flex-direction: column; gap: 12px; margin-bottom: 40px; }
.brand-feats li { display: flex; align-items: center; gap: 10px; font-size: 14px; color: #94a3b8; }
.brand-feats li i { color: #22d3ee; font-size: 16px; }
.back-btn {
  background: rgba(59,130,246,.15);
  border: 1px solid rgba(59,130,246,.35);
  color: #60a5fa;
  border-radius: 10px;
  padding: 10px 22px;
}
.back-btn:hover { background: rgba(59,130,246,.25); }

/* ===== 右侧表单 ===== */
.form-side {
  width: 460px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(10,18,35,.98);
  padding: 40px 32px;
  border-left: 1px solid rgba(148,163,184,.1);
}
.form-card { width: 100%; max-width: 380px; }

/* ===== Tabs ===== */
.tab-bar {
  display: flex;
  margin-bottom: 28px;
  border-bottom: 1px solid rgba(148,163,184,.15);
}
.tab-item {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  font-size: 14px;
  color: #64748b;
  cursor: pointer;
  border-bottom: 2px solid transparent;
  transition: all .2s;
  user-select: none;
}
.tab-item:hover { color: #94a3b8; }
.tab-item.active { color: #60a5fa; border-bottom-color: #3b82f6; font-weight: 600; }

/* ===== Form ===== */
.login-form { display: flex; flex-direction: column; gap: 4px; }

:deep(.dark-input .el-input__inner) {
  background: rgba(30,41,59,.8);
  border: 1px solid rgba(148,163,184,.2);
  border-radius: 10px;
  color: #e2e8f0;
  height: 46px;
  font-size: 14px;
  transition: border-color .2s;
}
:deep(.dark-input .el-input__inner:focus) { border-color: rgba(59,130,246,.6); }
:deep(.dark-input .el-input__inner::placeholder) { color: #475569; }
:deep(.dark-input .el-input__prefix) { color: #475569; line-height: 46px; }
:deep(.dark-input .el-input__suffix) { line-height: 46px; }
:deep(.el-form-item__error) {
  color: #f87171;
  font-size: 12px;
  padding-top: 2px;
}
:deep(.el-form-item) { margin-bottom: 22px; }
:deep(.el-form-item.is-error .el-input__inner) { border-color: #f56c6c !important; }

.captcha-row { display: flex; gap: 10px; align-items: center; }
.captcha-row :deep(.el-input) { flex: 1; }
.captcha-img { height: 46px; border-radius: 8px; cursor: pointer; border: 1px solid rgba(148,163,184,.2); flex-shrink: 0; }

:deep(.dark-check .el-checkbox__label) { color: #64748b; font-size: 13px; }
:deep(.dark-check .el-checkbox__inner) { background: rgba(30,41,59,.8); border-color: rgba(148,163,184,.3); }
:deep(.dark-check.is-checked .el-checkbox__inner) { background: #3b82f6; border-color: #3b82f6; }

.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 700;
  border-radius: 12px;
  background: linear-gradient(135deg, #1d4ed8, #0070d1);
  border: none;
  letter-spacing: 2px;
  margin-top: 6px;
  box-shadow: 0 4px 20px rgba(29,78,216,.4);
  transition: all .2s;
}
.submit-btn:hover { background: linear-gradient(135deg, #2563eb, #0080f0); transform: translateY(-1px); }

.form-footer {
  text-align: center;
  font-size: 12px;
  color: #334155;
  margin-top: 32px;
}

@media (max-width: 768px) {
  .brand-side { display: none; }
  .form-side { width: 100%; }
}
</style>
