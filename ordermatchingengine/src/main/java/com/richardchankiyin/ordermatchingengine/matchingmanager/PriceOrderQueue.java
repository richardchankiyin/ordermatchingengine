package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.utils.NumericUtils;

public class PriceOrderQueue {

	private final static int ROUNDSCALE = 6;
	
	private Map<String, OrderEvent> clOrdIdToOrderEvent;
	private Queue<OrderEvent> orderQueue;
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
	
	/**
	 * This is to add a new order single to the order book
	 * @param oe
	 */
	public void addOrder(OrderEvent oe) {
		Object clOrdId = oe.get(11);
		Objects.requireNonNull(clOrdId, "Tag 11: ClOrdId cannot be missing. ");
		Object msgType = oe.get(35);
		Objects.requireNonNull(msgType, "Tag 35: MsgType cannot be missing. ");
		if (!"D".equals(msgType))
			throw new IllegalArgumentException("Only accept NOS");
		Object qty = oe.get(38);
		Objects.requireNonNull(qty, "Tag 38: qty cannot be missing. ");
		long qtyLong = Long.parseLong(qty.toString());
		Object price = oe.get(44);
		Objects.requireNonNull(price, "Tag 44: price cannot be missing. ");
		if (orderPrice != NumericUtils.roundDouble(Double.parseDouble(price.toString()), ROUNDSCALE)) {
			throw new IllegalArgumentException(String.format("Price %s is not expected: %s", price, orderPrice));
		}
		Object side = oe.get(54);
		Objects.requireNonNull(side, "Tag 54: side cannot be missing. ");
		boolean isSideValid = this.isBuy ? "1".equals(side) : "2".equals(side);
		if (!isSideValid) {
			throw new IllegalArgumentException(String.format("side %s is not matching", side));
		}
		
		if (clOrdIdToOrderEvent.containsKey(clOrdId.toString())) {
			throw new IllegalArgumentException(String.format("ClOrdId: %s exists", clOrdId));
		}
		
		this.totalOrderQuantity += qtyLong;
		clOrdIdToOrderEvent.put(clOrdId.toString(), oe);
		orderQueue.add(oe);
	}
	
}
