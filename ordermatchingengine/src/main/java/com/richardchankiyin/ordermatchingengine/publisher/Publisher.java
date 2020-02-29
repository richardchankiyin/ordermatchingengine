package com.richardchankiyin.ordermatchingengine.publisher;

import java.util.Objects;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.IOrderMessageQueueReceiver;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.OrderMessageQueue;

public class Publisher implements IPublisher {
	
	private OrderMessageQueue queue = null;
	
	public Publisher(IOrderMessageQueueReceiver receiver, int capacity) {
		Objects.requireNonNull(receiver, "receiver cannot be null!");
		queue = new OrderMessageQueue("Publisher" + System.currentTimeMillis(), receiver, capacity);
		queue.start();
	}
	
	public Publisher(IOrderMessageQueueReceiver receiver) {
		this(receiver, 30);
	}
	

	@Override
	public void publish(OrderEvent oe) {
		queue.send(oe);
	}

	protected void finalize() {
		try {
			queue.stop();
			super.finalize();
		}
		catch (Throwable t) {
		}
		finally {
		}
	}
}
