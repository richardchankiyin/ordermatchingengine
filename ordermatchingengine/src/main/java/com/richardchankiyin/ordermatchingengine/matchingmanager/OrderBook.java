package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
	private final double initPrice;
	private double bid = SpreadRanges.getInstance().getMinPrice();
	private double ask = SpreadRanges.getInstance().getMaxPrice();
	private String symbol = null;
	private long bidQueueSize = 0;
	private long askQueueSize = 0;
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
		this.ask = initPrice;
		this.bid = SpreadRanges.getInstance().getSingleSpreadPrice(this.ask, false, 1);
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
	public String getSymbol() {
		return symbol;
	}
	
	/********* Add Order *********/
	private class AddOrderValidator extends AbstractOrderValidator {

		private final OrderValidationRule ADDORDERCLORDIDCHECKING
		= new OrderValidationRule("ADDORDERCLORDIDCHECKING", oe->{
			Object clOrdId = oe.get(11);
			if (clOrdId == null) {
				return new OrderValidationResult("Tag 11: ClOrdId cannot be missing. ");
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
				if (!"1".equals(side) || "2".equals(side)) {
					return new OrderValidationResult("Tag 54: Side can only be 1 or 2. ");
				}
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
					, ADDORDERSYMBOLCHECKING
					);
		}
		
	}
	@Override
	public void addOrder(OrderEvent oe) {
		// TODO Auto-generated method stub
		handleValidationResult(oe, addOrderValidator);
	}

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
