package com.tasksolactive.model.worker;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.tasksolactive.AppData;
import com.tasksolactive.entity.Tick;
import com.tasksolactive.model.TickCalculationManager;
import com.tasksolactive.model.TickStatisticsManager;

/*
 * Tick cleaner thread that removes ticks which is out of sliding time interval
 *
 * */
public class TickCleanerThread extends AbstractThread
{
	private static final Logger LOG = Logger.getLogger(TickCleanerThread.class);
	
	public TickCleanerThread()
	{
		super();
		this.setDaemon(true);
	}
	
	@Override
	public void run()
	{
		while(true)
		{			
			startStopWatch();
			Set<String> instrumentSet = TickStatisticsManager.getInstance().getInstrumentMapKeySet();
			for(String instrument : instrumentSet)
			{
				Lock lock = null;
				
				try
				{
					lock = TickStatisticsManager.getInstance().getInstrumentLock(instrument);
					lock.lock();
					setProcessing(true);
					
					cleanInstrumentTicks(instrument);
				}
				catch (Exception e)
				{
					LOG.error(e.getMessage(), e);
				}
				finally 
				{
					unlock(instrument, lock);
					setProcessing(false);
				}
			}
			
			stopStopWatch();
			LOG.trace(MessageFormat.format("Cleaner thread is completed in {0} for {1} number of instrments"
					, getElapsedTime(), instrumentSet.size()));
			
			//Sleep for a while
			try {
				Thread.sleep(AppData.TICK_CLEANER_INTERVAL);
			} catch (InterruptedException e) {
				//
			}
		}
	}
	
	/*
	 * Cleans ticks which are out of time sliding interval for given instrument.
	 * if there is no more ticks for this instrument, it is removed from maps.
	 * otherwise, instrument is sent to statistics calculation queue.
	 * Returns whether calculation is required
	 * */
	private boolean cleanInstrumentTicks(String instrument)
	{
		List<Tick> tickList = TickStatisticsManager.getInstance().getTickList(instrument);
		
		if(!tickList.isEmpty())
		{
			checkAggregationCalculationProcess();
			
			TickStatisticsManager.getInstance().putInstrumentMap(instrument, 
							tickList.parallelStream()
							.filter(Tick::validateTimestamp)
							.collect(Collectors.toList()));

			int lastSize = TickStatisticsManager.getInstance().getTickList(instrument).size();
			
			if(lastSize == 0)
			{
				TickStatisticsManager.getInstance().removeInstrument(instrument);
			}
			else
			{
				TickCalculationManager.getInstance().putInstrument(
						TickStatisticsManager.getInstance().getFilteredTickList(instrument));
				return true;
			}
		}
		else
		{
			TickStatisticsManager.getInstance().removeInstrument(instrument);
		}
		
		return false;
	}
}
