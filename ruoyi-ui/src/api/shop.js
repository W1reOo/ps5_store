import request from '@/utils/request'

// ===== 游戏 =====
export function getGameList(params) {
  return request({ url: '/shop/game/list', method: 'get', params })
}
export function getGameDetail(gameId) {
  return request({ url: `/shop/game/${gameId}`, method: 'get' })
}
export function getCategoryList() {
  return request({ url: '/shop/category/list', method: 'get' })
}

// ===== 游戏评论 =====
export function getGameComments(params) {
  return request({ url: '/shop/comment/list', method: 'get', params })
}
export function addGameComment(data) {
  return request({ url: '/shop/comment', method: 'post', data })
}

// ===== 购物车 =====
export function getCartList() {
  return request({ url: '/shop/cart/list', method: 'get' })
}
export function addToCart(data) {
  return request({ url: '/shop/cart/add', method: 'post', data })
}
export function updateCartQuantity(data) {
  return request({ url: '/shop/cart/update', method: 'put', data })
}
export function deleteCartItem(cartId) {
  return request({ url: `/shop/cart/${cartId}`, method: 'delete' })
}

// ===== 订单 =====
export function getMyOrders() {
  return request({ url: '/shop/order/my', method: 'get' })
}
export function getOrderDetail(orderId) {
  return request({ url: `/shop/order/${orderId}`, method: 'get' })
}
export function submitOrder(data) {
  return request({ url: '/shop/order/submit', method: 'post', data })
}
export function payOrder(orderId) {
  return request({ url: `/shop/order/pay/${orderId}`, method: 'put' })
}
export function cancelOrder(orderId) {
  return request({ url: `/shop/order/cancel/${orderId}`, method: 'put' })
}
export function finishOrder(orderId) {
  return request({ url: `/shop/order/finish/${orderId}`, method: 'put' })
}
export function requestRefund(orderId) {
  return request({ url: `/shop/order/refund/request/${orderId}`, method: 'put' })
}

// ===== 省市区 =====
export function getRegions(parentId = 0) {
  return request({ url: '/shop/address/region', method: 'get', params: { parentId } })
}

// ===== 收货地址 =====
export function getAddressList() {
  return request({ url: '/shop/address/list', method: 'get' })
}
export function addAddress(data) {
  return request({ url: '/shop/address', method: 'post', data })
}
export function updateAddress(data) {
  return request({ url: '/shop/address', method: 'put', data })
}
export function deleteAddress(addressId) {
  return request({ url: `/shop/address/${addressId}`, method: 'delete' })
}
export function setDefaultAddress(addressId) {
  return request({ url: `/shop/address/default/${addressId}`, method: 'put' })
}

// ===== 管理后台 =====
export function adminGetGameList(params) {
  return request({ url: '/shop/admin/game/list', method: 'get', params })
}
export function adminGetGameDetail(gameId) {
  return request({ url: `/shop/admin/game/${gameId}`, method: 'get' })
}
export function adminAddGame(data) {
  return request({ url: '/shop/admin/game', method: 'post', data })
}
export function adminUpdateGame(data) {
  return request({ url: '/shop/admin/game', method: 'put', data })
}
export function adminDeleteGame(gameIds) {
  return request({ url: `/shop/admin/game/${gameIds}`, method: 'delete' })
}
export function adminGetCategoryList() {
  return request({ url: '/shop/admin/category/list', method: 'get' })
}
export function adminAddCategory(data) {
  return request({ url: '/shop/admin/category', method: 'post', data })
}
export function adminGetOrderList(params) {
  return request({ url: '/shop/order/admin/list', method: 'get', params })
}
export function adminDeliverOrder(orderId) {
  return request({ url: `/shop/order/admin/deliver/${orderId}`, method: 'put' })
}
export function adminApproveRefund(orderId) {
  return request({ url: `/shop/order/admin/refund/approve/${orderId}`, method: 'put' })
}

// ===== 销售额统计 =====
export function adminGetRevenueOverview() {
    return request({ url: '/shop/admin/revenue/overview', method: 'get' })
}

export function adminGetRevenueTrend(params) {
    return request({ url: '/shop/admin/revenue/trend', method: 'get', params })
}
