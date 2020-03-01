package com.richardchankiyin.ordermatchingengine.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.IOrderMessageQueueReceiver;


/**
 * Service class for sending notification messages.
 */
@Service
public class AppEventOutputPublisher implements IOrderMessageQueueReceiver {
	private static final Logger logger = LoggerFactory.getLogger(AppEventOutputPublisher.class);
	
	// The SimpMessagingTemplate is used to send Stomp over WebSocket messages.
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	 
	@Override
	public void onEvent(OrderEvent oe) {
		logger.info("publishing event:\n{}\n", oe);
		
		messagingTemplate.convertAndSend("/topic/executionreport/notify", oe);
	}

}
