package com.richardchankiyin.ordermatchingengine.matchingmanager;

import java.util.Objects;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.IOrderMessageQueueReceiver;
import com.richardchankiyin.ordermatchingengine.order.statemachine.IOrderStateMachine;
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
	
	private void handleLogon(OrderEvent oe) {
		Object symbolValue = oe.get(54);
		Object lastTradedPrice = oe.get(44);
		if (symbolValue == null || lastTradedPrice == null) {
			throw new IllegalArgumentException("Tag 54: Symbol and Tag: 44 cannot be null");
		}
	}

}
