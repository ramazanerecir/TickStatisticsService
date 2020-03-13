package com.tasksolactive.model.worker;

import java.util.concurrent.locks.Lock;

import com.tasksolactive.model.TickStatisticsManager;
import com.tasksolactive.utils.StopWatch;

/*
 * AbstractThread to manage threads easily using common methods
 *
 * */
public abstract class AbstractThread extends Thread
{
	private boolean isProcessing;
	private StopWatch stopWatch;
	
	public AbstractThread()
	{
		stopWatch = new StopWatch();
	}
	
	@Override
	public abstract void run();
	
	public boolean isProcessing() 
	{
		return isProcessing;
	}
	
	public void setProcessing(boolean isProcessing) {
		this.isProcessing = isProcessing;
	}

	/*
	 * Thread Sleep until fetching all tick list for aggregation calculation
	 * */
	public void checkAggregationCalculationProcess()
	{
		while(TickStatisticsManager.getInstance().isAggrCalculationInProgress())
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				//
			}
		}
	}
	
	public void startStopWatch()
	{
		stopWatch.start();
	}
	
	public void stopStopWatch()
	{
		stopWatch.stop();
	}
	
	public void resetStopWatch()
	{
		stopWatch.reset();
	}
	
	public String getElapsedTime()
	{
		return stopWatch.getElapsedTime();
	}
	
	public void unlock(String instrument, Lock lock)
	{
		if(lock != null)
		{
			lock.unlock();
			TickStatisticsManager.getInstance().releaseInstrumentLock(instrument, lock);
		}
	}

}
