package com.richardchankiyin.ordermatchingengine.order;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public class OrderEventView extends OrderEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5953377782015584019L;

	public OrderEventView(OrderEvent oe) {
		super(oe);
	}

	@Override
	public Object put(Integer key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object putIfAbsent(Integer key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean remove(Object key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean replace(Integer key, Object oldValue, Object newValue) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object replace(Integer key, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object computeIfAbsent(Integer key,
			Function<? super Integer, ? extends Object> mappingFunction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object computeIfPresent(
			Integer key,
			BiFunction<? super Integer, ? super Object, ? extends Object> remappingFunction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object compute(
			Integer key,
			BiFunction<? super Integer, ? super Object, ? extends Object> remappingFunction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object merge(
			Integer key,
			Object value,
			BiFunction<? super Object, ? super Object, ? extends Object> remappingFunction) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void replaceAll(
			BiFunction<? super Integer, ? super Object, ? extends Object> function) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object clone() {
		throw new UnsupportedOperationException();
	}
	
	
}
