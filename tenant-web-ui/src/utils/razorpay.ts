import type { ICheckout, IRazorpaySuccess } from "@/api/billing"

/** Razorpay Checkout (https://razorpay.com/docs/payments/payment-gateway/web-integration/standard/) */
const CHECKOUT_SRC = "https://checkout.razorpay.com/v1/checkout.js"

declare global {
  interface Window {
    Razorpay?: new (options: Record<string, unknown>) => { open: () => void; on: (e: string, cb: (r: any) => void) => void }
  }
}

let loading: Promise<void> | undefined

function loadCheckout(): Promise<void> {
  if (window.Razorpay) return Promise.resolve()
  loading ??= new Promise<void>((resolve, reject) => {
    const script = document.createElement("script")
    script.src = CHECKOUT_SRC
    script.async = true
    script.onload = () => resolve()
    script.onerror = () => {
      loading = undefined
      reject(new Error("Could not load Razorpay Checkout. Check your internet connection."))
    }
    document.body.appendChild(script)
  })
  return loading
}

/**
 * Opens Razorpay Checkout. Resolves with the payment details to verify on the server;
 * rejects if the payment fails or the user closes the window.
 */
export async function payWithRazorpay(order: ICheckout): Promise<IRazorpaySuccess> {
  await loadCheckout()
  return new Promise((resolve, reject) => {
    const rzp = new window.Razorpay!({
      key: order.keyId,
      order_id: order.orderId,
      amount: order.amount,
      currency: order.currency,
      name: order.name,
      description: order.description,
      prefill: { name: order.prefillName, email: order.prefillEmail, contact: order.prefillContact },
      theme: { color: "#4f46e5" },
      handler: (response: IRazorpaySuccess) => resolve(response),
      modal: { ondismiss: () => reject(new Error("Payment cancelled")) }
    })
    rzp.on("payment.failed", (r: any) => reject(new Error(r?.error?.description || "Payment failed")))
    rzp.open()
  })
}
