package com.richardchankiyin.ordermatchingengine.order.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class OrderRepository implements IOrderUpdateable{
	private static final int[] fixtagnotsaved = {35};
	private Map<String, OrderEvent> orderMap = null;
	private OrderModel orderModel = null;
	
	public OrderRepository(int capacity) {
		this.orderMap = new HashMap<String, OrderEvent>(capacity);
		this.orderModel = new OrderModel();
	}

	@Override
	public void updateOrder(OrderEvent oe) {
		Objects.requireNonNull(oe, "Order Event cannot be null");
		Object okey = oe.get(11);
		Objects.requireNonNull(okey, "Tag 11 Key missing");
		OrderEvent existing = getOrderModel().getOrder(okey.toString());
		if (existing == null) {
			existing = new OrderEvent();
		}
		existing.putAll(copyOrderEvent(oe));
	}
	
	protected OrderEvent copyOrderEvent(OrderEvent oe) {
		OrderEvent copy = new OrderEvent(oe);
		//drop some fields
		for (int i: fixtagnotsaved) {
			copy.remove(i);
		}
		return copy;
	}
	
	public IOrderModel getOrderModel() {
		return this.orderModel;
	}

	private class OrderModel implements IOrderModel {

		@Override
		public boolean isClientOrderIdFound(String clientOrderId) {
			return orderMap.containsKey(clientOrderId);
		}

		@Override
		public OrderEvent getOrder(String clientOrderId) {
			return orderMap.get(clientOrderId);
		}
		
	}
}
