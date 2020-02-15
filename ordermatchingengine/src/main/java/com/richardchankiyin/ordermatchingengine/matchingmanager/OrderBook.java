package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.validation.AbstractOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.IOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationRule;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationRuleUtil;
import com.richardchankiyin.spreadcalc.SpreadRanges;
import com.richardchankiyin.utils.NumericUtils;
/**
 * Collection of IPriceOrderQueue to form
 * an order book, i.e. different buy and sell
 * order queues at different price
 * @author richard
 *
 */
public class OrderBook implements IOrderBook {
	private static final Logger logger = LoggerFactory.getLogger(OrderBook.class);
	private final static int ROUND_SCALE = 10;
	private final static int MAX_SPREAD = 24;
	private final double initPrice;
	private double bid = SpreadRanges.getInstance().getMinPrice();
	private double ask = SpreadRanges.getInstance().getMaxPrice();
	private double lowestBid = Double.MIN_VALUE;
	private double highestAsk = Double.MAX_VALUE;
	private String symbol = null;
	private long bidQueueSize = 0;
	private long totalBidQuantity = 0;
	private long askQueueSize = 0;
	private long totalAskQuantity = 0;
	private Map<String, OrderEvent> orderEventInternalMap = new HashMap<>();
	private TreeMap<Double, IPriceOrderQueue> bidPriceQueueMap = new TreeMap<>();
	private TreeMap<Double, IPriceOrderQueue> askPriceQueueMap = new TreeMap<>();
	private AddOrderValidator addOrderValidator = new AddOrderValidator();
	
	public OrderBook(String symbol, double initPrice) {
		Objects.requireNonNull(symbol, "symbol cannot be missing. ");
		
		if (initPrice <= 0) {
			throw new IllegalArgumentException("init price cannot be non-positive. ");
		}
		this.initPrice = NumericUtils.roundDouble(initPrice, ROUND_SCALE);
		if (!SpreadRanges.getInstance().isValidPrice(this.initPrice, false)) {
			throw new IllegalArgumentException("not a valid init price. ");
		}
		
		this.symbol = symbol;
		initBidAsk(this.initPrice);
	}
	
	private void initBidAsk(double initPrice) {
		// ask price = init price
		// bid price = ask price - 1 spread
		double ask = initPrice;
		double bid = SpreadRanges.getInstance().getSingleSpreadPrice(ask, false, 1);
		// add price queue
		bidPriceQueueMap.put(bid, new PriceOrderQueue(bid, true));
		askPriceQueueMap.put(ask, new PriceOrderQueue(ask, false));
		
		updateBidPrices(bid);
		updateAskPrices(ask);
	}
	
	private void updateBidPrices(double bidprice) {
		this.bid = bidprice;
		this.lowestBid = getLowestBid(this.bid, MAX_SPREAD);
		logger.debug("ask: {} highest ask: {} bid: {} lowest bid: {}", this.ask, this.highestAsk, this.bid, this.lowestBid);
	}
	
	private void updateAskPrices(double askprice) {
		this.ask = askprice;
		this.highestAsk = getHighestAsk(this.ask, MAX_SPREAD);
		logger.debug("ask: {} highest ask: {} bid: {} lowest bid: {}", this.ask, this.highestAsk, this.bid, this.lowestBid);
	}
	
	protected double getCurrentHighestBid() {
		double result = SpreadRanges.getInstance().getMinPrice();
		Iterator<Entry<Double, IPriceOrderQueue>> iterator = bidPriceQueueMap.descendingMap().entrySet().iterator();
		boolean isContinue = true;
		while (iterator.hasNext() && isContinue) {
			Entry<Double, IPriceOrderQueue> entry = iterator.next();
			if (entry.getValue().getQueueSize() > 0) {
				result = entry.getKey();
				isContinue = false;
			}
		}
		return result;
	}
	
	protected double getCurrentLowestAsk() {
		double result = SpreadRanges.getInstance().getMaxPrice();
		Iterator<Entry<Double, IPriceOrderQueue>> iterator = askPriceQueueMap.entrySet().iterator();
		boolean isContinue = true;
		while (iterator.hasNext() && isContinue) {
			Entry<Double, IPriceOrderQueue> entry = iterator.next();
			if (entry.getValue().getQueueSize() > 0) {
				result = entry.getKey();
				isContinue = false;
			}
		}
		return result;
	}
	
	protected Map<String, OrderEvent> getOrderEventInternalMap() {
		return this.orderEventInternalMap;
	}
	
