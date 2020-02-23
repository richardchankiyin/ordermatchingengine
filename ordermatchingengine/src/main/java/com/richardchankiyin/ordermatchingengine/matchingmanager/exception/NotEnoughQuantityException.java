package com.richardchankiyin.ordermatchingengine.matchingmanager.exception;

public class NotEnoughQuantityException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2861216311052338013L;
	
	public NotEnoughQuantityException(long quantity) {
		super("do not have enough quantity. quantity unreserved: " + quantity);
	}
	
	public NotEnoughQuantityException() {
		this(0);
	}

}
