package com.richardchankiyin.ordermatchingengine.order.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.OrderEventView;

public class OrderRepositoryTest {

	@Test(expected=NullPointerException.class)
	public void testUpdateNullOrderEvent() {
		OrderRepository repo = new OrderRepository(1);
		repo.updateOrder(null);
	}
	
	@Test(expected=NullPointerException.class)
	public void testUpdateClOrdIdMissing() {
		OrderRepository repo = new OrderRepository(1);
		OrderEvent oe = new OrderEvent();
		oe.put(1, 1);
		repo.updateOrder(oe);
	}
	
	@Test
	public void testUpdateFromNonexisting() {
		OrderRepository repo = new OrderRepository(1);
		IOrderModel orderModel = repo.getOrderModel();
		String clientOrderId = "1111";
		assertFalse(orderModel.isClientOrderIdFound(clientOrderId));
		assertNull(orderModel.getOrder(clientOrderId));
		OrderEvent oe = new OrderEvent();
		oe.put(11, clientOrderId);
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(39, "A");
		oe.put(40, 1);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		repo.updateOrder(oe);
		assertTrue(orderModel.isClientOrderIdFound(clientOrderId));
		OrderEvent updated = orderModel.getOrder(clientOrderId);
		assertNotNull(updated);
		assertTrue(updated instanceof OrderEventView);
		assertEquals(clientOrderId, updated.get(11));
		assertEquals(100, updated.get(38));
		assertEquals("A", updated.get(39));
		assertEquals(1, updated.get(40));
		assertEquals(1, updated.get(54));
		assertEquals("0005.HK", updated.get(55));
		assertNull(updated.get(35));
		
	}

	@Test
	public void testUpdateFromExisting() {
		OrderRepository repo = new OrderRepository(1);
		IOrderModel orderModel = repo.getOrderModel();
		String clientOrderId = "1111";
		OrderEvent oe = new OrderEvent();
		oe.put(11, clientOrderId);
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(39, "A");
		oe.put(40, 1);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		repo.updateOrder(oe);
		
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, clientOrderId);
		oe2.put(39, "0");
		repo.updateOrder(oe2);
		
		OrderEvent updated = orderModel.getOrder(clientOrderId);
		assertNotNull(updated);
		assertTrue(updated instanceof OrderEventView);
		assertEquals(clientOrderId, updated.get(11));
		assertEquals(100, updated.get(38));
		assertEquals("0", updated.get(39));
		assertEquals(1, updated.get(40));
		assertEquals(1, updated.get(54));
		assertEquals("0005.HK", updated.get(55));
		assertNull(updated.get(35));
		
	}
	
	@Test
	public void testDeepCopyDoneWhenSave() {
		OrderRepository repo = new OrderRepository(1);
		IOrderModel orderModel = repo.getOrderModel();
		String clientOrderId = "1111";
		OrderEvent oe = new OrderEvent();
		oe.put(11, clientOrderId);
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(39, "A");
		oe.put(40, 1);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		repo.updateOrder(oe);
		
		oe.put(38,2000);
		
		assertTrue(orderModel.isClientOrderIdFound(clientOrderId));
		OrderEvent updated = orderModel.getOrder(clientOrderId);
		assertNotNull(updated);
		assertTrue(updated instanceof OrderEventView);
		assertEquals(clientOrderId, updated.get(11));
		assertEquals(100, updated.get(38));
		assertEquals("A", updated.get(39));
		assertEquals(1, updated.get(40));
		assertEquals(1, updated.get(54));
		assertEquals("0005.HK", updated.get(55));
		assertNull(updated.get(35));
	}
}
