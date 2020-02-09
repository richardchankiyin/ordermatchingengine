package com.richardchankiyin.ordermatchingengine.matchingmanager;


import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public interface IOrderHandler {
	public void addOrder(OrderEvent oe);
	public void updateOrder(OrderEvent oe);
	public void cancelOrder(OrderEvent oe);
	
}
