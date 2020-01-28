package com.richardchankiyin.ordermatchingengine.order.validation;

import java.util.List;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public abstract class AbstractOrderValidator implements IOrderValidator {

	protected abstract List<IOrderValidator> getListOfOrderValidators();
	
	private static final String REASON_DELIMITER="|";
	
	@Override
	public OrderValidationResult validate(OrderEvent oe) {
		List<IOrderValidator> validators = getListOfOrderValidators();
		boolean isAccept = true;
		StringBuilder reasonBuilder = new StringBuilder();
		for (IOrderValidator validator: validators) {
			OrderValidationResult result = validator.validate(oe);
			isAccept &= result.isAccepted();
			if (!result.isAccepted()) {
				reasonBuilder.append(result.getRejectReason()).append(REASON_DELIMITER);
			}
		}
		
		if (isAccept) {
			return OrderValidationResult.getAcceptedInstance();
		} else {
			return new OrderValidationResult(reasonBuilder.toString());
		}

	}

}
