package com.richardchankiyin.ordermatchingengine.matchingmanager;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.validation.AbstractOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;

public interface IOrderHandler {
	static final Logger logger = LoggerFactory.getLogger(IOrderHandler.class);
	public void addOrder(OrderEvent oe);
	public void updateOrder(OrderEvent oe);
	public void cancelOrder(OrderEvent oe);
	
	default void handleValidationResult(OrderEvent oe, AbstractOrderValidator validator) {
		OrderValidationResult validationResult = validator.validate(oe);
		if (!validationResult.isAccepted()) {
			String rejectReason = validationResult.getRejectReason();
			logger.debug("OrderEvent: {} validator: {} Rejected Reason: {}", oe, validator, rejectReason);
			throw new IllegalArgumentException(rejectReason);
		}
	}
}
