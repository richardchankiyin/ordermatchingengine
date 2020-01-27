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
	public void testIsValidFirstRangeStartingNotGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.25, false));
	}
	
	@Test
	public void testIsValidFirstRangeMidValuesNotGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.249, false));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.248, false));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.011, false));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.012, false));
		assertFalse(SpreadRanges.getInstance().isValidPrice(0.0115, false));
		assertFalse(SpreadRanges.getInstance().isValidPrice(0.2495, false));
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
	public void testIsValidSecondRangeStartingNotGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.50, false));
	}
	
	@Test
	public void testIsValidSecondRangeMidValuesNotGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.490, false));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.495, false));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.255, false));
		assertTrue(SpreadRanges.getInstance().isValidPrice(0.260, false));
		assertFalse(SpreadRanges.getInstance().isValidPrice(0.2555, false));
		assertFalse(SpreadRanges.getInstance().isValidPrice(0.4955, false));
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
	
	@Test
	public void testIsValidFourthRangeStartingGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(10, true));
	}
	
	@Test
	public void testIsValidFourthRangeMidValuesGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(10.02, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(10.04, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(19.98, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(19.96, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(10.03, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(19.97, true));
	}
	
	@Test
	public void testIsValidFifthRangeStartingGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(20, true));
	}
	
	@Test
	public void testIsValidFifthRangeMidValuesGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(20.05, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(20.10, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(99.95, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(99.90, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(20.02, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(99.97, true));
	}
	
	@Test
	public void testIsValidSixthRangeStartingGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(100, true));
	}
	
	@Test
	public void testIsValidSixthRangeMidValuesGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(100.1, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(100.2, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(199.9, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(199.8, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(100.05, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(199.95, true));
	}
	
	@Test
	public void testIsValidSeventhRangeStartingGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(200, true));
	}
	
	@Test
	public void testIsValidSeventhRangeMidValuesGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(200.2, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(200.4, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(499.8, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(499.6, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(200.3, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(499.9, true));
	}
	
	@Test
	public void testIsValidEighthRangeStartingGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(500, true));
	}
	
	@Test
	public void testIsValidEighthRangeMidValuesGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(500.5, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(501, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(999.5, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(999.0, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(500.3, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(999.8, true));
	}
	
	@Test
	public void testIsValidNinethRangeStartingGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(1000, true));
	}
	
	@Test
	public void testIsValidNinethRangeMidValuesGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(1001, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(1002, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(1999, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(1998, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(1000.5, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(1999.5, true));
	}
	
	@Test
	public void testIsValidTenthRangeStartingGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(2000, true));
	}
	
	@Test
	public void testIsValidTenthRangeMidValuesGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(2002, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(2004, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(4998, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(4996, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(2001, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(4999, true));
	}
	
	@Test
	public void testIsValidEleventhRangeStartingGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(5000, true));
	}
	
	@Test
	public void testIsValidEleventhRangeMidValuesGoUp() {
		assertTrue(SpreadRanges.getInstance().isValidPrice(5005, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(5010, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(9995, true));
		assertTrue(SpreadRanges.getInstance().isValidPrice(9990, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(5001, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(9998, true));
	}
	
	@Test
	public void testIsValidOutsideRange() {
		assertFalse(SpreadRanges.getInstance().isValidPrice(10001, true));
		assertFalse(SpreadRanges.getInstance().isValidPrice(0.001, true));
	}
}
