package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.List;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public interface IPriceOrderQueue {
	public void addOrder(OrderEvent oe);
	public void updateOrder(OrderEvent oe);
	public void cancelOrder(OrderEvent oe);
	public List<OrderEvent> executeOrder(long quantity);
	public long getQueueSize();
	public long getTotalOrderQuantity();
}
