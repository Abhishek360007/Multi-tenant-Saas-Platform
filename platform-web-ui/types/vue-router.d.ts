import "vue-router"

declare module "vue-router" {
  interface RouteMeta {
    /**
     * Name shown in the sidebar and breadcrumbs
     */
    title?: string
    /**
     * Route icon; remember to import the svg @/icons/svg
     */
    svgIcon?: string
    /**
     * Route icon using an Element Plus icon directly (svgIcon wins if both are set)
     */
    elIcon?: string
    /**
     * Default false; when true the route is hidden from the sidebar
     */
    hidden?: boolean
    /**
     * Permissions required to open this route; multiple are allowed
     */
    roles?: string[]
    /**
     * Default true; when false it is hidden from breadcrumbs
     */
    breadcrumb?: boolean
    /**
     * Default false; when true it is pinned in tags-view
     */
    affix?: boolean
    /**
     * When a route has more than one child, it becomes nested automatically;
     * with only one child, that child is shown in the sidebar as the root route;
     * to always show the root route regardless of the number of children,
     * set alwaysShow: true to ignore the rule above and always show the root route
     */
    alwaysShow?: boolean
    /**
     * Example: activeMenu: "/xxx/xxx",
     * when set, opening the route highlights the sidebar item for activeMenu.
     * Useful on routes with hidden: true
     */
    activeMenu?: string
    /**
     * Cache this route page
     * Default false; true enables caching, and the route and page must then share the same Name
     */
    keepAlive?: boolean
  }
}
