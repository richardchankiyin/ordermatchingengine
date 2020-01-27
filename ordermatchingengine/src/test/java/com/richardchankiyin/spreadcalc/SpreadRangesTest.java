package com.richardchankiyin.spreadcalc;

import static org.junit.Assert.*;

import org.junit.Test;

public class SpreadRangesTest {

	@Test
	public void testIsValidFirstRangeStartingGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.01, true));
	}
	
	@Test
	public void testIsValidFirstRangeMidValuesGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.011, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.012, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.021, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.249, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(0.0115, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(0.2495, true));
	}
	
}
