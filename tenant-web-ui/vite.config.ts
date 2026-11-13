/// <reference types="vitest" />

import {type ConfigEnv, loadEnv, type UserConfigExport} from "vite"
import UnoCSS from "unocss/vite"
import path, {resolve} from "path"
import vue from "@vitejs/plugin-vue"
import svgLoader from "vite-svg-loader"
import vueJsx from "@vitejs/plugin-vue-jsx"
import {createSvgIconsPlugin} from "vite-plugin-svg-icons"

/** Configuration docs:https://cn.vitejs.dev/config */
export default (configEnv: ConfigEnv): UserConfigExport => {
  const viteEnv = loadEnv(configEnv.mode, process.cwd()) as ImportMetaEnv
  const { VITE_PUBLIC_PATH, VITE_BASE_API } = viteEnv
  return {
    /** Adjust for your deployment base */
    base: VITE_PUBLIC_PATH,
    resolve: {
      alias: {
        /** @ symbol points to the src directory */
        "@": resolve(__dirname, "./src")
      }
    },
    server: {
      /** host: true allows access over the network by IP */
      host: true, // host: "0.0.0.0"
      /** Port */
      port: 8555,
      /** Open the browser automatically */
      open: false,
      /** Allow cross-origin */
      cors: true,
      /** Exit if the port is in use */
      strictPort: false,
      /** API proxy */
      proxy: {
        "/api/v1": {
          target: VITE_BASE_API,
          ws: true,
          /** Allow cross-origin */
          changeOrigin: true,
          rewrite: (path: string) => path.replace("/api/v1", "")
        }
      },
      /** Warm up common files to speed up the first page load */
      warmup: {
        clientFiles: ["./src/{views,components}/*.vue"]
      }
    },
    build: {
      /** Warn when a single chunk exceeds 2048KB */
      chunkSizeWarningLimit: 2048,
      /** Disable gzip size reporting */
      reportCompressedSize: false,
      /** Static asset directory after build */
      assetsDir: "static",
      rollupOptions: {
        output: {
          /**
           * Chunking strategy
           * 1. These package names must exist or the build fails
           * 2. Remove this block if you do not want custom chunk splitting
           */
          manualChunks: {
            vue: ["vue", "vue-router", "pinia"],
            element: ["element-plus", "@element-plus/icons-vue"],
            vxe: ["vxe-table", "vxe-table-plugin-element", "xe-utils"]
          }
        }
      }
    },
    /** Minifier */
    esbuild: {
      /** Removed in production builds console.log */
      pure: ["console.log"],
      /** Removed in production builds debugger */
      drop: ["debugger"],
      /** Remove all comments in production builds */
      legalComments: "none"
    },
    /** Vite Plugins */
    plugins: [
      vue(),
      vueJsx(),
      /** Turn static SVGs into Vue components */
      svgLoader({ defaultImport: "url" }),
      /** SVG */
      createSvgIconsPlugin({
        iconDirs: [path.resolve(process.cwd(), "src/icons/svg")],
        symbolId: "icon-[dir]-[name]"
      }),
      /** UnoCSS */
      UnoCSS()
    ],
    /** Vitest Unit test settings:https://cn.vitest.dev/config */
    test: {
      include: ["tests/**/*.test.ts"],
      environment: "jsdom",
      setupFiles: ["tests/setup.ts"]
    }
  }
}
