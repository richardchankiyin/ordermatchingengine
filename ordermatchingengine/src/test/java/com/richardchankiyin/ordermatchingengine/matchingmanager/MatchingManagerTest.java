package com.richardchankiyin.ordermatchingengine.matchingmanager;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;
import com.richardchankiyin.ordermatchingengine.order.statemachine.IOrderStateMachine;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;
import com.richardchankiyin.ordermatchingengine.publisher.IPublisher;

public class MatchingManagerTest {
	
	private MatchingManager matchingMgr = null; 

	@Before
	public void setup() {
		matchingMgr = new MatchingManager(new IOrderStateMachine() {

			@Override
			public IOrderModel getOrderModel() {
				return null;
			}

			@Override
			public OrderValidationResult handleEvent(OrderEvent oe) {
				return null;
			}
			
		}, new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
			}
			
		});
	}
	
	@Test
	public void testLogonCheckingAccepted() {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 100);
		oe.put(54, "0001.HK");
		assertTrue(matchingMgr.getLogonChecking().validate(oe).isAccepted());
	}
	
	@Test
	public void testLogonCheckingMissingLastTradedPrice() {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(54, "0001.HK");
		OrderValidationResult result = matchingMgr.getLogonChecking().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("MATCHMGRLOGONCHECKING->Tag 54 Symbol and Tag 44 Price cannot be missing. ", result.getRejectReason());
	}
	
	@Test
	public void testLogonCheckingMissingSymbol() {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 100);
		OrderValidationResult result = matchingMgr.getLogonChecking().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("MATCHMGRLOGONCHECKING->Tag 54 Symbol and Tag 44 Price cannot be missing. ", result.getRejectReason());
	}
	
	@Test
	public void testLogonCheckingPriceNotNumeric() {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, "100aa");
		oe.put(54, "0001.HK");
		OrderValidationResult result = matchingMgr.getLogonChecking().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("MATCHMGRLOGONCHECKING->Tag 44: 100aa not a numeric figure. ", result.getRejectReason());
	}
	
	@Test
	public void testLogonCheckingPriceNotPositive() {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 0);
		oe.put(54, "0001.HK");
		OrderValidationResult result = matchingMgr.getLogonChecking().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("MATCHMGRLOGONCHECKING->Tag 44: 0 is not positive. ", result.getRejectReason());
	}
	
	@Test
	public void testLogonCheckingMgrLoggedOnAlready() {
		MatchingManager loggedOnMgr = new MatchingManager(new IOrderStateMachine() {

			@Override
			public IOrderModel getOrderModel() {
				return null;
			}

			@Override
			public OrderValidationResult handleEvent(OrderEvent oe) {
				return null;
			}
			
		}, new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
			}
			
		}) {
			public boolean isLoggedOn() { return true; }
		};
		
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 100);
		oe.put(54, "0001.HK");
		
		OrderValidationResult result = loggedOnMgr.getLogonChecking().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("MATCHMGRLOGONCHECKING->Tag 35: A Logon rejected as it is logged on. ", result.getRejectReason());

	}


}
