package com.richardchankiyin.ordermatchingengine.publisher;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public interface IPublisher {
	public void publisher(OrderEvent oe);
}
