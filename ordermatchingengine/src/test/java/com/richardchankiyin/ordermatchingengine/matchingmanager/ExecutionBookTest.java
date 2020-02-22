package com.richardchankiyin.ordermatchingengine.matchingmanager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class ExecutionBookTest {
	
	IExecutionBook executionBook = null;

	@Before
	public void setup() {
		executionBook = new ExecutionBook();
	}
	
	@Test(expected=NullPointerException.class)
	public void testProcessExecutionsNullActiveOrder() {
		OrderEvent oe = new OrderEvent();
		executionBook.processExecutions(null, Arrays.asList(oe));
	}
	
	@Test(expected=NullPointerException.class)
	public void testProcessExecutionsNullPassiveOrders() {
		OrderEvent oe = new OrderEvent();
		executionBook.processExecutions(oe, null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testProcessExecutionEmptyPassiveOrders() {
		OrderEvent oe = new OrderEvent();
		executionBook.processExecutions(oe, new ArrayList<OrderEvent>());
	}
	
	@Test
	public void testProcessExecutionBuyActiveOnePassive() {
		IExecutionBook thisbook = new ExecutionBook();
		OrderEvent activeoe = new OrderEvent();
		activeoe.put(11, "1111");
		activeoe.put(54, "1");
		OrderEvent passiveoe1 = new OrderEvent();
		passiveoe1.put(11, "2222");
		passiveoe1.put(32, 400L);
		passiveoe1.put(44, 60.0);
		passiveoe1.put(54, "2");
		passiveoe1.put(55, "0005.HK");
		List<OrderEvent> result = thisbook.processExecutions(activeoe, Arrays.asList(passiveoe1));
		assertEquals(1, result.size());
		assertTrue(result.get(0).containsKey(17));
		assertEquals("8", result.get(0).get(35));
		assertEquals("1111", result.get(0).get(11));
		assertEquals("2222", result.get(0).get(37));
		assertEquals(400L, result.get(0).get(38));
		assertEquals(60.0, result.get(0).get(44));
		assertEquals("1", result.get(0).get(54));
		assertEquals("0005.HK", result.get(0).get(55));
		
		assertNotNull(thisbook.getExecutionByExecId(result.get(0).get(17).toString()));
		assertEquals(1, thisbook.getExecutionsByOrderId("1111").size());
		assertEquals(1, thisbook.getExecutionsByOrderId("2222").size());
	}
	
	@Test
	public void testProcessExecutionSellActiveOnePassive() {
		IExecutionBook thisbook = new ExecutionBook();
		OrderEvent activeoe = new OrderEvent();
		activeoe.put(11, "1111");
		activeoe.put(54, "2");
		OrderEvent passiveoe1 = new OrderEvent();
		passiveoe1.put(11, "2222");
		passiveoe1.put(32, 400L);
		passiveoe1.put(44, 60.0);
		passiveoe1.put(54, "1");
		passiveoe1.put(55, "0005.HK");
		
		List<OrderEvent> result = thisbook.processExecutions(activeoe, Arrays.asList(passiveoe1));
		assertEquals(1, result.size());
		assertTrue(result.get(0).containsKey(17));
		assertEquals("8", result.get(0).get(35));
		assertEquals("1111", result.get(0).get(11));
		assertEquals("2222", result.get(0).get(37));
		assertEquals(400L, result.get(0).get(38));
		assertEquals(60.0, result.get(0).get(44));
		assertEquals("2", result.get(0).get(54));
		assertEquals("0005.HK", result.get(0).get(55));
		
		assertNotNull(thisbook.getExecutionByExecId(result.get(0).get(17).toString()));
		assertEquals(1, thisbook.getExecutionsByOrderId("1111").size());
		assertEquals(1, thisbook.getExecutionsByOrderId("2222").size());
	}
	
	@Test
	public void testProcessExecutionBuyActiveTwoPassive() {
		IExecutionBook thisbook = new ExecutionBook();
		OrderEvent activeoe = new OrderEvent();
		activeoe.put(11, "1111");
		activeoe.put(54, "1");
		OrderEvent passiveoe1 = new OrderEvent();
		passiveoe1.put(11, "2222");
		passiveoe1.put(32, 400L);
		passiveoe1.put(44, 60.0);
		passiveoe1.put(54, "2");
		passiveoe1.put(55, "0005.HK");
		OrderEvent passiveoe2 = new OrderEvent();
		passiveoe2.put(11, "3333");
		passiveoe2.put(32, 800L);
		passiveoe2.put(44, 60.0);
		passiveoe2.put(54, "2");
		passiveoe2.put(55, "0005.HK");
		List<OrderEvent> result = thisbook.processExecutions(activeoe, Arrays.asList(passiveoe1,passiveoe2));
		assertEquals(2, result.size());
		assertTrue(result.get(0).containsKey(17));
		assertTrue(result.get(1).containsKey(17));
		
		assertEquals("8", result.get(0).get(35));
		assertEquals("1111", result.get(0).get(11));
		assertEquals("2222", result.get(0).get(37));
		assertEquals(400L, result.get(0).get(38));
		assertEquals(60.0, result.get(0).get(44));
		assertEquals("1", result.get(0).get(54));
		assertEquals("0005.HK", result.get(0).get(55));
		
		assertEquals("8", result.get(1).get(35));
		assertEquals("1111", result.get(1).get(11));
		assertEquals("3333", result.get(1).get(37));
		assertEquals(800L, result.get(1).get(38));
		assertEquals(60.0, result.get(1).get(44));
		assertEquals("1", result.get(1).get(54));
		assertEquals("0005.HK", result.get(1).get(55));
		
		assertNotNull(thisbook.getExecutionByExecId(result.get(0).get(17).toString()));
		assertNotNull(thisbook.getExecutionByExecId(result.get(1).get(17).toString()));
		assertEquals(2, thisbook.getExecutionsByOrderId("1111").size());
		assertEquals(1, thisbook.getExecutionsByOrderId("2222").size());
		assertEquals(1, thisbook.getExecutionsByOrderId("3333").size());
	}

	@Test
	public void testProcessExecutionSellActiveTwoPassive() {
		IExecutionBook thisbook = new ExecutionBook();
		OrderEvent activeoe = new OrderEvent();
		activeoe.put(11, "1111");
		activeoe.put(54, "2");
		OrderEvent passiveoe1 = new OrderEvent();
		passiveoe1.put(11, "2222");
		passiveoe1.put(32, 400L);
		passiveoe1.put(44, 60.0);
		passiveoe1.put(54, "1");
		passiveoe1.put(55, "0005.HK");
		OrderEvent passiveoe2 = new OrderEvent();
		passiveoe2.put(11, "3333");
		passiveoe2.put(32, 800L);
		passiveoe2.put(44, 60.0);
		passiveoe2.put(54, "1");
		passiveoe2.put(55, "0005.HK");
		
		List<OrderEvent> result = thisbook.processExecutions(activeoe, Arrays.asList(passiveoe1,passiveoe2));
		assertEquals(2, result.size());
		assertTrue(result.get(0).containsKey(17));
		assertTrue(result.get(1).containsKey(17));
		
		assertEquals("8", result.get(0).get(35));
		assertEquals("1111", result.get(0).get(11));
		assertEquals("2222", result.get(0).get(37));
		assertEquals(400L, result.get(0).get(38));
		assertEquals(60.0, result.get(0).get(44));
		assertEquals("2", result.get(0).get(54));
		assertEquals("0005.HK", result.get(0).get(55));
		
		assertEquals("8", result.get(1).get(35));
		assertEquals("1111", result.get(1).get(11));
		assertEquals("3333", result.get(1).get(37));
		assertEquals(800L, result.get(1).get(38));
		assertEquals(60.0, result.get(1).get(44));
		assertEquals("2", result.get(1).get(54));
		assertEquals("0005.HK", result.get(1).get(55));
		
		assertNotNull(thisbook.getExecutionByExecId(result.get(0).get(17).toString()));
		assertNotNull(thisbook.getExecutionByExecId(result.get(1).get(17).toString()));
		assertEquals(2, thisbook.getExecutionsByOrderId("1111").size());
		assertEquals(1, thisbook.getExecutionsByOrderId("2222").size());
		assertEquals(1, thisbook.getExecutionsByOrderId("3333").size());
	}

}
