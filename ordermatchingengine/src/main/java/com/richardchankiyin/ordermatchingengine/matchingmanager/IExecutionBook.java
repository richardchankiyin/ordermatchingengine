package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.List;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public interface IExecutionBook {
	public List<OrderEvent> processExecutions(OrderEvent activeOrder, List<OrderEvent> passiveOrders);
}
