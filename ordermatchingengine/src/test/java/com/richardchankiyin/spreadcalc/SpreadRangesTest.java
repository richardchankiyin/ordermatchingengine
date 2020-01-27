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
}
