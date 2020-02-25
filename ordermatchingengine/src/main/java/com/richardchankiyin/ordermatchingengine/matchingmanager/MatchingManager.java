package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.matchingmanager.exception.NotEnoughQuantityException;
import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.IOrderMessageQueueReceiver;
import com.richardchankiyin.ordermatchingengine.order.statemachine.IOrderStateMachine;
import com.richardchankiyin.ordermatchingengine.order.validation.AbstractOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.AbstractPreconditionOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.IOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.IncomingOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationRule;
import com.richardchankiyin.ordermatchingengine.publisher.IPublisher;
import com.richardchankiyin.utils.TimeUtils;

public class MatchingManager implements IOrderMessageQueueReceiver {
	private static final Logger logger = LoggerFactory.getLogger(MatchingManager.class);
	private IOrderStateMachine om = null;
	private IOrderBook orderbook = null;
	private IExecutionBook executionBook = null;
	private IPublisher publisher = null;
	private String symbol = null;
	private boolean isLoggedOn = false;
	private double lastTradedPriceWhenStarted = Double.NaN;
	private MatchingManagerIncomingEventValidator validator = null;
	private Map<String, Consumer<OrderEvent>> eventHandlerMap = null;
	
	public MatchingManager(IOrderStateMachine om, IPublisher publisher) {
		Objects.requireNonNull(om, "OrderStateMachine cannot be null");
		Objects.requireNonNull(publisher, "Publisher cannot be null");
		this.om = om;
		this.publisher = publisher;
		this.validator = new MatchingManagerIncomingEventValidator();
		initHandlers();
		initParams();
	}
	
	// init the params
	private void initParams() {
		this.symbol = null;
		this.isLoggedOn = false;
		this.lastTradedPriceWhenStarted = Double.NaN;
	}
	
	private void initHandlers() {
		eventHandlerMap = new HashMap<>();
		eventHandlerMap.put("A", handleLogonEvent);
		eventHandlerMap.put("5", handleLogoutEvent);
		eventHandlerMap.put("D", handleNewOrderSingleEvent);
		eventHandlerMap.put("G", handleReplaceRequestEvent);
	}

