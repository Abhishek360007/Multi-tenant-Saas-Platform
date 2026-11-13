/** Login log */
export interface ILoginLog {
  /* ID */
  id: string
  /*Grant type*/
  grantType: string
  /*User ID*/
  userId: number
  /*User nickname*/
  nickname: string
  /*Username*/
  username: string
  /*Avatar*/
  avatar: string
  /*Request URL*/
  requestUrl: string
  /*Request IP*/
  requestIp: string
  /*Request URL*/
  address: string
  /*Operating system*/
  osName: string
  /*Browser*/
  browser: string
  /*Created at*/
  createTime: string
}

/** Operation log */
export interface IOperLog {
  /*ID*/
  id: string
  /*Title*/
  title: string
  /*Description*/
  description: string
  /*Service name*/
  serviceName: string
  /*User ID*/
  userId: number
  /*User nickname*/
  nickname: string
  /*Username*/
  username: string
  /*Avatar*/
  avatar: string
  /*Method*/
  method: string
  /*Method arguments*/
  methodArgs: string
  /*Request method*/
  requestMethod: string
  /*Request URL*/
  requestUrl: string
  /*Request IP*/
  requestIp: string
  /*Request URL*/
  address: string
  /*Response*/
  responseResult: string
  /*Status*/
  status: number
  /*Error log*/
  errorLog: string
  /*Duration (ms)*/
  timeCost: number
  /*Created at*/
  createTime: string
}
