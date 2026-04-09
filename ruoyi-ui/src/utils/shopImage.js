import shopDefaultCover from '@/assets/images/ps5-placeholder.svg'

export const SHOP_DEFAULT_COVER = shopDefaultCover

export function applyShopImageFallback(event, fallback = shopDefaultCover) {
  const img = event && event.target
  if (!img) {
    return
  }
  if (img.dataset.fallbackApplied === '1') {
    img.onerror = null
    return
  }
  img.dataset.fallbackApplied = '1'
  img.src = fallback
}
