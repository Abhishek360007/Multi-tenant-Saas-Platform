package com.abhishek.tenanthub.common.util;

/**
 * GST on a service price, in paise (1 rupee = 100 paise) to avoid rounding errors.
 * <p>
 * Intra-state supply (seller and buyer in the same state): CGST + SGST, half the rate each.
 * Inter-state supply: IGST at the full rate.
 *
 * @author Abhishek Raj
 */
public final class GstCalculator {

    private GstCalculator() {
    }

    /**
     * @param basePaise    price before tax
     * @param ratePercent  GST rate, e.g. 18
     * @param sellerState  GST state code of the seller (e.g. 29 for Karnataka)
     * @param buyerState   GST state code of the buyer (place of supply)
     */
    public static Quote quote(long basePaise, int ratePercent, int sellerState, int buyerState) {
        if (basePaise < 0 || ratePercent < 0) {
            throw new IllegalArgumentException("Amount and rate must not be negative");
        }
        // Round half up to the nearest paisa
        long tax = (basePaise * ratePercent + 50) / 100;
        boolean intraState = sellerState == buyerState;
        long cgst = intraState ? tax / 2 : 0;
        long sgst = intraState ? tax - cgst : 0;
        long igst = intraState ? 0 : tax;
        return new Quote(basePaise, ratePercent, intraState, cgst, sgst, igst, basePaise + tax);
    }

    /**
     * Amounts in paise.
     *
     * @param intraState true when CGST + SGST apply, false when IGST applies
     */
    public record Quote(long basePaise, int ratePercent, boolean intraState,
                        long cgstPaise, long sgstPaise, long igstPaise, long totalPaise) {
    }
}
