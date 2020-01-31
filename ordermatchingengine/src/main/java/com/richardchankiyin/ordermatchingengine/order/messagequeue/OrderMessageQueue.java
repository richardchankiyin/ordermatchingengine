package com.richardchankiyin.ordermatchingengine.order.messagequeue;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class OrderMessageQueue implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(OrderMessageQueue.class);
	private ArrayBlockingQueue<OrderEvent> queue = null;
	private IOrderMessageQueueReceiver receiver = null;
	private boolean isStopped = false;
	private String name = null;
	private Thread thread = null;
	public OrderMessageQueue(String name, IOrderMessageQueueReceiver receiver
			, int capacity) {
		
		Objects.requireNonNull(name, "name cannot be blank");
		Objects.requireNonNull(receiver, "receiver cannot be null");
		
		this.name = name;
		this.queue = new ArrayBlockingQueue<OrderEvent>(capacity, true);
		this.receiver = receiver;
		this.isStopped = false;
		this.thread = new Thread(this, name);
	}
	
	public String toString() {
		return String.format(
				"[name: %s | receiver: %s | queue: %s]", 
					name, receiver, queue);
	}

	public void start() {
		if (this.isStopped) {
			throw new IllegalStateException("queue stopped");
		}
		this.thread.start();
	}
	
	public void stop() {
		this.isStopped = true;
	}

	public void send(OrderEvent oe) {
		Objects.requireNonNull(oe, "Order Event null!");
		logger.info("{}", oe);
		boolean eventPutIntotheQueue = this.queue.offer(oe);
		if (!eventPutIntotheQueue) {
			throw new IllegalStateException("queue does not accept!");
		}
	}

	@Override
	public void run() {
		while (!this.isStopped) {
			try {
				OrderEvent oe = this.queue.take();
				logger.debug("taking from queue: {}", oe);
				this.receiver.onEvent(oe);
			}
			catch (InterruptedException ie) {
				logger.error("interrupted", ie);
			}
			catch (Throwable t) {
				logger.error("issue got when onEvent", t);
			}
		}
	}
	
}
