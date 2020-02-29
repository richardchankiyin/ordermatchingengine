package com.richardchankiyin.ordermatchingengine.order.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.OrderEventView;

public class OrderRepository implements IOrderUpdateable{
	private static final Logger logger = LoggerFactory.getLogger(OrderRepository.class);
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
		OrderEvent existing = orderMap.get(okey.toString());
		boolean isNewEvent = false;
		if (existing == null) {
			isNewEvent = true;
			existing = new OrderEvent();
		}
		OrderEvent updatedAs = copyOrderEvent(oe);
		logger.debug("[isNewEvent: {}][original event: {}][to be updated as: {}]", isNewEvent, existing, updatedAs);
		existing.putAll(updatedAs);		
		orderMap.put(okey.toString(), existing);
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
			OrderEvent oe = orderMap.get(clientOrderId);
			if (oe == null) {
				return null;
			} else {
				return new OrderEventView(oe);
			}
		}
		
		@Override
		public List<OrderEvent> getListOfOrders(Function<OrderEvent, Boolean> criterion) {
			return orderMap.values().stream().filter(i -> criterion.apply(i)).collect(Collectors.toList());
		}
		
		public String toString() {
			return orderMap == null ? "null" : orderMap.toString();
		}
	}
}