	@Override
	public double getBid() {
		return bid;
	}
	
	@Override
	public double getAsk() {
		return ask;
	}
	
	@Override
	public double getLowestBid() {
		return lowestBid;
	}
	
	@Override
	public double getHighestAsk() {
		return highestAsk;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}
	
	private double getLowestBid(double bid, int spread) {
		double result = SpreadRanges.getInstance().getMinPrice();
		try {
			result = SpreadRanges.getInstance().getSingleSpreadPrice(bid, false, 24);
		} catch (Exception e) {
			logger.error("error found when getting lowest bid: {} spread: {}", bid, spread, e);	
		}
		return result;
	}
	
	private double getHighestAsk(double ask, int spread) {
		double result = SpreadRanges.getInstance().getMaxPrice();
		try {
			result = SpreadRanges.getInstance().getSingleSpreadPrice(ask, true, 24);
		} catch (Exception e) {
			logger.error("error found when getting highest ask: {} spread: {}", ask, spread, e);	
		}
		return result;
	}
	
	/********* Add Order *********/
	private class AddOrderValidator extends AbstractOrderValidator {

		private final OrderValidationRule ADDORDERCLORDIDCHECKING
		= new OrderValidationRule("ADDORDERCLORDIDCHECKING", oe->{
			Object clOrdId = oe.get(11);
			if (clOrdId == null) {
				return new OrderValidationResult("Tag 11: ClOrdId cannot be missing. ");
			} else {
				if (getOrderEventInternalMap().containsKey(clOrdId.toString())) {
					return new OrderValidationResult("Tag 11: ClOrdId exists. ");
				}
			}
			return OrderValidationResult.getAcceptedInstance();
		});

		private final OrderValidationRule ADDORDERPRICECHECKING
		= new OrderValidationRule("ADDORDERPRICECHECKING", oe->{
			Object price = oe.get(44);
			if (price == null) {
				return new OrderValidationResult("Tag 44: Price cannot be missing. ");
			} 
			return OrderValidationResult.getAcceptedInstance();
		});
		
		private final OrderValidationRule ADDORDERSIDECHECKING
		= new OrderValidationRule("ADDORDERSIDECHECKING", oe->{
			Object side = oe.get(54);
			if (side == null) {
				return new OrderValidationResult("Tag 54: Side cannot be missing. ");
			} else {
				if (!("1".equals(side) || "2".equals(side))) {
					return new OrderValidationResult("Tag 54: Side can only be 1 or 2. ");
				}
			}			
			return OrderValidationResult.getAcceptedInstance();
		});
		
		private final OrderValidationRule ADDORDERPRICERANGECHECKING
		= new OrderValidationRule("ADDORDERPRICERANGECHECKING", oe->{
			Object price = oe.get(44);
			Object side = oe.get(54);
			try {
				if (price != null && side != null) {
					double pricedouble = Double.parseDouble(price.toString());
					if (("1".equals(side) || "2".equals(side))) {
						if (side == "1") {
							boolean isValid = SpreadRanges.getInstance().isValidPrice(pricedouble, false);
							logger.debug("price: {} isValid: {} lowestBid: {}", pricedouble, isValid, lowestBid);							
							if (!isValid) {
								return new OrderValidationResult(String.format("Tag 44: Price %s invalid for bid. ", price));
							}
							if (pricedouble < lowestBid) {
								return new OrderValidationResult(String.format("Tag 44: Price %s smaller than lowest bid. ", price, lowestBid));
							}

						} else {
							boolean isValid = SpreadRanges.getInstance().isValidPrice(pricedouble, true);
							logger.debug("price: {} isValid: {} highestAsk: {}", pricedouble, isValid, highestAsk);
							if (!isValid) {
								return new OrderValidationResult(String.format("Tag 44: Price %s invalid for ask. ", price));
							}
							if (pricedouble > highestAsk) {
								return new OrderValidationResult(String.format("Tag 44: Price %s higher than highest ask. ", price, highestAsk));
							}

						}
					}
				}
			} catch (Exception e) {
				logger.debug("issue when validating price range", e);
			}
			return OrderValidationResult.getAcceptedInstance();
		});
		
