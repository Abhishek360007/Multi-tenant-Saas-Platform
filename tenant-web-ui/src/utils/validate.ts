/** Is it an array */
export const isArray = (arg: unknown) => {
  return Array.isArray ? Array.isArray(arg) : Object.prototype.toString.call(arg) === "[object Array]"
}

/** Is it a string */
export const isString = (str: unknown) => {
  return typeof str === "string" || str instanceof String
}

/** Is it an external link */
export const isExternal = (path: string) => {
  const reg = /^(https?:|mailto:|tel:)/
  return reg.test(path)
}

/** Is it a URL (with protocol) */
export const isUrl = (url: string) => {
  const reg = /^(((ht|f)tps?):\/\/)?([^!@#$%^&*?.\s-]([^!@#$%^&*?.\s]{0,63}[^!@#$%^&*?.\s])?\.)+[a-z]{2,6}\/?/
  return reg.test(url)
}

/** Is it a URL or IP (with port) */
export const isUrlPort = (url: string) => {
  const reg = /^((ht|f)tps?:\/\/)?[\w-]+(\.[\w-]+)+:\d{1,5}\/?$/
  return reg.test(url)
}

/** Is it a domain (without protocol) */
export const isDomain = (domain: string) => {
  const reg = /^([0-9a-zA-Z-]{1,}\.)+([a-zA-Z]{2,})$/
  return reg.test(domain)
}

/** Is the version number in the format X.Y.Z */
export const isVersion = (version: string) => {
  const reg = /^\d+(?:\.\d+){2}$/
  return reg.test(version)
}

/** Is the time in 24-hour format (HH:mm:ss) */
export const is24H = (time: string) => {
  const reg = /^(?:[01]\d|2[0-3]):[0-5]\d:[0-5]\d$/
  return reg.test(time)
}

/** Is it an Indian mobile number */
export const isPhoneNumber = (str: string) => {
  const reg = /^(?:\+91[\-\s]?|0)?[6-9]\d{9}$/
  return reg.test(str)
}


/** Is it an email address (supports Unicode) */
export const isEmail = (email: string) => {
  const reg = /^[A-Za-z0-9\u4e00-\u9fa5]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/
  return reg.test(email)
}

/** Is it a MAC address */
export const isMAC = (mac: string) => {
  const reg =
    /^(([a-f0-9][0,2,4,6,8,a,c,e]:([a-f0-9]{2}:){4})|([a-f0-9][0,2,4,6,8,a,c,e]-([a-f0-9]{2}-){4}))[a-f0-9]{2}$/i
  return reg.test(mac)
}

/** Is it an IPv4 address */
export const isIPv4 = (ip: string) => {
  const reg =
    /^((\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])\.){3}(\d|[1-9]\d|1\d\d|2[0-4]\d|25[0-5])(?::(?:[0-9]|[1-9][0-9]{1,3}|[1-5][0-9]{4}|6[0-4][0-9]{3}|65[0-4][0-9]{2}|655[0-2][0-9]|6553[0-5]))?$/
  return reg.test(ip)
}


/** Image extensions */
export function isImage(value: string) {
  const reg = /\w(\.gif|\.jpeg|\.png|\.jpg|\.bmp)/
  return reg.test(value)
}

/** Archive formats */
export function isCompress(value: string) {
  const reg = /^[^\\/:\\*\\?"<>\\|]+\.(zip|gz|rar|7z|tar|xz|bz2|tar.gz|tar.xz|tar.bz2|tar.7z)$/
  return reg.test(value)
}

/** User password */
export function isPassword(value: string) {
  const reg = /^(?=.*[a-zA-Z])(?=.*[0-9])[A-Za-z0-9]{5,32}$/
  return reg.test(value)
}

/**  User name */
export function isUsername(value: string) {
  const reg = /^[a-zA-Z0-9_-]{5,32}$/
  return reg.test(value)
}

/** Validate mobile number*/
export function isPhone(value: string) {
  const reg = /^(?:\+91[\-\s]?|0)?[6-9]\d{9}$/
  return reg.test(value)
}

/** Phone number */
export function isTelphone(value: string) {
  const reg = /^(?:\+91[\-\s]?|0)?[6-9]\d{9}$|^0\d{2,4}[\-\s]?\d{6,8}$/
  return reg.test(value)
}

/** Whether the string is Json  */
export function isJSON(value: string) {
  try {
    JSON.parse(value)
    return true
  } catch (error) {
    return false
  }
}

//#region Indian tax IDs (mirrors server IndianTaxIds)
const GST_CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
/** GST state codes that differ from LGD state codes (india_area): AP, Ladakh, DNH and DD */
const LGD_TO_GST: Record<number, number> = { 28: 37, 37: 38, 38: 26 }

/** 6-digit Indian PIN code */
export function isPinCode(value: string) {
  return /^[1-9][0-9]{5}$/.test(value)
}

/** PAN, e.g. ABCPE1234F (4th letter is the holder type) */
export function isPan(value: string) {
  return /^[A-Z]{3}[PCHFATBLJG][A-Z][0-9]{4}[A-Z]$/.test(value)
}

/** GSTIN check character for the first 14 characters */
export function gstinCheckChar(first14: string) {
  let sum = 0
  for (let i = 0; i < 14; i++) {
    const product = GST_CHARSET.indexOf(first14[i]) * (i % 2 === 0 ? 1 : 2)
    sum += Math.floor(product / 36) + (product % 36)
  }
  return GST_CHARSET[(36 - (sum % 36)) % 36]
}

/** GSTIN format and checksum, e.g. 29ABCPE1234F1Z7 */
export function isGstin(value: string) {
  return (
    /^[0-9]{2}[A-Z]{3}[PCHFATBLJG][A-Z][0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$/.test(value) &&
    value[14] === gstinCheckChar(value.slice(0, 14))
  )
}

/** GST state code for an india_area code (state*1e9 + district*1e5 + subdistrict) */
export function gstStateCodeOfArea(areaCode: number) {
  const lgd = Math.floor(areaCode / 1e9)
  return LGD_TO_GST[lgd] ?? lgd
}
//#endregion
