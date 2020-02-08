package com.richardchankiyin.ordermatchingengine.matchingmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
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
	public void testAddOrderPriceSideMissing() {
		PriceOrderQueue poq = new PriceOrderQueue(30, true);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		
		poq.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderPriceBuySideNotMatch() {
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
	public void testAddOrderPriceSellSideNotMatch() {
		PriceOrderQueue poq = new PriceOrderQueue(30, false);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000);
		oe.put(44, 30);
		oe.put(54, "1");
		poq.addOrder(oe);
	}
	
	@Test
	public void testAddOrderPrice() {
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
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddOrderPriceDuplicateClOrdId() {
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
	
	@Test(expected=IllegalArgumentException.class)
	public void testUpdateOrderPriceClOrdIdNotFound() {
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
	public void testUpdateOrderPriceMsgTypeNewOrderSingle() {
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
	public void testUpdateOrderPriceMsgTypeCancelOrder() {
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
	public void testUpdateOrderPriceQtyMissing() {
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
	public void testUpdateOrderPriceQtyNoChange() {
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
	public void testUpdateOrderPriceQtyAmendUp() {
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
	public void testUpdateOrderPriceQtyLargerThanRemainingQty() {
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
	public void testUpdateOrderPriceQtyEqualsToRemainingQty() {
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
	public void testUpdateOrderPriceIncomingPriceNotMatch() {
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
	public void testUpdateOrderPriceIncomingSideNotMatch() {
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
	public void testUpdateOrderPriceOnCancelledOrder() {
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
	public void testUpdateOrderPriceOnFullyFilledOrder() {
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
	public void testUpdateOrderPrice() {
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
	public void testCancelOrderPriceClOrdIdNotFound() {
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
	public void testCancelOrderPriceMsgTypeNotAccepted() {
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
	public void testCancelOrderPriceOnCancelledOrder() {
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
}