	private Consumer<OrderEvent> handleLogonEvent = oe -> {
		this.isLoggedOn = true;
		this.symbol = oe.get(55).toString();
		this.lastTradedPriceWhenStarted = Double.parseDouble(oe.get(44).toString());
		this.orderbook = new OrderBook(symbol, lastTradedPriceWhenStarted);
		this.executionBook = new ExecutionBook();
		String msg = String.format("%s starting accepting orders", symbol);
		// publish a news msg to indicate accepting orders for this symbol
		OrderEvent news = new OrderEvent();
		news.put(33, 1);
		news.put(35, "B");
		news.put(148, msg);
		news.put(58, msg);
		this.publisher.publish(news);
	};
	private Consumer<OrderEvent> handleLogoutEvent = oe -> {
		this.isLoggedOn = false;
		// TODO cancel outstanding orders and DFD
	};
	private Consumer<OrderEvent> handleOthers = oe -> {
		logger.warn("incoming event {} has no proper handling function", oe);
	};
	private Consumer<OrderEvent> handleNewOrderSingleEvent = oe -> {
		// 1. change the incoming order to pending new
		oe.put(39, "A");
		om.handleEvent(oe);
		// 2. if orderbook execution ok (2.1.1/2.1.2...), become new, else become reject(2.2.1/2.2.2...)
		
		// getting nos side, then execute the reverse side in the orderbook
		// i.e. NOS bid -> ask, NOS ask -> bid
		boolean isNosBid = "1".equals(oe.get(54));
		Object qty = oe.get(38);
		long qtyLong = Long.parseLong(qty.toString());
		boolean isNosMarketOrder = "1".equals(oe.get(40));
		// NOS worst price = counterparty best price
		double nosWorstPrice = getNosWorstPrice(isNosBid, isNosMarketOrder, oe, orderbook);
		// market order will be All or nothing order (also known as Immediate Or Cancel)
		boolean isAllOrNothing = isNosMarketOrder;
		boolean isNosSuccess = true;
		Pair<Long,List<OrderEvent>> result = null;
		String rejectReason = null;
		
		try {
			result = orderbook.executeOrders(!isNosBid, qtyLong, nosWorstPrice, isAllOrNothing);			
		}
		catch (NotEnoughQuantityException ne) {
			isNosSuccess = false;
			rejectReason = ne.getMessage();
		}
		catch (Throwable t) {
			isNosSuccess = false;
			logger.error("Something wrong happened here....", t);
			rejectReason = "System error";
		}
		
		if (isNosSuccess) {
			// 2.1.1 mark suspended before execution book process
			long quantityUnexec = result != null ? result.getValue0() : qtyLong;
			List<OrderEvent> listOfCounterpartyOrders = result != null ? result.getValue1() : new ArrayList<>();
			
			// 2.1.2. filled/partially filled based on difference of tag 38 and tag 14
			long quantityExec = qtyLong - quantityUnexec;
			if (quantityExec > 0) {
				oe.put(14, quantityExec);
				oe.put(32, quantityExec);
			}
			oe.put(39, "0");
			// mark NOS as New status 
			om.handleEvent(oe);
			
			// mark NOS and counterparty orders as suspend
			oe.put(35, "G");
			oe.put(39, "9");
			om.handleEvent(oe);
			for (OrderEvent counterpartyOrder: listOfCounterpartyOrders) {
				counterpartyOrder.put(35, "G");
				counterpartyOrder.put(39, "9");
				om.handleEvent(counterpartyOrder);
			}
			
			if (!listOfCounterpartyOrders.isEmpty()) {
				List<OrderEvent> executions = executionBook.processExecutions(oe, listOfCounterpartyOrders);
				// publish executions
				for (OrderEvent execution: executions) {
					this.publisher.publish(execution);
				}
			}
			// mark NOS and counterparty orders as New (for NOS only) / partial filled/filled (for both NOS and counterparty orders)
			String nosPostExecStatus = quantityUnexec == 0 ? "2" : quantityUnexec < qtyLong ? "1" : "0";
			oe.put(35, "G");
			oe.put(39, nosPostExecStatus);
			om.handleEvent(oe);
			for (OrderEvent counterpartyOrder: listOfCounterpartyOrders) {
				counterpartyOrder.put(35, "G");
				long quantityCounterparty = Long.parseLong(counterpartyOrder.get(38).toString());
				long cumQuantityCounterparty = Long.parseLong(counterpartyOrder.get(14).toString());
				String counterpartyPostExecStatus = cumQuantityCounterparty < quantityCounterparty ? "1" : "2";
				counterpartyOrder.put(39, counterpartyPostExecStatus);
				om.handleEvent(counterpartyOrder);
			}
			
			// 2.1.3. for partially filled limited NOS order, add to orderbook
			if (!isNosMarketOrder && !"2".equals(nosPostExecStatus)) {
				oe.put(35, "D");
				orderbook.addOrder(oe);				
			}
			// send out order execution report
			handleExecutionReportMessage(oe);
			// 2.1.4. DFD for filled orders
			if ("2".equals(nosPostExecStatus)) {
				// Nos Fully Filled, DFD for the order
				oe.put(35, "G");
				oe.put(39, "3");
				om.handleEvent(oe);
				handleExecutionReportMessage(oe);
			}
			
			for (OrderEvent counterpartyOrder: listOfCounterpartyOrders) {
				if ("2".equals(counterpartyOrder.get(39))) {
					counterpartyOrder.put(35, "G");
					counterpartyOrder.put(39, "3");
					om.handleEvent(counterpartyOrder);
					handleExecutionReportMessage(counterpartyOrder);
				}				
			}
		}
		else {
			// 2.2.1 update order as rejected in om
			oe.put(39, "8");
			om.handleEvent(oe);
			
			// 2.2.2 create reject execution report
			// 2.2.3 publish reject execution report
			handleRejectMessage(oe,rejectReason);
		}
	};
	
