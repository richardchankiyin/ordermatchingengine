package com.richardchankiyin.ordermatchingengine.order.messagequeue;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public interface IOrderMessageQueueReceiver {
	public void onEvent(OrderEvent oe);
}
