package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.validation.AbstractOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.IOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationRule;
import com.richardchankiyin.utils.NumericUtils;

public class PriceOrderQueue {
	private static final Logger logger = LoggerFactory.getLogger(PriceOrderQueue.class);
	private final static int ROUNDSCALE = 6;
	
	private Map<String, OrderEvent> clOrdIdToOrderEvent;
	private Queue<OrderEvent> orderQueue;
	private AddOrderValidator addOrderValidator = new AddOrderValidator();
	private boolean isBuy;
	private long totalOrderQuantity;
	private double orderPrice;
	
	
	
	public PriceOrderQueue(double orderPrice, boolean isBuy) {
		if (orderPrice <= 0) {
			throw new IllegalArgumentException("price must be positive");
		}
		this.clOrdIdToOrderEvent = new HashMap<>();
		this.orderQueue = new LinkedList<>();
		this.orderPrice = NumericUtils.roundDouble(orderPrice,ROUNDSCALE);
		this.isBuy = isBuy;
		this.totalOrderQuantity = 0;		
	}
	
	public double getOrderPrice() {
		return this.orderPrice;
	}
	
	public long getTotalOrderQuantity() {
		return this.totalOrderQuantity;
	}
	
	public boolean isBuy() {
		return this.isBuy;
	}
	
	private class AddOrderValidator extends AbstractOrderValidator {

		private final OrderValidationRule CLORDIDCHECKING
			= new OrderValidationRule("CLORDIDCHECKING", oe->{
				Object clOrdId = oe.get(11);
				if (clOrdId == null) {
					return new OrderValidationResult("Tag 11: ClOrdId cannot be missing. ");
				} else {
					if (clOrdIdToOrderEvent.containsKey(clOrdId.toString())) {
						return new OrderValidationResult("Tag 11: ClOrdId exists. ");
					}
				}
				
				return OrderValidationResult.getAcceptedInstance();
			});
		
		private final OrderValidationRule MSGTYPECHECKING
			= new OrderValidationRule("MSGTYPECHECKING", oe->{
				Object msgType = oe.get(35);
				if (msgType == null) {
					return new OrderValidationResult("Tag 35: MsgType cannot be missing. ");
				} else {
					if (!"D".equals(msgType)) {
						return new OrderValidationResult("Tag 35: MsgType can only be D. ");
					}
				}
				
				return OrderValidationResult.getAcceptedInstance();
			});
		
		private final OrderValidationRule QTYCHECKING
			= new OrderValidationRule("QTYCHECKING", oe->{
				Object qty = oe.get(38);
				if (qty == null) {
					return new OrderValidationResult("Tag 38: Qty cannot be missing. ");
				} else {
					long qtyLong = 0;
					try {
						qtyLong = Long.parseLong(qty.toString());
					}
					catch (Exception e) {
						logger.debug("qty parsing issue", e);
						return new OrderValidationResult("Tag 38: Qty must be integer. ");
					}
					if (qtyLong <= 0) {
						return new OrderValidationResult("Tag 38: Qty must be positive. ");
					}
					
					return OrderValidationResult.getAcceptedInstance();
				}
			});
		
		private final OrderValidationRule PRICECHECKING
			= new OrderValidationRule("PRICECHECKING", oe->{
				Object price = oe.get(44);
				if (price == null) {
					return new OrderValidationResult("Tag 44: Price cannot be missing. ");
				} else {
					if (orderPrice != NumericUtils.roundDouble(Double.parseDouble(price.toString()), ROUNDSCALE)) {
						return new OrderValidationResult(String.format("Tag 44: Price %s is not the same as expected: %s. ", price, orderPrice));
					}
				}
				return OrderValidationResult.getAcceptedInstance();
			});
		
		private final OrderValidationRule SIDECHECKING
			= new OrderValidationRule("SIDECHECKING", oe->{
				Object side = oe.get(54);
				if (side == null) {
					return new OrderValidationResult("Tag 54: Side cannot be missing. ");
				} else {
					boolean isSideValid = isBuy ? "1".equals(side) : "2".equals(side);
					if (!isSideValid) {
						return new OrderValidationResult(String.format("Tag 54: Side %s is not valid. ", side));
					}
				}
				
				return OrderValidationResult.getAcceptedInstance();
			});
		
		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			return Arrays.asList(
					CLORDIDCHECKING
					, MSGTYPECHECKING
					, QTYCHECKING
					, PRICECHECKING
					, SIDECHECKING
					);
		}
		
	}
	
	private void handleValidationResult(OrderEvent oe, AbstractOrderValidator validator) {
		OrderValidationResult validationResult = validator.validate(oe);
		if (!validationResult.isAccepted()) {
			String rejectReason = validationResult.getRejectReason();
			logger.debug("OrderEvent: {} validator: {} Rejected Reason: {}", oe, validator, rejectReason);
			throw new IllegalArgumentException(rejectReason);
		}
	}
	
	/**
	 * This is to add a new order single to the order book
	 * @param oe
	 */
	public void addOrder(OrderEvent oe) {
		handleValidationResult(oe, addOrderValidator);
		Object clOrdId = oe.get(11);
		Object qty = oe.get(38);
		long qtyLong = Long.parseLong(qty.toString());
		this.totalOrderQuantity += qtyLong;
		clOrdIdToOrderEvent.put(clOrdId.toString(), oe);
		orderQueue.add(oe);
	}
	
}
