package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.List;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public interface IOrderBook extends IOrderHandler{
	
	public double getBid();
	public double getLowestBid();
	public long getBidQueueSize();
	public long getTotalBidQuantity();
	public double getAsk();
	public double getHighestAsk();
	public long getAskQueueSize();
	public long getTotalAskQuantity();
	public String getSymbol();
	public List<OrderEvent> executeOrder(boolean isBid, long quantity);
}
