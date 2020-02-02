package com.richardchankiyin.ordermatchingengine.order.validation;

public class OrderValidationResult {
	private static final OrderValidationResult _accepted = new OrderValidationResult();
	
	private boolean isAccepted;
	private String rejectReason;
	
	private OrderValidationResult() {
		this.isAccepted = true;
	}
	
	public static OrderValidationResult getAcceptedInstance() {
		return _accepted;
	}

	public OrderValidationResult(String rejectReason) {
		this.isAccepted = false;
		this.rejectReason = rejectReason;
	}
	
	public boolean isAccepted() {
		return this.isAccepted;
	}
	
	public String getRejectReason() {
		return this.rejectReason;
	}
	
	public String toString() {
		return this.isAccepted ? "Accepted" : "Not accepted with reason: " + this.rejectReason;
	}
}
