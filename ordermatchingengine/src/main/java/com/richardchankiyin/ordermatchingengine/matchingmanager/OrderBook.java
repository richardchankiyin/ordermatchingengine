package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.validation.AbstractOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.IOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationRule;
import com.richardchankiyin.spreadcalc.SpreadRanges;
/**
 * Collection of IPriceOrderQueue to form
 * an order book, i.e. different buy and sell
 * order queues at different price
 * @author richard
 *
 */
public class OrderBook implements IOrderBook {
	private static final Logger logger = LoggerFactory.getLogger(OrderBook.class);
	private double bid = SpreadRanges.getInstance().getMinPrice();
	private double ask = SpreadRanges.getInstance().getMaxPrice();
	private long getBidQueueSize = 0;
	private long getAskQueueSize = 0;
	
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

		
		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			return Arrays.asList(ADDORDERCLORDIDCHECKING);
		}
		
	}
	@Override
	public void addOrder(OrderEvent oe) {
		// TODO Auto-generated method stub

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
	public double getBid() {
		return bid;
	}

	@Override
	public double getAsk() {
		return ask;
	}

	@Override
	public List<OrderEvent> executeOrder(long quantity) {
		// TODO Auto-generated method stub
		return null;
	}


}
