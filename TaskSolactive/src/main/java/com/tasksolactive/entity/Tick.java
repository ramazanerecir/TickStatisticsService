package com.tasksolactive.entity;

import java.sql.Timestamp;
import java.util.Date;

import com.tasksolactive.AppData;

/*
 * Tick entity for instrument price
 * 
 * */
public class Tick
{
	private String instrument;
	private double price;
	private long timestamp;
	
	public Tick()
	{
		
	}
	
	public Tick(String instrument, double price, long timestamp) 
	{
		super();
		this.instrument = instrument;
		this.price = price;
		this.timestamp = timestamp;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	/*
	 * Validating whether Tick is in sliding time interval
	 * */
	public boolean validateTimestamp()
	{
		Timestamp time = new Timestamp(new Date().getTime());
		return this.getTimestamp() >= (time.getTime()-AppData.TICK_SLIDING_TIME_INTERVAL);
	}
	
	/*
	 * Validating Tick by instrument name, timestamp and price
	 * */
	public boolean validateTick()
	{
		return (!(!validateTimestamp() ||
				this.getInstrument() == null ||
				this.getInstrument().isEmpty() ||
				Double.isNaN(this.getPrice()) ||
				Double.isInfinite(this.getPrice()) ||
				this.getPrice() <= 0.0));
	}
	
	/*
	 * Creating copy of Tick without instrument name which is not required in calculation queue
	 * */
	public Tick lightCopy()
	{
		Tick tick = new Tick();
		tick.price = this.price;
		tick.timestamp = this.timestamp;
		return tick;
	}
	
	public String toLog()
	{
		StringBuilder builder = new StringBuilder();
		builder.append(instrument);
		builder.append(", ");
		builder.append(price);
		builder.append(",");
		builder.append(timestamp);
		
		return builder.toString();
	}
}
