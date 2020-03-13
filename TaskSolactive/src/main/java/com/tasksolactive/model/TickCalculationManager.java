package com.tasksolactive.model;

import java.text.MessageFormat;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

/*
 * Managing statistics calculation message queue for instruments for which statistics need to be recalculated
 *
 * List<Tick> can also be used in instrumentQueue in order to reduce locks (by instrument),
 * but this time memory usage will increase 
 * */
public class TickCalculationManager 
{
	private static final Logger LOG = Logger.getLogger(TickCalculationManager.class);
	
	private static TickCalculationManager instance;
	
	private LinkedBlockingQueue<String> instrumentQueue;
	
	private TickCalculationManager()
	{
		//
	}
	
	public static TickCalculationManager getInstance()
	{
		if (instance == null)
		{
			instance = new TickCalculationManager();
		}
		return instance;
	}
	
	public void init()
	{
		instrumentQueue = new LinkedBlockingQueue<>();
		LOG.debug("TickCalculationManager initialized");
	}
	
	public void putInstrument(String instrument)
	{
		try 
		{
			instrumentQueue.put(instrument);
		} 
		catch (InterruptedException e) 
		{
			LOG.error(MessageFormat.format("Failed to put instrument {0} to instrumentQueue", instrument));
			LOG.error(e,e);
		}
	}
	
	public String takeInstrument()
	{
		try
		{
			return instrumentQueue.take();
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
