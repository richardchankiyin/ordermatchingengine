package com.richardchankiyin.ordermatchingengine.publisher;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.IOrderMessageQueueReceiver;

public class PublisherTest {

	@Test(expected=NullPointerException.class)
	public void testPublisherIOrderMessageQueueReceiverNullReceiver() {
		new Publisher(null);
	}
	
	@Test
	public void testPublisherPublish() throws Exception{
		List<OrderEvent> published = new ArrayList<>();
		IPublisher publisher = new Publisher(new IOrderMessageQueueReceiver() {
			public void onEvent(OrderEvent oe) {
				published.add(oe);
			}
		});
		
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		publisher.publish(oe);
		
		Thread.sleep(500);
		
		assertTrue(1 == published.size());
		assertEquals("1111",published.get(0).get(11));
		assertEquals("D",published.get(0).get(35));
		
		
		
	}

}
