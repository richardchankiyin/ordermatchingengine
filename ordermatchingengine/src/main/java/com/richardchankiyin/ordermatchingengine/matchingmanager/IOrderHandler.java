package com.richardchankiyin.ordermatchingengine.matchingmanager;


import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.validation.AbstractOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;

public interface IOrderHandler {
	public void addOrder(OrderEvent oe);
	public void updateOrder(OrderEvent oe);
	public void cancelOrder(OrderEvent oe);
	
	default void handleValidationResult(OrderEvent oe, AbstractOrderValidator validator) {
		OrderValidationResult validationResult = validator.validate(oe);
		if (!validationResult.isAccepted()) {
			String rejectReason = validationResult.getRejectReason();
			throw new IllegalArgumentException(rejectReason);
		}
	}
}
