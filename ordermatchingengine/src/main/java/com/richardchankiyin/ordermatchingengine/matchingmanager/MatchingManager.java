package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.List;
import java.util.Objects;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.IOrderMessageQueueReceiver;
import com.richardchankiyin.ordermatchingengine.order.statemachine.IOrderStateMachine;
import com.richardchankiyin.ordermatchingengine.order.validation.AbstractOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.IOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationRule;
import com.richardchankiyin.ordermatchingengine.publisher.IPublisher;

public class MatchingManager implements IOrderMessageQueueReceiver {
	private IOrderStateMachine om = null;
	private IPublisher publisher = null;
	private String symbol = null;
	private boolean isLoggedOn = false;
	private double lastTradedPriceWhenStarted = Double.NaN;
	public MatchingManager(IOrderStateMachine om, IPublisher publisher) {
		Objects.requireNonNull(om, "OrderStateMachine cannot be null");
		Objects.requireNonNull(publisher, "Publisher cannot be null");
		this.om = om;
		this.publisher = publisher;
	}

	@Override
	public void onEvent(OrderEvent oe) {
		// TODO Auto-generated method stub

	}
	
	public boolean isLoggedOn() {
		return this.isLoggedOn;
	}
	
	private class MatchingManagerIncomingEventValidator extends AbstractOrderValidator {

		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	protected OrderValidationRule getLogonChecking() {
		return MATCHMGRLOGONCHECKING;
	}
	
	private final OrderValidationRule MATCHMGRLOGONCHECKING
		= new OrderValidationRule("MATCHMGRLOGONCHECKING", oe->{
			Object msgType = oe.get(35);
			if (msgType != null && "A".equals(msgType.toString())) {
				if (this.isLoggedOn()) {
					return new OrderValidationResult("Tag 35: A Logon rejected as it is logged on. ");
				} else {
					Object symbolVal = oe.get(54);
					Object priceVal = oe.get(44);
					if (symbolVal == null || priceVal == null) {
						return new OrderValidationResult("Tag 54 Symbol and Tag 44 Price cannot be missing. ");
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


}
