package com.abhishek.tenanthub.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GstCalculatorTest {

    @Test
    void intraStateSplitsIntoCgstAndSgst() {
        // ₹999 in Karnataka, seller in Karnataka: 18% = ₹179.82 -> CGST ₹89.91 + SGST ₹89.91
        var q = GstCalculator.quote(99_900, 18, 29, 29);
        assertTrue(q.intraState());
        assertEquals(8_991, q.cgstPaise());
        assertEquals(8_991, q.sgstPaise());
        assertEquals(0, q.igstPaise());
        assertEquals(117_882, q.totalPaise());
    }

    @Test
    void interStateUsesIgst() {
        // ₹2,999 to Maharashtra from Karnataka: IGST ₹539.82
        var q = GstCalculator.quote(299_900, 18, 29, 27);
        assertFalse(q.intraState());
        assertEquals(53_982, q.igstPaise());
        assertEquals(0, q.cgstPaise() + q.sgstPaise());
        assertEquals(353_882, q.totalPaise());
    }

    @Test
    void oddTaxPaiseStillAddsUp() {
        // ₹0.05 at 18% = 0.9 paise -> rounds to 1 paisa; CGST 0 + SGST 1
        var q = GstCalculator.quote(5, 18, 29, 29);
        assertEquals(1, q.cgstPaise() + q.sgstPaise());
        assertEquals(6, q.totalPaise());
    }

    @Test
    void freePlanHasNoTax() {
        var q = GstCalculator.quote(0, 18, 29, 7);
        assertEquals(0, q.totalPaise());
    }
}
