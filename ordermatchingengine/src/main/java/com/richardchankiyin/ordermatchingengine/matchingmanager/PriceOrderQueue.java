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
	private CancelOrderValidator cancelOrderValidator = new CancelOrderValidator();
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
	
	protected long getActualOrderQueueSize() {
		return this.orderQueue.size();
	}
	
	private void handleValidationResult(OrderEvent oe, AbstractOrderValidator validator) {
		OrderValidationResult validationResult = validator.validate(oe);
		if (!validationResult.isAccepted()) {
			String rejectReason = validationResult.getRejectReason();
			logger.debug("OrderEvent: {} validator: {} Rejected Reason: {}", oe, validator, rejectReason);
			throw new IllegalArgumentException(rejectReason);
		}
	}
	
	private void housekeepQueue() {
		boolean isContinue = true;
		while (isContinue) {
			OrderEvent oe = orderQueue.peek();
			if (oe != null) {
				Object status = oe.get(39);
				if ("2".equals(status) || "4".equals(status) || status == null) {
					OrderEvent oePolled = orderQueue.poll();
					Object clOrdId = oePolled.get(11);
					clOrdIdToOrderEvent.remove(clOrdId);
					logger.info("housekept order from book: {}", oePolled);
					logger.debug("Queue\n-------------\n{}\n----------", orderQueue);
					logger.debug("Map\n-------------\n{}\n----------", clOrdIdToOrderEvent);
				} else {
					isContinue = false;
				}
			} else {
				isContinue = false;
			}
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
		
		private final OrderValidationRule ADDORDERCUMQTYCHECKING
			= new OrderValidationRule("ADDORDERCUMQTYCHECKING", oe->{
				Object cumQty = oe.get(14);
				if (cumQty == null) {
					// accept cumQty missing
					return OrderValidationResult.getAcceptedInstance();
				} else {
					long cumQtyLong = 0;
					try {
						cumQtyLong = Long.parseLong(cumQty.toString());
					}
					catch (Exception e) {
						logger.debug("cumQty", e);
						return new OrderValidationResult("Tag 14: CumQty must be integer. ");
					}
					if (cumQtyLong <= 0) {
						return new OrderValidationResult("Tag 14: CumQty must be positive. ");
					}
					
					Object qty = oe.get(38);
					long qtyLong = 0;
					try {
						qtyLong = Long.parseLong(qty.toString());
						if (cumQtyLong >=  qtyLong) {
							return new OrderValidationResult(String.format("Tag 14: CumQty %s cannot be larger than/equals to Tag 38: Qty %s",cumQtyLong,qtyLong));
						}
					}
					catch (Exception e) {
						logger.debug("qty parsing issue", e);
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
					, ADDORDERCUMQTYCHECKING
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
		Object cumQty = oe.get(14);
		long cumQtyLong = 0;
		if (cumQty != null) {
			cumQtyLong = Long.parseLong(cumQty.toString());
		}
		long remainingQty = qtyLong - cumQtyLong;
		this.totalOrderQuantity += remainingQty;
		
		
		// new order event to separate from original reference
		OrderEvent oeForOrderBook = initializeOrder(oe, cumQtyLong);
		getOrderEventInternalMap().put(clOrdId.toString(), oeForOrderBook);
		orderQueue.add(oeForOrderBook);
		++queueSize;
	}
	
	private OrderEvent initializeOrder(OrderEvent oe, long cumQty) {
		OrderEvent oeForOrderBook = new OrderEvent(oe);
		// initialize fields
		oeForOrderBook.put(14, cumQty);
		oeForOrderBook.put(39, cumQty == 0 ? "0" : "1");		
		oeForOrderBook.remove(35);
		return oeForOrderBook;
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
		
		private final OrderValidationRule UPDATEORDERSTATUSCHECKING
		= new OrderValidationRule("UPDATEORDERSTATUSCHECKING", oe->{
			
			try {
				Object clOrdId = oe.get(11);
				if (getOrderEventInternalMap().containsKey(clOrdId.toString())) {
					OrderEvent originOrderEvent = getOrderEventInternalMap().get(clOrdId.toString());
					Object status = originOrderEvent.get(39);
					if (!("0".equals(status) || "1".equals(status))) {
						return new OrderValidationResult("Tag 39: only order at status 0 or 1 can be updated. ");
					}
				}
			}
			catch (Exception e) {
				logger.error("issues happened at checking", e);
				return new OrderValidationResult("Tag 39: Update Order validation failed. ");
			}
			
			return OrderValidationResult.getAcceptedInstance();
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
					, UPDATEORDERSTATUSCHECKING
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
		OrderEvent originalOrder = getOrderEventInternalMap().get(clOrdId.toString());
		Object originalQty = originalOrder.get(38);
		long qtyLong = Long.parseLong(qty.toString());
		long originalQtyLong = Long.parseLong(originalQty.toString());
		long diff = originalQtyLong - qtyLong;
		// update object in the order book
		originalOrder.put(38, qtyLong);
		this.totalOrderQuantity -= diff;
	}
	
	/******* Cancel Order *********/
	private class CancelOrderValidator extends AbstractOrderValidator {

		private final OrderValidationRule CANCELORDERCLORDIDCHECKING
		= new OrderValidationRule("CANCELORDERCLORDIDCHECKING", oe->{
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
		
		private final OrderValidationRule CANCELORDERMSGTYPECHECKING
		= new OrderValidationRule("CANCELORDERMSGTYPECHECKING", oe->{
			Object msgType = oe.get(35);
			if (msgType == null) {
				return new OrderValidationResult("Tag 35: MsgType cannot be missing. ");
			} else {
				if (!"F".equals(msgType)) {
					return new OrderValidationResult("Tag 35: MsgType can only be F. ");
				}
			}
			
			return OrderValidationResult.getAcceptedInstance();
		});
		
		private final OrderValidationRule CANCELORDERSTATUSCHECKING
		= new OrderValidationRule("CANCELORDERSTATUSCHECKING", oe->{
			
			try {
				Object clOrdId = oe.get(11);
				if (getOrderEventInternalMap().containsKey(clOrdId.toString())) {
					OrderEvent originOrderEvent = getOrderEventInternalMap().get(clOrdId.toString());
					Object status = originOrderEvent.get(39);
					if (!("0".equals(status) || "1".equals(status))) {
						return new OrderValidationResult("Tag 39: only order at status 0 or 1 can be cancelled. ");
					}
				}
			}
			catch (Exception e) {
				logger.error("issues happened at checking", e);
				return new OrderValidationResult("Tag 39: Update Order validation failed. ");
			}
			
			return OrderValidationResult.getAcceptedInstance();
		});
		
		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			return Arrays.asList(
					CANCELORDERCLORDIDCHECKING
					, CANCELORDERMSGTYPECHECKING
					, CANCELORDERSTATUSCHECKING
					);
		}
		
	}
	
	/**
	 * This is to add a new order single to the order book
	 * @param oe
	 */
	public void cancelOrder(OrderEvent oe) {
		handleValidationResult(oe, cancelOrderValidator);
		Object clOrdId = oe.get(11);
		OrderEvent originalOrder = getOrderEventInternalMap().get(clOrdId.toString());
		Object cumQty = originalOrder.get(14);
		Object qty = originalOrder.get(38);
		long cumQtyLong = 0;
		long qtyLong = 0;
		long remainQtyLong = 0;
		try {
			cumQtyLong = Long.parseLong(cumQty.toString());
			qtyLong = Long.parseLong(qty.toString());
			remainQtyLong = qtyLong - cumQtyLong;
		}
		catch (Exception e) {
			// issue happened above. However we cannot leave this doing nothing
			// will cancel this order too.
			logger.error("issue cancelling order", e);
		}
		// if partial filled -> filled; if new -> cancelled
		if (cumQtyLong == 0) {
			originalOrder.put(39, "4");
		} else {
			originalOrder.put(39, "2");
		}
		--queueSize;
		totalOrderQuantity -= remainQtyLong;
		
		housekeepQueue();
		
	}
}
