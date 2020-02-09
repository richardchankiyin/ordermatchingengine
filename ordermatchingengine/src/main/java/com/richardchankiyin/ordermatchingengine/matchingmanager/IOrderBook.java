package com.richardchankiyin.ordermatchingengine.matchingmanager;

public interface IOrderBook extends IOrderHandler{
	
	public double getBid();
	public double getAsk();

}
