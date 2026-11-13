// Node 25+ has its own built-in localStorage global that is unusable unless --localstorage-file is set,
// and it hides jsdom's. If storage is not usable, install a simple in-memory one so tests behave the same
// on every Node version.
function memoryStorage(): Storage {
  const data = new Map<string, string>()
  return {
    get length() {
      return data.size
    },
    clear: () => data.clear(),
    getItem: (key) => (data.has(key) ? data.get(key)! : null),
    key: (i) => Array.from(data.keys())[i] ?? null,
    removeItem: (key) => void data.delete(key),
    setItem: (key, value) => void data.set(key, String(value))
  }
}

const usable = (name: "localStorage" | "sessionStorage") => {
  try {
    return typeof (globalThis as any)[name]?.getItem === "function"
  } catch {
    return false
  }
}

for (const name of ["localStorage", "sessionStorage"] as const) {
  if (!usable(name)) {
    Object.defineProperty(globalThis, name, { value: memoryStorage(), configurable: true, writable: true })
  }
}
