import {shallowMount} from "@vue/test-utils"
import {describe, expect, it} from "vitest"
import Notify from "@/components/Notify/index.vue"
import NotifyList from "@/components/Notify/NotifyList.vue"

describe("Notify", () => {
  it("Renders", () => {
    const wrapper = shallowMount(Notify)
    expect(wrapper.classes("notify")).toBe(true)
  })
})

describe("NotifyList", () => {
  it("List Length is 0", () => {
    const wrapper = shallowMount(NotifyList, {
      props: {
        list: []
      }
    })
    expect(wrapper.find("el-empty").exists()).toBe(true)
  })
  it("List Length is not 0", () => {
    const wrapper = shallowMount(NotifyList, {
      props: {
        list: [{ id: 1, title: "Plan renewed", content: "Your Basic plan is active", createTime: "13-11-2026 10:00:00", status: 1 }]
      }
    })
    expect(wrapper.find("el-empty").exists()).toBe(false)
  })
})
