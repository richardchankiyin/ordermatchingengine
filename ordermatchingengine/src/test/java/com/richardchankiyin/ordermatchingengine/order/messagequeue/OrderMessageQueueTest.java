package com.richardchankiyin.ordermatchingengine.order.messagequeue;

import static org.junit.Assert.*;

import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class OrderMessageQueueTest {

	@Test(expected=NullPointerException.class)
	public void testNameNull() {
		new OrderMessageQueue(null, new IOrderMessageQueueReceiver() {
			public void onEvent(OrderEvent oe) {}
		}, 10);
	}
	
	@Test(expected=NullPointerException.class)
	public void testReceiverNull() {
		new OrderMessageQueue("a", null, 10);
	}

}
