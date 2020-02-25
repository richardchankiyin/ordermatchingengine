package com.richardchankiyin.ordermatchingengine.order.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;


import com.richardchankiyin.ordermatchingengine.order.OrderEvent;
import com.richardchankiyin.ordermatchingengine.order.model.IOrderModel;

/**
 * This class is to validate all incoming orders before being processed by the
 * order matching engine
 * 
 * @author richard
 *
 */
public class IncomingOrderValidator extends AbstractOrderValidator implements
		IOrderValidator {
	private static final Logger logger = LoggerFactory
			.getLogger(IncomingOrderValidator.class);
	private IOrderModel orderModel;

	private NewOrderSingleOrderValidator nosOrderValidator = null;
	private RequestRequestOrderValidator replaceRequestOrderValidator = null;
	private CancelOrderValidator cancelOrderValidator = null;

	public IncomingOrderValidator(IOrderModel orderModel) {
		Objects.requireNonNull(orderModel, "orderModel is null");
		this.orderModel = orderModel;
		this.nosOrderValidator = new NewOrderSingleOrderValidator();
		this.replaceRequestOrderValidator = new RequestRequestOrderValidator();
		this.cancelOrderValidator = new CancelOrderValidator();
	}

	private final OrderValidationRule DATATYPECHECKING = new OrderValidationRule(
			"DATATYPECHECKING", oe -> {
				Object orderQtyValue = oe.get(38);
				Object priceValue = oe.get(44);

				boolean isOrderQtyValueValid = true;
				boolean isPriceValueValid = true;
				if (orderQtyValue != null) {
					try {
						Integer.parseInt(orderQtyValue.toString());
					} catch (Exception e) {
						isOrderQtyValueValid = false;
					}
				}
				if (priceValue != null) {
					try {
						Double.parseDouble(priceValue.toString());
					} catch (Exception e) {
						isPriceValueValid = false;
					}
				}

				if (isOrderQtyValueValid && isPriceValueValid) {
					return OrderValidationResult.getAcceptedInstance();
				} else {
					StringBuilder rejectReason = new StringBuilder();
					if (!isOrderQtyValueValid) {
						rejectReason.append(String.format(
								"Tag 38: %s is not integer. ", orderQtyValue));
					}
					if (!isPriceValueValid) {
						rejectReason.append(String.format(
								"Tag 44: %s is not numeric. ", priceValue));
					}
					return new OrderValidationResult(rejectReason.toString());
				}
			});

	private final OrderValidationRule MSGTYPECHECKING = new OrderValidationRule(
			"MSGTYPECHECKING", oe -> {
				Object msgTypeValue = oe.get(35);
				Set<String> acceptedMsgTypes = new HashSet<String>(
						Arrays.asList("D", "F", "G"));
				if (msgTypeValue != null) {
					if (!acceptedMsgTypes.contains(msgTypeValue.toString())) {
						return new OrderValidationResult(String.format(
								"Tag 35: %s not accepted. Only accepts: %s .",
								msgTypeValue, acceptedMsgTypes));
					} else {
						return OrderValidationResult.getAcceptedInstance();
					}
				} else {
					return new OrderValidationResult(
							"Type 35 msg type is missing. ");
				}
			});

	private final OrderValidationRule SIDECHECKING = new OrderValidationRule(
			"SIDECHECKING", oe -> {
				Object sideValue = oe.get(54);
				if (sideValue != null) {
					if (!"1".equals(sideValue.toString())
							&& !"2".equals(sideValue.toString())) {
						return new OrderValidationResult(String.format(
								"Tag 54: %s not supported. ",
								sideValue.toString()));
					} else {
						return OrderValidationResult.getAcceptedInstance();
					}
				} else {
					return OrderValidationResult.getAcceptedInstance();
				}
			});

	private final class NewOrderSingleOrderValidator extends
			AbstractPreconditionOrderValidator {

		public NewOrderSingleOrderValidator() {
			super(oe -> oe!=null && "D".equals(oe.get(35)));
		}

		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			return Arrays.asList(NEWORDERSINGLECOMPULSORYFIELDCHECKING,
					NEWORDERSINGLECLIENTORDERIDISNEWCHECKING);
		}
	}

	private final class RequestRequestOrderValidator extends
			AbstractPreconditionOrderValidator {

		public RequestRequestOrderValidator() {
			super(oe -> oe!=null && "G".equals(oe.get(35)));
		}


		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			return Arrays.asList(REPLACEREQUESTCOMPULSORYFIELDCHECKING,
					REPLACEREQUESTANDCANCELREQUESTCLIENTORDERIDISNEWCHECKING,
					REPLACEREQUESTAMENDDOWNCHECKING,
					REPLACEREQUESTOTHERFIELDCHANGECHECKING);
		}
	}

	private final class CancelOrderValidator extends
			AbstractPreconditionOrderValidator {

		public CancelOrderValidator() {
			super(oe -> oe!=null && "F".equals(oe.get(35)));
		}

		@Override
		protected List<IOrderValidator> getListOfOrderValidators() {
			return Arrays.asList(CANCELREQUESTCOMPULSORYFIELDCHECKING
				, REPLACEREQUESTANDCANCELREQUESTCLIENTORDERIDISNEWCHECKING);
		}
	}

	private final OrderValidationRule NEWORDERSINGLECOMPULSORYFIELDCHECKING = new OrderValidationRule(
			"NEWORDERSINGLECOMPULSORYFIELDCHECKING",
			oe -> {
				Object orderTypeValue = oe.get(40);
				if (orderTypeValue != null) {
					if ("1".equals(orderTypeValue.toString())
							|| "2".equals(orderTypeValue.toString())) {
						Object clOrdIdValue = oe.get(11);
						Object sideValue = oe.get(54);
						Object symbolValue = oe.get(55);
						Object priceValue = oe.get(44);
						Object orderQtyValue = oe.get(38);
						if ("1".equals(orderTypeValue.toString())) {
							if (clOrdIdValue == null || sideValue == null
									|| symbolValue == null
									|| orderQtyValue == null) {
								return new OrderValidationResult(
										"Tag 11: ClOrdId, Tag 38: OrderQty, Tag 54: Side and Tag 55: Symbol cannot be missing for market order. ");
							}
						} else {
							if (clOrdIdValue == null || sideValue == null
									|| symbolValue == null
									|| priceValue == null
									|| orderQtyValue == null) {
								return new OrderValidationResult(
										"Tag 11: ClOrdId, Tag 38: OrderQty, Tag 44: Price, Tag 54: Side and Tag 55: Symbol cannot be missing for limit order. ");
							}
						}
						return OrderValidationResult.getAcceptedInstance();
					}
				}
				return new OrderValidationResult(
						String.format(
								"Tag 40: %s is not supported. Only Market(1) and Limit(2) are being supported. ",
								orderTypeValue != null ? orderTypeValue
										: "NULL"));
			});

	private final OrderValidationRule REPLACEREQUESTCOMPULSORYFIELDCHECKING = new OrderValidationRule(
			"REPLACEREQUESTCOMPULSORYFIELDCHECKING",
			oe -> {
				Object msgTypeValue = oe.get(35);
				if (msgTypeValue != null && "G".equals(msgTypeValue.toString())) {
					Object clOrdIdValue = oe.get(11);
					Object orderQtyValue = oe.get(38);
					if (clOrdIdValue == null || orderQtyValue == null) {
						return new OrderValidationResult(
								"Tag 11: ClOrdId, Tag 38: OrderQty cannot be missed in a replace request order. ");
					} else {
						return OrderValidationResult.getAcceptedInstance();
					}
				} else {
					// non replace request skip validation
					return OrderValidationResult.getAcceptedInstance();
				}
			});

	private final OrderValidationRule CANCELREQUESTCOMPULSORYFIELDCHECKING = new OrderValidationRule(
			"CANCELREQUESTCOMPULSORYFIELDCHECKING",
			oe -> {
				Object clOrdIdValue = oe.get(11);
				if (clOrdIdValue == null) {
					return new OrderValidationResult(
							"Tag 11: ClOrdId cannot be missed in a cancel request order. ");
				} else {
					return OrderValidationResult.getAcceptedInstance();
				}

			});

	private final OrderValidationRule NEWORDERSINGLECLIENTORDERIDISNEWCHECKING = new OrderValidationRule(
			"NEWORDERSINGLECLIENTORDERIDISNEWCHECKING",
			oe -> {
				Object msgTypeValue = oe.get(35);
				if (msgTypeValue != null && "D".equals(msgTypeValue.toString())) {
					Object clOrdIdValue = oe.get(11);
					if (clOrdIdValue != null) {
						boolean isIdFound = this.orderModel
								.isClientOrderIdFound(clOrdIdValue.toString());
						if (isIdFound) {
							return new OrderValidationResult(
									String.format(
											"Tag 11: %s is being used in other order. ",
											clOrdIdValue.toString()));
						}
					}
					return OrderValidationResult.getAcceptedInstance();
				} else {
					// non NOS skip validation
					return OrderValidationResult.getAcceptedInstance();
				}
			});

	private final OrderValidationRule REPLACEREQUESTANDCANCELREQUESTCLIENTORDERIDISNEWCHECKING = new OrderValidationRule(
			"REPLACEREQUESTANDCANCELREQUESTCLIENTORDERIDISNEWCHECKING",
			oe -> {
				Object clOrdIdValue = oe.get(11);
				if (clOrdIdValue != null) {
					boolean isIdFound = this.orderModel
							.isClientOrderIdFound(clOrdIdValue.toString());
					if (!isIdFound) {
						return new OrderValidationResult(String.format(
								"Tag 11: %s is not found. ",
								clOrdIdValue.toString()));
					}
				}
				return OrderValidationResult.getAcceptedInstance();

			});

	private final OrderValidationRule REPLACEREQUESTAMENDDOWNCHECKING = new OrderValidationRule(
			"REPLACEREQUESTAMENDDOWNCHECKING",
			oe -> {
				Object msgTypeValue = oe.get(35);
				if (msgTypeValue != null && "G".equals(msgTypeValue.toString())) {
					Object clOrdIdValue = oe.get(11);
					Object orderQtyValue = oe.get(38);
					if (clOrdIdValue != null && orderQtyValue != null) {
						long newOrderQty = 0;
						long oldOrderQty = 0;
						long oldOrderCumQty = 0;
						try {
							OrderEvent oldOe = orderModel.getOrder(clOrdIdValue
									.toString());
							if (oldOe != null) {
								newOrderQty = Long.parseLong(orderQtyValue
										.toString());
								oldOrderQty = Long.parseLong(oldOe.get(38)
										.toString());
								if (oldOe.containsKey(14)) {
									oldOrderCumQty = Long.parseLong(oldOe.get(14).toString());
								}									
							} else {
								// no existing order found, return 
								return OrderValidationResult.getAcceptedInstance();
							}
						} catch (Exception e) {
							logger.info("issues found.", e);
							newOrderQty = 0;
							oldOrderQty = 0;
							// issue found in getting numeric figure, dont continue
							// as below error is not meaningful
							return OrderValidationResult.getAcceptedInstance();
						}

						// amend up rejection
						if (newOrderQty >= oldOrderQty && newOrderQty != 0
								&& oldOrderQty != 0) {
							return new OrderValidationResult(
									String.format(
											"Tag 38: %s is larger/equal to %s which is not amend down for replace request order. ",
											newOrderQty, oldOrderQty));
						}
						
						// amend to value less than cumulative order qty rejection
						if (newOrderQty <= oldOrderCumQty) {
							return new OrderValidationResult(
									String.format(
											"Tag 38: %s is less than/equals to CumQty: %s for replace request order. ",
											newOrderQty, oldOrderCumQty));
						}
					}
					return OrderValidationResult.getAcceptedInstance();
				} else {
					// skip for other message type
					return OrderValidationResult.getAcceptedInstance();
				}
			});

	private final OrderValidationRule REPLACEREQUESTOTHERFIELDCHANGECHECKING = new OrderValidationRule(
			"REPLACEREQUESTOTHERFIELDCHANGECHECKING",
			oe -> {
				Object msgTypeValue = oe.get(35);
				if (msgTypeValue != null && "G".equals(msgTypeValue.toString())) {
					Object clOrdIdValue = oe.get(11);
					try {
						OrderEvent oldOe = orderModel.getOrder(clOrdIdValue
								.toString());
						if (oldOe != null) {
							Object oldSideValue = oldOe.get(54);
							Object oldSymbolValue = oldOe.get(55);
							Object oldOrdTypeValue = oldOe.get(40);
							Object oldPriceValue = oldOe.get(44);

							Object newSideValue = oe.get(54);
							Object newSymbolValue = oe.get(55);
							Object newOrdTypeValue = oe.get(40);
							Object newPriceValue = oe.get(44);

							boolean isReplaceValid = true;

							if (newSideValue != null
									&& !oldSideValue.equals(newSideValue)) {
								isReplaceValid = false;
							}

							if (newSymbolValue != null
									&& !oldSymbolValue.equals(newSymbolValue)) {
								isReplaceValid = false;
							}

							if (newOrdTypeValue != null
									&& !oldOrdTypeValue.equals(newOrdTypeValue)) {
								isReplaceValid = false;
							}

							if (newPriceValue != null
									&& !Objects.equals(oldPriceValue,
											newPriceValue)) {
								isReplaceValid = false;
							}

							if (!isReplaceValid) {
								return new OrderValidationResult(
										"Replace request order cannot alter Tag 54: Side, Tag 55: Symbol, Tag 40: OrderType, Tag 44: Price. ");
							}
						}
					} catch (Exception e) {
						// log exception here
						logger.info("issues found.", e);
					}
					return OrderValidationResult.getAcceptedInstance();
				} else {
					// skip for other message type
					return OrderValidationResult.getAcceptedInstance();
				}
			});

	@Override
	protected List<IOrderValidator> getListOfOrderValidators() {
		return Arrays.asList(
				// rule 1 datatype checking
				DATATYPECHECKING
				// rule 2 msg type checking
				, MSGTYPECHECKING
				// rule 3 side checking
				, SIDECHECKING
				// rule 4a new order single compulsory field checking
				// rule 5a new order single client order id checking
				, nosOrderValidator

				// rule 4b replace request compulsory field checking
				// rule 5b replace request/cancel request client order id
				// rule 6a replace request amend down checking
				// rule 7a replace request other field change checking
				, replaceRequestOrderValidator

				// rule 4c cancel request compulsory field checking
				// rule 5b replace request/cancel request client order id
				, cancelOrderValidator
				);
	}

	protected OrderValidationRule getDataTypeCheckingRule() {
		return DATATYPECHECKING;
	}

	protected OrderValidationRule getMsgTypeCheckingRule() {
		return MSGTYPECHECKING;
	}

	protected OrderValidationRule getSideCheckingRule() {
		return SIDECHECKING;
	}

	protected NewOrderSingleOrderValidator getNosOrderValidator() {
		return nosOrderValidator;
	}

	protected RequestRequestOrderValidator getReplaceRequestOrderValidator() {
		return replaceRequestOrderValidator;
	}
	
	protected CancelOrderValidator getCancelOrderValidator() {
		return cancelOrderValidator;
	}



}
