import { computed, ref } from "vue"
import store from "@/store"
import { defineStore } from "pinia"
import { mapKeysApi } from "@/api/dict"
import { IMapDictData } from "@/types/dict"

export const useDictStore = defineStore("dict", () => {
  // Dictionary map
  const dictMapKeys = ref<any>()

  /** Dictionary map key */
  const getDictMapKeys = () => {
    return new Promise((resolve, reject) => {
      mapKeysApi()
        .then((resp) => {
          dictMapKeys.value = resp.data
          resolve(resp)
        })
        .catch((error) => {
          reject(error)
        })
    })
  }

  // Fetch dictionary data
  const getDictMapList = (dictKey: string): IMapDictData[] => {
    return dictMapKeys.value[dictKey]! as IMapDictData[]
  }

  // Fetch dictionary data
  const getDictMapValue = (dictKey: string, dictData: Object) => {
    let val = "Unknown"
    getDictMapList(dictKey).forEach((value) => {
      if (value && value.dictValue === dictData) {
        val = value.dictLabel
      }
    })
    return val
  }

  const getUserSex = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_user_sex", value)
    }
  })

  const getShowHide = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_show_hide", value)
    }
  })

  const getNormalDisable = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_normal_disable", value)
    }
  })

  const getJobStatus = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_job_status", value)
    }
  })

  const getJobGroup = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_job_group", value)
    }
  })

  const getYesNo = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_yes_no", value)
    }
  })

  const getNoticeType = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_notice_type", value)
    }
  })

  const getNoticeStatus = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_notice_status", value)
    }
  })

  const getOperType = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_oper_type", value)
    }
  })

  const getCommonStatus = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_common_status", value)
    }
  })

  const getMenuType = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_menu_type", value)
    }
  })

  const getMenuTarget = computed(() => {
    return (value: number) => {
      return getDictMapValue("sys_menu_target", value)
    }
  })

  const getQueryType = computed(() => {
    return (value: number) => {
      return getDictMapValue("statistics_query_type", value)
    }
  })

  return {
    dictMapKeys,
    getDictMapKeys,
    getDictMapList,
    getDictMapValue,
    getUserSex,
    getYesNo,
    getShowHide,
    getNormalDisable,
    getJobStatus,
    getJobGroup,
    getNoticeType,
    getNoticeStatus,
    getOperType,
    getCommonStatus,
    getMenuType,
    getMenuTarget,
    getQueryType
  }
})

/** Use outside setup */
export function useDictStoreHook() {
  return useDictStore(store)
}
