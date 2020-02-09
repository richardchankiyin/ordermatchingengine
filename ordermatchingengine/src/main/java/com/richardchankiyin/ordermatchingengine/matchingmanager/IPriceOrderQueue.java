package com.richardchankiyin.ordermatchingengine.matchingmanager;


public interface IPriceOrderQueue extends IOrderHandler{

	public long getQueueSize();
	public long getTotalOrderQuantity();
}
