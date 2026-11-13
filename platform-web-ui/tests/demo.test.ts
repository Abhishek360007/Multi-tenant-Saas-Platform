import {describe, expect, it} from "vitest"

/**
 * @description Every example in this file demonstrates basic Vitest usage
 * @link https://cn.vitest.dev/api
 * @api describe: creates a scope
 * @api test/it: defines a set of expectations; takes a test name and a function with expectations
 * @api expect: creates assertions
 * @api toBe: asserts primitive equality or that objects share the same reference
 * @api toEqual: asserts the actual value equals the received value or has the same structure (objects are compared recursively)
 */

const author1 = {
  name: "pany",
  email: "user@example.com",
  url: "https://github.com/pany-ang"
}

const author2 = {
  name: "pany",
  email: "user@example.com",
  url: "https://github.com/pany-ang"
}

describe("Scope name goes here", () => {
  it("Primitive types", () => {
    expect(1 + 1).toBe(2)
  })
  it("Reference types", () => {
    expect(author1).toEqual(author2)
  })
})
