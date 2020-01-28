package com.richardchankiyin.ordermatchingengine.order.validation;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public interface IOrderValidator {
	public OrderValidationRuleResult validate(OrderEvent oe);
}
