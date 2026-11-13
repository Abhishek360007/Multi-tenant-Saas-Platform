// json Return value
interface IJsonResult<T> {
  /* Status code 200: success, 100: failure, anything else: error */
  status: number
  /* Response message */
  message: string
  /* Data */
  data: T
}

// Paged query
interface IPagination<T> {
  /* Total pages */
  total: number
  /* Data list */
  list: T[]
}

// Paged response
interface IPageResult<T> {
  /* Status code 200: success, 100: failure, anything else: error */
  status: number
  /* Response message */
  message: string
  /* Paged data */
  data: IPagination<T>
}
