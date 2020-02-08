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

public class PriceOrderQueue implements IPriceOrderQueue{
	private static final Logger logger = LoggerFactory.getLogger(PriceOrderQueue.class);
	private final static int ROUNDSCALE = 6;
	
	private Map<String, OrderEvent> clOrdIdToOrderEvent;
	private Queue<OrderEvent> orderQueue;
	private AddOrderValidator addOrderValidator = new AddOrderValidator();
	private UpdateOrderValidator updateOrderValidator = new UpdateOrderValidator();
	private boolean isBuy;
	private long totalOrderQuantity;
	private double orderPrice;
	private long queueSize = 0;
	
	
	
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
	
	public long getQueueSize() {
		return this.queueSize;
	}
	
	protected Map<String, OrderEvent> getOrderEventInternalMap() {
		return this.clOrdIdToOrderEvent;
	}
	
	private void handleValidationResult(OrderEvent oe, AbstractOrderValidator validator) {
		OrderValidationResult validationResult = validator.validate(oe);
		if (!validationResult.isAccepted()) {
			String rejectReason = validationResult.getRejectReason();
			logger.debug("OrderEvent: {} validator: {} Rejected Reason: {}", oe, validator, rejectReason);
			throw new IllegalArgumentException(rejectReason);
		}
	}
	
	/******* Add Order *********/
	
	private class AddOrderValidator extends AbstractOrderValidator {

		private final OrderValidationRule ADDORDERCLORDIDCHECKING
			= new OrderValidationRule("ADDORDERCLORDIDCHECKING", oe->{
				Object clOrdId = oe.get(11);
				if (clOrdId == null) {
					return new OrderValidationResult("Tag 11: ClOrdId cannot be missing. ");
				} else {
					if (getOrderEventInternalMap().containsKey(clOrdId.toString())) {
						return new OrderValidationResult("Tag 11: ClOrdId exists. ");
					}
				}
				
				return OrderValidationResult.getAcceptedInstance();
			});
		
		private final OrderValidationRule ADDORDERMSGTYPECHECKING
			= new OrderValidationRule("ADDORDERMSGTYPECHECKING", oe->{
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
		
		private final OrderValidationRule ADDORDERQTYCHECKING
			= new OrderValidationRule("ADDORDERQTYCHECKING", oe->{
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
		
		private final OrderValidationRule ADDORDERPRICECHECKING
			= new OrderValidationRule("ADDORDERPRICECHECKING", oe->{
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
		
		private final OrderValidationRule ADDORDERSIDECHECKING
			= new OrderValidationRule("ADDORDERSIDECHECKING", oe->{
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
					ADDORDERCLORDIDCHECKING
					, ADDORDERMSGTYPECHECKING
					, ADDORDERQTYCHECKING
					, ADDORDERPRICECHECKING
					, ADDORDERSIDECHECKING
					);
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
		// new order event to separate from original reference
		OrderEvent oeForOrderBook = new OrderEvent(oe);
		// initialize fields
		oeForOrderBook.put(14, 0L);
		clOrdIdToOrderEvent.put(clOrdId.toString(), oeForOrderBook);
		orderQueue.add(oeForOrderBook);
		++queueSize;
	}
	
	/******* Update Order *********/
	private class UpdateOrderValidator extends AbstractOrderValidator {

		private final OrderValidationRule UPDATEORDERCLORDIDCHECKING
		= new OrderValidationRule("UPDATEORDERCLORDIDCHECKING", oe->{
			Object clOrdId = oe.get(11);
			if (clOrdId == null) {
				return new OrderValidationResult("Tag 11: ClOrdId cannot be missing. ");
			} else {
				if (!getOrderEventInternalMap().containsKey(clOrdId.toString())) {
					return new OrderValidationResult("Tag 11: ClOrdId does not exist. ");
				}
			}
			
			return OrderValidationResult.getAcceptedInstance();
		});
		
		private final OrderValidationRule UPDATEORDERMSGTYPECHECKING
		= new OrderValidationRule("UPDATEORDERMSGTYPECHECKING", oe->{
			Object msgType = oe.get(35);
			if (msgType == null) {
				return new OrderValidationResult("Tag 35: MsgType cannot be missing. ");
			} else {
				if (!"G".equals(msgType)) {
					return new OrderValidationResult("Tag 35: MsgType can only be G. ");
				}
			}
			
			return OrderValidationResult.getAcceptedInstance();
		});
		
		private final OrderValidationRule UPDATEORDERQTYCHECKING
		= new OrderValidationRule("UPDATEORDERQTYCHECKING", oe->{
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
				
				
				try {
					Object clOrdId = oe.get(11);
					if (getOrderEventInternalMap().containsKey(clOrdId.toString())) {
						OrderEvent originOrderEvent = getOrderEventInternalMap().get(clOrdId.toString());
						// check original order qty
						Object originalQty = originOrderEvent.get(38);
						long originQtyLong = Long.parseLong(originalQty.toString());
						if (qtyLong >= originQtyLong) {
							return new OrderValidationResult(String.format("Tag 38: qty %s cannot be larger/equals to origin qty: %s. ",qty,originalQty));
						}
						// check new qty >= CumQty
						Object originCumQty = originOrderEvent.get(14);
						long originCumQtyLong = Long.parseLong(originCumQty.toString());
						long remainQtyLong = originQtyLong - originCumQtyLong;
						if (qtyLong >= remainQtyLong) {
							return new OrderValidationResult(String.format("Tag 38: qty %s cannot be larger than/equals to remainingQty: %s. ",qty,remainQtyLong));
						}
					}
					
				}
				catch (Exception e) {
					logger.error("issues happened at checking", e);
					return new OrderValidationResult("Tag 38: Update Order validation failed. ");
				}
				return OrderValidationResult.getAcceptedInstance();
			}
		});
		
		private final OrderValidationRule UPDATEORDERPRICECHECKING
		= new OrderValidationRule("UPDATEORDERPRICECHECKING", oe->{
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
		
		private final OrderValidationRule UPDATEORDERSIDECHECKING
		= new OrderValidationRule("UPDATEORDERSIDECHECKING", oe->{
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
					UPDATEORDERCLORDIDCHECKING
					, UPDATEORDERMSGTYPECHECKING
					, UPDATEORDERQTYCHECKING
					, UPDATEORDERPRICECHECKING
					, UPDATEORDERSIDECHECKING
					);
		}
		
	}
	
	/**
	 * This is to update an order in the order book
	 * @param oe
	 */
	public void updateOrder(OrderEvent oe) {
		handleValidationResult(oe, updateOrderValidator);
		Object clOrdId = oe.get(11);
		Object qty = oe.get(38);
		OrderEvent originalOrder = clOrdIdToOrderEvent.get(clOrdId.toString());
		Object originalQty = originalOrder.get(38);
		long qtyLong = Long.parseLong(qty.toString());
		long originalQtyLong = Long.parseLong(originalQty.toString());
		long diff = originalQtyLong - qtyLong;
		// update object in the order book
		originalOrder.put(38, qtyLong);
		this.totalOrderQuantity -= diff;
	}
	
}
