<template>
  <div class="app-container">
    <el-row :gutter="10" class="chart">
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <v-chart autoresize :option="option1" />
      </el-col>
      <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
        <v-chart autoresize :option="option2" />
      </el-col>
    </el-row>
    <v-chart autoresize class="chart2" :option="option3" />
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue"
import { use } from "echarts/core"
import VChart from "vue-echarts"
import { CanvasRenderer } from "echarts/renderers"
import { PieChart, LineChart, BarChart } from "echarts/charts"
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent, ToolboxComponent } from "echarts/components"

use([
  CanvasRenderer,
  PieChart,
  LineChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent,
  ToolboxComponent
])

const toolbox1 = {
  show: true,
  feature: {
    mark: {
      show: true
    },
    dataView: {
      show: true,
      readOnly: false,
      title: "Data view"
    },
    restore: {
      show: true,
      title: "Restore"
    },
    saveAsImage: {
      show: true,
      title: "Save"
    }
  }
}

const toolbox2 = {
  show: true,
  feature: {
    mark: {
      show: true
    },
    dataView: {
      show: true,
      readOnly: false,
      title: "Data view"
    },
    magicType: {
      show: true,
      type: ["line", "bar"],
      title: {
        line: "Switch to line chart",
        bar: "Switch to bar chart"
      }
    },
    restore: {
      show: true,
      title: "Restore"
    },
    saveAsImage: {
      show: true,
      title: "Save"
    }
  }
}

const grid1 = {
  left: "3%",
  right: "3%",
  bottom: "3%",
  containLabel: true
}

const option1 = ref({
  title: {
    text: "User sources",
    left: "center"
  },
  tooltip: {
    trigger: "item",
    formatter: "{b} : {c} ({d}%)"
  },
  grid: grid1,
  legend: {
    orient: "vertical",
    left: "left",
    data: ["WhatsApp", "Instagram", "YouTube", "X", "LinkedIn", "Paytm", "Flipkart", "Meesho"]
  },
  toolbox: toolbox1,
  series: [
    {
      name: "Traffic Sources",
      type: "pie",
      radius: "55%",
      center: ["50%", "60%"],
      data: [
        { value: 1350, name: "WhatsApp" },
        { value: 2101, name: "Instagram" },
        { value: 334, name: "YouTube" },
        { value: 165, name: "X" },
        { value: 190, name: "LinkedIn" },
        { value: 1248, name: "Paytm" },
        { value: 548, name: "Flipkart" },
        { value: 348, name: "Meesho" }
      ],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)"
        }
      }
    }
  ]
})

const option2 = ref({
  title: {
    text: "Traffic",
    left: "center"
  },
  tooltip: {
    trigger: "axis",
    axisPointer: {
      type: "shadow"
    }
  },
  grid: grid1,
  toolbox: toolbox2,
  xAxis: [
    {
      type: "category",
      data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"],
      axisTick: {
        alignWithLabel: true
      }
    }
  ],
  yAxis: [
    {
      type: "value"
    }
  ],
  series: [
    {
      name: "API Traffic",
      type: "bar",
      barWidth: "60%",
      data: [19849, 45645, 22656, 111334, 64948, 53642, 36665]
    }
  ]
})

const option3 = ref({
  title: {
    text: "E-commerce sales"
  },
  tooltip: {
    trigger: "axis",
    formatter: (params: any) => {
      let relVal = params[0].name
      params.forEach((element: any) => {
        relVal += `<br/> ${element.marker} ${element.seriesName} : ${element.value} 10k`
      })
      return relVal
    }
  },
  legend: {
    data: ["Flipkart", "Amazon", "Meesho", "Myntra", "Nykaa"]
  },
  grid: grid1,
  toolbox: toolbox2,
  xAxis: {
    type: "category",
    boundaryGap: false,
    data: ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"]
  },
  yAxis: {
    type: "value"
  },
  series: [
    {
      name: "Flipkart",
      type: "line",
      stack: "Total",
      data: [120, 132, 101, 134, 90, 230, 210]
    },
    {
      name: "Amazon",
      type: "line",
      stack: "Total",
      data: [220, 182, 191, 234, 290, 330, 310]
    },
    {
      name: "Meesho",
      type: "line",
      stack: "Total",
      data: [150, 232, 201, 154, 190, 330, 410]
    },
    {
      name: "Myntra",
      type: "line",
      stack: "Total",
      data: [320, 332, 301, 334, 390, 330, 320]
    },
    {
      name: "Nykaa",
      type: "line",
      stack: "Total",
      data: [820, 932, 901, 934, 1290, 1330, 1320]
    }
  ]
})
</script>

<style lang="scss" scoped>
.chart {
  height: 350px;
}

.chart2 {
  height: 500px;
  margin-top: 50px;
}
</style>
