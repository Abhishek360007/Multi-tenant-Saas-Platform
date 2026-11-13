/** Dynamic route settings */
interface RouteSettings {
  /**
   * Enable dynamic routes?
   * 1. Requires backend support: the user details endpoint must return a field used to load dynamic routes (this project uses roles)
   * 2. If the project does not need different pages for different users, set async: false
   */
  async: boolean
  /** When dynamic routes are disabled:
   * 1. put every route in the constant routes (all signed-in users can access the same pages)
   * 2. the system assigns the signed-in user a default role with no effect
   */
  defaultRoles: Array<string>
  /**
   * Cache routes three or more levels deep?
   * 1. When enabled, routes are flattened (third-level and deeper routes become second-level)
   * 2. Because they become second-level routes, nested child routes under second-level routes stop working
   */
  thirdLevelRouteCache: boolean
}

const routeSettings: RouteSettings = {
  async: true,
  defaultRoles: ["DEFAULT_ROLE"],
  thirdLevelRouteCache: false
}

export default routeSettings
