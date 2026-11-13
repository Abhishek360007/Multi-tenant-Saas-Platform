import {onMounted, ref} from "vue"

type OptionValue = string | number

/** Select Expected data format */
interface SelectOption {
  value: OptionValue
  label: string
  disabled?: boolean
}

/** Input format: only the API function is needed for now */
interface FetchSelectProps {
  api: () => Promise<IJsonResult<SelectOption[]>>
}

export function useFetchSelect(props: FetchSelectProps) {
  const { api } = props

  const loading = ref<boolean>(false)

  const options = ref<SelectOption[]>([])

  const value = ref<OptionValue>("")

  /** Call the API to fetch data */
  const loadData = () => {
    loading.value = true
    options.value = []
    api()
      .then((resp) => {
        const { status, data } = resp
        if (status === 200) {
          options.value = data
        }
      })
      .finally(() => {
        loading.value = false
      })
  }

  onMounted(() => {
    loadData()
  })

  return {
    loading,
    options,
    value
  }
}
