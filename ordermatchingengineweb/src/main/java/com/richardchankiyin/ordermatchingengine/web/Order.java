package com.richardchankiyin.ordermatchingengine.web;

import com.richardchankiyin.ordermatchingengine.order.OrderEvent;

public class Order {
	private String clOrdId;
	private String msgType;
	private String side;
	private String symbol;
	private String ordType;
	private Double price;
	private Long quantity;
	public String getClOrdId() {
		return clOrdId;
	}
	public void setClOrdId(String clOrdId) {
		this.clOrdId = clOrdId;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public String getOrdType() {
		return ordType;
	}
	public void setOrdType(String ordType) {
		this.ordType = ordType;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	
	public OrderEvent toOrderEvent() {
		OrderEvent oe = new OrderEvent();
		if (clOrdId != null)
			oe.put(11, clOrdId);
		if (msgType != null)
			oe.put(35, msgType);
		if (side != null)
			oe.put(54, side);
		if (symbol != null)
			oe.put(55, symbol);
		if (ordType != null)
			oe.put(40, ordType);
		if (price != null)
			oe.put(44, price);
		if (quantity != null)
			oe.put(38, quantity);
		
		return oe;
	}
}
