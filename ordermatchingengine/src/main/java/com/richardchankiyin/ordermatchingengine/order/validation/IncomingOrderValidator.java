package com.richardchankiyin.ordermatchingengine.order.validation;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;
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
	private IOrderModel orderModel;
	public IncomingOrderValidator(IOrderModel orderModel) {
		Objects.requireNonNull(orderModel, "orderModel is null");
		this.orderModel = orderModel;
	}
	
	private final OrderValidationRule DATATYPECHECKING 
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

	
	private final OrderValidationRule NEWORDERSINGLECOMPULSORYFIELDCHECKING 
		= new OrderValidationRule("NEWORDERSINGLECOMPULSORYFIELDCHECKING", oe->{
				Object msgTypeValue = oe.get(35);
				if (msgTypeValue != null && "D".equals(msgTypeValue.toString())) {
					Object orderTypeValue = oe.get(40);
					if (orderTypeValue != null) {
						if ("1".equals(orderTypeValue.toString()) || "2".equals(orderTypeValue.toString())) {
							Object clOrdIdValue = oe.get(11);
							Object sideValue = oe.get(54);
							Object symbolValue = oe.get(55);
							Object priceValue = oe.get(44);
							Object orderQtyValue = oe.get(38);
							if ("1".equals(orderTypeValue.toString())) {
								if (clOrdIdValue == null || sideValue == null || symbolValue == null || orderQtyValue == null) {
									return new OrderValidationResult("Tag 11: ClOrdId, Tag 38: OrderQty, Tag 54: Side and Tag 55: Symbol cannot be missing for market order. ");
								}
							} else {
								if (clOrdIdValue == null || sideValue == null || symbolValue == null || priceValue == null || orderQtyValue == null) {
									return new OrderValidationResult("Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. ");
								}
							}
							return OrderValidationResult.getAcceptedInstance();
						}
					} 
					return new OrderValidationResult(
							String.format("Tag 40: %s is not supported. Only Market(1) and Limit(2) are being supported. ", orderTypeValue != null ? orderTypeValue : "NULL"));
					
				} else {
					// non NOS skip validation
					return OrderValidationResult.getAcceptedInstance();
				}
			});

	
	private final OrderValidationRule NEWORDERSINGLECLIENTORDERIDISNEWCHECKING
		= new OrderValidationRule("NEWORDERSINGLECLIENTORDERIDISNEWCHECKING", oe-> {
			Object msgTypeValue = oe.get(35);
			if (msgTypeValue != null && "D".equals(msgTypeValue.toString()))  {
				Object clOrdIdValue = oe.get(11);
				if (clOrdIdValue != null) {
					boolean isIdFound = this.orderModel.isClientOrderIdFound(clOrdIdValue.toString());
					if (isIdFound) {
						return new OrderValidationResult(String.format("Tag 11: %s is being used in other order. ", clOrdIdValue.toString()));
					}
				}
				return OrderValidationResult.getAcceptedInstance();
			} else {
				// non NOS skip validation
				return OrderValidationResult.getAcceptedInstance();
			}
		});
	
	@Override
	protected List<IOrderValidator> getListOfOrderValidators() {
		return Arrays.asList(
			// rule 1 datatype checking	
			DATATYPECHECKING			
			// rule 2a new order single order checking
			, NEWORDERSINGLECOMPULSORYFIELDCHECKING
			// rule 3a new order single client order id checking
			, NEWORDERSINGLECLIENTORDERIDISNEWCHECKING
			
		);
	}
	
	protected OrderValidationRule getDataTypeCheckingRule() {
		return DATATYPECHECKING;
	}
	
	protected OrderValidationRule getNewOrderSingleCompulsoryFieldChecking() {
		return NEWORDERSINGLECOMPULSORYFIELDCHECKING;
	}
	
	protected OrderValidationRule getNewOrderSingleClientOrderIdIsNewChecking() {
		return NEWORDERSINGLECLIENTORDERIDISNEWCHECKING;
	}
	
	

}
