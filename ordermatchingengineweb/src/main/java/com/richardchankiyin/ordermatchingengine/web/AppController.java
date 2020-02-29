package com.richardchankiyin.ordermatchingengine.web;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import com.richardchankiyin.ordermatchingengine.matchingmanager.MatchingManager;
import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.OrderMessageQueue;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.OrderMessageQueueForTest;
import com.richardchankiyin.ordermatchingengine.order.model.OrderRepository;
import com.richardchankiyin.ordermatchingengine.order.statemachine.IOrderStateMachine;
import com.richardchankiyin.ordermatchingengine.order.statemachine.OrderStateMachine;
import com.richardchankiyin.ordermatchingengine.publisher.IPublisher;
import com.richardchankiyin.ordermatchingengine.publisher.Publisher;

@RestController
public class AppController {

	int orderRepoSize = 100;
	boolean isStart = false;
	String symbol = "0005.HK";
	double initPrice = 60;
	OrderRepository orderRepo = null;
	IOrderStateMachine om = null;
	MatchingManager matchingManager = null;
	IPublisher publisher = null;
	int orderMsgQueueSize = 100;
	OrderMessageQueue queue = null;
	
	public AppController() {
		orderRepo = new OrderRepository(orderRepoSize);
		om = new OrderStateMachine(orderRepo.getOrderModel(), orderRepo);
		publisher = new Publisher(new AppEventOutputPublisher());
		matchingManager = new MatchingManager(om, publisher);
		queue = new OrderMessageQueue(symbol + "_queue", matchingManager, orderMsgQueueSize);
		queue.start();
	}
	
	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}
	
	@RequestMapping("/symbol")
	public String symbol() {
		return symbol;
	}
	
	@RequestMapping("/login")
	public String login() {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "A");
		oe.put(44, initPrice);
		oe.put(55, symbol);
		queue.send(oe);
		return oe.toString();
	}

}
