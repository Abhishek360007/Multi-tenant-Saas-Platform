package com.abhishek.tenanthub.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IndianTaxIdsTest {

    @Test
    void validatesPan() {
        assertTrue(IndianTaxIds.isValidPan("ABCPE1234F"));
        assertTrue(IndianTaxIds.isValidPan("AAPFU0939F"));
        assertFalse(IndianTaxIds.isValidPan("ABCDE1234F")); // D is not a holder type
        assertFalse(IndianTaxIds.isValidPan("abcde1234f"));
        assertFalse(IndianTaxIds.isValidPan("ABCDE12345"));
    }

    @Test
    void validatesGstinChecksum() {
        assertTrue(IndianTaxIds.isValidGstin("27AAPFU0939F1ZV"));
        assertFalse(IndianTaxIds.isValidGstin("27AAPFU0939F1ZW")); // wrong check character
        assertFalse(IndianTaxIds.isValidGstin("27AAPFU0939F1Z"));  // too short
    }

    @Test
    void computesCheckCharacter() {
        assertEquals('V', IndianTaxIds.gstinCheckChar("27AAPFU0939F1Z"));
        // Any body gets a check character that round-trips through validation
        var body = "29ABCPE1234F1Z";
        assertTrue(IndianTaxIds.isValidGstin(body + IndianTaxIds.gstinCheckChar(body)));
    }

    @Test
    void validatesPinCode() {
        assertTrue(IndianTaxIds.isValidPinCode("560001"));
        assertFalse(IndianTaxIds.isValidPinCode("060001"));
        assertFalse(IndianTaxIds.isValidPinCode("56001"));
    }

    @Test
    void mapsLgdStatesToGstCodes() {
        assertEquals(29, IndianTaxIds.gstStateCodeOfArea(29052505542L)); // Karnataka
        assertEquals(27, IndianTaxIds.gstStateCodeOfArea(27048307179L)); // Maharashtra
        assertEquals(37, IndianTaxIds.gstStateCodeOfArea(28000000000L)); // Andhra Pradesh
        assertEquals(38, IndianTaxIds.gstStateCodeOfArea(37000000000L)); // Ladakh
        assertEquals(26, IndianTaxIds.gstStateCodeOfArea(38000000000L)); // DNH and DD
        assertEquals(7, IndianTaxIds.gstStateCodeOfArea(7000000000L));   // Delhi
    }

    @Test
    void crossChecksGstinPanAndState() {
        var mumbai = 27048307179L;
        assertNull(IndianTaxIds.validate("27AAPFU0939F1ZV", "AAPFU0939F", mumbai));
        assertNull(IndianTaxIds.validate("", "", mumbai)); // both optional
        assertNull(IndianTaxIds.validate(null, "AAPFU0939F", mumbai));
        assertEquals("PAN does not match the PAN inside the GSTIN",
                IndianTaxIds.validate("27AAPFU0939F1ZV", "ABCPE1234F", mumbai));
        assertTrue(IndianTaxIds.validate("27AAPFU0939F1ZV", null, 29052505542L).startsWith("GSTIN state code 27"));
        assertTrue(IndianTaxIds.validate("27AAPFU0939F1ZW", null, mumbai).startsWith("Invalid GSTIN"));
        assertTrue(IndianTaxIds.validate(null, "BAD", mumbai).startsWith("Invalid PAN"));
    }
}
