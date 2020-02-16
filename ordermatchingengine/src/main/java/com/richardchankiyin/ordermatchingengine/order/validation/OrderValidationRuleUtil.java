package com.richardchankiyin.ordermatchingengine.order.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class OrderValidationRuleUtil {
	private static final Logger logger = LoggerFactory.getLogger(OrderValidationRuleUtil.class);
	private static final OrderValidationRule ADDORDERMSGTYPECHECKING
	= new OrderValidationRule("ADDORDERMSGTYPECHECKING", oe->{
		Object msgType = oe.get(35);
		if (msgType == null) {
			return new OrderValidationResult("Tag 35: MsgType cannot be missing. ");
		} else {
			if (!"D".equals(msgType)) {
				return new OrderValidationResult("Tag 35: MsgType can only be D. ");
			}
		}
		
		return OrderValidationResult.getAcceptedInstance();
	});
	
	private static final OrderValidationRule ADDORDERQTYCHECKING
	= new OrderValidationRule("ADDORDERQTYCHECKING", oe->{
		Object qty = oe.get(38);
		if (qty == null) {
			return new OrderValidationResult("Tag 38: Qty cannot be missing. ");
		} else {
			long qtyLong = 0;
			try {
				qtyLong = Long.parseLong(qty.toString());
			}
			catch (Exception e) {
				logger.debug("qty parsing issue", e);
				return new OrderValidationResult("Tag 38: Qty must be integer. ");
			}
			if (qtyLong <= 0) {
				return new OrderValidationResult("Tag 38: Qty must be positive. ");
			}
			
			return OrderValidationResult.getAcceptedInstance();
		}
	});
	
	private static final OrderValidationRule ADDORDERCUMQTYCHECKING
	= new OrderValidationRule("ADDORDERCUMQTYCHECKING", oe->{
		Object cumQty = oe.get(14);
		if (cumQty == null) {
			// accept cumQty missing
			return OrderValidationResult.getAcceptedInstance();
		} else {
			long cumQtyLong = 0;
			try {
				cumQtyLong = Long.parseLong(cumQty.toString());
			}
			catch (Exception e) {
				logger.debug("cumQty", e);
				return new OrderValidationResult("Tag 14: CumQty must be integer. ");
			}
			if (cumQtyLong <= 0) {
				return new OrderValidationResult("Tag 14: CumQty must be positive. ");
			}
			
			Object qty = oe.get(38);
			long qtyLong = 0;
			try {
				qtyLong = Long.parseLong(qty.toString());
				if (cumQtyLong >=  qtyLong) {
					return new OrderValidationResult(String.format("Tag 14: CumQty %s cannot be larger than/equals to Tag 38: Qty %s",cumQtyLong,qtyLong));
				}
			}
			catch (Exception e) {
				logger.debug("qty parsing issue", e);
			}
			return OrderValidationResult.getAcceptedInstance();
		}
	});
	
	private static final OrderValidationRule UPDATEORDERMSGTYPECHECKING
	= new OrderValidationRule("UPDATEORDERMSGTYPECHECKING", oe->{
		Object msgType = oe.get(35);
		if (msgType == null) {
			return new OrderValidationResult("Tag 35: MsgType cannot be missing. ");
		} else {
			if (!"G".equals(msgType)) {
				return new OrderValidationResult("Tag 35: MsgType can only be G. ");
			}
		}
		
		return OrderValidationResult.getAcceptedInstance();
	});
	
	public static OrderValidationRule getAddOrderMsgTypeChecking() {
		return ADDORDERMSGTYPECHECKING;
	}
	
	public static OrderValidationRule getAddOrderQtyChecking() {
		return ADDORDERQTYCHECKING;
	}
	
	public static OrderValidationRule getAddOrderCumQtyChecking() {
		return ADDORDERCUMQTYCHECKING;
	}
	
	public static OrderValidationRule getUpdateOrderMsgTypeChecking() {
		return UPDATEORDERMSGTYPECHECKING;
	}
}
