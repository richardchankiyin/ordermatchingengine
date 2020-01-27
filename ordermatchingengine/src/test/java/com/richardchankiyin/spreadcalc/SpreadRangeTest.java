package com.richardchankiyin.spreadcalc;

import static org.junit.Assert.*;

import org.junit.Test;

public class SpreadRangeTest {

	@Test(expected=IllegalArgumentException.class)
	public void testSpreadRangeStartFromNegativeThrowException() {
		new SpreadRange(-0.5,1,1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSpreadRangeEndWithNegativeThrowException() {
		new SpreadRange(1,-0.5,1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSpreadRangeSpreadNegativeThrowException() {
		new SpreadRange(1,1,-0.5);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSpreadRangeStartFromZeroThrowException() {
		new SpreadRange(0,1,1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSpreadRangeEndWithZeroThrowException() {
		new SpreadRange(1,0,1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSpreadRangeSpreadZeroThrowException() {
		new SpreadRange(1,1,0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSpreadRangeStartWithGreaterThanEndWith() {
		new SpreadRange(11,10,0.5);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSpreadRangeStartWithEqualsToEndWith() {
		new SpreadRange(10,10,0.5);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSpreadRangeCannotSupportOneSpreadDiff() {
		new SpreadRange(10,20,13);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testSpreadRangeResidualFound() {
		new SpreadRange(10,20,7);
	}
	
	@Test
	public void testSpreadRangeValid() {
		SpreadRange sr = new SpreadRange(10,20,5);
		assertTrue(10d == sr.getStartFrom());
		assertTrue(20d == sr.getEndWith());
		assertTrue(5d == sr.getSpread());
	}
	
	@Test
	public void testIsInRangeSpotNotInRange() {
		SpreadRange sr = new SpreadRange(10,20,5);
		assertFalse(sr.isInRange(30, true));
		assertFalse(sr.isInRange(30, false));
		assertFalse(sr.isInRange(5, true));
		assertFalse(sr.isInRange(5, false));
	}

	@Test
	public void testIsInRangeSpotSameAsEndWith() {
		SpreadRange sr = new SpreadRange(10,20,5);
		assertFalse(sr.isInRange(20, true));
		assertTrue(sr.isInRange(20, false));
	}
	
	@Test
	public void testIsInRangeSpotSameAsStartFrom() {
		SpreadRange sr = new SpreadRange(10,20,5);
		assertFalse(sr.isInRange(10, false));
		assertTrue(sr.isInRange(10, true));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDisplaceGoUpWithExceptionAsNotInRange() {
		SpreadRange sr = new SpreadRange(10,100,2);
		assertFalse(sr.isInRange(15, true));
		sr.displace(15, true, 10);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDisplaceNotGoUpWithExceptionAsNotInRange() {
		SpreadRange sr = new SpreadRange(10,100,2);
		assertFalse(sr.isInRange(75, false));
		sr.displace(75, false, 10);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDisplaceResultLargerThanEndWith() {
		SpreadRange sr = new SpreadRange(10,100,2);
		sr.displace(80, true, 21);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testDisplaceResultSmallerThanStartFrom() {
		SpreadRange sr = new SpreadRange(10,100,2);
		sr.displace(20, false, 6);
	}
	
	@Test
	public void testDisplaceResultGoUp() {
		SpreadRange sr = new SpreadRange(10,100,2);
		assertTrue(12 == sr.displace(10,true,1));
		assertTrue(14 == sr.displace(10,true,2));
		assertTrue(100 == sr.displace(10,true,45));
	}
	
	@Test
	public void testDisplaceResultGoUp2() {
		SpreadRange sr = new SpreadRange(100.0,200.0,0.1);
		assertTrue(100.1 == sr.displace(100,true,1));
		assertTrue(100.2 == sr.displace(100,true,2));
		assertTrue(100.3 == sr.displace(100.2,true,1));
	}
	
	@Test
	public void testDisplaceResultNotGoUp() {
		SpreadRange sr = new SpreadRange(10,100,2);
		assertTrue(98 == sr.displace(100,false,1));
		assertTrue(96 == sr.displace(100,false,2));
		assertTrue(10 == sr.displace(100,false,45));
	}
	
	@Test
	public void testDisplaceResultNotGoUp2() {
		SpreadRange sr = new SpreadRange(100.0,200.0,0.1);
		assertTrue(199.9 == sr.displace(200,false,1));
		assertTrue(199.8 == sr.displace(200,false,2));
		assertEquals("199.7", String.format("%.1f", sr.displace(199.8,false,1)));
		assertEquals("179.9", String.format("%.1f", sr.displace(180.4,false,5)));
	}

	
}
