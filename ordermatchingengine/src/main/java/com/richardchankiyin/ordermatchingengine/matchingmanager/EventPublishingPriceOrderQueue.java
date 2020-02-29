package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.List;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.publisher.IPublisher;

public class EventPublishingPriceOrderQueue extends PriceOrderQueue {
	private IPublisher publisher = null;
	public EventPublishingPriceOrderQueue(double orderPrice, boolean isBuy, IPublisher publisher) {
		super(orderPrice, isBuy);
		this.publisher = publisher;
	}
	
	private void publishChange() {
		long queueSize = this.getQueueSize();
		long queueQuantity = this.getTotalOrderQuantity();
		double price = this.getOrderPrice();
		boolean isBid = this.isBuy();
		
		OrderEvent oe = new OrderEvent();
		oe.put(35, "B");
		oe.put(148, "PriceOrderQueue change");
		oe.put(58, "PriceOrderQueue change");
		oe.put(54, isBid ? "1" : "2");
		oe.put(44, price);
		oe.put(38, queueQuantity);
		oe.put(417, queueSize);
		
		this.publisher.publish(oe);
	}

	@Override
	public void addOrder(OrderEvent oe) {
		super.addOrder(oe);
		publishChange();
	}

	@Override
	public void updateOrder(OrderEvent oe) {
		super.updateOrder(oe);
		publishChange();
	}

	@Override
	public void cancelOrder(OrderEvent oe) {
		super.cancelOrder(oe);
		publishChange();
	}

	@Override
	public List<OrderEvent> executeOrder(long quantity) {
		List<OrderEvent> result = super.executeOrder(quantity);
		publishChange();
		return result;
	}
	
	

}
