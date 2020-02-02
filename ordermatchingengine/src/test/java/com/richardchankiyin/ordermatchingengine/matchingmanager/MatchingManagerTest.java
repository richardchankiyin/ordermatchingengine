package com.richardchankiyin.ordermatchingengine.matchingmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.OrderMessageQueueForTest;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;
import com.richardchankiyin.ordermatchingengine.order.statemachine.IOrderStateMachine;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationRule;
import com.richardchankiyin.ordermatchingengine.publisher.IPublisher;

public class MatchingManagerTest {
	private static final Logger logger = LoggerFactory.getLogger(MatchingManagerTest.class);
	
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
	
	private OrderValidationResult getMessageTypeOrderValidationResult(String msgType) {
		OrderValidationRule r = matchingMgr.getMsgTypeChecking();
		OrderEvent oe = new OrderEvent();
		if (msgType != null)
			oe.put(35, msgType);
		return r.validate(oe);
	}
	
	@Test
	public void testMsgTypeCheckingAccepted() {
		getMessageTypeOrderValidationResult("A").isAccepted();
		getMessageTypeOrderValidationResult("5").isAccepted();
		getMessageTypeOrderValidationResult("D").isAccepted();
		getMessageTypeOrderValidationResult("F").isAccepted();
		getMessageTypeOrderValidationResult("G").isAccepted();
	}
	
	@Test
	public void testMsgTypeCheckingRejected() {
		OrderValidationResult result = getMessageTypeOrderValidationResult("0");
		assertFalse(result.isAccepted());
		assertEquals("MATCHMGRMSGTYPECHECKING->Tag 35: 0 not accepted. Only accepts: [A, D, 5, F, G] .", result.getRejectReason());
		
		OrderValidationResult result2 = getMessageTypeOrderValidationResult("1");
		assertFalse(result2.isAccepted());
		assertEquals("MATCHMGRMSGTYPECHECKING->Tag 35: 1 not accepted. Only accepts: [A, D, 5, F, G] .", result2.getRejectReason());
		
		OrderValidationResult result3 = getMessageTypeOrderValidationResult(null);
		assertFalse(result3.isAccepted());
		assertEquals("MATCHMGRMSGTYPECHECKING->Type 35 msg type is missing. ", result3.getRejectReason());
		
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

	@Test
	public void testLogoutCheckingMgrOnLoggedOnMgr() {
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
		oe.put(35, "5");
		assertTrue(loggedOnMgr.getLogoutChecking().validate(oe).isAccepted());
	}
	
	@Test
	public void testLogoutCheckingMgrOnNotLoggedOnMgr() {
		MatchingManager loggedOutMgr = new MatchingManager(new IOrderStateMachine() {

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
			public boolean isLoggedOn() { return false; }
		};
		
		OrderEvent oe = new OrderEvent();
		oe.put(35, "5");
		OrderValidationResult result = loggedOutMgr.getLogoutChecking().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("MATCHMGRLOGOUTCHECKING->Tag 35: 5 Logout on a non-logged-in machine rejected. ", result.getRejectReason());
	}
	
	private OrderValidationResult getOrderValidationResultAfterLogon(String msgType) {
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
		
		OrderValidationRule r = loggedOnMgr.getCanAcceptOrderChecking();
		
		OrderEvent oe = new OrderEvent();
		if (msgType != null)
			oe.put(35, msgType);
		return r.validate(oe);
	}
	
	@Test
	public void testCanAcceptOrderAfterLogon() {
		assertTrue(getOrderValidationResultAfterLogon("D").isAccepted());
		assertTrue(getOrderValidationResultAfterLogon("F").isAccepted());
		assertTrue(getOrderValidationResultAfterLogon("G").isAccepted());
	}
	
	private OrderValidationResult getOrderValidationResultBeforeLogon(String msgType) {
		MatchingManager notLoggedOnMgr = new MatchingManager(new IOrderStateMachine() {

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
			public boolean isLoggedOn() { return false; }
		};
		
		OrderValidationRule r = notLoggedOnMgr.getCanAcceptOrderChecking();
		
		OrderEvent oe = new OrderEvent();
		if (msgType != null)
			oe.put(35, msgType);
		return r.validate(oe);
	}
	
	@Test
	public void testCanAcceptOrderBeforeLogon() {
		OrderValidationResult result = getOrderValidationResultBeforeLogon("D");
		assertFalse(result.isAccepted());
		assertEquals("MATCHMGRCANACCEPTORDERCHECKING->Order only accepted after logon. ", result.getRejectReason());
		
		OrderValidationResult result2 = getOrderValidationResultBeforeLogon("F");
		assertFalse(result2.isAccepted());
		assertEquals("MATCHMGRCANACCEPTORDERCHECKING->Order only accepted after logon. ", result2.getRejectReason());
		
		OrderValidationResult result3 = getOrderValidationResultBeforeLogon("G");
		assertFalse(result3.isAccepted());
		assertEquals("MATCHMGRCANACCEPTORDERCHECKING->Order only accepted after logon. ", result3.getRejectReason());
	}
	
	
	@Test
	public void testLogon() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		MatchingManager testLogonMatchingManager = new MatchingManager(new IOrderStateMachine() {

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
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		assertFalse(testLogonMatchingManager.isLoggedOn());
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testLogonQueue", testLogonMatchingManager, 10);
		
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 100);
		oe.put(54, "0001.HK");
		queue.start();
		queue.send(oe);
		queue.stop();
		queue.join();
		
		assertTrue(testLogonMatchingManager.isLoggedOn());
		assertEquals(1, publishedOrderEvent.size());
		
	}

}