		private final OrderValidationRule ADDORDERSYMBOLCHECKING
		= new OrderValidationRule("ADDORDERSYMBOLCHECKING", oe->{
			Object symbol = oe.get(55);
			if (symbol == null) {
				return new OrderValidationResult("Tag 55: Symbol cannot be missing. ");
			} else {
				String originalSymbol = getSymbol();
				if (!symbol.equals(originalSymbol)) {
					return new OrderValidationResult(String.format("Tag 55: %s is not the same as expected %s. ",symbol,originalSymbol));
				}
			}
			return OrderValidationResult.getAcceptedInstance();
		});
		
		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			return Arrays.asList(ADDORDERCLORDIDCHECKING
					, OrderValidationRuleUtil.getAddOrderMsgTypeChecking()
					, OrderValidationRuleUtil.getAddOrderQtyChecking()
					, OrderValidationRuleUtil.getAddOrderCumQtyChecking()
					, ADDORDERPRICECHECKING
					, ADDORDERSIDECHECKING
					, ADDORDERPRICERANGECHECKING
					, ADDORDERSYMBOLCHECKING
					);
		}
		
	}
	
	
	
	@Override
	public void addOrder(OrderEvent oe) {
		handleValidationResult(oe, addOrderValidator);
		//TODO to be implemented
		Object clOrdId = oe.get(11);
		Object price = oe.get(44);
		double priceDouble = Double.parseDouble(price.toString());
		Object side = oe.get(54);
		boolean isBuy = "1".equals(side.toString());
		// 1. from Map<Double,IPriceOrderQueue> retrieving IPriceOrderQueue object from price tag 44
		// 2. if null from 2, create IPriceOrderQueue and add
		Map<Double, IPriceOrderQueue> priceOrderQueue = isBuy ? bidPriceQueueMap : askPriceQueueMap;
		IPriceOrderQueue queue = priceOrderQueue.computeIfAbsent(priceDouble, p->new PriceOrderQueue(p, isBuy));
		// 3. Before adding, retrieving IPriceOrderQueue.getQueueSize and getTotalQuantity, then IPriceOrderQueue.addOrder
		long beforeQueueSize = queue.getQueueSize();
		long beforeTotalQuantity = queue.getTotalOrderQuantity();
		queue.addOrder(oe);
		// 4. retrieving again the IPriceOrderQueue.getQueueSize and getTotalQuantity, from the differences, update internal
		long afterQueueSize = queue.getQueueSize();
		long afterTotalQuantity = queue.getTotalOrderQuantity();
		long queueSizeDiff = afterQueueSize - beforeQueueSize;
		long totalQuantityDiff = afterTotalQuantity - beforeTotalQuantity;
		if (isBuy) {
			this.bidQueueSize += queueSizeDiff;
			this.totalBidQuantity += totalQuantityDiff;
		} else {
			this.askQueueSize += queueSizeDiff;
			this.totalAskQuantity += totalQuantityDiff;
		}
		// 5. update bid/ask price 
		updateBidAskPriceDueToAddOrder(isBuy, priceDouble);
		// 6. add order to internal map for clordid duplication checking
		getOrderEventInternalMap().put(clOrdId.toString(), oe);
	}
	
	private void updateBidAskPriceDueToAddOrder(boolean isBuy, double orderPrice) {
		if (isBuy) {
			double bidPriceToBeUpdated = this.bid;
			if (orderPrice > this.bid) {
				bidPriceToBeUpdated = orderPrice; 
			} else {
				IPriceOrderQueue bidQueue = bidPriceQueueMap.get(this.bid);
				if (bidQueue.getQueueSize() == 0) {
					bidPriceToBeUpdated = this.getCurrentHighestBid();
				}
			}
			
			if (bidPriceToBeUpdated != this.bid) {
				updateBidPrices(bidPriceToBeUpdated);
			}
		} else {
			double askPriceToBeUpdated = this.ask;
			if (orderPrice < this.ask) {
				askPriceToBeUpdated = orderPrice;
			} else {
				IPriceOrderQueue askQueue = askPriceQueueMap.get(this.ask);
				if (askQueue.getQueueSize() == 0) {
					askPriceToBeUpdated = this.getCurrentLowestAsk();
				}
			}
			
			if (askPriceToBeUpdated != this.ask) {
				updateAskPrices(askPriceToBeUpdated);
			}
		}
	}
	
	/********* Update Order *********/
	@Override
	public void updateOrder(OrderEvent oe) {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancelOrder(OrderEvent oe) {
		// TODO Auto-generated method stub

	}	

	@Override
	public List<OrderEvent> executeOrder(boolean isBid, long quantity) {
		// TODO Auto-generated method stub
		return null;
	}

}
