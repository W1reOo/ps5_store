import auth from '@/plugins/auth'
import router, { constantRoutes, dynamicRoutes } from '@/router'
import { getRouters } from '@/api/menu'
import Layout from '@/layout/index'
import ParentView from '@/components/ParentView'
import InnerLink from '@/layout/components/InnerLink'

const permission = {
  state: {
    routes: [],
    addRoutes: [],
    defaultRoutes: [],
    topbarRouters: [],
    sidebarRouters: []
  },
  mutations: {
    SET_ROUTES: (state, routes) => {
      state.addRoutes = routes
      state.routes = constantRoutes.concat(routes)
    },
    SET_DEFAULT_ROUTES: (state, routes) => {
      state.defaultRoutes = constantRoutes.concat(routes)
    },
    SET_TOPBAR_ROUTES: (state, routes) => {
      state.topbarRouters = routes
    },
    SET_SIDEBAR_ROUTERS: (state, routes) => {
      state.sidebarRouters = routes
    },
  },
  actions: {
    // 生成路由
    GenerateRoutes({ commit, rootGetters }) {
      return new Promise(resolve => {
        // 向后端请求路由数据
        getRouters().then(res => {
          const roles = rootGetters.roles || []
          const isStaffOnly = roles.includes('shop_staff') && !roles.includes('admin')
          const sdata = JSON.parse(JSON.stringify(res.data))
          const rdata = JSON.parse(JSON.stringify(res.data))
          const rawSidebarRoutes = filterAsyncRouter(sdata)
          const rawRewriteRoutes = filterAsyncRouter(rdata, false, true)
          const sidebarRoutes = isStaffOnly ? removeStaffHomeRoutes(rawSidebarRoutes) : rawSidebarRoutes
          const rewriteRoutes = isStaffOnly ? removeStaffHomeRoutes(rawRewriteRoutes) : rawRewriteRoutes
          const asyncRoutes = filterDynamicRoutes(dynamicRoutes)
          const baseSidebarRoutes = isStaffOnly
            ? constantRoutes.filter(route => route.path !== '/index')
            : constantRoutes
          rewriteRoutes.push({ path: '*', redirect: '/404', hidden: true })
          router.addRoutes(asyncRoutes)
          commit('SET_ROUTES', rewriteRoutes)
          commit('SET_SIDEBAR_ROUTERS', baseSidebarRoutes.concat(sidebarRoutes))
          commit('SET_DEFAULT_ROUTES', sidebarRoutes)
          commit('SET_TOPBAR_ROUTES', sidebarRoutes)
          resolve(rewriteRoutes)
        })
      })
    }
  }
}

function removeStaffHomeRoutes(routes = []) {
  return routes
    .filter(route => !isStaffHomeRoute(route))
    .map(route => {
      const next = { ...route }
      if (next.children && next.children.length) {
        next.children = removeStaffHomeRoutes(next.children)
      }
      return next
    })
}

function isStaffHomeRoute(route = {}) {
  const path = String(route.path || '').toLowerCase()
  const component = String(route.component || '').toLowerCase()
  const title = String((route.meta && route.meta.title) || route.name || '').toLowerCase()
  return path === '/index'
    || path === 'home'
    || path === '/shop/home'
    || path.includes('/shop/home')
    || component === 'shop/home/index'
    || component.includes('shop/home/index')
    || title.includes('商城首页')
    || title === '首页'
}

// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap, lastRouter = false, type = false) {
  return asyncRouterMap.filter(route => {
    if (type && route.children) {
      route.children = filterChildren(route.children)
    }
    if (route.component) {
      // Layout ParentView 组件特殊处理
      if (route.component === 'Layout') {
        route.component = Layout
      } else if (route.component === 'ParentView') {
        route.component = ParentView
      } else if (route.component === 'InnerLink') {
        route.component = InnerLink
      } else {
        route.component = loadView(route.component)
      }
    }
    if (route.children != null && route.children && route.children.length) {
      route.children = filterAsyncRouter(route.children, route, type)
    } else {
      delete route['children']
      delete route['redirect']
    }
    return true
  })
}

function filterChildren(childrenMap, lastRouter = false) {
  var children = []
  childrenMap.forEach(el => {
    el.path = lastRouter ? lastRouter.path + '/' + el.path : el.path
    if (el.children && el.children.length && el.component === 'ParentView') {
      children = children.concat(filterChildren(el.children, el))
    } else {
      children.push(el)
    }
  })
  return children
}

// 动态路由遍历，验证是否具备权限
export function filterDynamicRoutes(routes) {
  const res = []
  routes.forEach(route => {
    if (route.permissions) {
      if (auth.hasPermiOr(route.permissions)) {
        res.push(route)
      }
    } else if (route.roles) {
      if (auth.hasRoleOr(route.roles)) {
        res.push(route)
      }
    }
  })
  return res
}

export const loadView = (view) => {
  if (process.env.NODE_ENV === 'development') {
    return (resolve) => require([`@/views/${view}`], resolve)
  } else {
    // 使用 import 实现生产环境的路由懒加载
    return () => import(`@/views/${view}`)
  }
}

export default permission
