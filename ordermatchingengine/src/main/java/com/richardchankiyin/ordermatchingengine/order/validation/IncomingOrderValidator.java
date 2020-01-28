package com.richardchankiyin.ordermatchingengine.order.validation;

import java.util.Arrays;
import java.util.List;
/**
 * This class is to validate all incoming orders
 * before being processed by the order matching
 * engine
 * 
 * @author richard
 *
 */
public class IncomingOrderValidator extends AbstractOrderValidator implements
		IOrderValidator {
	private static final IncomingOrderValidator instance = new IncomingOrderValidator();
	
	private static final OrderValidationRule DATATYPECHECKING 
		= new OrderValidationRule("DATATYPECHECKING", oe->{
				Object orderQtyValue = oe.get(38);
				Object priceValue = oe.get(44);
				
				boolean isOrderQtyValueValid = true;
				boolean isPriceValueValid = true;
				if (orderQtyValue != null) {
					try {
						Integer.parseInt(orderQtyValue.toString());
					}
					catch (Exception e) {
						isOrderQtyValueValid = false;
					}
				}				
				if (priceValue != null) {
					try {
						Double.parseDouble(priceValue.toString());
					}
					catch (Exception e) {
						isPriceValueValid = false;
					}
				}
				
				if (isOrderQtyValueValid && isPriceValueValid) {
					return OrderValidationResult.getAcceptedInstance();
				} else {
					StringBuilder rejectReason = new StringBuilder();
					if (!isOrderQtyValueValid) {
						rejectReason.append(String.format("Tag 38: %s is not integer. ", orderQtyValue));
					}
					if (!isPriceValueValid) {
						rejectReason.append(String.format("Tag 44: %s is not numeric. ", priceValue));
					}					
					return new OrderValidationResult(rejectReason.toString());
				}
			});
	
	public static IncomingOrderValidator getInstance() {
		return instance;
	}
	
	@Override
	protected List<IOrderValidator> getListOfOrderValidators() {
		return Arrays.asList(
			// rule 1 datatype checking	
			DATATYPECHECKING
		);
	}
	
	protected OrderValidationRule getDataTypeCheckingRule() {
		return DATATYPECHECKING;
	}
	
	

}
