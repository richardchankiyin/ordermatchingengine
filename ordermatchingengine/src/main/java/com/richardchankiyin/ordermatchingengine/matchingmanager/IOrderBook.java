package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.List;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public interface IOrderBook extends IOrderHandler{
	
	public double getBid();
	public double getAsk();
	public String getSymbol();
	public List<OrderEvent> executeOrder(boolean isBid, long quantity);
}
