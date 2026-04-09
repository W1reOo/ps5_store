import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

// 无需登录即可访问的路径（商城所有页面对外开放，需要登录的操作由各页面自行检查）
const whiteList = [
  '/', '/login', '/register',
  '/shop/home', '/shop/detail/*',
  '/shop/account', '/shop/cart', '/shop/orders'
]

// 后台管理路径前缀（非 admin 不应进入）
const adminPaths = ['/index', '/system', '/monitor', '/tool', '/shop/category', '/shop/game', '/shop/order']
const isAdminPath = (path) => adminPaths.some(p => path === p || path.startsWith(p + '/') || path.startsWith(p + '?'))

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

/** 商城前台路由前缀：浏览器标签使用独立品牌名，不沿用后台 VUE_APP_TITLE */
const SHOP_PREFIX = '/shop'

/** 使用商城品牌作为浏览器标签（与 VUE_APP_SHOP_TITLE 一致，非管理后台标题） */
const isStorefrontPublicTitle = (path) =>
  path.startsWith(SHOP_PREFIX) || path === '/login' || path === '/register'

router.beforeEach((to, from, next) => {
  NProgress.start()
  if (getToken()) {
    if (!isStorefrontPublicTitle(to.path)) {
      to.meta.title && store.dispatch('settings/setTitle', to.meta.title)
    }
    const isLock = store.getters.isLock
    /* has token*/
    if (to.path === '/login') {
      // 已登录再访问登录页：admin去后台，普通用户去商城首页
      const roles = store.getters.roles || []
      next({ path: roles.includes('admin') ? '/index' : '/shop/home' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else if (isLock && to.path !== '/lock') {
      next({ path: '/lock' })
      NProgress.done()
    } else if (!isLock && to.path === '/lock') {
      next({ path: '/' })
      NProgress.done()
    } else {
      if (store.getters.roles.length === 0) {
        isRelogin.show = true
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          store.dispatch('GenerateRoutes').then(accessRoutes => {
            router.addRoutes(accessRoutes)
            const roles = store.getters.roles || []
            const isAdmin = roles.includes('admin')
            if (isAdmin) {
              // 管理员：进后台（目标是 /index）
              next({ ...to, replace: true })
            } else {
              // 普通用户：若误入后台则踢到商城首页，否则正常进入
              next({ path: isAdminPath(to.path) ? '/shop/home' : to.path, replace: true })
            }
          })
        }).catch(err => {
          store.dispatch('LogOut').then(() => {
            Message.error(err)
            next({ path: '/' })
          })
        })
      } else {
        // roles 已有，直接放行（但非 admin 访问后台路径要拦截）
        const roles = store.getters.roles || []
        if (!roles.includes('admin') && isAdminPath(to.path)) {
          next({ path: '/shop/home' })
        } else {
          next()
        }
      }
    }
  } else {
    // 没有token
    if (isWhiteList(to.path)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach((to) => {
  if (isStorefrontPublicTitle(to.path)) {
    const brand = process.env.VUE_APP_SHOP_TITLE || 'PS5游戏光盘商城'
    const sub = (to.meta && to.meta.title) ? String(to.meta.title).trim() : ''
    document.title = sub ? `${sub} - ${brand}` : brand
  }
  NProgress.done()
})
