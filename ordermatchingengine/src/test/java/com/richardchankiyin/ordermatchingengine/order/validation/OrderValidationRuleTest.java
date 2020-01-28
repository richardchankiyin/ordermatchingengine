package com.richardchankiyin.ordermatchingengine.order.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class OrderValidationRuleTest {

	@Test
	public void testValidatePassAccepted() {
		OrderValidationRule rule = new OrderValidationRule
				("dummyaccepted", oe -> 
					OrderValidationRuleResult.getAcceptedInstance());
		assertTrue(rule.validate(new OrderEvent()).isAccepted());
	}
	
	@Test
	public void testValidateRejectedWithNameAsReasonPrefix() {
		OrderValidationRule rule = new OrderValidationRule
				("dummyrejected", oe -> 
					new OrderValidationRuleResult("reason"));
		OrderValidationRuleResult r = rule.validate(new OrderEvent());
		assertFalse(r.isAccepted());
		assertEquals("dummyrejected->reason", r.getRejectReason());
	}

}
