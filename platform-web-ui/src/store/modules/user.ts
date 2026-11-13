import { ref } from "vue"
import store from "@/store"
import { defineStore } from "pinia"
import { ElMessage } from "element-plus"
import { useNoticeStoreHook } from "./notice"
import { useSettingsStore } from "./settings"
import { useTagsViewStore } from "./tags-view"
import { getToken, removeToken, setToken } from "@/utils/cache/local-storage"
import { loginApi, getUserInfoApi, getUserPermsApi, logoutApi, getPublicKeyApi } from "@/api/login"
import { type IPermsButton, ILoginData, IPermsMenus, ILoginUserInfo } from "@/types/pms"
import { resetRouter } from "@/router"
import JSEncrypt from "jsencrypt"

export const useUserStore = defineStore("user", () => {

  // RSA Encrypt
  const encryptor = ref<JSEncrypt>(new JSEncrypt())

  // token Info
  const accessToken = ref<string>(getToken() || "")

  // User details
  const userInfo = ref<ILoginUserInfo>()

  // User permission buttons
  const buttons = ref<IPermsButton[]>([])

  // User permission menus
  const menus = ref<IPermsMenus[]>([])

  const noticeStore = useNoticeStoreHook()

  const tagsViewStore = useTagsViewStore()

  const settingsStore = useSettingsStore()

  /** Sign in */
  const userLogin = async (loginData: ILoginData) => {
    return new Promise((resolve, reject) => {
      let encryptLoginData: ILoginData = {
        username: encryptor.value.encrypt(loginData.username).toString(),
        password: encryptor.value.encrypt(loginData.password).toString(),
        codeText: loginData.codeText,
        codeKey: loginData.codeKey,
        rememberMe: loginData.rememberMe
      }
      loginApi(encryptLoginData)
        .then((resp) => {
          const token = resp.data.token_type + resp.data.access_token
          setToken(token)
          accessToken.value = token
          resolve(resp)
        })
        .catch((error) => {
          reject(error)
        })
    })
  }

  /** Get user details */
  const getUserInfo = () => {
    return new Promise((resolve, reject) => {
      getUserInfoApi()
        .then((resp) => {
          if (resp.data) {
            userInfo.value = resp.data
            resolve(resp)
          } else {
            fedLogout()
            reject(resp.message)
          }
        })
        .catch((error) => {
          reject(error)
        })
    })
  }

  /** Get user permission menus */
  const getUserPerms = () => {
    return new Promise((resolve, reject) => {
      getUserPermsApi()
        .then((resp) => {
          if (resp.data) {
            buttons.value = resp.data.buttons
            menus.value = resp.data.menus
            if (resp.data.menus.length === 0) {
              ElMessage({
                message: "You do not have permission. Contact your administrator.!",
                type: "warning"
              })
              fedLogout()
            }
            resolve(resp)
          } else {
            fedLogout()
            reject(resp.message)
          }
        })
        .catch((error) => {
          reject(error)
        })
    })
  }

  /** Sign out */
  const userLogout = () => {
    return new Promise((resolve, reject) => {
      logoutApi()
        .then((resp) => {
          fedLogout()
          resolve(resp)
        })
        .catch((error) => {
          reject(error)
        })
    })
  }

  /** Front-end sign-out without calling the backend: remove all cookie */
  const fedLogout = () => {
    removeToken()
    userInfo.value = undefined
    accessToken.value = ""
    resetRouter()

    // Close WebSocket
    noticeStore.stopWebSocket()
    noticeStore.broadcast = []
    noticeStore.pushNotices = []

    buttons.value = []
    menus.value = []
    _resetTagsView()
  }

  /** Reset Visited Views and Cached Views */
  const _resetTagsView = () => {
    if (!settingsStore.cacheTagsView) {
      tagsViewStore.delAllVisitedViews()
      tagsViewStore.delAllCachedViews()
    }
  }

  /** Get public key */
  const getPublicKey = () => {
    return new Promise((resolve, reject) => {
      getPublicKeyApi()
        .then((resp) => {
          if (resp.data) {
            encryptor.value.setPublicKey(resp.data)
            resolve(resp)
          } else {
            reject(resp.message)
          }
        })
        .catch((error) => {
          reject(error)
        })
    })
  }

  return {
    accessToken,
    userInfo,
    buttons,
    menus,
    userLogin,
    getUserPerms,
    getUserInfo,
    userLogout,
    getPublicKey,
    fedLogout
  }
})

/** Use outside setup */
export function useUserStoreHook() {
  return useUserStore(store)
}
