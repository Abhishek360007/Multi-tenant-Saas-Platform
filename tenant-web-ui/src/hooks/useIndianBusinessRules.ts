import { type FormItemRule } from "element-plus"
import { gstStateCodeOfArea, isGstin, isPan, isPinCode } from "@/utils/validate"

interface BusinessFields {
  areaCode?: number
  gstin?: string
  pan?: string
}

type Validator = (rule: any, value: any, callback: (error?: Error) => void) => void

/** Validation rules for PIN code, GSTIN and PAN. getForm returns the current form model. */
export function useIndianBusinessRules(getForm: () => BusinessFields) {
  const pinValidator: Validator = (_rule, value, callback) =>
    isPinCode(value) ? callback() : callback(new Error("PIN code must be 6 digits"))

  const gstinValidator: Validator = (_rule, value, callback) => {
    if (!value) return callback() // optional
    if (!isGstin(value)) return callback(new Error("Invalid GSTIN (e.g. 29ABCPE1234F1Z7)"))
    const form = getForm()
    if (form.areaCode) {
      const expected = gstStateCodeOfArea(form.areaCode)
      if (Number(value.slice(0, 2)) !== expected) {
        return callback(new Error(`GSTIN must start with ${String(expected).padStart(2, "0")} for the selected state`))
      }
    }
    if (form.pan && form.pan !== value.slice(2, 12)) return callback(new Error("PAN does not match the GSTIN"))
    callback()
  }

  const panValidator: Validator = (_rule, value, callback) => {
    if (!value) return callback() // optional
    if (!isPan(value)) return callback(new Error("Invalid PAN (e.g. ABCPE1234F)"))
    const gstin = getForm().gstin
    if (gstin && isGstin(gstin) && gstin.slice(2, 12) !== value) return callback(new Error("PAN does not match the GSTIN"))
    callback()
  }

  const rules: Record<"pinCode" | "gstin" | "pan", FormItemRule[]> = {
    pinCode: [
      { required: true, message: "Enter a PIN code", trigger: "blur" },
      { validator: pinValidator, trigger: "blur" }
    ],
    gstin: [{ validator: gstinValidator, trigger: "blur" }],
    pan: [{ validator: panValidator, trigger: "blur" }]
  }
  return rules
}

/** Uppercases tax IDs as the user types */
export const toUpperTaxId = (value: string) => value.toUpperCase().replace(/\s/g, "")
