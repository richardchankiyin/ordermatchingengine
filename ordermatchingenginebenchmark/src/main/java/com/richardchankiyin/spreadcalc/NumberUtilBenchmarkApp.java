package com.richardchankiyin.spreadcalc;

import java.io.IOException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.RunnerException;

import com.richardchankiyin.utils.NumericUtils;

public class NumberUtilBenchmarkApp {
	private static final double longdouble = 200.1111255554444556666678555223333;
	public static void main(String[] args) throws RunnerException, IOException {
		org.openjdk.jmh.Main.main(args);
	}

	@Benchmark
    @BenchmarkMode(Mode.All)
	public double roundDouble_longdouble_6() {
		return NumericUtils.roundDouble(longdouble, 6);
	}
}
