package com.richardchankiyin.spreadcalc;


public class SpreadRange {
	private static final double VERY_LITTLE_NUM = 0.0000001;
	private double startFrom;
	private double endWith;
	private double spread;
	
	public double getStartFrom() {
		return startFrom;
	}
	public double getEndWith() {
		return endWith;
	}
	public double getSpread() {
		return spread;
	}
	
	public SpreadRange(double startFrom, double endWith, double spread) {
		// positive checks:
		if (startFrom <= 0)
			throw new IllegalArgumentException("startFrom < 0");
		if (endWith <= 0) 
			throw new IllegalArgumentException("endWith < 0");
		if (spread <= 0)
			throw new IllegalArgumentException("spread < 0");
		
		// range check:
		if (startFrom >= endWith)
			throw new IllegalArgumentException("startWith: " + startFrom + " >= endWith: " + endWith);
		
		// spread check
		double noOfSpreadDouble = (endWith - startFrom)/spread;
		if (noOfSpreadDouble < 1)
			throw new IllegalArgumentException("noOfSpread supporting: " + noOfSpreadDouble + " < l");
		double residual = Math.IEEEremainder((endWith - startFrom), spread);
		if (!isLogicallyZero(residual))
			throw new IllegalArgumentException("residual: " + residual + " > 0");
		
		
		this.startFrom = startFrom;
		this.endWith = endWith;
		this.spread = spread;
	}
	
	private boolean isLogicallyZero(double num) {
		return num < VERY_LITTLE_NUM && num > VERY_LITTLE_NUM * -1;
	}
	
	public boolean isInRange(double spot, boolean isGoUp) {
		// if isGoUp true, exclude endWith; else exclude startWith
		if (isGoUp) {
			if (!(spot < this.endWith && spot >= this.startFrom))
				return false;
		} else {
			if (!(spot <= this.endWith && spot > this.startFrom))
				return false;
		}
		
		// edge case acceptance
		if (spot == this.startFrom || spot == this.endWith)
			return true;
		
		double residual = Math.IEEEremainder((spot - startFrom),spread);
		return isLogicallyZero(residual);		
	}
	
	public double displace(double spot, boolean isGoUp, int noOfSpreads) {
		if (noOfSpreads < 1)
			throw new IllegalArgumentException("noOfSpreads: " + noOfSpreads + " < 1");
		boolean isInRange = isInRange(spot, isGoUp);
		if (isInRange == false)
			throw new IllegalArgumentException("isInRange (spot: " + spot + " isGoUp:" + isGoUp + " not in range!");
		double result = 0;
		if (isGoUp) {
			result = spot + this.spread * noOfSpreads;
		} else {
			result = spot - this.spread * noOfSpreads;
		}
		
		if (result >= this.startFrom && result <= this.endWith) {
			return result;
		} else {
			throw new IllegalArgumentException("Result: " + result + " not in btw of start from: " + this.startFrom + " and end with: " + this.endWith);
		}
	}
}
