package com.tasksolactive.model;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.tasksolactive.entity.Tick;

/*
 * Managing statistics calculation message queue for instruments for which statistics need to be recalculated
 *
 * Queue has pair of Instrument as String and ticks as List which belongs to this instrument
 * */
public class TickCalculationManager 
{
	private static final Logger LOG = Logger.getLogger(TickCalculationManager.class);
	
	private static TickCalculationManager instance;
	
	private LinkedBlockingQueue<Entry<String, List<Tick>>> instrumentQueue;
	
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
	
	public void putInstrument(Entry<String, List<Tick>> instrumentEntry)
	{
		try 
		{
			instrumentQueue.put(instrumentEntry);
		} 
		catch (InterruptedException e) 
		{
			LOG.error(MessageFormat.format("Failed to put instrument {0} to instrumentQueue", 
					instrumentEntry.getKey()));
			LOG.error(e,e);
		}
	}
	
	public Entry<String, List<Tick>> takeInstrument()
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
