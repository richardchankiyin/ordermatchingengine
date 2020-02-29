package com.richardchankiyin.ordermatchingengine.order.model;

import java.util.List;
import java.util.function.Function;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public interface IOrderModel {
	boolean isClientOrderIdFound(String clientOrderId);
	
	OrderEvent getOrder(String clientOrderId);
	
	List<OrderEvent> getListOfOrders(Function<OrderEvent, Boolean> criterion);
}
