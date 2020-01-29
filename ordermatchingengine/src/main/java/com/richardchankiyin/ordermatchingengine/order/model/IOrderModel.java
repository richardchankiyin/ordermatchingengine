package com.richardchankiyin.ordermatchingengine.order.model;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public interface IOrderModel {
	boolean isClientOrderIdFound(String clientOrderId);
	
	OrderEvent getOrder(String clientOrderId);
}
