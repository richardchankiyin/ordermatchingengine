package com.richardchankiyin.ordermatchingengine.order.validation;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.OrderEventView;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;

public class IncomingOrderValidatorTest {
	
	private IncomingOrderValidator validator = null;
	
	@Before
	public void setup() {
		validator = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return false;
			}
			public OrderEvent getOrder(String clientOrderId) {
				return null;
			}
		});
	}

	@Test
	public void testDataTypeCheckingRuleTag38AndTag44Valid() {
		OrderValidationRule r = validator.getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(38, 111);
		oe.put(44, 111.5);
		
		assertTrue(r.validate(oe).isAccepted());
		
	}
	
	@Test
	public void testDataTypeCheckingRuleTag38ValidAndTag44Missing() {
		OrderValidationRule r = validator.getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(38, 111);
		
		assertTrue(r.validate(oe).isAccepted());
		
	}
	
	@Test
	public void testDataTypeCheckingRuleTag38MissingAndTag44Missing() {
		OrderValidationRule r = validator.getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testDataTypeCheckingRuleTag38NotValidTag44Valid() {
		OrderValidationRule r = validator.getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(38, "111a");
		oe.put(44, 111.5);
		
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("DATATYPECHECKING->Tag 38: 111a is not integer. ", result.getRejectReason());
	}
	
	@Test
	public void testDataTypeCheckingRuleTag38ValidTag44NotValid() {
		OrderValidationRule r = validator.getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(38, 111);
		oe.put(44, "111.5a");
		
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("DATATYPECHECKING->Tag 44: 111.5a is not numeric. ", result.getRejectReason());
		
	}
	
	@Test
	public void testDataTypeCheckingRuleTag38NotValidTag44NotValid() {
		OrderValidationRule r = validator.getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(38, "111a");
		oe.put(44, "111.5a");
		
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("DATATYPECHECKING->Tag 38: 111a is not integer. Tag 44: 111.5a is not numeric. ", result.getRejectReason());
	}

	private OrderValidationResult getMessageTypeOrderValidationResult(String msgType) {
		OrderValidationRule r = validator.getMsgTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		if (msgType != null)
			oe.put(35, msgType);
		return r.validate(oe);
	}
	
	@Test
	public void testMsgTypeCheckingAccepted() {
		getMessageTypeOrderValidationResult("D").isAccepted();
		getMessageTypeOrderValidationResult("F").isAccepted();
		getMessageTypeOrderValidationResult("G").isAccepted();
	}
	
	@Test
	public void testMsgTypeCheckingRejected() {
		OrderValidationResult result = getMessageTypeOrderValidationResult("A");
		assertFalse(result.isAccepted());
		assertEquals("MSGTYPECHECKING->Tag 35: A not accepted. Only accepts: [D, F, G] .", result.getRejectReason());
		
		OrderValidationResult result2 = getMessageTypeOrderValidationResult("5");
		assertFalse(result2.isAccepted());
		assertEquals("MSGTYPECHECKING->Tag 35: 5 not accepted. Only accepts: [D, F, G] .", result2.getRejectReason());
		
		OrderValidationResult result3 = getMessageTypeOrderValidationResult(null);
		assertFalse(result3.isAccepted());
		assertEquals("MSGTYPECHECKING->Type 35 msg type is missing. ", result3.getRejectReason());
		
	}
	
	@Test
	public void testSideCheckingRuleBuyValid() {
		OrderValidationRule r = validator.getSideCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(54, "1");
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testSideCheckingRuleSellValid() {
		OrderValidationRule r = validator.getSideCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(54, "2");
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testSideCheckingRuleInvalid() {
		OrderValidationRule r = validator.getSideCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(54, "3");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("SIDECHECKING->Tag 54: 3 not supported. ", result.getRejectReason());
	}
	
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleSkippingNonNOS() {
		OrderEvent oe = new OrderEvent();
		IOrderValidator r = validator.getNosOrderValidator();
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleMarketOrderValid() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "1");
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleLimitOrderValid() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "2");
		oe.put(44, 100);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleMarketOrderMissingClOrdId() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "1");
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 54: Side and Tag 55: Symbol cannot be missing for market order. |", result.getRejectReason());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleLimitOrderMissingClOrdId() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "2");
		oe.put(44, 100);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. |", result.getRejectReason());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleMarketOrderMissingOrderQty() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(40, "1");
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 54: Side and Tag 55: Symbol cannot be missing for market order. |", result.getRejectReason());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleLimitOrderMissingOrderQty() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(40, "2");
		oe.put(44, 100);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. |", result.getRejectReason());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleLimitOrderMissingPrice() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "11111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "2");
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. |", result.getRejectReason());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleMarketOrderMissingSide() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 54: Side and Tag 55: Symbol cannot be missing for market order. |", result.getRejectReason());		
	}
	
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleLimitOrderMissingSide() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "2");
		oe.put(44, 100);
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. |", result.getRejectReason());		
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleMarketOrderMissingSymbol() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "1");
		oe.put(54, "1");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 54: Side and Tag 55: Symbol cannot be missing for market order. |", result.getRejectReason());		
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleLimitOrderMissingSymbol() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "2");
		oe.put(44, 100);
		oe.put(54, "1");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. |", result.getRejectReason());		
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingNotMarketNotLimitOrder() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "3");
		oe.put(44, 100);
		oe.put(54, "1");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 40: 3 is not supported. Only Market(1) and Limit(2) are being supported. |", result.getRejectReason());		
	}
	
	@Test
	public void testReplaceRequestCompulsoryFieldCheckingValid() {
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 2);
				oe.put(44, 58);
				oe.put(54, 1);
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		IOrderValidator r = validator2.getReplaceRequestOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000);
		oe.put(40, 2);
		oe.put(44, 58);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testReplaceRequestCompulsoryFieldCheckingMissingOrderQty() {
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 2);
				oe.put(44, 58);
				oe.put(54, 1);
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		
		IOrderValidator r = validator2.getReplaceRequestOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("REPLACEREQUESTCOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty cannot be missed in a replace request order. |", result.getRejectReason());		
	}
	
	@Test
	public void testReplaceRequestCompulsoryFieldCheckingMissingClOrdId() {
		IOrderValidator r = validator.getReplaceRequestOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(35, "G");
		oe.put(38, 100);
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("REPLACEREQUESTCOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty cannot be missed in a replace request order. |", result.getRejectReason());		
	}
	
	@Test
	public void testCancelRequestCompulsoryFieldCheckingValid() {
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 2);
				oe.put(44, 58);
				oe.put(54, 1);
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		IOrderValidator r = validator2.getCancelOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testCancelRequestCompulsoryFieldCheckingMissingClOrdId() {
		IOrderValidator r = validator.getCancelOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(35, "F");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("CANCELREQUESTCOMPULSORYFIELDCHECKING->Tag 11: ClOrdId cannot be missed in a cancel request order. |", result.getRejectReason());		
	}
	
	@Test
	public void testNewOrderSingleClientOrderId() {
		IOrderValidator r = validator.getNosOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(40, "1");
		oe.put(44, 58);
		oe.put(54, "0005.HK");
		oe.put(55, "1");
		assertTrue(r.validate(oe).isAccepted());
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, "1");
				oe.put(44, 58);
				oe.put(54, 1);
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		
		IOrderValidator r2 = validator2.getNosOrderValidator();
		OrderValidationResult result2 = r2.validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("NEWORDERSINGLECLIENTORDERIDISNEWCHECKING->Tag 11: 1111 is being used in other order. |", result2.getRejectReason());		
		
	}
	
	@Test
	public void testReplaceRequestClientOrderId() {
		IOrderValidator r = validator.getReplaceRequestOrderValidator();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000L);
		oe.put(40, "1");
		oe.put(44, 58);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("REPLACEREQUESTANDCANCELREQUESTCLIENTORDERIDISNEWCHECKING->Tag 11: 1111 is not found. |", result.getRejectReason());
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, "1");
				oe.put(44, 58);
				oe.put(54, 1);
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		assertTrue(validator2.validate(oe).isAccepted());		
	}
	
	@Test
	public void testCancelRequestClientOrderId() {
		IOrderValidator r = validator.getCancelOrderValidator();
		
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("REPLACEREQUESTANDCANCELREQUESTCLIENTORDERIDISNEWCHECKING->Tag 11: 1111 is not found. |", result.getRejectReason());
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return true;
			}
			public OrderEvent getOrder(String clientOrderId) {
				return new OrderEvent();
			}
		});
		assertTrue(validator2.validate(oe).isAccepted());		
	}
	
	@Test
	public void testReplaceRequestAmendDownValid() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 1000);
		oe.put(40, "2");
		oe.put(44, 60);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 2000);
				oe.put(39, "0");
				oe.put(40, "2");
				oe.put(44, 60);
				oe.put(54, "1");
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		
		assertTrue(validator2.validate(oe).isAccepted());
	}
	
	@Test
	public void testReplaceRequestAmendUpInvalid() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000);
		oe.put(40, "2");
		oe.put(44, 60);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 2000);
				oe.put(39, "0");
				oe.put(40, "2");
				oe.put(44, 60);
				oe.put(54, "1");
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		
		OrderValidationResult result2 = validator2.validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("REPLACEREQUESTAMENDDOWNCHECKING->Tag 38: 3000 is larger/equal to 2000 which is not amend down for replace request order. ||", result2.getRejectReason());		
		
	}
	
	@Test
	public void testReplaceRequestOtherFieldChangeCheckingMarketOrderValid() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000);
		oe.put(40, 1);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 1);
				oe.put(54, 1);
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		
		assertTrue(validator2.validate(oe).isAccepted());
	}
	
	@Test
	public void testReplaceRequestOtherFieldChangeCheckingLimitOrderValid() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000);
		oe.put(40, 2);
		oe.put(44, 58);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 2);
				oe.put(44, 58);
				oe.put(54, 1);
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		
		assertTrue(validator2.validate(oe).isAccepted());
	}
	
	@Test
	public void testReplaceRequestOtherFieldChangeCheckingChangedOrderType() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000);
		oe.put(40, 1);
		oe.put(44, 58);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 2);
				oe.put(44, 58);
				oe.put(54, 1);
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		
		OrderValidationResult result2 = validator2.validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("REPLACEREQUESTOTHERFIELDCHANGECHECKING->Replace request order cannot alter Tag 54: Side, Tag 55: Symbol, Tag 40: OrderType, Tag 44: Price. ||", result2.getRejectReason());
	}
	
	@Test
	public void testReplaceRequestOtherFieldChangeCheckingChangedPrice() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000);
		oe.put(40, 1);
		oe.put(44, 58);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 1);
				oe.put(44, 57);
				oe.put(54, 1);
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		
		OrderValidationResult result2 = validator2.validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("REPLACEREQUESTOTHERFIELDCHANGECHECKING->Replace request order cannot alter Tag 54: Side, Tag 55: Symbol, Tag 40: OrderType, Tag 44: Price. ||", result2.getRejectReason());
	}
	
	@Test
	public void testReplaceRequestOtherFieldChangeCheckingChangedSide() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000);
		oe.put(40, 1);
		oe.put(44, 58);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 1);
				oe.put(44, 58);
				oe.put(54, 2);
				oe.put(55, "0005.HK");
				return new OrderEventView(oe);
			}
		});
		
		OrderValidationResult result2 = validator2.validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("REPLACEREQUESTOTHERFIELDCHANGECHECKING->Replace request order cannot alter Tag 54: Side, Tag 55: Symbol, Tag 40: OrderType, Tag 44: Price. ||", result2.getRejectReason());
	}
	
	@Test
	public void testReplaceRequestOtherFieldChangeCheckingChangedSymbol() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000);
		oe.put(40, 1);
		oe.put(44, 58);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 1);
				oe.put(44, 58);
				oe.put(54, 1);
				oe.put(55, "0001.HK");
				return new OrderEventView(oe);
			}
		});
		
		OrderValidationResult result2 = validator2.validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("REPLACEREQUESTOTHERFIELDCHANGECHECKING->Replace request order cannot alter Tag 54: Side, Tag 55: Symbol, Tag 40: OrderType, Tag 44: Price. ||", result2.getRejectReason());
	}
	
	@Test
	public void testValidateNewOrderSingleMarketOrderAccepted() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000);
		oe.put(40, 1);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return false;
			}
			public OrderEvent getOrder(String clientOrderId) {
				return null;
			}
		});
		
		assertTrue(validator2.validate(oe).isAccepted());
	}
	
	@Test
	public void testValidateNewOrderSingleLimitOrderAccepted() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000);
		oe.put(40, 2);
		oe.put(44, 58);
		oe.put(54, 1);
		oe.put(55, "0005.HK");
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return false;
			}
			public OrderEvent getOrder(String clientOrderId) {
				return null;
			}
		});
		
		assertTrue(validator2.validate(oe).isAccepted());
	}
	
	@Test
	public void testValidateReplaceRequestAmendDownAccepted() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 1000);

		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 1);
				oe.put(44, 58);
				oe.put(54, 1);
				oe.put(55, "0001.HK");
				return new OrderEventView(oe);
			}
		});

		assertTrue(validator2.validate(oe).isAccepted());
	}
	
	@Test
	public void testValidateReplaceRequestAmendUpRejected() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 4000);

		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 1);
				oe.put(44, 58);
				oe.put(54, 1);
				oe.put(55, "0001.HK");
				return new OrderEventView(oe);
			}
		});

		assertFalse(validator2.validate(oe).isAccepted());
	}
	
	@Test
	public void testValidateReplaceRequestClOrdIdNotFound() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 4000);

		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return false;
			}
			public OrderEvent getOrder(String clientOrderId) {
				return null;
			}
		});

		assertFalse(validator2.validate(oe).isAccepted());
	}
	
	@Test
	public void testValidateCancelOrderAccepted() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");

		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 4000);
				oe.put(40, 1);
				oe.put(44, 58);
				oe.put(54, 1);
				oe.put(55, "0001.HK");
				return new OrderEventView(oe);
			}
		});
		
		assertTrue(validator2.validate(oe).isAccepted());
	}
	
	@Test
	public void testValidateCancelOrderClOrdIdNotFound() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");

		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return false;
			}
			public OrderEvent getOrder(String clientOrderId) {
				return null;
			}
		});
		
		assertFalse(validator2.validate(oe).isAccepted());
	}
}
