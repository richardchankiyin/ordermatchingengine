package com.richardchankiyin.ordermatchingengine.order.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

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

}
