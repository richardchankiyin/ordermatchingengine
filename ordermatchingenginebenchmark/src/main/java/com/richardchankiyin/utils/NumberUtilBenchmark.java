package com.richardchankiyin.utils;


import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

import com.richardchankiyin.utils.NumericUtils;

public class NumberUtilBenchmark {
	private static final double longdouble = 200.1111255554444556666678555223333;

	@Benchmark
    @BenchmarkMode(Mode.All)
	public double roundDouble_longdouble_6() {
		return NumericUtils.roundDouble(longdouble, 6);
	}
}
