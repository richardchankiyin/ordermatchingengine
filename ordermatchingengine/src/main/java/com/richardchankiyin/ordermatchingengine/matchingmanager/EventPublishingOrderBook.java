package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.List;

import org.javatuples.Pair;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.publisher.IPublisher;

public class EventPublishingOrderBook extends OrderBook {

	private IPublisher publisher = null;
	
	public EventPublishingOrderBook(String symbol, double initPrice, IPublisher publisher) {
		super(symbol, initPrice);
		this.publisher = publisher;
		publishPrice(true, this.getBid());
		publishPrice(false, this.getAsk());
	}
	
	protected IPriceOrderQueue createPriceOrderQueue(double price, boolean isBuy) {
		return new EventPublishingPriceOrderQueue(price,isBuy,publisher);
	}
	
	private void publishPrice(boolean isBid, double price) {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "B");
		oe.put(148, "OrderBook Price: " + this.getSymbol());
		oe.put(58, (isBid ? "bid price: " : "ask price") + ":" + price);
		oe.put(54, isBid ? "1" : "2");
		oe.put(44, price);
		this.publisher.publish(oe);
	}
	
	private void publishPriceChange(boolean isBid, double priceBefore, double priceAfter) {
		if (priceBefore != priceAfter) {
			publishPrice(isBid, priceAfter);
		}
	}
	
	private void publishQuantity(boolean isBid, long quantity) {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "B");
		oe.put(148, "OrderBook Quantity: " + this.getSymbol());
		oe.put(58, (isBid ? "bid quantity: " : "ask quantity") + ":" + quantity);
		oe.put(54, isBid ? "1" : "2");
		oe.put(38, quantity);
		this.publisher.publish(oe);
	}
	
	private void publishQuantityChange(boolean isBid, long quantityBefore, long quantityAfter) {
		if (quantityBefore != quantityAfter) {
			publishQuantity(isBid, quantityAfter);
		}
	}

	@Override
	public void addOrder(OrderEvent oe) {
		double bidBefore = this.getBid();
		double askBefore = this.getAsk();
		long bidQuantityBefore = this.getTotalBidQuantity();
		long askQuantityBefore = this.getTotalAskQuantity();
		super.addOrder(oe);
		double bidAfter = this.getBid();
		double askAfter = this.getAsk();
		long bidQuantityAfter = this.getTotalBidQuantity();
		long askQuantityAfter = this.getTotalAskQuantity();
		publishPriceChange(true, bidBefore, bidAfter);
		publishPriceChange(false, askBefore, askAfter);
		publishQuantityChange(true, bidQuantityBefore, bidQuantityAfter);
		publishQuantityChange(false, askQuantityBefore, askQuantityAfter);
	}
	
	

	@Override
	public void updateOrder(OrderEvent oe) {
		double bidBefore = this.getBid();
		double askBefore = this.getAsk();
		long bidQuantityBefore = this.getTotalBidQuantity();
		long askQuantityBefore = this.getTotalAskQuantity();
		super.updateOrder(oe);
		double bidAfter = this.getBid();
		double askAfter = this.getAsk();
		long bidQuantityAfter = this.getTotalBidQuantity();
		long askQuantityAfter = this.getTotalAskQuantity();
		publishPriceChange(true, bidBefore, bidAfter);
		publishPriceChange(false, askBefore, askAfter);
		publishQuantityChange(true, bidQuantityBefore, bidQuantityAfter);
		publishQuantityChange(false, askQuantityBefore, askQuantityAfter);
	}

	@Override
	public void cancelOrder(OrderEvent oe) {
		double bidBefore = this.getBid();
		double askBefore = this.getAsk();
		long bidQuantityBefore = this.getTotalBidQuantity();
		long askQuantityBefore = this.getTotalAskQuantity();
		super.cancelOrder(oe);
		double bidAfter = this.getBid();
		double askAfter = this.getAsk();
		long bidQuantityAfter = this.getTotalBidQuantity();
		long askQuantityAfter = this.getTotalAskQuantity();
		publishPriceChange(true, bidBefore, bidAfter);
		publishPriceChange(false, askBefore, askAfter);
		publishQuantityChange(true, bidQuantityBefore, bidQuantityAfter);
		publishQuantityChange(false, askQuantityBefore, askQuantityAfter);
	}

	@Override
	public Pair<Long, List<OrderEvent>> executeOrders(boolean isBid,
			long quantity, double bestPrice, boolean isAllOnly) {
		double bidBefore = this.getBid();
		double askBefore = this.getAsk();
		long bidQuantityBefore = this.getTotalBidQuantity();
		long askQuantityBefore = this.getTotalAskQuantity();
		Pair<Long, List<OrderEvent>> result = super.executeOrders(isBid, quantity, bestPrice, isAllOnly);
		double bidAfter = this.getBid();
		double askAfter = this.getAsk();
		long bidQuantityAfter = this.getTotalBidQuantity();
		long askQuantityAfter = this.getTotalAskQuantity();
		publishPriceChange(true, bidBefore, bidAfter);
		publishPriceChange(false, askBefore, askAfter);
		publishQuantityChange(true, bidQuantityBefore, bidQuantityAfter);
		publishQuantityChange(false, askQuantityBefore, askQuantityAfter);
		return result;
	}
	
	

}
