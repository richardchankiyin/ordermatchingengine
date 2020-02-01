package com.richardchankiyin.ordermatchingengine.order.statemachine;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderUpdateable;
import com.richardchankiyin.ordermatchingengine.order.validation.AbstractOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.IOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationRule;

public class OrderStateMachine {
	private IOrderModel model = null;
	private IOrderUpdateable orderUpdateable = null;
	private OrderStateMachineProcessOrderValidator processOrderValidator = null;
	
	public OrderStateMachine(IOrderModel model, IOrderUpdateable orderUpdateable) {
		Objects.requireNonNull(model, "IOrderModel is null");
		Objects.requireNonNull(orderUpdateable, "IOrderUpateable is null");
		this.model = model;
		this.orderUpdateable = orderUpdateable;
		this.processOrderValidator = new OrderStateMachineProcessOrderValidator();
	}
	
	public OrderValidationResult handleEvent(OrderEvent oe) {
		//TODO to be implemented
		return null;
	}

	private final OrderValidationRule NOSFROMNONEXISTINGTOPENDINGNEW =
			new OrderValidationRule("NOSFROMNONEXISTINGTOPENDINGNEW", oe->{
				Object clOrdId = oe.get(11);
				Object msgType = oe.get(35);
				if (clOrdId != null && "D".equals(msgType)) {
					Object ordStatus = oe.get(39);
					boolean isOrderExist = model.isClientOrderIdFound(clOrdId.toString());
					if (isOrderExist) {
						if (ordStatus != null && "A".equals(ordStatus.toString())) {
							return new OrderValidationResult(
								String.format(
									"Tag 11: ClOrdId : %s exists, cannot create an order with this value. "
										, clOrdId));
						}
						// ignore other nos checking if it exists
						return OrderValidationResult.getAcceptedInstance();
					} else {
						// blame non-A value for tag 39
						if (ordStatus == null || !"A".equals(ordStatus.toString())) {
							return new OrderValidationResult(
								"Tag 39 must be A for a blank new order creation. ");
						}
					}
					return OrderValidationResult.getAcceptedInstance();
				} else {
					return OrderValidationResult.getAcceptedInstance();
				}
	});
	
	protected OrderValidationRule getNosFromNonexistingToPendingNew() {
		return NOSFROMNONEXISTINGTOPENDINGNEW;		
	}
	
	private class OrderStateMachineProcessOrderValidator extends AbstractOrderValidator {
		
		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			return Arrays.asList(
					NOSFROMNONEXISTINGTOPENDINGNEW
					);
		}
		
	}
}