	private Consumer<OrderEvent> handleReplaceRequestEvent = oe -> {
		// 1. change the order status to suspend
		String originalStatus = oe.get(39).toString();
		boolean isUpdateSuccess = true;
		oe.put(39, "9");
		try {
			om.handleEvent(oe);
			// 2. order book update order
			this.orderbook.updateOrder(oe);
		}
		catch (Throwable t) {
			logger.debug("update order failed....", t);
			isUpdateSuccess = false;
		}
		// 3. update om based on status
		oe.put(39, originalStatus);
		// 4. publish execution report 
		if (isUpdateSuccess) {
			oe.put(39, "5");
			handleExecutionReportMessage(oe);
		} else {
			handleRejectMessage(oe, "Order replace rejected.");
		}
	};
	
	protected Consumer<OrderEvent> getHandleLogonEvent() {
		return this.handleLogonEvent;
	}
	
	protected Consumer<OrderEvent> getHandleLogoffEvent() {
		return this.handleLogoutEvent;
	}
	
	private double getNosWorstPrice(boolean isNosBid, boolean isNosMarketOrder, OrderEvent oe, IOrderBook ob) {
		if (isNosMarketOrder) {
			return isNosBid ? ob.getHighestAsk() : ob.getLowestBid();				
		} else {
			return Double.parseDouble(oe.get(44).toString());
		}
	}
	
	private void handleRejectMessage(OrderEvent oein, String message) {
		if (oein != null) {
			// need to create a new instance for message as the input event
			// can be further modified
			OrderEvent oe = new OrderEvent(oein);
			oe.put(35, "8");
			oe.put(39, "8");
			oe.put(58, message);
			oe.put(60, TimeUtils.getCurrentTimestamp());
			publisher.publish(oe);
		}
	}
	
	private void handleExecutionReportMessage(OrderEvent oein) {
		if (oein != null) {
			// need to create a new instance for message as the input event
			// can be further modified
			OrderEvent oe = new OrderEvent(oein);
			oe.put(35, "8");
			oe.put(60, TimeUtils.getCurrentTimestamp());
			publisher.publish(oe);
		}
	}
	
	@Override
	public void onEvent(OrderEvent oe) {
		logger.info("incoming order: {}", oe);
		try {
			OrderValidationResult validationResult = this.validator.validate(oe);
			if (validationResult.isAccepted()) {
				eventHandlerMap.getOrDefault(oe.get(35).toString(), handleOthers).accept(oe);
			} else {
				//validation failure
				logger.debug("validation failed: {}", validationResult);
				handleRejectMessage(oe, validationResult.getRejectReason());
			}
		}
		catch (Throwable t) {
			logger.error("Something wrong happened at event handling", t);
			handleRejectMessage(oe, "We are unable to process your order!");
		}
	}
	
	public boolean isLoggedOn() {
		return this.isLoggedOn;
	}
	
	public String getSymbol() {
		return this.symbol;
	}
	
	public double getLastTradedPriceWhenStarted() {
		return this.lastTradedPriceWhenStarted;
	}
	
