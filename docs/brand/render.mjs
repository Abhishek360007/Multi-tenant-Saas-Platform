// Renders TenantHub brand assets. Run from an empty folder after `npm i @resvg/resvg-js@2.6.2 png-to-ico@2.1.8`:
//   node render.mjs <repoRoot>
import { Resvg } from "@resvg/resvg-js"
import pngToIco from "png-to-ico"
import fs from "node:fs"
import path from "node:path"

const root = process.argv[2]
const w = (rel, data) => {
  const p = path.join(root, rel)
  fs.mkdirSync(path.dirname(p), { recursive: true })
  fs.writeFileSync(p, data)
  console.log("wrote", rel, data.length)
}

// Hub mark: a central node linked to six tenant nodes on a gradient tile. 100x100 viewBox.
const nodes = [...Array(6)].map((_, i) => {
  const a = (Math.PI / 3) * i - Math.PI / 2
  return [50 + 29 * Math.cos(a), 50 + 29 * Math.sin(a)].map((v) => +v.toFixed(2))
})
const mark = (x = 0, y = 0, s = 1) => `
<g transform="translate(${x} ${y}) scale(${s})">
  <rect width="100" height="100" rx="24" fill="url(#thg)"/>
  <g stroke="#fff" stroke-width="4" stroke-linecap="round" opacity="0.85">
    ${nodes.map(([nx, ny]) => `<line x1="50" y1="50" x2="${nx}" y2="${ny}"/>`).join("")}
  </g>
  <polygon points="${nodes.map((n) => n.join(",")).join(" ")}" fill="none" stroke="#fff" stroke-width="2.5" opacity="0.45"/>
  ${nodes.map(([nx, ny]) => `<circle cx="${nx}" cy="${ny}" r="7" fill="#fff"/>`).join("")}
  <circle cx="50" cy="50" r="12" fill="#fff"/>
  <circle cx="50" cy="50" r="6" fill="#4f46e5"/>
</g>`
const defs = `<defs><linearGradient id="thg" x1="0" y1="0" x2="1" y2="1">
  <stop offset="0" stop-color="#4f46e5"/><stop offset="1" stop-color="#06b6d4"/></linearGradient></defs>`

const markSvg = `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100" width="100" height="100">${defs}${mark()}</svg>`

// Wordmark: 421x181 to match the layout slots the UIs were designed for.
const wordmark = (textColor, subtitle, subColor) => `
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 421 181" width="421" height="181">${defs}
  ${mark(14, 40, 1.01)}
  <text x="128" y="${subtitle ? 100 : 108}" font-family="Segoe UI, Arial, sans-serif" font-weight="700" font-size="50" fill="${textColor}">Tenant<tspan fill="#06b6d4">Hub</tspan></text>
  ${subtitle ? `<text x="133" y="140" font-family="Segoe UI, Arial, sans-serif" font-weight="600" font-size="25" letter-spacing="1" fill="${subColor}">${subtitle}</text>` : ""}
</svg>`

const png = (svg, width) =>
  new Resvg(svg, { fitTo: { mode: "width", value: width }, font: { loadSystemFonts: true } }).render().asPng()

// Shared brand sources
w("docs/brand/tenanthub-logo.svg", Buffer.from(markSvg.trim() + "\n"))
const logo256 = png(markSvg, 256)
w("docs/brand/tenanthub-logo.png", logo256)
w("docs/brand/tenanthub-wordmark.png", png(wordmark("#0f172a"), 842))

const ico = await pngToIco([png(markSvg, 16), png(markSvg, 32), png(markSvg, 48)])
w("server/tenanthub-service/src/main/resources/static/favicon.ico", ico)
w("server/tenanthub-service/src/main/resources/static/tenanthub_logo.png", logo256)

for (const [app, subtitle] of [["platform-web-ui", "Platform Admin"], ["tenant-web-ui", "Tenant Portal"]]) {
  w(`${app}/public/favicon.ico`, ico)
  w(`${app}/src/assets/layouts/logo.png`, png(markSvg, 205))
  w(`${app}/src/assets/layouts/logo-text-1.png`, png(wordmark("#ffffff"), 421)) // dark sidebar
  w(`${app}/src/assets/layouts/logo-text-2.png`, png(wordmark("#0f172a", subtitle, "#475569"), 421)) // light header
}
