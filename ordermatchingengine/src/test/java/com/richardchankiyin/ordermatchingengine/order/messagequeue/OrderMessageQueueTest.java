package com.richardchankiyin.ordermatchingengine.order.messagequeue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class OrderMessageQueueTest {
	private static final Logger logger = LoggerFactory.getLogger(OrderMessageQueueTest.class);
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

	@Test(expected=IllegalStateException.class)
	public void testSendBeforeStart() {
		OrderEvent oe = new OrderEvent();
		oe.put(1, 1);
		
		
		List<OrderEvent> received = new ArrayList<OrderEvent>(2);
		IOrderMessageQueueReceiver receiver = new IOrderMessageQueueReceiver() {
			public void onEvent(OrderEvent o) {
				received.add(o);
			}
		};
		
		OrderMessageQueue queue = new OrderMessageQueue("sendbeforestart", receiver, 10);
		queue.send(oe);
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSendAfterStop() {
		OrderEvent oe = new OrderEvent();
		oe.put(1, 1);
		
		
		List<OrderEvent> received = new ArrayList<OrderEvent>(2);
		IOrderMessageQueueReceiver receiver = new IOrderMessageQueueReceiver() {
			public void onEvent(OrderEvent o) {
				received.add(o);
			}
		};
		
		OrderMessageQueue queue = new OrderMessageQueue("sendafterstop", receiver, 10);
		queue.start();
		queue.stop();
		assertTrue(true);
		queue.send(oe);
	}
	
	@Test(expected=IllegalStateException.class)
	public void testStartAfterStop() {
		List<OrderEvent> received = new ArrayList<OrderEvent>(2);
		IOrderMessageQueueReceiver receiver = new IOrderMessageQueueReceiver() {
			public void onEvent(OrderEvent o) {
				received.add(o);
			}
		};
		OrderMessageQueue queue = new OrderMessageQueue("startafterstop", receiver, 10);
		queue.start();
		assertTrue(true);
		queue.stop();
		assertTrue(true);
		queue.start();
		fail("we should not be here");
	}
	
	@Test
	public void testSendAndReceive() throws Exception{
		OrderEvent oe = new OrderEvent();
		oe.put(1, 1);
		OrderEvent oe2 = new OrderEvent();
		oe2.put(2, 2);
		
		List<OrderEvent> received = new ArrayList<OrderEvent>(2);
		IOrderMessageQueueReceiver receiver = new IOrderMessageQueueReceiver() {
			public void onEvent(OrderEvent o) {
				received.add(o);
			}
		};
		
		OrderMessageQueue queue = new OrderMessageQueue("sendandreceivetest", receiver, 10);
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		
		Thread.sleep(100);
		
		assertEquals(2, received.size());
		assertEquals(1,received.get(0).get(1));
		assertEquals(2,received.get(1).get(2));
		
		queue.stop();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testRestartWillFail() {
		IOrderMessageQueueReceiver receiver = new IOrderMessageQueueReceiver() {
			public void onEvent(OrderEvent o) {
			}
		};
		OrderMessageQueue queue = new OrderMessageQueue("restartfail", receiver, 10);
		queue.start();
		queue.stop();
		queue.start();
		
	}
	
	@Test
	public void testOnEventThrowExceptionHandled() throws Exception{
		OrderEvent oe = new OrderEvent();
		oe.put(1, 1);
		OrderEvent oe2 = new OrderEvent();
		oe2.put(2, 2);
		
		List<OrderEvent> received = new ArrayList<OrderEvent>(2);
		IOrderMessageQueueReceiver receiver = new IOrderMessageQueueReceiver() {
			public void onEvent(OrderEvent o) {
				received.add(o);
				throw new RuntimeException("receiver throws exception!");
			}
		};
		
		OrderMessageQueue queue = new OrderMessageQueue("oneventhrowexceptionhandled", receiver, 10);
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		
		Thread.sleep(100);
		
		logger.debug("received: {}", received);
		assertEquals(2, received.size());
		assertEquals(1,received.get(0).get(1));
		assertEquals(2,received.get(1).get(2));
		
		queue.stop();
	}
	
	@Test(expected=IllegalStateException.class)
	public void testMessageQueueFull() throws Exception{
		OrderEvent oe = new OrderEvent();
		oe.put(1, 1);
		OrderEvent oe2 = new OrderEvent();
		oe2.put(2, 2);
		OrderEvent oe3 = new OrderEvent();
		oe3.put(3, 3);
		
		Thread t = Thread.currentThread();
		IOrderMessageQueueReceiver receiver = new IOrderMessageQueueReceiver() {
			public void onEvent(OrderEvent o) {
				try {
					logger.debug("incoming oe: {}", o);
					t.join();
				}
				catch (Exception e) {
					logger.error("error:", e);
				}
			}
		};
		
		OrderMessageQueue queue = new OrderMessageQueue("messagequeuefull", receiver, 1);
		queue.start();
		queue.send(oe);
		logger.debug("queue after 1: {}", queue);
		queue.send(oe2);
		logger.debug("queue after 2: {}", queue);
		queue.send(oe3);
		logger.debug("queue after 3: {}", queue);
		fail("should not come to this line");
	}
}
