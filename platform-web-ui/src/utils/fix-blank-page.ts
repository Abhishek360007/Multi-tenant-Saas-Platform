import { useTagsViewStoreHook } from "@/store/modules/tags-view"

/**
 * Purpose: fix a blank page caused by combining <transition> and <keep-alive>
 * Cause: appears to be a Vue bug:https://github.com/vuejs/core/issues/7121
 * Reproduce without this function:
 * 1. Open a page
 * 2. Edit and save that page TS code
 * 3. Switch pages in the browser
 * 4. Result: the content area stays blank
 */
export const fixBlankPage = () => {
  const tagsViewStore = useTagsViewStoreHook()
  tagsViewStore.cachedViews = [...tagsViewStore.cachedViews]
}
