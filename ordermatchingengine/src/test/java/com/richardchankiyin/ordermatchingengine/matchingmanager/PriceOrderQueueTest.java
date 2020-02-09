package com.richardchankiyin.ordermatchingengine.matchingmanager;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class PriceOrderQueueTest {

	@Test(expected=IllegalArgumentException.class)
	public void testPriceOrderQueuePriceNegative() {
		new PriceOrderQueue(0, true);
	}
	
	@Test
	public void testPriceOrderQueueSuccess() {
		PriceOrderQueue poq = new PriceOrderQueue(20.5, true);
		assertTrue(20.5 == poq.getOrderPrice());
		assertTrue(poq.isBuy());
		assertEquals(0, poq.getTotalOrderQuantity());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderClOrdIdMissing() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderMsgTypeMissing() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderMsgTypeReplaceRequestReject() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderMsgTypeCancelRequestReject() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderQtyMissing() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderQtyNonPositive() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 0);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderQtyNotInteger() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, "xxx");
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderCumQtyNonPositive() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(14, -30);
		oe.put(35, "D");
		oe.put(38, 1000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderCumQtyNotInteger() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(14, "xxx");
		oe.put(35, "D");
		oe.put(38, 1000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderCumQtyEqualsToQty() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(14, 1000);
		oe.put(35, "D");
		oe.put(38, 1000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderCumQtyLargerThanQty() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(14, 2000);
		oe.put(35, "D");
		oe.put(38, 1000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderPriceMissing() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderPriceNotMatchConstructorPrice() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, -30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderSideMissing() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderBuySideNotMatch() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "2");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderSellSideNotMatch() {
		PriceOrderQueue poq = new PriceOrderQueue(30, false);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderDuplicateClOrdId() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
	}
	
	@Test
	public void testAddOrderStatusNew() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		assertTrue(30 == poq.getOrderPrice());
		assertTrue(poq.isBuy());
		assertEquals(2000, poq.getTotalOrderQuantity());
		assertEquals(1, poq.getQueueSize());
		assertNull(poq.getOrderEventInternalMap().get("1111").get(35));
		assertEquals("0", poq.getOrderEventInternalMap().get("1111").get(39));
				
		
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "2222");
		oe2.put(35, "D");
		oe2.put(38, 1000);
		oe2.put(44, 30);
		oe2.put(54, "1");
		poq.addOrder(oe2);
		
		assertTrue(30 == poq.getOrderPrice());
		assertTrue(poq.isBuy());
		assertEquals(3000, poq.getTotalOrderQuantity());
		assertEquals(2, poq.getQueueSize());
		assertNull(poq.getOrderEventInternalMap().get("2222").get(35));
		assertEquals("0", poq.getOrderEventInternalMap().get("2222").get(39));
	}
	
	@Test
	public void testAddOrderPartialFilled() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(14, 1000);
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		assertTrue(30 == poq.getOrderPrice());
		assertTrue(poq.isBuy());
		assertEquals(1000, poq.getTotalOrderQuantity());
		assertEquals(1, poq.getQueueSize());
		assertNull(poq.getOrderEventInternalMap().get("1111").get(35));
		assertEquals("1", poq.getOrderEventInternalMap().get("1111").get(39));
		
		
		OrderEvent oe2 = new OrderEvent();
		oe2.put(11, "2222");
		oe2.put(14, 3000);
		oe2.put(35, "D");
		oe2.put(38, 5000);
		oe2.put(44, 30);
		oe2.put(54, "1");
		poq.addOrder(oe2);
		
		assertTrue(30 == poq.getOrderPrice());
		assertTrue(poq.isBuy());
		assertEquals(3000, poq.getTotalOrderQuantity());
		assertEquals(2, poq.getQueueSize());
		assertNull(poq.getOrderEventInternalMap().get("2222").get(35));
		assertEquals("1", poq.getOrderEventInternalMap().get("2222").get(39));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderClOrdIdNotFound() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "G");
		oe.put(38, 1000);
		oe.put(44, 30);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderMsgTypeNewOrderSingle() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 1000);
		oe.put(44, 30);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderMsgTypeCancelOrder() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		oe.put(38, 1000);
		oe.put(44, 30);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderQtyMissing() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(44, 30);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderQtyNoChange() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderQtyAmendUp() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000);
		oe.put(44, 30);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderQtyLargerThanRemainingQty() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true) {
			protected Map<String, OrderEvent> getOrderEventInternalMap() {
				Map<String, OrderEvent> map = new HashMap<String, OrderEvent>();
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 3000);
				oe.put(39, "1");
				oe.put(44, 30);
				oe.put(54, "1");
				oe.put(14, 2000);
				map.put("1111", oe);
				return map;
			}
		};
		
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderQtyEqualsToRemainingQty() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true) {
			protected Map<String, OrderEvent> getOrderEventInternalMap() {
				Map<String, OrderEvent> map = new HashMap<String, OrderEvent>();
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 3000);
				oe.put(39, "1");
				oe.put(44, 30);
				oe.put(54, "1");
				oe.put(14, 2000);
				map.put("1111", oe);
				return map;
			}
		};
		
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 1000);
		oe.put(44, 30);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderIncomingPriceNotMatch() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 1000);
		oe.put(44, 35);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderIncomingSideNotMatch() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "2");
		
		poq.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderOnCancelledOrder() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true) {
			protected Map<String, OrderEvent> getOrderEventInternalMap() {
				Map<String, OrderEvent> map = new HashMap<String, OrderEvent>();
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 3000);
				oe.put(39, "4");
				oe.put(44, 30);
				oe.put(54, "1");
				oe.put(14, 0);
				map.put("1111", oe);
				return map;
			}
		};
		
		OrderEvent oe = new OrderEvent();
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderOnFullyFilledOrder() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true) {
			protected Map<String, OrderEvent> getOrderEventInternalMap() {
				Map<String, OrderEvent> map = new HashMap<String, OrderEvent>();
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 3000);
				oe.put(39, "4");
				oe.put(44, 30);
				oe.put(54, "2");
				oe.put(14, 3000);
				map.put("1111", oe);
				return map;
			}
		};
		
		OrderEvent oe = new OrderEvent();
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
	}
	
	@Test
	public void testUpdateOrder() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		
		poq.updateOrder(oe);
		
		assertEquals(1, poq.getQueueSize());
		assertEquals(2000, poq.getTotalOrderQuantity());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCancelOrderClOrdIdNotFound() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.cancelOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCancelOrderMsgTypeNotAccepted() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		poq.cancelOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCancelOrderOnCancelledOrder() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true) {
			protected Map<String, OrderEvent> getOrderEventInternalMap() {
				Map<String, OrderEvent> map = new HashMap<String, OrderEvent>();
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 3000);
				oe.put(39, "4");
				oe.put(44, 30);
				oe.put(54, "2");
				oe.put(14, 3000);
				map.put("1111", oe);
				return map;
			}
		}; 
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		poq.cancelOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCancelOrderOnFilledOrder() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true) {
			protected Map<String, OrderEvent> getOrderEventInternalMap() {
				Map<String, OrderEvent> map = new HashMap<String, OrderEvent>();
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(38, 3000);
				oe.put(39, "2");
				oe.put(44, 30);
				oe.put(54, "2");
				oe.put(14, 3000);
				map.put("1111", oe);
				return map;
			}
		}; 
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		poq.cancelOrder(oe);
	}
	
	@Test
	public void testCancelOrderOnNew() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		poq.cancelOrder(oe);
		
		assertEquals(0, poq.getTotalOrderQuantity());
		assertEquals(0, poq.getQueueSize());
		assertEquals(0, poq.getActualOrderQueueSize());
		assertFalse(poq.getOrderEventInternalMap().containsKey("1111"));
	}
	
	@Test
	public void testCancelOrderOnNewNotHousekept() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "D");
		oe.put(38, 5000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "F");
		poq.cancelOrder(oe);
		
		assertEquals(3000, poq.getTotalOrderQuantity());
		assertEquals(1, poq.getQueueSize());
		assertEquals(2, poq.getActualOrderQueueSize());
		assertTrue(poq.getOrderEventInternalMap().containsKey("2222"));
	}
	
	@Test
	public void testCancelOrderOn2NewHousekeptTogetherAtLast() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "D");
		oe.put(38, 5000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "F");
		poq.cancelOrder(oe);
		
		assertEquals(3000, poq.getTotalOrderQuantity());
		assertEquals(1, poq.getQueueSize());
		assertEquals(2, poq.getActualOrderQueueSize());
		assertTrue(poq.getOrderEventInternalMap().containsKey("2222"));
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		poq.cancelOrder(oe);
		
		assertEquals(0, poq.getTotalOrderQuantity());
		assertEquals(0, poq.getQueueSize());
		assertEquals(0, poq.getActualOrderQueueSize());
		assertFalse(poq.getOrderEventInternalMap().containsKey("2222"));
		assertFalse(poq.getOrderEventInternalMap().containsKey("1111"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testExecuteOrderZero() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		poq.executeOrder(0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testExecuteOrderNegative() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		poq.executeOrder(-1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testExecuteOrderInsufficient() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "D");
		oe.put(38, 5000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		poq.executeOrder(10000);
	}
	
	@Test
	public void testExecuteOrderOneOrderAddedFullyExecuted() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		List<OrderEvent> executedList = poq.executeOrder(3000);
		
		assertEquals(0, poq.getTotalOrderQuantity());
		assertEquals(0, poq.getQueueSize());
		assertEquals(0, poq.getActualOrderQueueSize());
		assertFalse(poq.getOrderEventInternalMap().containsKey("1111"));
		assertEquals(1, executedList.size());
		assertEquals("1111", executedList.get(0).get(11));
		assertEquals(3000L, executedList.get(0).get(14));
		assertEquals(3000L,executedList.get(0).get(32));
		assertEquals(3000L,executedList.get(0).get(38));
		assertEquals("2", executedList.get(0).get(39));
	}
	
	@Test
	public void testExecuteOrderOneOrderAddedPartiallyExecuted() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		List<OrderEvent> executedList = poq.executeOrder(1000);
		assertEquals(2000, poq.getTotalOrderQuantity());
		assertEquals(1, poq.getQueueSize());
		assertEquals(1, poq.getActualOrderQueueSize());
		assertTrue(poq.getOrderEventInternalMap().containsKey("1111"));
		assertEquals(1, executedList.size());
		assertEquals("1111", executedList.get(0).get(11));
		assertEquals(1000L, executedList.get(0).get(14));
		assertEquals(1000L,executedList.get(0).get(32));
		assertEquals(3000L,executedList.get(0).get(38));
		assertEquals("1", executedList.get(0).get(39));
	}
	
	@Test
	public void testExecuteOrderTwoOrdersAddedOneFullyFilledOnePartiallyFilled() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "D");
		oe.put(38, 5000L);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		List<OrderEvent> executedList = poq.executeOrder(4000);
		
		assertEquals(4000, poq.getTotalOrderQuantity());
		assertEquals(1, poq.getQueueSize());
		assertEquals(1, poq.getActualOrderQueueSize());
		assertFalse(poq.getOrderEventInternalMap().containsKey("1111"));
		assertTrue(poq.getOrderEventInternalMap().containsKey("2222"));
		
		assertEquals("1111", executedList.get(0).get(11));
		assertEquals(3000L, executedList.get(0).get(14));
		assertEquals(3000L,executedList.get(0).get(32));
		assertEquals(3000L,executedList.get(0).get(38));
		assertEquals("2", executedList.get(0).get(39));
		
		assertEquals("2222", executedList.get(1).get(11));
		assertEquals(1000L, executedList.get(1).get(14));
		assertEquals(1000L,executedList.get(1).get(32));
		assertEquals(5000L,executedList.get(1).get(38));
		assertEquals("1", executedList.get(1).get(39));
	}
	
	@Test
	public void testExecuteOrderOneOrderAddedOneFilledByTwoExecutionFully() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 5000L);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
		
		List<OrderEvent> executedList = poq.executeOrder(3000);
		
		assertEquals(2000, poq.getTotalOrderQuantity());
		assertEquals(1, poq.getQueueSize());
		assertEquals(1, poq.getActualOrderQueueSize());
		assertTrue(poq.getOrderEventInternalMap().containsKey("1111"));
		assertEquals(3000L, executedList.get(0).get(14));
		assertEquals(3000L,executedList.get(0).get(32));
		assertEquals(5000L,executedList.get(0).get(38));
		assertEquals("1", executedList.get(0).get(39));
		
		executedList = poq.executeOrder(2000);
		
		assertEquals(0, poq.getTotalOrderQuantity());
		assertEquals(0, poq.getQueueSize());
		assertEquals(0, poq.getActualOrderQueueSize());
		assertFalse(poq.getOrderEventInternalMap().containsKey("1111"));
		assertEquals(5000L, executedList.get(0).get(14));
		assertEquals(2000L,executedList.get(0).get(32));
		assertEquals(5000L,executedList.get(0).get(38));
		assertEquals("2", executedList.get(0).get(39));
	}
}
