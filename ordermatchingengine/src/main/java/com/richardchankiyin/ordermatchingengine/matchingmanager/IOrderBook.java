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
	/**
	 * isBid = true means to execute order at buy order queue, thus caller should be at selling.
	 * vice versa
	 * isBid = false means to execute order at sell order queue, this caller should be at buying
	 * 
	 * therefore isBid true, => selling and best price is for buy (as low as possible) and is worst
	 * price for selling.
	 * for isBid = false, => buying and best price is for sell (as high as possible) and is worst
	 * price for buying
	 * @param isBid
	 * @param quantity
	 * @param worstPrice
	 * @return
	 */
	public List<OrderEvent> executeOrders(boolean isBid, long quantity, double bestPrice);
}
