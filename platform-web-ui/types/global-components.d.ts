import SvgIcon from "@/components/SvgIcon/index.vue"

/** Components registered globally with app.component must be declared here to get Volar type hints */
declare module "vue" {
  export interface GlobalComponents {
    SvgIcon: typeof SvgIcon
  }
}

export {}
