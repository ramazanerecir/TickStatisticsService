package com.tasksolactive.model.worker;

import java.text.MessageFormat;
import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;

import com.tasksolactive.entity.Tick;
import com.tasksolactive.model.TickCalculationManager;
import com.tasksolactive.model.TickMessageManager;
import com.tasksolactive.model.TickStatisticsManager;

/*
 * Tick process thread that that takes tick messages and puts to the instrument map
 *
 * */
public class TickProcessThread extends AbstractThread
{	
	private static final Logger LOG = Logger.getLogger(TickProcessThread.class);
	
	public TickProcessThread()
	{
		super();
		this.setDaemon(true);
	}
	
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				Tick tick = TickMessageManager.getInstance().takeTick();
				
				if(!tick.validateTimestamp())
				{
					LOG.warn(MessageFormat.format("Tick {0} is failed to be processed in sliding time interval", tick.toLog()));
					continue;
				}
				
				Lock lock = null;
				
				try
				{
					lock = TickStatisticsManager.getInstance().getInstrumentLock(tick.getInstrument());
					lock.lock();
					setProcessing(true);
					
					checkAggregationCalculationProcess();
					
					if(tick.validateTimestamp() && put(tick))
					{
						TickCalculationManager.getInstance().putInstrument(
								TickStatisticsManager.getInstance()
										.getFilteredTickList(tick.getInstrument()));
					}
					else
					{
						if(!tick.validateTimestamp())
							LOG.warn(MessageFormat.format("Tick {0} is failed to be processed in sliding time interval due to the delay on acquiring lock", tick.toLog()));
					}
				}
				catch (Exception e1)
				{
					LOG.error(e1.getMessage(), e1);
				}
				finally 
				{
					unlock(tick.getInstrument(), lock);
					setProcessing(false);
				}
			}
			catch (Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
			
		}
	}
	
	private boolean put(Tick tick)
	{
		try 
		{
			LOG.trace(MessageFormat.format("Adding incoming Tick : {0}", tick.toLog()));
			TickStatisticsManager.getInstance().putTick(tick);
			
			return true;
		} 
		catch (Exception e) 
		{
			LOG.error(MessageFormat.format("Tick {0} cannot be put to instrumentMap. It will be added to message queue", tick.toLog()));
			LOG.error(e.getMessage(), e);
			
			TickMessageManager.getInstance().putTick(tick);
			return false;
		}
	}
}