	private class MatchingManagerIncomingEventValidator extends AbstractOrderValidator {

		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			return Arrays.asList(
					// rule 1 msg type checking
					MATCHMGRMSGTYPECHECKING
					// rule 2a logon checking
					, MATCHMGRLOGONCHECKING
					// rule 2b logout checking
					, MATCHMGRLOGOUTCHECKING
					// rule 3 order after logged on checking
					, MATCHMGRCANACCEPTORDERCHECKING
					// rule 4 NOS/Replace/Cancel order validation
					, new IncomingMatchingOrderValidator()
			);
		}
		
	}
	
	protected OrderValidationRule getMsgTypeChecking() {
		return MATCHMGRMSGTYPECHECKING;
	}
	
	protected OrderValidationRule getLogonChecking() {
		return MATCHMGRLOGONCHECKING;
	}
	
	protected OrderValidationRule getLogoutChecking() {
		return MATCHMGRLOGOUTCHECKING;
	}
	
	protected OrderValidationRule getCanAcceptOrderChecking() {
		return MATCHMGRCANACCEPTORDERCHECKING;
	}
	
	private final OrderValidationRule MATCHMGRMSGTYPECHECKING
		= new OrderValidationRule("MATCHMGRMSGTYPECHECKING", oe->{
			Object msgTypeValue = oe.get(35);
			Set<String> acceptedMsgTypes = new HashSet<String>(Arrays.asList("D","F","G", "A", "5"));
			if (msgTypeValue != null) {
				if (!acceptedMsgTypes.contains(msgTypeValue.toString())) {
					return new OrderValidationResult(String.format("Tag 35: %s not accepted. Only accepts: %s .", msgTypeValue, acceptedMsgTypes));
				} else {
					return OrderValidationResult.getAcceptedInstance();
				}
			} else {
				return new OrderValidationResult("Type 35 msg type is missing. ");
			}
		});
	
	private final OrderValidationRule MATCHMGRLOGONCHECKING
		= new OrderValidationRule("MATCHMGRLOGONCHECKING", oe->{
			Object msgType = oe.get(35);
			if (msgType != null && "A".equals(msgType.toString())) {
				if (this.isLoggedOn()) {
					return new OrderValidationResult("Tag 35: A Logon rejected as it is logged on. ");
				} else {
					Object symbolVal = oe.get(55);
					Object priceVal = oe.get(44);
					if (symbolVal == null || priceVal == null) {
						return new OrderValidationResult("Tag 55 Symbol and Tag 44 Price cannot be missing. ");
					} else {
						double price = -1;
						try {
							price = Double.parseDouble(priceVal.toString());
						}
						catch (Exception e) {
							return new OrderValidationResult(String.format("Tag 44: %s not a numeric figure. ", priceVal));
						}
						
						if (price <= 0) {
							return new OrderValidationResult(String.format("Tag 44: %s is not positive. ", priceVal));
						} else {
							return OrderValidationResult.getAcceptedInstance();
						}
					}
				}
			} else {
				return OrderValidationResult.getAcceptedInstance();
			}
		});

	private final OrderValidationRule MATCHMGRLOGOUTCHECKING
		= new OrderValidationRule("MATCHMGRLOGOUTCHECKING", oe->{
			Object msgType = oe.get(35);
			if (msgType != null && "5".equals(msgType.toString())) {
				if (!this.isLoggedOn()) {
					return new OrderValidationResult("Tag 35: 5 Logout on a non-logged-in machine rejected. ");
				} else {
					return OrderValidationResult.getAcceptedInstance();
				}
			} else {
				return OrderValidationResult.getAcceptedInstance();
			}
		});

	private final OrderValidationRule MATCHMGRCANACCEPTORDERCHECKING
		= new OrderValidationRule("MATCHMGRCANACCEPTORDERCHECKING", oe->{
			Object msgType = oe.get(35);
			if (msgType != null) {
				Set<String> msgTypesAcceptedAfterLogon = new HashSet<String>(Arrays.asList("D","F","G"));
				if (msgTypesAcceptedAfterLogon.contains(msgType.toString())) {
					if (this.isLoggedOn()) {
						return OrderValidationResult.getAcceptedInstance();
					} else {
						return new OrderValidationResult("Order only accepted after logon. ");
					}
				}
			} else {
				return OrderValidationResult.getAcceptedInstance();
			}
			return OrderValidationResult.getAcceptedInstance();
		});
	
	private final OrderValidationRule INCOMINGORDERSYMBOLCHECKING
		= new OrderValidationRule("INCOMINGORDERSYMBOLCHECKING", oe->{
			Object symbolInThisOrder = oe.get(55);
			if (this.symbol != null) {
				if (!this.symbol.equals(symbolInThisOrder)) {
					return new OrderValidationResult(
							String.format("Symbol: %s not match with one assigned by login: %s", symbolInThisOrder, this.symbol));
				}
			}
			return OrderValidationResult.getAcceptedInstance();
		});
	
	private final class IncomingMatchingOrderValidator extends AbstractPreconditionOrderValidator {

		public IncomingMatchingOrderValidator() {
			super(oe->{
				if (oe == null)
					return false;
				Set<String> acceptedMsgTypes = new HashSet<String>(Arrays.asList("D","F","G"));
				return acceptedMsgTypes.contains(oe.get(35));
			});			
		}

		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			return Arrays.asList(INCOMINGORDERSYMBOLCHECKING, new IncomingOrderValidator(om.getOrderModel()));
		}
		
	}
}
