import store from "@/store"
import { responseTimesToLocal } from "@/utils/utc-time"
import { defineStore } from "pinia"
import { computed, ref } from "vue"
import { Client } from "@stomp/stompjs"
// @ts-ignore
import SockJS from "sockjs-client/dist/sockjs.min.js"
import { getToken } from "@/utils/cache/local-storage"
import { useTenantStoreHook } from "./tenant"
import { getEnvBaseURL } from "@/utils"
import { IPushMessage } from "@/types/base"

export const useNoticeStore = defineStore("notice", () => {
  // Broadcast message
  const broadcast = ref<IPushMessage[]>([])

  // Push message
  const pushNotices = ref<IPushMessage[]>([])

  // Tenant creation result message
  const createTenantNotice = ref<IPushMessage>()

  const stompClientActive = ref<boolean>(false)

  // websocket Client
  const stompClient = ref<Client>()

  // Start WebSocket
  const startWebSocket = () => {
    return new Promise<void>((resolve, reject) => {
      const tenantStore = useTenantStoreHook()
      const stompClientAgent = new Client({
        connectHeaders: {
          Authorization: getToken()!,
          "x-tenant-id": tenantStore.getCurrentTenantId()
        },
        debug: function () {},
        reconnectDelay: 5000, // Reconnect delay
        heartbeatIncoming: 4000,
        heartbeatOutgoing: 4000
      })

      const wsUrl = `${getEnvBaseURL()}/stomp/push`

      // Use SockJS instead brokenURL
      stompClientAgent.webSocketFactory = function () {
        return new SockJS(wsUrl)
      }

      // Connect
      stompClientAgent.onConnect = (frame) => {
        broadcast.value = []
        pushNotices.value = []

        console.log("WebSocket connected :>> ", frame.headers["user-name"])

        // Subscribe to the broadcast topic
        stompClientAgent.subscribe("/topic/broadcast/notice", (resp) => {
          const result = responseTimesToLocal(JSON.parse(resp.body)) as IPushMessage
          broadcast.value?.push(result)
        })

        // Subscribe to the push topic
        stompClientAgent.subscribe("/user/queue/single/push", (resp) => {
          const result = responseTimesToLocal(JSON.parse(resp.body)) as IPushMessage
          pushNotices.value?.push(result)
        })

        // Subscribe to the tenant creation result topic
        stompClientAgent.subscribe("/user/queue/single/create/tenant", (resp) => {
          const result = responseTimesToLocal(JSON.parse(resp.body)) as IPushMessage
          createTenantNotice.value = result
          pushNotices.value?.push(result)
        })
      }

      // Error
      stompClientAgent.onStompError = function (frame) {
        console.log("Broker reported error: " + frame.headers["message"])
        console.log("Additional details: " + frame.body)
        reject(frame.body)
      }

      // Start
      stompClientAgent.activate()

      stompClient.value = stompClientAgent

      stompClientActive.value = stompClientAgent.active

      resolve()
    })
  }

  const stopWebSocket = () => {
    // Stop
    if (stompClient.value) {
      stompClient.value!.deactivate()
    }
  }

  const getStompClientActive = (): boolean => {
    return stompClientActive.value
  }

  // Receive broadcast messages
  const getBroadcast = computed(() => {
    return broadcast.value
  })

  // Receive push messages
  const getPushNotices = computed(() => {
    return pushNotices.value
  })

  return {
    broadcast,
    pushNotices,
    createTenantNotice,
    getBroadcast,
    getPushNotices,
    startWebSocket,
    stopWebSocket,
    getStompClientActive
  }
})

/** Use outside setup */
export function useNoticeStoreHook() {
  return useNoticeStore(store)
}
