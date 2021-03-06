package com.richardchankiyin.ordermatchingengine.order.statemachine;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderUpdateable;
import com.richardchankiyin.ordermatchingengine.order.validation.AbstractOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.IOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationRule;

public class OrderStateMachine implements IOrderStateMachine{
	private final static Logger logger = LoggerFactory.getLogger(OrderStateMachine.class);
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
		OrderValidationResult result = processOrderValidator.validate(oe);
		if (result.isAccepted()) {
			orderUpdateable.updateOrder(oe);
		}
		return result;
	}
	
	public IOrderModel getOrderModel() {
		return model;
	}

	private Set<String> fromListToSet(List<String> r) {
		Set<String> result = new HashSet<>();
		result.addAll(r);
		return result;
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
	
	private final OrderValidationRule NOSFROMPENDINGNEWTONEWORREJ
		= new OrderValidationRule("NOSFROMPENDINGNEWTONEWORREJ", oe->{
			Object clOrdId = oe.get(11);
			Object msgType = oe.get(35);
			if (clOrdId != null && "D".equals(msgType)) {
				Object ordStatus = oe.get(39);
				boolean isOrderExist = model.isClientOrderIdFound(clOrdId.toString());
				if (isOrderExist) {
					OrderEvent oldOe = model.getOrder(clOrdId.toString());
					Object oldOrdStatus = oldOe.get(39);
					if (("0".equals(ordStatus) || "8".equals(ordStatus)) && "A".equals(oldOrdStatus)) {
						return OrderValidationResult.getAcceptedInstance();
					} else {
						// reject as only from A->0 or A->8
						return new OrderValidationResult(String.format("Tag 39: from %s to %s not accepted. Only A to 0/8. ", oldOrdStatus, ordStatus));
					}
				} else {
					return OrderValidationResult.getAcceptedInstance();
				}
			} else {
				return OrderValidationResult.getAcceptedInstance();
			}
		});
	
	
	private final OrderValidationRule REPLACEREQUESTSTATUSCHANGE
		= new OrderValidationRule("REPLACEREQUESTSTATUSCHANGE", oe->{
			
			
			Map<String, Set<String>> fromStatusToStatusMap = new HashMap<>();
			// new to Suspended/Rejected
			fromStatusToStatusMap.put("0",fromListToSet(Arrays.asList("8","9")));
			// partial filled to Suspended
			fromStatusToStatusMap.put("1",fromListToSet(Arrays.asList("9")));
			// Suspended to New/Partial Filled/Filled/Rejected
			fromStatusToStatusMap.put("9",fromListToSet(Arrays.asList("0","1","2","8")));
			// Filled to Done For Day
			fromStatusToStatusMap.put("2",fromListToSet(Arrays.asList("3")));
			
			Object clOrdId = oe.get(11);
			Object msgType = oe.get(35);
			
			if (clOrdId != null && "G".equals(msgType)) {
				String ordStatus = oe.get(39).toString();
				boolean isOrderExist = model.isClientOrderIdFound(clOrdId.toString());
				if (isOrderExist) {
					OrderEvent oldOe = model.getOrder(clOrdId.toString());
					String oldOrdStatus = oldOe.get(39).toString();
					
					if (!fromStatusToStatusMap.containsKey(oldOrdStatus)) {
						return new OrderValidationResult(String.format("Tag 39: %s cannot be further replaced. ", oldOrdStatus));
					} else {
						Set<String> acceptedStatus = fromStatusToStatusMap.get(oldOrdStatus);
						logger.debug("oldOrdStatus: {} ordStatus: {} acceptedStatus: {}", oldOrdStatus, ordStatus, acceptedStatus);
						if (!acceptedStatus.contains(ordStatus)) {
							return new OrderValidationResult(String.format("Tag 39: from %s to %s replace request is rejected. ", oldOrdStatus, ordStatus));
						} else {
							return OrderValidationResult.getAcceptedInstance();
						}
					}					
				} else {
					return new OrderValidationResult(String.format("Tag 11: %s Replace request on a non-exist order is rejected. ", clOrdId));
				}
			}
			
			return OrderValidationResult.getAcceptedInstance();
		});
	
	private final OrderValidationRule CANCELREQUESTSTATUSCHANGE
		= new OrderValidationRule("CANCELREQUESTSTATUSCHANGE", oe->{
			Map<String, List<String>> fromStatusToStatusMap = new HashMap<>();
			// new to cancelled
			fromStatusToStatusMap.put("0",Arrays.asList("4"));
			// partial filled to filled
			fromStatusToStatusMap.put("1",Arrays.asList("2"));
			
			Object clOrdId = oe.get(11);
			Object msgType = oe.get(35);
			
			if (clOrdId != null && "F".equals(msgType)) {
				Object ordStatus = oe.get(39);
				boolean isOrderExist = model.isClientOrderIdFound(clOrdId.toString());
				if (isOrderExist) {
					OrderEvent oldOe = model.getOrder(clOrdId.toString());
					Object oldOrdStatus = oldOe.get(39);
					if (!fromStatusToStatusMap.containsKey(oldOrdStatus)) {
						return new OrderValidationResult(String.format("Tag 39: %s cannot be further cancelled. ", oldOrdStatus));
					} else {
						List<String> acceptedStatus = fromStatusToStatusMap.get(oldOrdStatus);
						if (!acceptedStatus.contains(ordStatus)) {
							return new OrderValidationResult(String.format("Tag 39: from %s to %s cancel request is rejected. ", oldOrdStatus, ordStatus));
						} else {
							return OrderValidationResult.getAcceptedInstance();
						}
					}		
					
				} else {
					return new OrderValidationResult(String.format("Tag 11: %s Cancel request on a non-exist order is rejected. ", clOrdId));
				}
			}
			return OrderValidationResult.getAcceptedInstance();
		});
	
	
	protected OrderValidationRule getNosFromNonexistingToPendingNew() {
		return NOSFROMNONEXISTINGTOPENDINGNEW;		
	}
	
	protected OrderValidationRule getNosFromPendingNewToNewOrRej() {
		return NOSFROMPENDINGNEWTONEWORREJ;
	}
	
	protected OrderValidationRule getReplaceRequestStatusChange() {
		return REPLACEREQUESTSTATUSCHANGE;
	}
	
	protected OrderValidationRule getCancelRequestStatusChange() {
		return CANCELREQUESTSTATUSCHANGE;
	}
	
	private class OrderStateMachineProcessOrderValidator extends AbstractOrderValidator {
		
		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			return Arrays.asList(
					NOSFROMNONEXISTINGTOPENDINGNEW
					, NOSFROMPENDINGNEWTONEWORREJ
					, REPLACEREQUESTSTATUSCHANGE
					, CANCELREQUESTSTATUSCHANGE
					);
		}
		
	}
}
