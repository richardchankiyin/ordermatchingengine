package com.richardchankiyin.spreadcalc;

import java.io.IOException;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.RunnerException;

/**
 * Hello world!
 *
 */
public class SpreadRangeBenchmarkApp 
{
    public static void main( String[] args ) throws RunnerException, IOException
    {
    	org.openjdk.jmh.Main.main(args);
    }
    
    @Benchmark
    @BenchmarkMode(Mode.All)
    public double getSingleSpreadPrice_20_true_50() {
    	double result = SpreadRanges.getInstance().getSingleSpreadPrice(20, true, 50);
    	return result;
    }
}
