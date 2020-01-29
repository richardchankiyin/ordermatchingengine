package com.richardchankiyin.ordermatchingengine.order;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class OrderEventViewTest {
	
	private OrderEventView instance = null;
	
	@Before
	public void setUp() {
		OrderEvent oe = new OrderEvent();
		oe.put(1, 1);
		oe.put(2, "2");
		oe.put(3, 3.3d);
		
		instance = new OrderEventView(oe);
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

	@Test
	public void testGet() {
		assertEquals(1, instance.get(1));
		assertEquals("2", instance.get(2));
		assertEquals(3.3d, instance.get(3));
	}
	
	@Test
	public void testSize() {
		assertEquals(3, instance.size());
	}
	
	@Test
	public void testDeepCopyDone() {
		OrderEvent oe = new OrderEvent();
		oe.put(1, 1);
		oe.put(2, "2");
		oe.put(3, 3.3d);
		OrderEventView view = new OrderEventView(oe);
		assertEquals(1, view.get(1));
		assertEquals("2", view.get(2));
		assertEquals(3.3d, view.get(3));
		
		
		oe.put(1, 2);
		oe.put(2, "222");
		oe.remove(3);
		oe.put(4, 4);
		assertEquals(1, view.get(1));
		assertEquals("2", view.get(2));
		assertEquals(3.3d, view.get(3));
		
	}
	
}
