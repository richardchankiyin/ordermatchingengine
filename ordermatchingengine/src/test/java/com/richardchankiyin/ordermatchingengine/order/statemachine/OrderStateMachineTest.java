package com.richardchankiyin.ordermatchingengine.order.statemachine;

import static org.junit.Assert.*;

import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderUpdateable;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;

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
	public void testNosFromNonexistingToPendingNewValidationRuleWithClOrdIdFound() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, "A");
				return oe;
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
		OrderValidationResult result = om.getNosFromNonexistingToPendingNew().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMNONEXISTINGTOPENDINGNEW->Tag 11: ClOrdId : 1111 exists, cannot create an order with this value. ", result.getRejectReason());
		
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
		
		// Non-exist -> New: Rejected
		OrderStateMachine om = new OrderStateMachine(model, orderUpdateable);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "0");
		OrderValidationResult result = om.getNosFromNonexistingToPendingNew().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMNONEXISTINGTOPENDINGNEW->Tag 39 must be A for a blank new order creation. ", result.getRejectReason());
		
		// Non-exist -> Suspended: Rejected
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "9");
		result = om.getNosFromNonexistingToPendingNew().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMNONEXISTINGTOPENDINGNEW->Tag 39 must be A for a blank new order creation. ", result.getRejectReason());
		
		// Non-exist -> PartiallyFilled: Rejected
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "1");
		result = om.getNosFromNonexistingToPendingNew().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMNONEXISTINGTOPENDINGNEW->Tag 39 must be A for a blank new order creation. ", result.getRejectReason());
		
		// Non-exist -> Filled: Rejected
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "2");
		result = om.getNosFromNonexistingToPendingNew().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMNONEXISTINGTOPENDINGNEW->Tag 39 must be A for a blank new order creation. ", result.getRejectReason());
		
		// Non-exist -> DFD: Rejected
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "3");
		result = om.getNosFromNonexistingToPendingNew().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMNONEXISTINGTOPENDINGNEW->Tag 39 must be A for a blank new order creation. ", result.getRejectReason());

	}

	@Test
	public void testNosFromPendingNewToNewAccepted() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, "A");
				return oe;
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
		oe.put(39, "0");
		
		assertTrue(om.getNosFromPendingNewToNew().validate(oe).isAccepted());
	}
	
	@Test
	public void testNosFromPendingNewToNewRejected() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, "A");
				return oe;
			}
		};
		
		IOrderUpdateable orderUpdateable = new IOrderUpdateable() {
			@Override
			public void updateOrder(OrderEvent oe) {}
		};
		
		// NOS A -> 9 rejected
		OrderStateMachine om = new OrderStateMachine(model, orderUpdateable);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "9");
		OrderValidationResult result = om.getNosFromPendingNewToNew().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMPENDINGNEWTONEW->Tag 39: from A to 9 not accepted. Only A to 0", result.getRejectReason());

		// NOS A -> 1 rejected
		om = new OrderStateMachine(model, orderUpdateable);
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "1");
		result = om.getNosFromPendingNewToNew().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMPENDINGNEWTONEW->Tag 39: from A to 1 not accepted. Only A to 0", result.getRejectReason());

		// NOS A -> 2 rejected
		om = new OrderStateMachine(model, orderUpdateable);
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "2");
		result = om.getNosFromPendingNewToNew().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMPENDINGNEWTONEW->Tag 39: from A to 2 not accepted. Only A to 0", result.getRejectReason());
		
		// NOS A -> 3 rejected
		om = new OrderStateMachine(model, orderUpdateable);
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "3");
		result = om.getNosFromPendingNewToNew().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMPENDINGNEWTONEW->Tag 39: from A to 3 not accepted. Only A to 0", result.getRejectReason());
		
		
	}
}
