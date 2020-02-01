package com.richardchankiyin.ordermatchingengine.order.statemachine;

import static org.junit.Assert.*;

import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderUpdateable;

public class OrderStateMachineTest {

	@Test
	public void testNosFromNonexistingToPendingNewValidationRuleAccepted() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return false;
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				return null;
			}
		};
		
		IOrderUpdateable orderUpdateable = new IOrderUpdateable() {
			@Override
			public void updateOrder(OrderEvent oe) {}
		};
		
		OrderStateMachine om = new OrderStateMachine(model, orderUpdateable);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "A");
		assertTrue(om.getNosFromNonexistingToPendingNew().validate(oe).isAccepted());
		
	}
	
	@Test
	public void testNosFromNonexistingToPendingNewValidationRuleRejected() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return false;
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				return null;
			}
		};
		
		IOrderUpdateable orderUpdateable = new IOrderUpdateable() {
			@Override
			public void updateOrder(OrderEvent oe) {}
		};
		
		// Non-exist -> New Rejected
		OrderStateMachine om = new OrderStateMachine(model, orderUpdateable);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "0");
		assertFalse(om.getNosFromNonexistingToPendingNew().validate(oe).isAccepted());
		
		
	}

}
