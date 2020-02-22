package com.richardchankiyin.ordermatchingengine.order.statemachine;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderUpdateable;
import com.richardchankiyin.ordermatchingengine.order.model.OrderRepository;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;

public class OrderStateMachineTest {
	private static final Logger logger = LoggerFactory.getLogger(OrderStateMachineTest.class);
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
		
		assertTrue(om.getNosFromPendingNewToNewOrRej().validate(oe).isAccepted());
	}
	
	@Test
	public void testNosFromPendingNewToRejectAccepted() {
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
		oe.put(39, "8");
		
		assertTrue(om.getNosFromPendingNewToNewOrRej().validate(oe).isAccepted());
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
		OrderValidationResult result = om.getNosFromPendingNewToNewOrRej().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMPENDINGNEWTONEWORREJ->Tag 39: from A to 9 not accepted. Only A to 0/8. ", result.getRejectReason());

		// NOS A -> 1 rejected
		om = new OrderStateMachine(model, orderUpdateable);
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "1");
		result = om.getNosFromPendingNewToNewOrRej().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMPENDINGNEWTONEWORREJ->Tag 39: from A to 1 not accepted. Only A to 0/8. ", result.getRejectReason());

		// NOS A -> 2 rejected
		om = new OrderStateMachine(model, orderUpdateable);
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "2");
		result = om.getNosFromPendingNewToNewOrRej().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMPENDINGNEWTONEWORREJ->Tag 39: from A to 2 not accepted. Only A to 0/8. ", result.getRejectReason());
		
		// NOS A -> 3 rejected
		om = new OrderStateMachine(model, orderUpdateable);
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(39, "3");
		result = om.getNosFromPendingNewToNewOrRej().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NOSFROMPENDINGNEWTONEWORREJ->Tag 39: from A to 3 not accepted. Only A to 0/8. ", result.getRejectReason());
		
		
	}
	
	@Test
	public void testReplaceRequestOnNonExistingClOrdId() {
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
		oe.put(35, "G");
		oe.put(39, "9");
		
		OrderValidationResult result = om.getReplaceRequestStatusChange().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("REPLACEREQUESTSTATUSCHANGE->Tag 11: 1111 Replace request on a non-exist order is rejected. ", result.getRejectReason());

	}
	
	@Test
	public void testReplaceRequestOnStatusPendingNewNotAccepted() {
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
		oe.put(35, "G");
		oe.put(39, "9");
		OrderValidationResult result = om.getReplaceRequestStatusChange().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("REPLACEREQUESTSTATUSCHANGE->Tag 39: A cannot be further replaced. ", result.getRejectReason());

	}
	
	@Test
	public void testReplaceRequestOnStatusDFDNotAccepted() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, "3");
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
		oe.put(35, "G");
		oe.put(39, "9");
		OrderValidationResult result = om.getReplaceRequestStatusChange().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("REPLACEREQUESTSTATUSCHANGE->Tag 39: 3 cannot be further replaced. ", result.getRejectReason());

	}
	
	@Test
	public void testReplaceRequestFromNewToSuspendedAccepted() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, "0");
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
		oe.put(35, "G");
		oe.put(39, "9");
		
		assertTrue(om.getReplaceRequestStatusChange().validate(oe).isAccepted());
		
	}
	
	@Test
	public void testReplaceRequestFromPartialFilledToSuspendedAccepted() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, "1");
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
		oe.put(35, "G");
		oe.put(39, "9");
		
		assertTrue(om.getReplaceRequestStatusChange().validate(oe).isAccepted());
		
	}
	
	@Test
	public void testReplaceRequestFromFilledToDFD() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, "2");
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
		oe.put(35, "G");
		oe.put(39, "3");
		
		assertTrue(om.getReplaceRequestStatusChange().validate(oe).isAccepted());
		
	}
	
	@Test
	public void testReplaceRequestFromSuspectedToPartialFilled() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, "9");
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
		oe.put(35, "G");
		oe.put(39, "1");
		
		assertTrue(om.getReplaceRequestStatusChange().validate(oe).isAccepted());
		
	}
	
	@Test
	public void testReplaceRequestFromSuspectedToFilled() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, "9");
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
		oe.put(35, "G");
		oe.put(39, "2");
		
		assertTrue(om.getReplaceRequestStatusChange().validate(oe).isAccepted());
	}
	
	private OrderValidationResult replaceRequestValidation(String fromStatus, String toStatus) {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, fromStatus);
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
		oe.put(35, "G");
		oe.put(39, toStatus);
		
		return om.getReplaceRequestStatusChange().validate(oe);
	}
	
	@Test
	public void testReplaceRequestStatusChangeInvalid() {
		List<String> statusFromTo = 
			Arrays.asList("A0", "A9", "A1", "A2", "A3", "01", "02", "03", "13");
		for (String i: statusFromTo) {
			assertFalse(replaceRequestValidation(
					String.valueOf(i.charAt(0)), String.valueOf(i.charAt(1)))
						.isAccepted());
		}
	}
	
	@Test
	public void testCancelRequestOnNonExistClOrdId() {
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
		oe.put(35, "F");
		oe.put(39, "4");
		
		OrderValidationResult result = om.getCancelRequestStatusChange().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("CANCELREQUESTSTATUSCHANGE->Tag 11: 1111 Cancel request on a non-exist order is rejected. ", result.getRejectReason());

	}
	
	@Test
	public void testCancelRequestFromNewToCancelled() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, "0");
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
		oe.put(35, "F");
		oe.put(39, "4");
		
		assertTrue(om.getCancelRequestStatusChange().validate(oe).isAccepted());
		
	}
	
	@Test
	public void testCancelRequestFromPartialFilledToFilled() {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, "1");
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
		oe.put(35, "F");
		oe.put(39, "2");
		
		assertTrue(om.getCancelRequestStatusChange().validate(oe).isAccepted());
		
	}
	
	
	private OrderValidationResult cancelRequestValidation(String fromStatus, String toStatus) {
		IOrderModel model = new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(39, fromStatus);
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
		oe.put(35, "F");
		oe.put(39, toStatus);
		
		return om.getCancelRequestStatusChange().validate(oe);
	}
	
	@Test
	public void testCancelRequestStatusChangeInvalid() {
		List<String> statusFromTo = 
			Arrays.asList("01", "02", "03", "09", "10", "13", "14", "A0", "A1", "A2", "A3", "A4", "9A", "90", "91", "92", "93", "94");
		for (String i: statusFromTo) {
			assertFalse(cancelRequestValidation(
					String.valueOf(i.charAt(0)), String.valueOf(i.charAt(1)))
						.isAccepted());
		}
	}
	
	@Test
	public void testPendingNewToNewToPartialFillToFillToDFD() {
		OrderRepository orderUpdateable = new OrderRepository(5);
		IOrderModel model = orderUpdateable.getOrderModel();
		OrderStateMachine om = new OrderStateMachine(model, orderUpdateable);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "11111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(39, "A");
		oe.put(40, 2);
		oe.put(44, 58);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		OrderValidationResult result = om.handleEvent(oe);
		assertTrue(result.isAccepted());
		OrderEvent order = model.getOrder("11111");
		logger.debug("order pending new: {}", order);
		assertEquals("A", order.get(39));
		
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "11111");
		oe2.put(35, "D");
		oe2.put(38, 2000);
		oe2.put(39, "0");
		oe2.put(40, 2);
		oe2.put(44, 58);
		oe2.put(54, 1);
		oe2.put(55, "0005.HK");
		OrderValidationResult result2 = om.handleEvent(oe2);
		assertTrue(result2.isAccepted());
		OrderEvent order2 = model.getOrder("11111");
		logger.debug("order new: {}", order2);
		assertEquals("0", order2.get(39));
		
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "11111");
		oe3.put(35, "G");
		oe3.put(38, 2000);
		oe3.put(39, "9");
		oe3.put(40, 2);
		oe3.put(44, 58);
		oe3.put(54, 1);
		oe3.put(55, "0005.HK");
		OrderValidationResult result3 = om.handleEvent(oe3);
		assertTrue(result3.isAccepted());
		OrderEvent order3 = model.getOrder("11111");
		logger.debug("order suspended: {}", order3);
		assertEquals("9", order3.get(39));
		
		OrderEvent oe4 = new OrderEvent();
		oe4.put(11, "11111");
		oe4.put(14, 1200);
		oe4.put(35, "G");
		oe4.put(38, 2000);
		oe4.put(39, "1");
		oe4.put(40, 2);
		oe4.put(44, 58);
		oe4.put(54, 1);
		oe4.put(55, "0005.HK");
		OrderValidationResult result4 = om.handleEvent(oe4);
		assertTrue(result4.isAccepted());
		OrderEvent order4 = model.getOrder("11111");
		logger.debug("order partial filled: {}", order4);
		assertEquals("1", order4.get(39));
		
		OrderEvent oe5 = new OrderEvent();
		oe5.put(11, "11111");
		oe5.put(14, 1200);
		oe5.put(35, "G");
		oe5.put(38, 2000);
		oe5.put(39, "9");
		oe5.put(40, 2);
		oe5.put(44, 58);
		oe5.put(54, 1);
		oe5.put(55, "0005.HK");
		OrderValidationResult result5 = om.handleEvent(oe5);
		assertTrue(result5.isAccepted());
		OrderEvent order5 = model.getOrder("11111");
		logger.debug("order suspended: {}", order5);
		assertEquals("9", order5.get(39));
				
		OrderEvent oe6 = new OrderEvent();
		oe6.put(11, "11111");
		oe6.put(14, 2000);
		oe6.put(35, "G");
		oe6.put(38, 2000);
		oe6.put(39, "2");
		oe6.put(40, 2);
		oe6.put(44, 58);
		oe6.put(54, 1);
		oe6.put(55, "0005.HK");
		OrderValidationResult result6 = om.handleEvent(oe6);
		assertTrue(result6.isAccepted());
		OrderEvent order6 = model.getOrder("11111");
		logger.debug("order filled: {}", order6);
		assertEquals("2", order6.get(39));
		
		OrderEvent oe7 = new OrderEvent();
		oe7.put(11, "11111");
		oe7.put(14, 2000);
		oe7.put(35, "G");
		oe7.put(38, 2000);
		oe7.put(39, "3");
		oe7.put(40, 2);
		oe7.put(44, 58);
		oe7.put(54, 1);
		oe7.put(55, "0005.HK");
		OrderValidationResult result7 = om.handleEvent(oe7);
		assertTrue(result7.isAccepted());
		OrderEvent order7 = model.getOrder("11111");
		logger.debug("order dfd: {}", order7);
		assertEquals("3", order7.get(39));
		
	}
	
	@Test
	public void testPendingNewToNewToCancelled() {
		OrderRepository orderUpdateable = new OrderRepository(5);
		IOrderModel model = orderUpdateable.getOrderModel();
		OrderStateMachine om = new OrderStateMachine(model, orderUpdateable);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "11111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(39, "A");
		oe.put(40, 2);
		oe.put(44, 58);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		OrderValidationResult result = om.handleEvent(oe);
		assertTrue(result.isAccepted());
		OrderEvent order = model.getOrder("11111");
		logger.debug("order pending new: {}", order);
		assertEquals("A", order.get(39));
		
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "11111");
		oe2.put(35, "D");
		oe2.put(38, 2000);
		oe2.put(39, "0");
		oe2.put(40, 2);
		oe2.put(44, 58);
		oe2.put(54, 1);
		oe2.put(55, "0005.HK");
		OrderValidationResult result2 = om.handleEvent(oe2);
		assertTrue(result2.isAccepted());
		OrderEvent order2 = model.getOrder("11111");
		logger.debug("order new: {}", order2);
		assertEquals("0", order2.get(39));
		
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "11111");
		oe3.put(35, "F");
		oe3.put(38, 2000);
		oe3.put(39, "4");
		oe3.put(40, 2);
		oe3.put(44, 58);
		oe3.put(54, 1);
		oe3.put(55, "0005.HK");
		OrderValidationResult result3 = om.handleEvent(oe3);
		assertTrue(result3.isAccepted());
		OrderEvent order3 = model.getOrder("11111");
		logger.debug("order cancelled: {}", order3);
		assertEquals("4", order3.get(39));
	}
	
	@Test
	public void testPendingNewToNewToSuspendedToPartialFilledToFilledViaCancelRequest() {
		OrderRepository orderUpdateable = new OrderRepository(5);
		IOrderModel model = orderUpdateable.getOrderModel();
		OrderStateMachine om = new OrderStateMachine(model, orderUpdateable);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "11111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(39, "A");
		oe.put(40, 2);
		oe.put(44, 58);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		OrderValidationResult result = om.handleEvent(oe);
		assertTrue(result.isAccepted());
		OrderEvent order = model.getOrder("11111");
		logger.debug("order pending new: {}", order);
		assertEquals("A", order.get(39));
		
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "11111");
		oe2.put(35, "D");
		oe2.put(38, 2000);
		oe2.put(39, "0");
		oe2.put(40, 2);
		oe2.put(44, 58);
		oe2.put(54, 1);
		oe2.put(55, "0005.HK");
		OrderValidationResult result2 = om.handleEvent(oe2);
		assertTrue(result2.isAccepted());
		OrderEvent order2 = model.getOrder("11111");
		logger.debug("order new: {}", order2);
		assertEquals("0", order2.get(39));
		
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "11111");
		oe3.put(35, "G");
		oe3.put(38, 2000);
		oe3.put(39, "9");
		oe3.put(40, 2);
		oe3.put(44, 58);
		oe3.put(54, 1);
		oe3.put(55, "0005.HK");
		OrderValidationResult result3 = om.handleEvent(oe3);
		assertTrue(result3.isAccepted());
		OrderEvent order3 = model.getOrder("11111");
		logger.debug("order suspended: {}", order3);
		assertEquals("9", order3.get(39));
		
		OrderEvent oe4 = new OrderEvent();
		oe4.put(11, "11111");
		oe4.put(14, 1200);
		oe4.put(35, "G");
		oe4.put(38, 2000);
		oe4.put(39, "1");
		oe4.put(40, 2);
		oe4.put(44, 58);
		oe4.put(54, 1);
		oe4.put(55, "0005.HK");
		OrderValidationResult result4 = om.handleEvent(oe4);
		assertTrue(result4.isAccepted());
		OrderEvent order4 = model.getOrder("11111");
		logger.debug("order partial filled: {}", order4);
		assertEquals("1", order4.get(39));
		
		OrderEvent oe5 = new OrderEvent();
		oe5.put(11, "11111");
		oe5.put(14, 1200);
		oe5.put(35, "F");
		oe5.put(38, 1200);
		oe5.put(39, "2");
		oe5.put(40, 2);
		oe5.put(44, 58);
		oe5.put(54, 1);
		oe5.put(55, "0005.HK");
		OrderValidationResult result5 = om.handleEvent(oe5);
		assertTrue(result5.isAccepted());
		OrderEvent order5 = model.getOrder("11111");
		logger.debug("order filled: {}", order5);
		assertEquals("2", order5.get(39));
	}
	
}
