package com.tasksolactive.entity;

/*
 * Statistics entity for aggregated and each instrument
 * 
 * */
public class Statistics 
{
	private double avg;
	private double max;
	private double min;
	private double maxDrawdown;
	private double volatility;
	private double quantile;
	private double twap;
	private double twap2;
	private int count;
	
	public Statistics()
	{
		super();
		
		avg = 0.0;
		max = 0.0;
		min = 0.0;
		maxDrawdown = 0.0;
		volatility = 0.0;
		quantile = 0.0;
		twap = 0.0;
		twap2 = 0.0;
		count = 0;
	}
	
	public Statistics(Tick tick)
	{
		super();
		
		avg = tick.getPrice();
		max = tick.getPrice();
		min = tick.getPrice();
		maxDrawdown = 0.0;
		volatility = 0.0;
		quantile = 0.0;
		twap = tick.getPrice();
		twap2 = tick.getPrice();
		count = 1;
	}
	
	public double getAvg() {
		return avg;
	}
	public void setAvg(double avg) {
		this.avg = avg;
	}
	public double getMax() {
		return max;
	}
	public void setMax(double max) {
		this.max = max;
	}
	public double getMin() {
		return min;
	}
	public void setMin(double min) {
		this.min = min;
	}
	public double getMaxDrawdown() {
		return maxDrawdown;
	}
	public void setMaxDrawdown(double maxDrawdown) {
		this.maxDrawdown = maxDrawdown;
	}
	public double getVolatility() {
		return volatility;
	}
	public void setVolatility(double volatility) {
		this.volatility = volatility;
	}
	public double getQuantile() {
		return quantile;
	}
	public void setQuantile(double quantile) {
		this.quantile = quantile;
	}
	public double getTwap() {
		return twap;
	}
	public void setTwap(double twap) {
		this.twap = twap;
	}
	public double getTwap2() {
		return twap2;
	}
	public void setTwap2(double twap2) {
		this.twap2 = twap2;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
