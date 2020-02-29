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
import com.richardchankiyin.ordermatchingengine.order.model.OrderRepository;
import com.richardchankiyin.ordermatchingengine.order.statemachine.IOrderStateMachine;
import com.richardchankiyin.ordermatchingengine.order.statemachine.OrderStateMachine;
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
			
		}
		, new IPublisher() {
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
		oe.put(55, "0001.HK");
		assertTrue(matchingMgr.getLogonChecking().validate(oe).isAccepted());
	}
	
	@Test
	public void testLogonCheckingMissingLastTradedPrice() {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(55, "0001.HK");
		OrderValidationResult result = matchingMgr.getLogonChecking().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("MATCHMGRLOGONCHECKING->Tag 55 Symbol and Tag 44 Price cannot be missing. ", result.getRejectReason());
	}
	
	@Test
	public void testLogonCheckingMissingSymbol() {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 100);
		OrderValidationResult result = matchingMgr.getLogonChecking().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("MATCHMGRLOGONCHECKING->Tag 55 Symbol and Tag 44 Price cannot be missing. ", result.getRejectReason());
	}
	
	@Test
	public void testLogonCheckingPriceNotNumeric() {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, "100aa");
		oe.put(55, "0001.HK");
		OrderValidationResult result = matchingMgr.getLogonChecking().validate(oe);
		assertFalse(result.isAccepted());
		assertEquals("MATCHMGRLOGONCHECKING->Tag 44: 100aa not a numeric figure. ", result.getRejectReason());
	}
	
	@Test
	public void testLogonCheckingPriceNotPositive() {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 0);
		oe.put(55, "0001.HK");
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
			
		}
		, new IPublisher() {
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
			
		}
		, new IPublisher() {
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
			
		}
		, new IPublisher() {
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
			
		}
		, new IPublisher() {
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
			
		}
		, new IPublisher() {
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
			
		}
		, new IPublisher() {
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
		oe.put(55, "0001.HK");
		queue.start();
		queue.send(oe);
		queue.stop();
		queue.join();
		
		assertTrue(testLogonMatchingManager.isLoggedOn());
		assertEquals(1, publishedOrderEvent.size());
		
	}
	
	@Test
	public void testNosSymbolNotCorrect() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(1);
		MatchingManager testNosManager = new MatchingManager(new OrderStateMachine(orderRepo.getOrderModel(), orderRepo), 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testNosSymbolNotCorrectQueue", testNosManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0001.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "1");
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		queue.stop();
		//queue.join();
		
		assertTrue(testNosManager.isLoggedOn());
		assertEquals(2, publishedOrderEvent.size());
		assertTrue(publishedOrderEvent.get(1).get(58).toString().contains("INCOMINGORDERSYMBOLCHECKING->Symbol: 0005.HK not match with one assigned by login: 0001.HK"));
	}
	
	@Test
	public void testNosMarketOrderNoEnoughQuantity() throws Exception {
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(1);
		MatchingManager testNosManager = new MatchingManager(new OrderStateMachine(orderRepo.getOrderModel(), orderRepo), 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testNosMarketOrderNoEnoughQuantity", testNosManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "1");
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		queue.stop();
		
		assertTrue(testNosManager.isLoggedOn());
		assertEquals(2, publishedOrderEvent.size());
		
		assertTrue(publishedOrderEvent.get(1).get(58).toString().contains("do not have enough quantity. quantity unreserved: 1200"));
	}
	
	@Test
	public void testNosLimitOrderNoExecutionAddedToOrderBook() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(1);
		MatchingManager testNosManager = new MatchingManager(new OrderStateMachine(orderRepo.getOrderModel(), orderRepo), 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testNosLimitOrderNoExecutionAddedToOrderBook", testNosManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		queue.stop();
		
		assertTrue(testNosManager.isLoggedOn());
		assertEquals(2, publishedOrderEvent.size());
		
		assertEquals("8",publishedOrderEvent.get(1).get(35));
		assertEquals(1200L,publishedOrderEvent.get(1).get(38));
	}
	
	@Test
	public void testNosMultipleLimitOrdersNoExecutionAddedToOrderBook() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testNosManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testNosMultipleLimitOrdersNoExecutionAddedToOrderBook", testNosManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 1600L);
		oe3.put(40, "2");
		oe3.put(44, 59.55);
		oe3.put(54, "1");
		oe3.put(55, "0005.HK");
		OrderEvent oe4 = new OrderEvent();
		oe4.put(11, "3333");
		oe4.put(35, "D");
		oe4.put(38, 800L);
		oe4.put(40, "2");
		oe4.put(44, 59.8);
		oe4.put(54, "2");
		oe4.put(55, "0005.HK");
		OrderEvent oe5 = new OrderEvent();
		oe5.put(11, "4444");
		oe5.put(35, "D");
		oe5.put(38, 2000L);
		oe5.put(40, "2");
		oe5.put(44, 59.9);
		oe5.put(54, "2");
		oe5.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		queue.send(oe4);
		queue.send(oe5);
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		queue.stop();
		
		assertTrue(testNosManager.isLoggedOn());
		assertEquals(5, publishedOrderEvent.size());
		
		assertEquals("0", om.getOrderModel().getOrder("1111").get(39));
		assertEquals("0", om.getOrderModel().getOrder("2222").get(39));
		assertEquals("0", om.getOrderModel().getOrder("3333").get(39));
		assertEquals("0", om.getOrderModel().getOrder("4444").get(39));
	}
	
	@Test
	public void testNosMultipleLimitOrdersExecutionsDrivenBySellOrder() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testNosManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testNosMultipleLimitOrdersExecutionsDrivenBySellOrder", testNosManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 1600L);
		oe3.put(40, "2");
		oe3.put(44, 59.55);
		oe3.put(54, "1");
		oe3.put(55, "0005.HK");
		OrderEvent oe4 = new OrderEvent();
		oe4.put(11, "3333");
		oe4.put(35, "D");
		oe4.put(38, 800L);
		oe4.put(40, "2");
		oe4.put(44, 59.6);
		oe4.put(54, "2");
		oe4.put(55, "0005.HK");
		OrderEvent oe5 = new OrderEvent();
		oe5.put(11, "4444");
		oe5.put(35, "D");
		oe5.put(38, 2000L);
		oe5.put(40, "2");
		oe5.put(44, 59.5);
		oe5.put(54, "2");
		oe5.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		queue.send(oe4);
		queue.send(oe5);
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		queue.stop();
		
		assertTrue(testNosManager.isLoggedOn());
		
		logger.debug("orderRepo: {}", orderRepo.getOrderModel());
		
		assertEquals("1", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(400L, om.getOrderModel().getOrder("1111").get(14));
		assertEquals("3", om.getOrderModel().getOrder("2222").get(39));
		assertEquals(1600L, om.getOrderModel().getOrder("2222").get(14));
		assertEquals("0", om.getOrderModel().getOrder("3333").get(39));
		assertEquals("3", om.getOrderModel().getOrder("4444").get(39));
		assertEquals(2000L, om.getOrderModel().getOrder("4444").get(14));
	}
	
	@Test
	public void testNosMultipleLimitOrdersExecutionsDrivenByBuyOrder() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testNosManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
	
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testNosMultipleLimitOrdersExecutionsDrivenByBuyOrder", testNosManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");

		OrderEvent oe1 = new OrderEvent();
		oe1.put(11, "1111");
		oe1.put(35, "D");
		oe1.put(38, 800L);
		oe1.put(40, "2");
		oe1.put(44, 59.6);
		oe1.put(54, "2");
		oe1.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "2222");
		oe2.put(35, "D");
		oe2.put(38, 2000L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "3333");
		oe3.put(35, "D");
		oe3.put(38, 1200L);
		oe3.put(40, "2");
		oe3.put(44, 59.45);
		oe3.put(54, "1");
		oe3.put(55, "0005.HK");
		OrderEvent oe4 = new OrderEvent();
		oe4.put(11, "4444");
		oe4.put(35, "D");
		oe4.put(38, 2400L);
		oe4.put(40, "2");
		oe4.put(44, 59.6);
		oe4.put(54, "1");
		oe4.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe1);
		queue.send(oe2);
		queue.send(oe3);
		queue.send(oe4);
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		queue.stop();
		
		assertTrue(testNosManager.isLoggedOn());
		
		logger.debug("orderRepo: {}", orderRepo.getOrderModel());
		
		assertEquals("1", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(400L, om.getOrderModel().getOrder("1111").get(14));
		assertEquals("3", om.getOrderModel().getOrder("2222").get(39));
		assertEquals(2000L, om.getOrderModel().getOrder("2222").get(14));
		assertEquals("0", om.getOrderModel().getOrder("3333").get(39));
		assertEquals("3", om.getOrderModel().getOrder("4444").get(39));
		assertEquals(2400L, om.getOrderModel().getOrder("4444").get(14));
		
		
		
	}
		
	@Test
	public void testNosMultipleLimitOrdersNoExecutionThenExecutedByBuyMarketOrder() throws Exception {
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testNosManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testNosMultipleLimitOrdersNoExecutionThenExecutedByBuyMarketOrder", testNosManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 1600L);
		oe3.put(40, "2");
		oe3.put(44, 59.55);
		oe3.put(54, "1");
		oe3.put(55, "0005.HK");
		OrderEvent oe4 = new OrderEvent();
		oe4.put(11, "3333");
		oe4.put(35, "D");
		oe4.put(38, 800L);
		oe4.put(40, "2");
		oe4.put(44, 59.8);
		oe4.put(54, "2");
		oe4.put(55, "0005.HK");
		OrderEvent oe5 = new OrderEvent();
		oe5.put(11, "4444");
		oe5.put(35, "D");
		oe5.put(38, 2000L);
		oe5.put(40, "2");
		oe5.put(44, 59.9);
		oe5.put(54, "2");
		oe5.put(55, "0005.HK");
		OrderEvent oe6 = new OrderEvent();
		oe6.put(11, "5555");
		oe6.put(35, "D");
		oe6.put(38, 1200L);
		oe6.put(40, "1");
		oe6.put(54, "1");
		oe6.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		queue.send(oe4);
		queue.send(oe5);
		queue.send(oe6);
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		queue.stop();
		Thread.sleep(1000);
		assertTrue(testNosManager.isLoggedOn());
		assertEquals("0", om.getOrderModel().getOrder("1111").get(39));
		assertEquals("0", om.getOrderModel().getOrder("2222").get(39));
		assertEquals("3", om.getOrderModel().getOrder("3333").get(39));
		assertEquals(800L, om.getOrderModel().getOrder("3333").get(14));
		assertEquals("1", om.getOrderModel().getOrder("4444").get(39));
		assertEquals(400L, om.getOrderModel().getOrder("4444").get(14));
		assertEquals("3", om.getOrderModel().getOrder("5555").get(39));
		assertEquals(1200L, om.getOrderModel().getOrder("5555").get(14));
		
	}
	
	@Test
	public void testNosMultipleLimitOrdersNoExecutionThenExecutedBySellMarketOrder() throws Exception {
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testNosManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testNosMultipleLimitOrdersNoExecutionThenExecutedBySellMarketOrder", testNosManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 1600L);
		oe3.put(40, "2");
		oe3.put(44, 59.55);
		oe3.put(54, "1");
		oe3.put(55, "0005.HK");
		OrderEvent oe4 = new OrderEvent();
		oe4.put(11, "3333");
		oe4.put(35, "D");
		oe4.put(38, 800L);
		oe4.put(40, "2");
		oe4.put(44, 59.8);
		oe4.put(54, "2");
		oe4.put(55, "0005.HK");
		OrderEvent oe5 = new OrderEvent();
		oe5.put(11, "4444");
		oe5.put(35, "D");
		oe5.put(38, 2000L);
		oe5.put(40, "2");
		oe5.put(44, 59.9);
		oe5.put(54, "2");
		oe5.put(55, "0005.HK");
		OrderEvent oe6 = new OrderEvent();
		oe6.put(11, "5555");
		oe6.put(35, "D");
		oe6.put(38, 2000L);
		oe6.put(40, "1");
		oe6.put(54, "2");
		oe6.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		queue.send(oe4);
		queue.send(oe5);
		queue.send(oe6);
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		queue.stop();
		Thread.sleep(1000);
		assertTrue(testNosManager.isLoggedOn());
		assertEquals("1", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(400L, om.getOrderModel().getOrder("1111").get(14));
		assertEquals("3", om.getOrderModel().getOrder("2222").get(39));
		assertEquals(1600L, om.getOrderModel().getOrder("2222").get(14));
		assertEquals("0", om.getOrderModel().getOrder("3333").get(39));
		assertEquals("0", om.getOrderModel().getOrder("4444").get(39));
		assertEquals("3", om.getOrderModel().getOrder("5555").get(39));
		assertEquals(2000L, om.getOrderModel().getOrder("5555").get(14));
	}
	
	@Test
	public void testNosBuyOrderNoExecutionAddedToOrderBookThenPartialFilledFinallyFullFilled() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testNosManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testNosBuyOrderNoExecutionAddedToOrderBookThenPartialFilledFinallyFullFilled", testNosManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 400L);
		oe3.put(40, "2");
		oe3.put(44, 59.5);
		oe3.put(54, "2");
		oe3.put(55, "0005.HK");
		OrderEvent oe4 = new OrderEvent();
		oe4.put(11, "3333");
		oe4.put(35, "D");
		oe4.put(38, 1600L);
		oe4.put(40, "2");
		oe4.put(44, 59.5);
		oe4.put(54, "2");
		oe4.put(55, "0005.HK");
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		Thread.sleep(1000);
		
		assertTrue(testNosManager.isLoggedOn());
		assertEquals("1", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(400L, om.getOrderModel().getOrder("1111").get(14));
		
		queue.send(oe4);
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		queue.stop();
		Thread.sleep(1000);
		logger.debug("orderRepo: {}", orderRepo.getOrderModel());
		assertEquals("3", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(1200L, om.getOrderModel().getOrder("1111").get(14));
		
		
	}
	
	@Test
	public void testNosSellOrderNoExecutionAddedToOrderBookThenPartialFilledFinallyFullFilled() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testNosManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testNosBuyOrderNoExecutionAddedToOrderBookThenPartialFilledFinallyFullFilled", testNosManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 400L);
		oe3.put(40, "2");
		oe3.put(44, 59.5);
		oe3.put(54, "1");
		oe3.put(55, "0005.HK");
		OrderEvent oe4 = new OrderEvent();
		oe4.put(11, "3333");
		oe4.put(35, "D");
		oe4.put(38, 1600L);
		oe4.put(40, "2");
		oe4.put(44, 59.5);
		oe4.put(54, "1");
		oe4.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		Thread.sleep(1000);
		
		assertTrue(testNosManager.isLoggedOn());
		assertEquals("1", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(400L, om.getOrderModel().getOrder("1111").get(14));
		
		queue.send(oe4);
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		queue.stop();
		Thread.sleep(1000);
		logger.debug("orderRepo: {}", orderRepo.getOrderModel());
		assertEquals("3", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(1200L, om.getOrderModel().getOrder("1111").get(14));
	}
	
	@Test
	public void testReplaceRequestOrderNotFound() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testReplaceRequestManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testReplaceRequestOrderNotFound", testReplaceRequestManager, 2);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "G");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}		
		queue.stop();

		
		assertTrue(testReplaceRequestManager.isLoggedOn());
		assertTrue(publishedOrderEvent.get(1).get(58).toString().contains("REPLACEREQUESTANDCANCELREQUESTCLIENTORDERIDISNEWCHECKING->Tag 11: 1111 is not found. "));
		
	}
	
	@Test
	public void testReplaceRequestOrderAmendUpReject() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testReplaceRequestManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testReplaceRequestOrderAmendUpReject", testReplaceRequestManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		logger.debug("orderRepo before replace request: {}", orderRepo.getOrderModel());
		assertTrue(testReplaceRequestManager.isLoggedOn());
		assertEquals("0", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(1200L, om.getOrderModel().getOrder("1111").get(38));
		
		oe2.put(35, "G");
		oe2.put(38, 2000L);
		queue.send(oe2);

		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		logger.debug("orderRepo after replace request: {}", orderRepo.getOrderModel());
		
		assertEquals("0", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(1200L, om.getOrderModel().getOrder("1111").get(38));
		
		Thread.sleep(500);
		queue.stop();
		
		logger.debug("publishedOrderEvent: {}", publishedOrderEvent);
		String expectedStr = "REPLACEREQUESTAMENDDOWNCHECKING->Tag 38: 2000 is larger/equal to 1200 which is not amend down for replace request order.";
		assertTrue(publishedOrderEvent.get(2).get(58).toString().contains(expectedStr));
	}
	
	@Test
	public void testReplaceRequestOrderPriceChangeReject() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testReplaceRequestManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testReplaceRequestOrderPriceChangeReject", testReplaceRequestManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		logger.debug("orderRepo before replace request: {}", orderRepo.getOrderModel());
		assertTrue(testReplaceRequestManager.isLoggedOn());
		assertEquals("0", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(1200L, om.getOrderModel().getOrder("1111").get(38));
		
		oe2.put(35, "G");
		oe2.put(44, 60);
		queue.send(oe2);

		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		logger.debug("orderRepo after replace request: {}", orderRepo.getOrderModel());
		
		assertEquals("0", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(59.5, om.getOrderModel().getOrder("1111").get(44));
		
		Thread.sleep(500);
		queue.stop();
		
		logger.debug("publishedOrderEvent: {}", publishedOrderEvent);
		String expectedStr = "REPLACEREQUESTOTHERFIELDCHANGECHECKING->Replace request order cannot alter Tag 54: Side, Tag 55: Symbol, Tag 40: OrderType, Tag 44: Price.";
		assertTrue(publishedOrderEvent.get(2).get(58).toString().contains(expectedStr));
	}
	
	@Test
	public void testReplaceRequestOrderBuyAtNewState() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testReplaceRequestManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testReplaceRequestOrderBuyAtNewState", testReplaceRequestManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		logger.debug("orderRepo before replace request: {}", orderRepo.getOrderModel());
		assertTrue(testReplaceRequestManager.isLoggedOn());
		assertEquals("0", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(1200L, om.getOrderModel().getOrder("1111").get(38));
		
		oe2.put(35, "G");
		oe2.put(38, 800L);
		queue.send(oe2);

		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		Thread.sleep(500);
		
		logger.debug("orderRepo after replace request: {}", orderRepo.getOrderModel());
		
		assertEquals("0", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(800L, om.getOrderModel().getOrder("1111").get(38));
		
		queue.stop();
	}
	
	
	@Test
	public void testReplaceRequestOrderSellAtNewState() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testReplaceRequestManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testReplaceRequestOrderSellAtNewState", testReplaceRequestManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		logger.debug("orderRepo before replace request: {}", orderRepo.getOrderModel());
		assertTrue(testReplaceRequestManager.isLoggedOn());
		assertEquals("0", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(1200L, om.getOrderModel().getOrder("1111").get(38));
		
		oe2.put(35, "G");
		oe2.put(38, 800L);
		queue.send(oe2);

		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		Thread.sleep(500);
		
		logger.debug("orderRepo after replace request: {}", orderRepo.getOrderModel());
		
		assertEquals("0", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(800L, om.getOrderModel().getOrder("1111").get(38));
		
		queue.stop();
	}
	
	
	@Test
	public void testReplaceRequestPartiallyFilledBuyOrder() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testReplaceRequestManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testReplaceRequestPartiallyFilledBuyOrder", testReplaceRequestManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 2000L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 400L);
		oe3.put(40, "2");
		oe3.put(44, 59.5);
		oe3.put(54, "2");
		oe3.put(55, "0005.HK");
		
		queue.start();		
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		assertTrue(testReplaceRequestManager.isLoggedOn());
		assertEquals("1", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(2000L, om.getOrderModel().getOrder("1111").get(38));
		assertEquals(400L, om.getOrderModel().getOrder("1111").get(14));
		
		oe2.put(35, "G");
		oe2.put(38, 1200L);

		logger.debug("orderRepo before replace request: {}", orderRepo.getOrderModel());
		
		queue.send(oe2);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		Thread.sleep(1000);
		
		logger.debug("orderRepo after replace request: {}", orderRepo.getOrderModel());
		
		assertEquals("1", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(1200L, om.getOrderModel().getOrder("1111").get(38));
		assertEquals(400L, om.getOrderModel().getOrder("1111").get(14));
		
	}
	
	@Test
	public void testReplaceRequestPartiallyFilledSellOrder() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testReplaceRequestManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testReplaceRequestPartiallyFilledSellOrder", testReplaceRequestManager, 10);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 2000L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 400L);
		oe3.put(40, "1");
		oe3.put(44, 59.5);
		oe3.put(54, "1");
		oe3.put(55, "0005.HK");
		
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		assertTrue(testReplaceRequestManager.isLoggedOn());
		assertEquals("1", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(2000L, om.getOrderModel().getOrder("1111").get(38));
		assertEquals(400L, om.getOrderModel().getOrder("1111").get(14));
		
		oe2.put(35, "G");
		oe2.put(38, 1200L);

		logger.debug("orderRepo before replace request: {}", orderRepo.getOrderModel());
		
		queue.send(oe2);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		Thread.sleep(1000);
		
		logger.debug("orderRepo after replace request: {}", orderRepo.getOrderModel());
		
		assertEquals("1", om.getOrderModel().getOrder("1111").get(39));
		assertEquals(1200L, om.getOrderModel().getOrder("1111").get(38));
		assertEquals(400L, om.getOrderModel().getOrder("1111").get(14));
	}
	
	@Test
	public void testReplaceRequestOrderAlreadyDoneForDayReject() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testCancelManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testReplaceRequestOrderAlreadyDoneForDayReject", testCancelManager, 2);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 1200L);
		oe3.put(40, "2");
		oe3.put(44, 59.5);
		oe3.put(54, "1");
		oe3.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		assertTrue(testCancelManager.isLoggedOn());
		
		oe2.put(35, "G");
		oe2.put(38, 800L);
		queue.send(oe2);
		
		Thread.sleep(500);
		queue.stop();
		
		logger.debug("publishedOrderEvent: {}", publishedOrderEvent);
		String expectedStr = "Replacing Request/Cancelling an order with status: 3 not allowed. ";
		assertTrue(publishedOrderEvent.get(publishedOrderEvent.size() - 1).get(58).toString().contains(expectedStr));
	}
	
	
	@Test
	public void testCancelOrderNotFoundReject() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testCancelManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testCancelOrderNotFoundReject", testCancelManager, 2);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(55, "0005.HK");
		oe2.put(35, "F");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		assertTrue(testCancelManager.isLoggedOn());
		
		Thread.sleep(500);
		queue.stop();
		
		logger.debug("publishedOrderEvent: {}", publishedOrderEvent);
		String expectedStr = "REPLACEREQUESTANDCANCELREQUESTCLIENTORDERIDISNEWCHECKING->Tag 11: 1111 is not found. ";
		assertTrue(publishedOrderEvent.get(1).get(58).toString().contains(expectedStr));
	}
	
	@Test
	public void testCancelOrderAlreadyDoneForDayReject() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testCancelManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testCancelOrderAlreadyDoneForDayReject", testCancelManager, 2);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 1200L);
		oe3.put(40, "2");
		oe3.put(44, 59.5);
		oe3.put(54, "1");
		oe3.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		assertTrue(testCancelManager.isLoggedOn());
		
		oe2.put(35, "F");
		queue.send(oe2);
		
		Thread.sleep(500);
		queue.stop();
		
		logger.debug("publishedOrderEvent: {}", publishedOrderEvent);
		String expectedStr = "Replacing Request/Cancelling an order with status: 3 not allowed. ";
		assertTrue(publishedOrderEvent.get(publishedOrderEvent.size() - 1).get(58).toString().contains(expectedStr));
	}
	
	@Test
	public void testCancelBuyOrderAtNewStatus() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testCancelManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testCancelBuyOrderAtNewStatus", testCancelManager, 2);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		assertTrue(testCancelManager.isLoggedOn());
		
		oe2.put(35, "F");
		queue.send(oe2);
		
		Thread.sleep(500);
		queue.stop();
		
		logger.debug("publishedOrderEvent: {}", publishedOrderEvent);
		
		assertEquals("4", publishedOrderEvent.get(publishedOrderEvent.size() - 1).get(39));
	}
	
	@Test
	public void testCancelSellOrderAtNewStatus() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testCancelManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testCancelSellOrderAtNewStatus", testCancelManager, 2);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		assertTrue(testCancelManager.isLoggedOn());
		
		oe2.put(35, "F");
		queue.send(oe2);
		
		Thread.sleep(500);
		queue.stop();
		
		logger.debug("publishedOrderEvent: {}", publishedOrderEvent);
		
		assertEquals("4", publishedOrderEvent.get(publishedOrderEvent.size() - 1).get(39));
	}
	
	@Test
	public void testCancelBuyOrderAtPartialFilledStatus() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testCancelManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testCancelBuyOrderAtPartialFilledStatus", testCancelManager, 2);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "1");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 800L);
		oe3.put(40, "2");
		oe3.put(44, 59.5);
		oe3.put(54, "2");
		oe3.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		assertTrue(testCancelManager.isLoggedOn());
		
		oe2.put(35, "F");
		queue.send(oe2);
		
		Thread.sleep(500);
		queue.stop();
		
		logger.debug("publishedOrderEvent: {}", publishedOrderEvent);
		
		OrderEvent executionReportEvent = publishedOrderEvent.get(publishedOrderEvent.size() - 2);
		OrderEvent dfdEvent = publishedOrderEvent.get(publishedOrderEvent.size() - 1);
		
		assertEquals(1200L, executionReportEvent.get(38));
		assertEquals(800L, executionReportEvent.get(14));
		assertEquals("2", executionReportEvent.get(39));
		assertEquals("3", dfdEvent.get(39));
	}
	
	
	@Test
	public void testCancelSellOrderAtPartialFilledStatus() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testCancelManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testCancelSellOrderAtPartialFilledStatus", testCancelManager, 2);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 800L);
		oe3.put(40, "2");
		oe3.put(44, 59.5);
		oe3.put(54, "1");
		oe3.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		assertTrue(testCancelManager.isLoggedOn());
		
		oe2.put(35, "F");
		queue.send(oe2);
		
		Thread.sleep(500);
		queue.stop();
		
		logger.debug("publishedOrderEvent: {}", publishedOrderEvent);
		
		OrderEvent executionReportEvent = publishedOrderEvent.get(publishedOrderEvent.size() - 2);
		OrderEvent dfdEvent = publishedOrderEvent.get(publishedOrderEvent.size() - 1);
		
		assertEquals(1200L, executionReportEvent.get(38));
		assertEquals(800L, executionReportEvent.get(14));
		assertEquals("2", executionReportEvent.get(39));
		assertEquals("3", dfdEvent.get(39));
	}
	
	@Test
	public void testLogoff() throws Exception{
		List<OrderEvent> publishedOrderEvent = new ArrayList<>();
		OrderRepository orderRepo = new OrderRepository(10);
		OrderStateMachine om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		MatchingManager testLogoffManager = new MatchingManager(om, 
				new IPublisher() {
			@Override
			public void publish(OrderEvent oe) {
				publishedOrderEvent.add(oe);
				logger.debug("publish event: {}", oe);
			}			
		});
		OrderMessageQueueForTest queue = new OrderMessageQueueForTest("testLogoff", testLogoffManager, 2);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, 60);
		oe.put(55, "0005.HK");
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "1111");
		oe2.put(35, "D");
		oe2.put(38, 1200L);
		oe2.put(40, "2");
		oe2.put(44, 59.5);
		oe2.put(54, "2");
		oe2.put(55, "0005.HK");
		OrderEvent oe3 = new OrderEvent();
		oe3.put(11, "2222");
		oe3.put(35, "D");
		oe3.put(38, 1200L);
		oe3.put(40, "2");
		oe3.put(44, 59.5);
		oe3.put(54, "2");
		oe3.put(55, "0005.HK");
		
		queue.start();
		queue.send(oe);
		queue.send(oe2);
		queue.send(oe3);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		assertTrue(testLogoffManager.isLoggedOn());
		
		OrderEvent oe4 = new OrderEvent();
		oe4.put(35, "5");
		
		queue.send(oe4);
		
		while (queue.getQueueSize() > 0) {
			Thread.sleep(500);
		}
		
		Thread.sleep(500);
		
		assertFalse(testLogoffManager.isLoggedOn());
		
		queue.stop();
		
		logger.debug("publishedOrderEvent: {}", publishedOrderEvent);
		assertEquals("1111", publishedOrderEvent.get(publishedOrderEvent.size() - 2).get(11));
		assertEquals("4", publishedOrderEvent.get(publishedOrderEvent.size() - 2).get(39));
		assertEquals("2222", publishedOrderEvent.get(publishedOrderEvent.size() - 1).get(11));
		assertEquals("4", publishedOrderEvent.get(publishedOrderEvent.size() - 1).get(39));
	}
}
