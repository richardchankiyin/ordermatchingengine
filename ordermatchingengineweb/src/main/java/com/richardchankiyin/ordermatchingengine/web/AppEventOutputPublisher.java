package com.richardchankiyin.ordermatchingengine.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.IOrderMessageQueueReceiver;

public class AppEventOutputPublisher implements IOrderMessageQueueReceiver {
	private static final Logger logger = LoggerFactory.getLogger(AppEventOutputPublisher.class);
	@Override
	public void onEvent(OrderEvent oe) {
		// TODO Auto-generated method stub
		logger.info("publishing event:\n{}\n", oe);
	}

}
