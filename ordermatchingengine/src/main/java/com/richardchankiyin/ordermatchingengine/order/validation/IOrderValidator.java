package com.richardchankiyin.ordermatchingengine.order.validation;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public interface IOrderValidator {
	public OrderValidationResult validate(OrderEvent oe);
}
