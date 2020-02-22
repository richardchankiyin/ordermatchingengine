package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class MatchingManager implements IOrderMessageQueueReceiver {
	private static final Logger logger = LoggerFactory.getLogger(MatchingManager.class);
	private IOrderStateMachine om = null;
	private IOrderBook orderbook = null;
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
	}

	private Consumer<OrderEvent> handleLogonEvent = oe -> {
		this.isLoggedOn = true;
		this.symbol = oe.get(55).toString();
		this.lastTradedPriceWhenStarted = Double.parseDouble(oe.get(44).toString());
		this.orderbook = new OrderBook(symbol, lastTradedPriceWhenStarted);
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
		// change the incoming order to pending new
		oe.put(39, "A");
		om.handleEvent(oe);
		
		
		
	};
	
	
	protected Consumer<OrderEvent> getHandleLogonEvent() {
		return this.handleLogonEvent;
	}
	
	protected Consumer<OrderEvent> getHandleLogoffEvent() {
		return this.handleLogoutEvent;
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
			}
		}
		catch (Throwable t) {
			logger.error("Something wrong happened at event handling", t);
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
					// rule 4 NOS order validation
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
			return Arrays.asList(new IncomingOrderValidator(om.getOrderModel()));
		}
		
	}
}
