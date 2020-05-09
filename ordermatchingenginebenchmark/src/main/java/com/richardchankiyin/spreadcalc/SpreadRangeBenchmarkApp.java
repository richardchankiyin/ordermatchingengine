package com.richardchankiyin.spreadcalc;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;

/**
 * Hello world!
 *
 */
public class SpreadRangeBenchmarkApp 
{
    @Benchmark
    @BenchmarkMode(Mode.All)
    public double getSingleSpreadPrice_20_true_50() {
    	double result = SpreadRanges.getInstance().getSingleSpreadPrice(20, true, 50);
    	return result;
    }
}
