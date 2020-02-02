package com.richardchankiyin.ordermatchingengine.order.messagequeue;

public class OrderMessageQueueForTest extends OrderMessageQueue {

	public OrderMessageQueueForTest(String name,
			IOrderMessageQueueReceiver receiver, int capacity) {
		super(name, receiver, capacity);
		
	}
	
	public void join() {
		try {
			this.getThread().join();
		}
		catch (Exception e) {
			
		}
	}

}
