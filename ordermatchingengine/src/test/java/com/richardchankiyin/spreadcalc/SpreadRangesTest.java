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
	
	@Test
	public void testIsValidSecondRangeStartingGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.25, true));
	}
	
	@Test
	public void testIsValidSecondRangeMidValuesGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.255, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.260, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.490, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.495, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(0.2555, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(0.4955, true));
	}
	
	@Test
	public void testIsValidThirdRangeStartingGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.50, true));
	}
	
	@Test
	public void testIsValidThirdRangeMidValuesGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.51, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.52, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(9.98, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(9.99, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(0.511, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(9.995, true));
	}
	
}
