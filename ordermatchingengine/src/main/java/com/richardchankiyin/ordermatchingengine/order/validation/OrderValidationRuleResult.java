package com.richardchankiyin.ordermatchingengine.order.validation;

public class OrderValidationRuleResult {
	private static final OrderValidationRuleResult _accepted = new OrderValidationRuleResult();
	
	private boolean isAccepted;
	private String rejectReason;
	
	private OrderValidationRuleResult() {
		this.isAccepted = true;
	}
	
	public static OrderValidationRuleResult getAcceptedInstance() {
		return _accepted;
	}

	public OrderValidationRuleResult(String rejectReason) {
		this.isAccepted = false;
		this.rejectReason = rejectReason;
	}
	
	public boolean isAccepted() {
		return this.isAccepted;
	}
	
	public String getRejectReason() {
		return this.rejectReason;
	}
}
