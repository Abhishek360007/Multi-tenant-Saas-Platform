import { describe, expect, it } from "vitest"
import { formatINR, formatIndianNumber, formatPaise, parseAppDate, requestTimesToUtc, responseTimesToLocal } from "@/utils/utc-time"

// Expected values assume IST: run with TZ=Asia/Kolkata npx vitest run
describe("API <-> UI date conversion", () => {
  it("shows UTC date-times as local DD-MM-YYYY", () => {
    expect(responseTimesToLocal({ createTime: "2026-11-13 08:15:23" })).toEqual({ createTime: "13-11-2026 13:45:23" })
  })

  it("shows dates as DD-MM-YYYY without shifting the day", () => {
    expect(responseTimesToLocal(["2026-01-26"])).toEqual(["26-01-2026"])
  })

  it("converts nested structures and leaves other strings alone", () => {
    const data = { list: [{ loginTime: "2026-11-13 18:40:00", name: "Acme", code: "2026" }] }
    expect(responseTimesToLocal(data)).toEqual({ list: [{ loginTime: "14-11-2026 00:10:00", name: "Acme", code: "2026" }] })
  })

  it("sends local DD-MM-YYYY date-times back as UTC", () => {
    expect(requestTimesToUtc({ expired: "13-11-2026 13:45:23" })).toEqual({ expired: "2026-11-13 08:15:23" })
  })

  it("round-trips", () => {
    const api = { t: "2026-03-31 23:59:59", d: "2026-08-15" }
    expect(requestTimesToUtc(responseTimesToLocal(api))).toEqual(api)
  })

  it("parses UI and API strings", () => {
    expect(parseAppDate("13-11-2026 13:45:23").format("YYYY-MM-DD HH:mm")).toBe("2026-11-13 13:45")
    expect(parseAppDate("2026-11-13").format("DD-MM-YYYY")).toBe("13-11-2026")
  })
})

describe("Indian money and numbers", () => {
  it("formats rupees with lakh/crore grouping", () => {
    expect(formatINR(123456)).toBe("₹1,23,456.00")
    expect(formatINR(12345678.5)).toBe("₹1,23,45,678.50")
  })

  it("formats paise", () => {
    expect(formatPaise(99900)).toBe("₹999.00")
  })

  it("groups plain numbers the Indian way", () => {
    expect(formatIndianNumber(10000000)).toBe("1,00,00,000")
  })
})
