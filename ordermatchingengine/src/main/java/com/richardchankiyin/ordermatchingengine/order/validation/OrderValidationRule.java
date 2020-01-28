package com.richardchankiyin.ordermatchingengine.order.validation;

import java.util.function.Function;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class OrderValidationRule implements IOrderValidator{
	private String name;
	private Function<OrderEvent, OrderValidationRuleResult> rule;
	public OrderValidationRule(String name, Function<OrderEvent, OrderValidationRuleResult> rule) {
		this.name = name;
		this.rule = rule;
	}
	
	public OrderValidationRuleResult validate(OrderEvent oe) {
		OrderValidationRuleResult r = this.rule.apply(oe);
		if (r.isAccepted()) {
			return r;
		} else {
			OrderValidationRuleResult nr = 
				new OrderValidationRuleResult(name + "->" +r.getRejectReason());
			return nr;
		}
	}

}
