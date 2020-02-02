package com.richardchankiyin.ordermatchingengine.order.statemachine;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;

public interface IOrderStateMachine {
	public IOrderModel getOrderModel();
	public OrderValidationResult handleEvent(OrderEvent oe);
}
