package com.abhishek.tenanthub.common.util;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Validation for Indian tax identifiers and postal codes.
 * <ul>
 *   <li>PAN: 5 letters, 4 digits, 1 letter (e.g. ABCPE1234F). The 4th letter is the holder type.</li>
 *   <li>GSTIN: 2-digit GST state code + PAN + entity number + 'Z' + mod-36 check character
 *       (e.g. 29ABCPE1234F1Z7).</li>
 *   <li>PIN code: 6 digits, not starting with 0.</li>
 * </ul>
 *
 * @author Abhishek Raj
 */
public final class IndianTaxIds {

    // 4th character: P person, C company, H HUF, F firm, A AOP, T trust, B BOI, L local authority,
    // J artificial juridical person, G government
    private static final Pattern PAN = Pattern.compile("^[A-Z]{3}[PCHFATBLJG][A-Z][0-9]{4}[A-Z]$");
    private static final Pattern GSTIN = Pattern.compile("^[0-9]{2}[A-Z]{3}[PCHFATBLJG][A-Z][0-9]{4}[A-Z][1-9A-Z]Z[0-9A-Z]$");
    private static final Pattern PIN_CODE = Pattern.compile("^[1-9][0-9]{5}$");
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * GST state codes that differ from the LGD state codes used by india_area.
     * All other states/UTs use the same number in both systems.
     */
    private static final Map<Integer, Integer> LGD_TO_GST_OVERRIDES = Map.of(
            28, 37, // Andhra Pradesh (GST 28 is the pre-2014 undivided state)
            37, 38, // Ladakh
            38, 26  // Dadra and Nagar Haveli and Daman and Diu
    );

    private IndianTaxIds() {
    }

    public static boolean isValidPan(String pan) {
        return pan != null && PAN.matcher(pan).matches();
    }

    public static boolean isValidPinCode(String pin) {
        return pin != null && PIN_CODE.matcher(pin).matches();
    }

    /**
     * Format and checksum check.
     */
    public static boolean isValidGstin(String gstin) {
        if (gstin == null || !GSTIN.matcher(gstin).matches()) {
            return false;
        }
        return gstin.charAt(14) == gstinCheckChar(gstin.substring(0, 14));
    }

    /**
     * Computes the GSTIN check character for the first 14 characters.
     */
    public static char gstinCheckChar(String first14) {
        int sum = 0;
        for (int i = 0; i < 14; i++) {
            int value = CHARSET.indexOf(first14.charAt(i));
            int product = value * (i % 2 == 0 ? 1 : 2);
            sum += product / 36 + product % 36;
        }
        return CHARSET.charAt((36 - sum % 36) % 36);
    }

    /**
     * The PAN embedded in a GSTIN (characters 3 to 12).
     */
    public static String panOfGstin(String gstin) {
        return gstin.substring(2, 12);
    }

    /**
     * The GST state code (01-38) for an india_area code (state*1e9 + district*1e5 + subdistrict).
     */
    public static int gstStateCodeOfArea(long areaCode) {
        int lgdState = (int) (areaCode / 1_000_000_000L);
        return LGD_TO_GST_OVERRIDES.getOrDefault(lgdState, lgdState);
    }

    /**
     * Checks GSTIN and PAN together with the tenant's area. Returns an error message, or null if valid.
     * Blank values are allowed (not every business is GST-registered).
     */
    public static String validate(String gstin, String pan, Long areaCode) {
        boolean hasGstin = gstin != null && !gstin.isBlank();
        boolean hasPan = pan != null && !pan.isBlank();
        if (hasPan && !isValidPan(pan)) {
            return "Invalid PAN. Expected format: ABCPE1234F";
        }
        if (!hasGstin) {
            return null;
        }
        if (!isValidGstin(gstin)) {
            return "Invalid GSTIN. Check the format (e.g. 29ABCPE1234F1Z7) and the last character";
        }
        if (hasPan && !panOfGstin(gstin).equals(pan)) {
            return "PAN does not match the PAN inside the GSTIN";
        }
        if (areaCode != null && areaCode > 0) {
            int expected = gstStateCodeOfArea(areaCode);
            int actual = Integer.parseInt(gstin.substring(0, 2));
            if (expected != actual) {
                return String.format("GSTIN state code %02d does not match the selected state (expected %02d)", actual, expected);
            }
        }
        return null;
    }
}
