package com.richardchankiyin.ordermatchingengine.order.validation;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class AbstractOrderValidatorTest {

	@Test
	public void testValidateSingleRuleAccepted() {
		AbstractOrderValidator validator = new AbstractOrderValidator() {
			protected List<IOrderValidator> getListOfOrderValidators() {
				return Arrays.asList(new OrderValidationRule
				("dummyaccepted", oe -> 
					OrderValidationResult.getAcceptedInstance()));
			}
		};
		assertTrue(validator.validate(new OrderEvent()).isAccepted());
	}
	
	@Test
	public void testValidateSingleRuleRejected() {
		AbstractOrderValidator validator = new AbstractOrderValidator() {
			protected List<IOrderValidator> getListOfOrderValidators() {
				return Arrays.asList(new OrderValidationRule
						("dummyrejected", oe -> 
						new OrderValidationResult("reason")));
			}
		};
		OrderValidationResult r = validator.validate(new OrderEvent());
		assertFalse(r.isAccepted());
		assertEquals("dummyrejected->reason|",r.getRejectReason());
		
	}
	
	@Test
	public void testValidateMultipleRulesAllAccepted() {
		AbstractOrderValidator validator = new AbstractOrderValidator() {
			protected List<IOrderValidator> getListOfOrderValidators() {
				return Arrays.asList(
						new OrderValidationRule("dummyaccepted1", oe -> 
							OrderValidationResult.getAcceptedInstance())
						, new OrderValidationRule("dummyaccepted2", oe -> 
						OrderValidationResult.getAcceptedInstance())
						);
			}
		};
		assertTrue(validator.validate(new OrderEvent()).isAccepted());
	}
	
	@Test
	public void testValidateMultipleRulesAllRejected() {
		AbstractOrderValidator validator = new AbstractOrderValidator() {
			protected List<IOrderValidator> getListOfOrderValidators() {
				return Arrays.asList(
					new OrderValidationRule
						("dummyrejected1", oe -> 
							new OrderValidationResult("reason1"))
					, new OrderValidationRule
						("dummyrejected2", oe -> 
							new OrderValidationResult("reason2"))
				);
			}
		};
		OrderValidationResult r = validator.validate(new OrderEvent());
		assertFalse(r.isAccepted());
		assertEquals("dummyrejected1->reason1|dummyrejected2->reason2|"
				,r.getRejectReason());
	}
	
	@Test
	public void testValidateMultipleRulesSomeAcceptedSomeRejectedWillBeRejected() {
		AbstractOrderValidator validator = new AbstractOrderValidator() {
			protected List<IOrderValidator> getListOfOrderValidators() {
				return Arrays.asList(
					new OrderValidationRule("dummyaccepted1", oe -> 
						OrderValidationResult.getAcceptedInstance())						
					, new OrderValidationRule
						("dummyrejected1", oe -> 
							new OrderValidationResult("reason1"))
					, new OrderValidationRule("dummyaccepted2", oe -> 
						OrderValidationResult.getAcceptedInstance())	
					, new OrderValidationRule
						("dummyrejected2", oe -> 
							new OrderValidationResult("reason2"))
				);
			}
		};
		OrderValidationResult r = validator.validate(new OrderEvent());
		assertFalse(r.isAccepted());
		assertEquals("dummyrejected1->reason1|dummyrejected2->reason2|"
				,r.getRejectReason());
	}

}
