package com.richardchankiyin.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumericUtils {
	public static double roundDouble(double input, int roundScale) {
		return new BigDecimal(String.format("%s", input)).setScale(roundScale,RoundingMode.HALF_UP).doubleValue();
	}
}
