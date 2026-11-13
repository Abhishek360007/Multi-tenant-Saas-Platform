import {cloneVNode, createVNode, defineComponent, h, KeepAlive, type VNode} from "vue"
import {useRoute} from "vue-router"
import {useTagsViewStore} from "@/store/modules/tags-view"

interface CompConsumerProps {
  component: VNode
}

/** compMap stores route names and their components */
const compMap = new Map<string, VNode>()

/**
 * CompConsumer Component
 * Usage: replace the <keep-alive> tag and its contents with:<CompConsumer :component="Component" />
 * Pro: cached routes only need the route name, not the component Name
 * Con: dynamic routes pointing at the same component share it (e.g. editing /info/1 also changes /info/2)
 */
export const CompConsumer = defineComponent(
  (props: CompConsumerProps) => {
    const tagsViewStore = useTagsViewStore()
    const route = useRoute()
    return () => {
      // Get the passed component
      const component = props.component
      // If there is no name, handle it directly name
      if (!route.name) return component
      // Get the current component name
      const compName = (component.type as any)?.name
      // Get the current route name
      const routeName = route.name as string
      let Comp: VNode
      // Check whether compMap already has this component
      if (compMap.has(routeName)) {
        // If it does, render with it
        Comp = compMap.get(routeName)!
      } else {
        // Otherwise clone the component, create a new one and add it to compMap
        const node = cloneVNode(component)
        if (compName && compName === routeName) {
          ;(node.type as any).name = `__${compName}__CUSTOM_NAME`
        }
        // @ts-expect-error this is VNode
        Comp = defineComponent({
          name: routeName,
          setup() {
            return () => node
          }
        })
        compMap.set(routeName, Comp)
      }
      // Use createVNode to create a KeepAlive component that caches the components in cachedViews
      return createVNode(
        KeepAlive,
        {
          include: tagsViewStore.cachedViews
        },
        {
          default: () => h(Comp)
        }
      )
    }
  },
  {
    name: "CompConsumer",
    props: ["component"]
  }
)
