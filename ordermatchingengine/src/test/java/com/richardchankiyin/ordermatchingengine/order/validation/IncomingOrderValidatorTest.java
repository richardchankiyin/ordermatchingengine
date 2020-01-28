package com.richardchankiyin.ordermatchingengine.order.validation;

import static org.junit.Assert.*;

import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class IncomingOrderValidatorTest {

	@Test
	public void testDataTypeCheckingRuleTag38AndTag44Valid() {
		OrderValidationRule r = IncomingOrderValidator.getInstance().getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(38, 111);
		oe.put(44, 111.5);
		
		assertTrue(r.validate(oe).isAccepted());
		
	}
	
	@Test
	public void testDataTypeCheckingRuleTag38ValidAndTag44Missing() {
		OrderValidationRule r = IncomingOrderValidator.getInstance().getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(38, 111);
		
		assertTrue(r.validate(oe).isAccepted());
		
	}
	
	@Test
	public void testDataTypeCheckingRuleTag38MissingAndTag44Missing() {
		OrderValidationRule r = IncomingOrderValidator.getInstance().getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testDataTypeCheckingRuleTag38NotValidTag44Valid() {
		OrderValidationRule r = IncomingOrderValidator.getInstance().getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(38, "111a");
		oe.put(44, 111.5);
		
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("DATATYPECHECKING->Tag 38: 111a is not integer. ", result.getRejectReason());
	}
	
	@Test
	public void testDataTypeCheckingRuleTag38ValidTag44NotValid() {
		OrderValidationRule r = IncomingOrderValidator.getInstance().getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(38, 111);
		oe.put(44, "111.5a");
		
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("DATATYPECHECKING->Tag 44: 111.5a is not numeric. ", result.getRejectReason());
		
	}
	
	@Test
	public void testDataTypeCheckingRuleTag38NotValidTag44NotValid() {
		OrderValidationRule r = IncomingOrderValidator.getInstance().getDataTypeCheckingRule();
		OrderEvent oe = new OrderEvent();
		oe.put(38, "111a");
		oe.put(44, "111.5a");
		
		OrderValidationResult result = r.validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("DATATYPECHECKING->Tag 38: 111a is not integer. Tag 44: 111.5a is not numeric. ", result.getRejectReason());
	}

	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleSkippingNonNOS() {
		OrderValidationRule r = IncomingOrderValidator.getInstance().getNewOrderSingleCompulsoryFieldChecking();
		OrderEvent oe = new OrderEvent();
		assertTrue(r.validate(oe).isAccepted());
	}
	
	@Test
	public void testNewOrderSingleCompulsoryFieldCheckingRuleMarketOrderValid() {
		OrderValidationRule r = IncomingOrderValidator.getInstance().getNewOrderSingleCompulsoryFieldChecking();
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
		OrderValidationRule r = IncomingOrderValidator.getInstance().getNewOrderSingleCompulsoryFieldChecking();
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
		OrderValidationRule r = IncomingOrderValidator.getInstance().getNewOrderSingleCompulsoryFieldChecking();
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
		OrderValidationRule r = IncomingOrderValidator.getInstance().getNewOrderSingleCompulsoryFieldChecking();
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
		OrderValidationRule r = IncomingOrderValidator.getInstance().getNewOrderSingleCompulsoryFieldChecking();
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
		OrderValidationRule r = IncomingOrderValidator.getInstance().getNewOrderSingleCompulsoryFieldChecking();
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
		OrderValidationRule r = IncomingOrderValidator.getInstance().getNewOrderSingleCompulsoryFieldChecking();
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
	
}
