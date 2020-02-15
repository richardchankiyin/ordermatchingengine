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
	public void testOrderBookAddOrderBuy() {
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
	}
	
	@Test
	public void testOrderBookAddOrderSell() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.25);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.25 == orderBook.getAsk());
	}
}
