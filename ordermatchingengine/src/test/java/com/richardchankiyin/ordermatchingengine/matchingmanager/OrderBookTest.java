package com.richardchankiyin.ordermatchingengine.matchingmanager;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class OrderBookTest {

	@Test(expected=NullPointerException.class)
	public void testOrderBookSymbolMissing() {
		new OrderBook(null, 10);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookPriceNegative() {
		new OrderBook("0005.HK", -1);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookPriceZero() {
		new OrderBook("0005.HK", 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookPriceInvalidInSpreadRange() {
		new OrderBook("0005.HK", 19.999);
	}

	@Test
	public void testOrderBook() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		assertTrue(20 == orderBook.getAsk());
		assertTrue(21.20 == orderBook.getHighestAsk());
		assertTrue(19.98 == orderBook.getBid());
		assertTrue(19.50 == orderBook.getLowestBid());
		assertEquals("0005.HK", orderBook.getSymbol());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookAddOrderClOrdIdMissing() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 30);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookAddOrderDuplicateClOrdId() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20) {
			protected Map<String, OrderEvent> getOrderEventInternalMap() {
				Map<String, OrderEvent> map = new HashMap<>();
				OrderEvent oe = new OrderEvent();
				oe.put(11, "1111");
				oe.put(35, "D");
				oe.put(38, 3000L);
				oe.put(44, 30);
				oe.put(54, "1");
				oe.put(55, "0005.HK");
				map.put("1111", oe);
				return map;				
			}
		};
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 30);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookAddOrderMsgTypeMissing() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(38, 3000L);
		oe.put(44, 30);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookAddOrderMsgTypeNotAccepted() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000L);
		oe.put(44, 30);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookAddOrderSymbolMissing() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000L);
		oe.put(44, 30);
		oe.put(54, "1");
		orderBook.addOrder(oe);
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookAddOrderSymbolNotMatch() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 3000L);
		oe.put(44, 30);
		oe.put(54, "1");
		oe.put(55, "0004.HK");
		orderBook.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookAddOrderBuyPriceTooLow() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 10);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookAddOrderSellPriceTooHigh() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 30);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
	}
	
	@Test
	public void testOrderBookAddOrderBuyLowerThanInit() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 59.95);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(59.95 == orderBook.getBid());
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(3000L, orderBook.getTotalBidQuantity());
	}
	
	@Test
	public void testOrderBookAddOrderSellHigherThanInit() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000L);
		oe.put(44, 60.25);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.25 == orderBook.getAsk());
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(2000L, orderBook.getTotalAskQuantity());
	}
	
	@Test
	public void testOrderBookAddOrderBuyHigherThanInit() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 5000L);
		oe.put(44, 60.1);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.1 == orderBook.getBid());
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(5000L, orderBook.getTotalBidQuantity());
	}
	
	@Test
	public void testOrderBookAddOrderSellLowerThanInit() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 59.95);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(59.95 == orderBook.getAsk());
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(3000L, orderBook.getTotalAskQuantity());
	}
	
	@Test
	public void testOrderBookAddMultipleBuyOrders() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 59.5);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(59.5 == orderBook.getBid());
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(3000L, orderBook.getTotalBidQuantity());
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "D");
		oe.put(38, 4000L);
		oe.put(44, 59.25);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(59.5 == orderBook.getBid());
		assertEquals(2, orderBook.getBidQueueSize());
		assertEquals(7000L, orderBook.getTotalBidQuantity());
		
		
		oe = new OrderEvent();
		oe.put(11, "3333");
		oe.put(35, "D");
		oe.put(38, 5000L);
		oe.put(44, 59.75);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(59.75 == orderBook.getBid());
		assertEquals(3, orderBook.getBidQueueSize());
		assertEquals(12000L, orderBook.getTotalBidQuantity());
		
	}
	
	@Test
	public void testOrderBookAddMultipleSellOrders() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.95);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.95 == orderBook.getAsk());
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(3000L, orderBook.getTotalAskQuantity());
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "D");
		oe.put(38, 4000L);
		oe.put(44, 61.25);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.95 == orderBook.getAsk());
		assertEquals(2, orderBook.getAskQueueSize());
		assertEquals(7000L, orderBook.getTotalAskQuantity());
		
		oe = new OrderEvent();
		oe.put(11, "3333");
		oe.put(35, "D");
		oe.put(38, 5000L);
		oe.put(44, 60.25);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.25 == orderBook.getAsk());
		assertEquals(3, orderBook.getAskQueueSize());
		assertEquals(12000L, orderBook.getTotalAskQuantity());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookUpdateOrderClOrdIdNotFound() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 20);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.updateOrder(oe);
	}
}
