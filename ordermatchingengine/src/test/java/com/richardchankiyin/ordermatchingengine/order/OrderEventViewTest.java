package com.richardchankiyin.ordermatchingengine.order;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class OrderEventViewTest {
	
	private OrderEventView instance = null;
	
	@Before
	public void setUp() {
		instance = new OrderEventView(new OrderEvent());
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testClear() {
		instance.clear();
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testPutIntegerObject() {
		instance.put(1, 1);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testPutAllMapOfQextendsIntegerQextendsObject() {
		instance.putAll(null);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testRemoveObject() {
		instance.remove(null);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testPutIfAbsentIntegerObject() {
		instance.putIfAbsent(1, 1);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testRemoveObjectObject() {
		instance.remove(null, null);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testReplaceIntegerObjectObject() {
		instance.replace(1, 1, 1);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testReplaceIntegerObject() {
		instance.replace(1,1);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testComputeIfAbsentIntegerFunctionOfQsuperIntegerQextendsObject() {
		instance.computeIfAbsent(1, null);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testComputeIfPresentIntegerBiFunctionOfQsuperIntegerQsuperObjectQextendsObject() {
		instance.computeIfPresent(1, null);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testComputeIntegerBiFunctionOfQsuperIntegerQsuperObjectQextendsObject() {
		instance.compute(1, null);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testMergeIntegerObjectBiFunctionOfQsuperObjectQsuperObjectQextendsObject() {
		instance.merge(1, 1, null);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testReplaceAllBiFunctionOfQsuperIntegerQsuperObjectQextendsObject() {
		instance.replaceAll(null);
	}

	@Test(expected=UnsupportedOperationException.class)
	public void testClone() {
		instance.clone();
	}

}
