package com.richardchankiyin.ordermatchingengine.order;

import java.util.HashMap;

public class OrderEvent extends HashMap<Integer, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public OrderEvent() {
		super();
	}
	
	public OrderEvent(OrderEvent oe) {
		super(oe);
	}
}
