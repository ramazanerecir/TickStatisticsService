package com.tasksolactive.model.worker;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.tasksolactive.enumtype.CalculationType;

/*
 * Tick calculation thread pooler for calculating statistics for instrument
 *
 * */
public class TickCalculationThreadPooler
{
	private static final Logger LOG = Logger.getLogger(TickCalculationThreadPooler.class);
	
	private int poolerSize;
	private Set<TickCalculationThread> poolerSet;
	
	public TickCalculationThreadPooler(int poolerSize)
	{
		super();
		this.poolerSize = poolerSize;
		poolerSet = Collections.synchronizedSet(new HashSet<TickCalculationThread>());
	}
	
	public void init()
	{
		LOG.debug("Initializing TickCalculationThreadPooler");
		poolerSet.clear();
		
		for (int i = 0; i < poolerSize; i++)
		{
			TickCalculationThread pooler = new TickCalculationThread(CalculationType.SINGLE);
			poolerSet.add(pooler);
			pooler.start();
		}
		LOG.debug(MessageFormat.format("TickCalculationThreadPooler initialized with {0} threads", poolerSize));
	}
	
	public void stop()
	{	
		LOG.debug("TickCalculationThreadPooler stop start");
		
		for (TickCalculationThread pooler : poolerSet)
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
		
		LOG.debug("TickCalculationThreadPooler stop end");
	}
}
