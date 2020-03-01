package com.richardchankiyin.ordermatchingengine.web;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
import com.richardchankiyin.ordermatchingengine.order.validation.IncomingOrderValidator;
import com.richardchankiyin.ordermatchingengine.order.validation.OrderValidationResult;
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
	IncomingOrderValidator incomingOrderValidator = null;
	IPublisher publisher = null;
	int orderMsgQueueSize = 100;
	OrderMessageQueue queue = null;
	
	public AppController() {
		orderRepo = new OrderRepository(orderRepoSize);
		orderModel = orderRepo.getOrderModel();
		incomingOrderValidator = new IncomingOrderValidator(orderModel);
		om = new OrderStateMachine(orderModel, orderRepo);
		publisher = new Publisher(new AppEventOutputPublisher());
		matchingManager = new MatchingManager(om, publisher);
		queue = new OrderMessageQueue(symbol + "_queue", matchingManager, orderMsgQueueSize);
		queue.start();
	}
	
	@RequestMapping("/")
	public String index() {
		return matchingManager != null ? matchingManager.toString() : "matching manager not found";
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
	
	@PostMapping("/order")
	public String newOrder(@RequestBody Order order) {
		OrderEvent oe = order.toOrderEvent();
		String clOrdId = UUID.randomUUID().toString();
		oe.put(11, clOrdId);
		oe.put(35, "D");
		OrderValidationResult validationResult = incomingOrderValidator.validate(oe);
		if (validationResult.isAccepted()) {
			queue.send(oe);
			return "new order sent with clOrdId: " + clOrdId;
		} else {
			return validationResult.getRejectReason();
		}
	}
	
	@PutMapping("/order")
	public String replaceOrder(@RequestBody Order order) {
		OrderEvent oe = order.toOrderEvent();
		oe.put(35, "G");
		OrderValidationResult validationResult = incomingOrderValidator.validate(oe);
		if (validationResult.isAccepted()) {
			queue.send(oe);
			return "replace request order sent with clOrdId: " + oe.get(11);
		} else {
			return validationResult.getRejectReason();
		}
	}
	
	
	@DeleteMapping("/order/{clOrdId}")
	public String cancelOrder(@PathVariable String clOrdId) {
		OrderEvent oe = new OrderEvent();
		oe.put(11, clOrdId);
		oe.put(35, "F");
		oe.put(55, symbol);
		OrderValidationResult validationResult = incomingOrderValidator.validate(oe);
		if (validationResult.isAccepted()) {
			queue.send(oe);
			return "cancel order sent with clOrdId: " + oe.get(11);
		} else {
			return validationResult.getRejectReason();
		}
	}
}
