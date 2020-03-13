package com.tasksolactive.model;

import java.text.MessageFormat;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.tasksolactive.entity.Tick;

/*
 * Managing tick message queue which are coming from tick service
 *
 * */
public class TickMessageManager 
{
	private static final Logger LOG = Logger.getLogger(TickMessageManager.class);
	
	private static TickMessageManager instance;
	
	private LinkedBlockingQueue<Tick> tickQueue;
	
	private TickMessageManager()
	{
		//
	}
	
	public static TickMessageManager getInstance()
	{
		if (instance == null)
		{
			instance = new TickMessageManager();
		}
		return instance;
	}
	
	public void init()
	{
		tickQueue = new LinkedBlockingQueue<>();
		LOG.debug("TickMessageManager initialized");
	}
	
	public void putTick(Tick tick)
	{
		try 
		{
			tickQueue.put(tick);
		} 
		catch (InterruptedException e) 
		{
			LOG.error(MessageFormat.format("Failed to put tickQueue {0} to instrumentQueue", tick.toLog()));
			LOG.error(e,e);
		}
	}
	
	public Tick takeTick()
	{
		try
		{
			return tickQueue.take();
		}
		catch(Exception e)
		{
			return null;
		}
	}
}
