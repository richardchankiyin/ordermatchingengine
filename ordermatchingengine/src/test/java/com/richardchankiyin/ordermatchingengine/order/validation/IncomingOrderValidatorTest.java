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

}
