package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.List;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;


public interface IPriceOrderQueue extends IOrderHandler{
	public List<OrderEvent> executeOrder(long quantity);
	public long getQueueSize();
	public long getTotalOrderQuantity();
}
