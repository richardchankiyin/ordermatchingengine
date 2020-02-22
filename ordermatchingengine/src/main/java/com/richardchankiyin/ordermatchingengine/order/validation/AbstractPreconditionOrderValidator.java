package com.richardchankiyin.ordermatchingengine.order.validation;

import java.util.function.Function;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public abstract class AbstractPreconditionOrderValidator extends AbstractOrderValidator {

	private Function<OrderEvent,Boolean> function = null;
	
	public AbstractPreconditionOrderValidator(Function<OrderEvent,Boolean> function) {
		this.function = function;
	}
	
	@Override
	public OrderValidationResult validate(OrderEvent oe) {
		if (function != null && function.apply(oe)) {
			return super.validate(oe);
		} else {
			return OrderValidationResult.getAcceptedInstance();
		}
	}

}
