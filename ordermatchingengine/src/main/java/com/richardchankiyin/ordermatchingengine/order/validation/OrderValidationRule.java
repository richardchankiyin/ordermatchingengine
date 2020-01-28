package com.richardchankiyin.ordermatchingengine.order.validation;

import java.util.function.Function;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class OrderValidationRule implements IOrderValidator{
	private String name;
	private Function<OrderEvent, OrderValidationResult> rule;
	public OrderValidationRule(String name, Function<OrderEvent, OrderValidationResult> rule) {
		this.name = name;
		this.rule = rule;
	}
	
	public OrderValidationResult validate(OrderEvent oe) {
		OrderValidationResult r = this.rule.apply(oe);
		if (r.isAccepted()) {
			return r;
		} else {
			OrderValidationResult nr = 
				new OrderValidationResult(name + "->" +r.getRejectReason());
			return nr;
		}
	}

}
