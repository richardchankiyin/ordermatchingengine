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
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleMarketOrderValid() {
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
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
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
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
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "1");
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 54: Side and Tag 55: Symbol cannot be missing for market order. ", result.getRejectReason());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleLimitOrderMissingClOrdId() {
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "2");
		oe.put(44, 100);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. ", result.getRejectReason());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleMarketOrderMissingOrderQty() {
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(40, "1");
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 54: Side and Tag 55: Symbol cannot be missing for market order. ", result.getRejectReason());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleLimitOrderMissingOrderQty() {
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(40, "2");
		oe.put(44, 100);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. ", result.getRejectReason());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleLimitOrderMissingPrice() {
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "11111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "2");
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. ", result.getRejectReason());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleMarketOrderMissingSide() {
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "1");
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 54: Side and Tag 55: Symbol cannot be missing for market order. ", result.getRejectReason());		
	}
	
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleLimitOrderMissingSide() {
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "2");
		oe.put(44, 100);
		oe.put(55, "0005.HK");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. ", result.getRejectReason());		
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleMarketOrderMissingSymbol() {
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "1");
		oe.put(54, "1");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 54: Side and Tag 55: Symbol cannot be missing for market order. ", result.getRejectReason());		
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleLimitOrderMissingSymbol() {
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "2");
		oe.put(44, 100);
		oe.put(54, "1");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. ", result.getRejectReason());		
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingNotMarketNotLimitOrder() {
		OrderValidationRule r = validator.getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 100);
		oe.put(40, "3");
		oe.put(44, 100);
		oe.put(54, "1");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("NEWORDERSINGLECOMPULSORYFIELDCHECKING->Tag 40: 3 is not supported. Only Market(1) and Limit(2) are being supported. ", result.getRejectReason());		
	}
	
	@Test
	public void testReplaceRequestCompulsoryFieldCheckingValid() {
		OrderValidationRule r = validator.getReplaceRequestCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 100);
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testReplaceRequestCompulsoryFieldCheckingMissingOrderQty() {
		OrderValidationRule r = validator.getReplaceRequestCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("REPLACEREQUESTCOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty cannot be missed in a replace request order", result.getRejectReason());		
	}
	
	@Test
	public void testReplaceRequestCompulsoryFieldCheckingMissingClOrdId() {
		OrderValidationRule r = validator.getReplaceRequestCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(35, "G");
		oe.put(38, 100);
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("REPLACEREQUESTCOMPULSORYFIELDCHECKING->Tag 11: ClOrdId, Tag 38: OrderQty cannot be missed in a replace request order", result.getRejectReason());		
	}
	
	@Test
	public void testCancelRequestCompulsoryFieldCheckingValid() {
		OrderValidationRule r = validator.getCancelRequestCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testCancelRequestCompulsoryFieldCheckingMissingClOrdId() {
		OrderValidationRule r = validator.getCancelRequestCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(35, "F");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("CANCELREQUESTCOMPULSORYFIELDCHECKING->Tag 11: ClOrdId cannot be missed in a cancel request order. ", result.getRejectReason());		
	}
	
	@Test
	public void testNewOrderSingleClientOrderId() {
		OrderValidationRule r = validator.getNewOrderSingleClientOrderIdIsNewChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		assertTrue(r.validate(oe).isAccepted());
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return true;
			}
			public OrderEvent getOrder(String clientOrderId) {
				return new OrderEvent();
			}
		});
		
		OrderValidationRule r2 = validator2.getNewOrderSingleClientOrderIdIsNewChecking();
		OrderValidationResult result2 = r2.validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("NEWORDERSINGLECLIENTORDERIDISNEWCHECKING->Tag 11: 1111 is being used in other order. ", result2.getRejectReason());		
		
	}
	
	@Test
	public void testReplaceRequestClientOrderId() {
		OrderValidationRule r = validator.getRequestRequestAndCancelRequestClientOrderIdIsNewChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("REPLACEREQUESTANDCANCELREQUESTCLIENTORDERIDISNEWCHECKING->Tag 11: 1111 is not found. ", result.getRejectReason());
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return true;
			}
			public OrderEvent getOrder(String clientOrderId) {
				return new OrderEvent();
			}
		});
		assertTrue(validator2.getRequestRequestAndCancelRequestClientOrderIdIsNewChecking().validate(oe).isAccepted());		
	}
	
	@Test
	public void testCancelRequestClientOrderId() {
		OrderValidationRule r = validator.getRequestRequestAndCancelRequestClientOrderIdIsNewChecking();
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("REPLACEREQUESTANDCANCELREQUESTCLIENTORDERIDISNEWCHECKING->Tag 11: 1111 is not found. ", result.getRejectReason());
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return true;
			}
			public OrderEvent getOrder(String clientOrderId) {
				return new OrderEvent();
			}
		});
		assertTrue(validator2.getRequestRequestAndCancelRequestClientOrderIdIsNewChecking().validate(oe).isAccepted());		
	}
	
	@Test
	public void testReplaceRequestAmendDownValid() {
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
				oe.put(38, 2000);
				return new OrderEventView(oe);
			}
		});
		
		assertTrue(validator2.getReplaceRequestAmendDownChecking().validate(oe).isAccepted());
	}
	
	@Test
	public void testReplaceRequestAmendUpValid() {
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000);
		
		IncomingOrderValidator validator2 = new IncomingOrderValidator(new IOrderModel() {
			public boolean isClientOrderIdFound(String clientOrderId) {
				return "1111".equals(clientOrderId);
			}
			public OrderEvent getOrder(String clientOrderId) {
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 2000);
				return new OrderEventView(oe);
			}
		});
		
		OrderValidationResult result2 = validator2.getReplaceRequestAmendDownChecking().validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("REPLACEREQUESTAMENDDOWNCHECKING->Tag 38: 3000 is larger/equal to 2000 which is not amend down for replace request order. ", result2.getRejectReason());		
		
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
		
		assertTrue(validator2.getReplaceRequestOtherFieldChangeChecking().validate(oe).isAccepted());
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
		
		assertTrue(validator2.getReplaceRequestOtherFieldChangeChecking().validate(oe).isAccepted());
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
		
		OrderValidationResult result2 = validator2.getReplaceRequestOtherFieldChangeChecking().validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("REPLACEREQUESTOTHERFIELDCHANGECHECKING->Replace request order cannot alter Tag 54: Side, Tag 55: Symbol, Tag 40: OrderType, Tag 44: Price. ", result2.getRejectReason());
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
		
		OrderValidationResult result2 = validator2.getReplaceRequestOtherFieldChangeChecking().validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("REPLACEREQUESTOTHERFIELDCHANGECHECKING->Replace request order cannot alter Tag 54: Side, Tag 55: Symbol, Tag 40: OrderType, Tag 44: Price. ", result2.getRejectReason());
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
		
		OrderValidationResult result2 = validator2.getReplaceRequestOtherFieldChangeChecking().validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("REPLACEREQUESTOTHERFIELDCHANGECHECKING->Replace request order cannot alter Tag 54: Side, Tag 55: Symbol, Tag 40: OrderType, Tag 44: Price. ", result2.getRejectReason());
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
		
		OrderValidationResult result2 = validator2.getReplaceRequestOtherFieldChangeChecking().validate(oe);
		assertFalse(result2.isAccepted());
		assertEquals("REPLACEREQUESTOTHERFIELDCHANGECHECKING->Replace request order cannot alter Tag 54: Side, Tag 55: Symbol, Tag 40: OrderType, Tag 44: Price. ", result2.getRejectReason());
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
	
}
