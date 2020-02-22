package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.OrderEventView;

public class ExecutionBook implements IExecutionBook {
	private static final Logger logger = LoggerFactory.getLogger(ExecutionBook.class);
	private final Map<String,List<OrderEvent>> executionByOrderIdMap = new HashMap<>();
	private final Map<String,OrderEvent> executionByExecIdMap = new HashMap<>();
	
	private void validate(OrderEvent activeOrder, List<OrderEvent> passiveOrders) {
		Objects.requireNonNull(activeOrder, "active order cannot be null!");
		Objects.requireNonNull(passiveOrders, "passive order list cannot be null!");
		if (passiveOrders.isEmpty()) {
			throw new IllegalArgumentException("passive order list is empty!");
		}
	}
	
	private String generateExecId(Object activeOrdId, Object passOrdId) {
		return activeOrdId + "_" + passOrdId + "_" + System.currentTimeMillis();
	}
	
	public OrderEvent getExecutionByExecId(String execId) {
		return executionByExecIdMap.get(execId);
	}
	
	public List<OrderEvent> getExecutionsByOrderId(String orderId) {
		return executionByOrderIdMap.get(orderId);
	}
	
	@Override
	public List<OrderEvent> processExecutions(OrderEvent activeOrder,
			List<OrderEvent> passiveOrders) {
		validate(activeOrder, passiveOrders);
		List<OrderEvent> result = new ArrayList<>();
		for (OrderEvent passiveOrder: passiveOrders) {
			String execId = generateExecId(activeOrder.get(11), passiveOrder.get(11));
			OrderEvent execItem = new OrderEvent();
			execItem.put(17, execId);
			String activeOrderId = activeOrder.getOrDefault(11, "activeOrderIdUnknown-" + activeOrder).toString();
			String passiveOrderId = passiveOrder.getOrDefault(11, "passiveOrderIdUnknown-" + passiveOrder).toString();
			execItem.put(11, activeOrderId);
			execItem.put(37, passiveOrderId);
			execItem.put(35, "8");
			execItem.put(38, passiveOrder.get(32));
			execItem.put(44, passiveOrder.get(44));
			execItem.put(54, activeOrder.get(54));
			execItem.put(55, passiveOrder.get(55));
			execItem.put(60, System.currentTimeMillis());
			// add a deep view-only copy for external reference
			OrderEvent execItemToBePut = new OrderEventView(execItem);
			executionByExecIdMap.put(execId, execItemToBePut);
			executionByOrderIdMap.compute(activeOrderId, (k,v)-> {
				if (v == null) {
					return Arrays.asList(execItemToBePut);
				} else {
					v.add(execItemToBePut);
					return v;
				}
			});
			executionByOrderIdMap.compute(passiveOrderId, (k,v)-> {
				if (v == null) {
					return Arrays.asList(execItemToBePut);
				} else {
					v.add(execItemToBePut);
					return v;
				}
			});
			
			result.add(execItemToBePut); 
		}
		logger.debug("executionExecIdMap: {}", executionByExecIdMap);
		logger.debug("executionByOrderIdMap: {}", executionByOrderIdMap);
		logger.debug("execution result: {}", result);
		
		return result;
	}

}
