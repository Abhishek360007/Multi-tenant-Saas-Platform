import {type RouteLocationNormalized} from "vue-router"

const loginUrl = "/login"

/** Routes that skip sign-in (matched by path) */
const whiteListByPath: string[] = [loginUrl]

/** Routes that skip sign-in (matched by name) */
const whiteListByName: string[] = []

/** Check the allowlist */
const isWhiteList = (to: RouteLocationNormalized) => {
  // path Matching either path or name is enough
  return whiteListByPath.indexOf(to.path) !== -1 || whiteListByName.indexOf(to.name as any) !== -1
}

export { isWhiteList, whiteListByPath, loginUrl }
