package com.richardchankiyin.ordermatchingengine.matchingmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.junit.Test;

import com.richardchankiyin.ordermatchingengine.matchingmanager.exception.NotEnoughQuantityException;
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
	public void testOrderBookAddOrderBuyPriceTooHigh() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
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
	public void testOrderBookAddOrderSellPriceTooLow() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 10);
		oe.put(54, "2");
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
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookUpdateOrderMsgTypeNotAccepted() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 20);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 2000L);
		oe.put(44, 20);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookUpdateOrderQtyLargerThanOriginal() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 20);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 4000L);
		oe.put(44, 20);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookUpdateOrderPriceMissing() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 20);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000L);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookUpdateOrderPriceChanged() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 20);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000L);
		oe.put(44, 19.5);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookUpdateOrderSideMissing() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 20);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000L);
		oe.put(44, 20);
		oe.put(55, "0005.HK");
		orderBook.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookUpdateOrderSideChanged() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 20);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000L);
		oe.put(44, 20);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.updateOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookUpdateOrderSymbolChanged() {
		IOrderBook orderBook = new OrderBook("0005.HK", 20);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 20);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000L);
		oe.put(44, 20);
		oe.put(54, "1");
		oe.put(55, "0006.HK");
		orderBook.updateOrder(oe);
	}
	
	@Test
	public void testOrderBookUpdateOrderBuyOrder() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		assertTrue(60 == orderBook.getBid());
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(3000L, orderBook.getTotalBidQuantity());
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000L);
		oe.put(44, 60);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.updateOrder(oe);
		
		assertTrue(60 == orderBook.getBid());
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(2000L, orderBook.getTotalBidQuantity());
	}
	
	@Test
	public void testOrderBookUpdateOrderSellOrder() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		assertTrue(60 == orderBook.getAsk());
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(3000L, orderBook.getTotalAskQuantity());
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		oe.put(38, 2000L);
		oe.put(44, 60);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.updateOrder(oe);
		
		assertTrue(60 == orderBook.getAsk());
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(2000L, orderBook.getTotalAskQuantity());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookCancelOrderClOrdIdMissing() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(35, "F");
		oe.put(38, 3000L);
		oe.put(44, 60);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.cancelOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookCancelOrderClOrdIdNotFound() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		oe.put(38, 3000L);
		oe.put(44, 60);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.cancelOrder(oe);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookCancelOrderMsgTypeNotAccepted() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "G");
		orderBook.cancelOrder(oe);
	}
	
	@Test
	public void testOrderBookCancelOrderBuyOrder() {
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
		oe.put(11, "1111");
		oe.put(35, "F");
		orderBook.cancelOrder(oe);
		assertTrue(59.5 == orderBook.getBid());
		assertEquals(0, orderBook.getBidQueueSize());
		assertEquals(0L, orderBook.getTotalBidQuantity());
	}
	
	@Test
	public void testOrderBookCancelOrderSellOrder() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.5 == orderBook.getAsk());
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(3000L, orderBook.getTotalAskQuantity());
		
		
		oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "F");
		orderBook.cancelOrder(oe);
		assertTrue(60.5 == orderBook.getAsk());
		assertEquals(0, orderBook.getAskQueueSize());
		assertEquals(0L, orderBook.getTotalAskQuantity());
	}
	
	@Test
	public void testOrderBookCancelOrderMultipleBuyOrders() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.5 == orderBook.getBid());
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(3000L, orderBook.getTotalBidQuantity());
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "D");
		oe.put(38, 4000L);
		oe.put(44, 60.0);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.5 == orderBook.getBid());
		assertEquals(2, orderBook.getBidQueueSize());
		assertEquals(7000L, orderBook.getTotalBidQuantity());
		
		
		oe = new OrderEvent();
		oe.put(11, "3333");
		oe.put(35, "D");
		oe.put(38, 6000L);
		oe.put(44, 60.55);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.55 == orderBook.getBid());
		assertEquals(3, orderBook.getBidQueueSize());
		assertEquals(13000L, orderBook.getTotalBidQuantity());
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "F");
		orderBook.cancelOrder(oe);
		assertTrue(60.55 == orderBook.getBid());
		assertEquals(2, orderBook.getBidQueueSize());
		assertEquals(9000L, orderBook.getTotalBidQuantity());
	}
	
	@Test
	public void testOrderBookCancelOrderMultipleSellOrders() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.5 == orderBook.getAsk());
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(3000L, orderBook.getTotalAskQuantity());
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "D");
		oe.put(38, 4000L);
		oe.put(44, 60.0);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.0 == orderBook.getAsk());
		assertEquals(2, orderBook.getAskQueueSize());
		assertEquals(7000L, orderBook.getTotalAskQuantity());
		
		
		oe = new OrderEvent();
		oe.put(11, "3333");
		oe.put(35, "D");
		oe.put(38, 6000L);
		oe.put(44, 60.55);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.0 == orderBook.getAsk());
		assertEquals(3, orderBook.getAskQueueSize());
		assertEquals(13000L, orderBook.getTotalAskQuantity());
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "F");
		orderBook.cancelOrder(oe);
		assertTrue(60.5 == orderBook.getAsk());
		assertEquals(2, orderBook.getAskQueueSize());
		assertEquals(9000L, orderBook.getTotalAskQuantity());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookExecuteOrderPriceNegative() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.5 == orderBook.getBid());
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(3000L, orderBook.getTotalBidQuantity());
		
		orderBook.executeOrders(true, 5000, -10, false);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOrderBookExecuteOrderQuantityNegative() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.5 == orderBook.getBid());
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(3000L, orderBook.getTotalBidQuantity());
		
		orderBook.executeOrders(true, -5000, 70, false);
	}
	
	@Test
	public void testOrderBookExecuteOrderBuyNotEnoughAvailableQuantityIsAllFalse() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.5 == orderBook.getBid());
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(3000L, orderBook.getTotalBidQuantity());
		
		Pair<Long,List<OrderEvent>> resultValues = orderBook.executeOrders(true, 5000, 60, false);
		long quantityUnexe = resultValues.getValue0();
		List<OrderEvent> result = resultValues.getValue1();
		assertEquals(2000L, quantityUnexe);
		assertEquals(1, result.size());
		assertEquals("1111", result.get(0).get(11));
		assertEquals(3000L, result.get(0).get(38));
		assertEquals(3000L, result.get(0).get(14));
		assertEquals(60.5, result.get(0).get(44));
		assertEquals("1", result.get(0).get(54));
		assertEquals("0005.HK", result.get(0).get(55));
		assertTrue(60.5 == orderBook.getBid());
		assertEquals(0, orderBook.getBidQueueSize());
		assertEquals(0, orderBook.getTotalBidQuantity());
	}
	
	@Test
	public void testOrderBookExecuteOrderBuyNotEnoughAvailableQuantityIsAllTrue() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.5 == orderBook.getBid());
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(3000L, orderBook.getTotalBidQuantity());
		
		try {
			orderBook.executeOrders(true, 5000, 60, true);
			fail("should not reach here");
		}
		catch (NotEnoughQuantityException ie) {
			
		}
		assertTrue(60.5 == orderBook.getBid());
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(3000L, orderBook.getTotalBidQuantity());
	}
	
	
	@Test
	public void testOrderBookExecuteOrderSellNotEnoughAvailableQuantityIsAllFalse() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.5 == orderBook.getAsk());
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(3000L, orderBook.getTotalAskQuantity());
		
		Pair<Long,List<OrderEvent>> resultValues = orderBook.executeOrders(false, 5000, 61, false);
		long quantityUnexe = resultValues.getValue0();
		List<OrderEvent> result = resultValues.getValue1();
		assertEquals(2000L, quantityUnexe);
		assertEquals(1, result.size());
		assertEquals("1111", result.get(0).get(11));
		assertEquals(3000L, result.get(0).get(38));
		assertEquals(3000L, result.get(0).get(14));
		assertEquals(60.5, result.get(0).get(44));
		assertEquals("2", result.get(0).get(54));
		assertEquals("0005.HK", result.get(0).get(55));
		assertTrue(60.5 == orderBook.getAsk());
		assertEquals(0, orderBook.getAskQueueSize());
		assertEquals(0, orderBook.getTotalAskQuantity());

	}
	
	@Test
	public void testOrderBookExecuteOrderSellNotEnoughAvailableQuantityIsAllTrue() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		assertTrue(60.5 == orderBook.getAsk());
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(3000L, orderBook.getTotalAskQuantity());
		try {
			orderBook.executeOrders(false, 5000, 61, true);
			fail("should not reach here");
		}
		catch (NotEnoughQuantityException ie) {
			
		}
		assertTrue(60.5 == orderBook.getAsk());
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(3000L, orderBook.getTotalAskQuantity());
	}
	
	@Test(expected=NotEnoughQuantityException.class)
	public void testOrderBookExecuteOrderBuyBestPriceHigherThanBid() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		orderBook.executeOrders(true, 3000L, 62, true);
	}
	
	@Test
	public void testOrderBookExecuteOrderBuyOneCompleteOrder() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		Pair<Long,List<OrderEvent>> resultValues = orderBook.executeOrders(true, 3000L, 60.5, false);
		long quantityUnexec = resultValues.getValue0();
		List<OrderEvent> result = resultValues.getValue1();
		assertEquals(0L, quantityUnexec);
		assertEquals(1, result.size());
		assertEquals("1111",result.get(0).get(11));
		assertEquals(3000L,result.get(0).get(38));
		assertEquals(3000L,result.get(0).get(14));
		assertEquals(60.5,result.get(0).get(44));
		assertEquals("1",result.get(0).get(54));
		assertEquals("0005.HK",result.get(0).get(55));
		
		assertEquals(0, orderBook.getBidQueueSize());
		assertEquals(0, orderBook.getTotalBidQuantity());
	}
	
	@Test
	public void testOrderBookExecuteOrderBuyOneIncompleteOrder() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		Pair<Long,List<OrderEvent>> resultValues = orderBook.executeOrders(true, 2000L, 60.5, false);
		long quantityUnexec = resultValues.getValue0();
		List<OrderEvent> result = resultValues.getValue1();
		assertEquals(0L, quantityUnexec);
		assertEquals(1, result.size());
		assertEquals("1111",result.get(0).get(11));
		assertEquals(3000L,result.get(0).get(38));
		assertEquals(2000L,result.get(0).get(14));
		assertEquals(60.5,result.get(0).get(44));
		assertEquals("1",result.get(0).get(54));
		assertEquals("0005.HK",result.get(0).get(55));
		
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(1000, orderBook.getTotalBidQuantity());
		assertTrue(60.5 == orderBook.getBid());
	}

	@Test
	public void testOrderBookExecuteOrderBuyTwoOneCompleteOneIncomplete() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "D");
		oe.put(38, 5000L);
		oe.put(44, 60.3);
		oe.put(54, "1");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		Pair<Long,List<OrderEvent>> resultValues = orderBook.executeOrders(true, 5000L, 60.3, false);
		long quantityUnexec = resultValues.getValue0();
		List<OrderEvent> result = resultValues.getValue1();
		assertEquals(0L, quantityUnexec);
		assertEquals(2, result.size());
		assertEquals("1111",result.get(0).get(11));
		assertEquals(3000L,result.get(0).get(38));
		assertEquals(3000L,result.get(0).get(14));
		assertEquals(60.5,result.get(0).get(44));
		assertEquals("1",result.get(0).get(54));
		assertEquals("0005.HK",result.get(0).get(55));
		
		assertEquals("2222",result.get(1).get(11));
		assertEquals(5000L,result.get(1).get(38));
		assertEquals(2000L,result.get(1).get(14));
		assertEquals(60.3,result.get(1).get(44));
		assertEquals("1",result.get(1).get(54));
		assertEquals("0005.HK",result.get(1).get(55));
		
		assertEquals(1, orderBook.getBidQueueSize());
		assertEquals(3000, orderBook.getTotalBidQuantity());
		assertTrue(60.3 == orderBook.getBid());
		
	}
	
	@Test(expected=NotEnoughQuantityException.class)
	public void testOrderBookExecuteOrderSellBestPriceLowerThanAsk() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		orderBook.executeOrders(false, 3000L, 58, true);
	}
	
	@Test
	public void testOrderBookExecuteOrderSellOneCompleteOrder() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		Pair<Long,List<OrderEvent>> resultValues = orderBook.executeOrders(false, 3000L, 60.5, false);
		long quantityUnexec = resultValues.getValue0();
		List<OrderEvent> result = resultValues.getValue1();
		assertEquals(0L, quantityUnexec);
		assertEquals(1, result.size());
		assertEquals("1111",result.get(0).get(11));
		assertEquals(3000L,result.get(0).get(38));
		assertEquals(3000L,result.get(0).get(14));
		assertEquals(60.5,result.get(0).get(44));
		assertEquals("2",result.get(0).get(54));
		assertEquals("0005.HK",result.get(0).get(55));
		
		assertEquals(0, orderBook.getAskQueueSize());
		assertEquals(0, orderBook.getTotalAskQuantity());
	}
	
	@Test
	public void testOrderBookExecuteOrderSellOneIncompleteOrder() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		Pair<Long,List<OrderEvent>> resultValues = orderBook.executeOrders(false, 2000L, 61, false);
		long quantityUnexec = resultValues.getValue0();
		List<OrderEvent> result = resultValues.getValue1();
		assertEquals(0L, quantityUnexec);
		assertEquals(1, result.size());
		assertEquals("1111",result.get(0).get(11));
		assertEquals(3000L,result.get(0).get(38));
		assertEquals(2000L,result.get(0).get(14));
		assertEquals(60.5,result.get(0).get(44));
		assertEquals("2",result.get(0).get(54));
		assertEquals("0005.HK",result.get(0).get(55));
		
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(1000L, orderBook.getTotalAskQuantity());
		assertTrue(60.5 == orderBook.getAsk());
	}
	
	
	@Test
	public void testOrderBookExecuteOrderSellTwoOneCompleteOneIncomplete() {
		IOrderBook orderBook = new OrderBook("0005.HK", 60);
		OrderEvent oe = new OrderEvent();
		oe.put(11, "1111");
		oe.put(35, "D");
		oe.put(38, 3000L);
		oe.put(44, 60.5);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		oe = new OrderEvent();
		oe.put(11, "2222");
		oe.put(35, "D");
		oe.put(38, 5000L);
		oe.put(44, 60.8);
		oe.put(54, "2");
		oe.put(55, "0005.HK");
		orderBook.addOrder(oe);
		
		Pair<Long,List<OrderEvent>> resultValues = orderBook.executeOrders(false, 5000L, 60.8, false);
		long quantityUnexec = resultValues.getValue0();
		List<OrderEvent> result = resultValues.getValue1();
		assertEquals(0L, quantityUnexec);
		assertEquals(2, result.size());
		assertEquals("1111",result.get(0).get(11));
		assertEquals(3000L,result.get(0).get(38));
		assertEquals(3000L,result.get(0).get(14));
		assertEquals(60.5,result.get(0).get(44));
		assertEquals("2",result.get(0).get(54));
		assertEquals("0005.HK",result.get(0).get(55));
		
		assertEquals("2222",result.get(1).get(11));
		assertEquals(5000L,result.get(1).get(38));
		assertEquals(2000L,result.get(1).get(14));
		assertEquals(60.8,result.get(1).get(44));
		assertEquals("2",result.get(1).get(54));
		assertEquals("0005.HK",result.get(1).get(55));
		
		assertEquals(1, orderBook.getAskQueueSize());
		assertEquals(3000, orderBook.getTotalAskQuantity());
		assertTrue(60.8 == orderBook.getAsk());
		
	}
}
