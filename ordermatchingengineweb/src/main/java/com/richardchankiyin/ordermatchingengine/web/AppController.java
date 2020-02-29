package com.richardchankiyin.ordermatchingengine.web;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.richardchankiyin.ordermatchingengine.matchingmanager.MatchingManager;
import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.messagequeue.OrderMessageQueue;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;
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
	IOrderModel orderModel = null;
	IOrderStateMachine om = null;
	MatchingManager matchingManager = null;
	IPublisher publisher = null;
	int orderMsgQueueSize = 100;
	OrderMessageQueue queue = null;
	
	public AppController() {
		orderRepo = new OrderRepository(orderRepoSize);
		orderModel = orderRepo.getOrderModel();
		om = new OrderStateMachine(orderModel, orderRepo);
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

	@RequestMapping("/logoff")
	public String logogff() {
		OrderEvent oe = new OrderEvent();
		oe.put(35, "5");
		queue.send(oe);
		return oe.toString();
	}
	
	@GetMapping("/order/{clOrdId}")
	public String getOrder(@PathVariable String clOrdId) {
		OrderEvent oe = orderModel.getOrder(clOrdId);
		return oe != null ? oe.toString() : "not found";
	}
}
