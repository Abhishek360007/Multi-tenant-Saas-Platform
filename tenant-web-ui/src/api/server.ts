import {IServerInfo} from "@/types/sys"
import {httpGet} from "@/utils/service"

/** Query server information */
export const getServerInfoApi = () => {
  return httpGet<any, IJsonResult<IServerInfo>>("/server/info")
}
