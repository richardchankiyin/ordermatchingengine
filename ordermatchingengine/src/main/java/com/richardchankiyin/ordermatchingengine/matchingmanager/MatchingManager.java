package com.richardchankiyin.ordermatchingengine.matchingmanager;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.IOrderMessageQueueReceiver;
import com.richardchankiyin.ordermatchingengine.order.statemachine.IOrderStateMachine;
import com.richardchankiyin.ordermatchingengine.publisher.IPublisher;

public class MatchingManager implements IOrderMessageQueueReceiver {
	
	public MatchingManager(IOrderStateMachine om, IPublisher publisher) {
		
	}

	@Override
	public void onEvent(OrderEvent oe) {
		// TODO Auto-generated method stub

	}

}
