package com.tasksolactive.model.worker;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/*
 * Tick process thread pooler for processing incoming tick requests
 *
 * */
public class TickProcessThreadPooler
{
	private static final Logger LOG = Logger.getLogger(TickProcessThreadPooler.class);
	
	private int poolerSize;
	private Set<TickProcessThread> poolerSet;
	
	public TickProcessThreadPooler(int poolerSize)
	{
		super();
		this.poolerSize = poolerSize;
		poolerSet = Collections.synchronizedSet(new HashSet<TickProcessThread>());
	}
	
	public void init()
	{
		LOG.debug("Initializing TickProcessThreadPooler");
		poolerSet.clear();
		
		for (int i = 0; i < poolerSize; i++)
		{
			TickProcessThread pooler = new TickProcessThread();
			poolerSet.add(pooler);
			pooler.start();
		}

		LOG.debug(MessageFormat.format("TickProcessThreadPooler initialized with {0} threads", poolerSize));
	}
	
	public void stop()
	{
		LOG.debug("TickProcessThreadPooler stop start");
		
		for (TickProcessThread pooler : poolerSet)
		{
			try 
			{
				while(pooler.isProcessing())
				{
					Thread.sleep(100);
				}
			} 
			catch (InterruptedException e) 
			{
				//
			}
			finally
			{
				pooler.interrupt();
			}
		}
		poolerSet.clear();
		
		LOG.debug("TickProcessThreadPooler stop end");
	}
}
