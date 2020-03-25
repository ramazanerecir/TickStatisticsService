package com.tasksolactive.model;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.tasksolactive.AppData;
import com.tasksolactive.enumtype.CalculationType;
import com.tasksolactive.model.worker.TickCalculationThread;
import com.tasksolactive.model.worker.TickCalculationThreadPooler;
import com.tasksolactive.model.worker.TickCleanerThread;
import com.tasksolactive.model.worker.TickProcessThreadPooler;

/*
 * Scheduling aggregated calculation thread and starting tick cleaner thread
 *
 * */
public class SchedulerService 
{
	private static final Logger LOG = Logger.getLogger(SchedulerService.class);
	
	private static SchedulerService instance;
	
	private TickProcessThreadPooler processThreadPooler;
	private TickCalculationThreadPooler calculationThreadPooler;
	private ScheduledExecutorService executorService;
	private TickCleanerThread tickCleanerThread;
	
	public SchedulerService()
	{
		//
	}
	
	public static SchedulerService getInstance()
	{
		if (instance == null)
		{
			instance = new SchedulerService();
		}
		return instance;
	}
	
	public void start()
	{
		processThreadPooler = new TickProcessThreadPooler(AppData.TICK_PROCESS_POOLER_SIZE);
		processThreadPooler.init();
		
		calculationThreadPooler = new TickCalculationThreadPooler(AppData.TICK_CALCULATION_POOLER_SIZE);
		calculationThreadPooler.init();
		
		scheduleAggregatedCalculation();
		startTickCleanerThread();
		
		LOG.debug("SchedulerService started");
	}
	
	private void scheduleAggregatedCalculation()
	{
		LOG.debug("Scheduling aggregated calculation thread");
		
		executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				new TickCalculationThread(CalculationType.AGGREGATED).start();
			}
		}, 
				0, AppData.CALCULATION_AGGREGATED_TIMER, TimeUnit.MILLISECONDS);
	}
	
	private void startTickCleanerThread()
	{
		LOG.debug("Starting tick cleaner thread");
		
		tickCleanerThread = new TickCleanerThread();
		tickCleanerThread.start();
	}
	
	public void stop()
	{
		processThreadPooler.stop();
		calculationThreadPooler.stop();
		
		executorService.shutdown();
		tickCleanerThread.interrupt();
				
		LOG.debug("SchedulerService stop");
	}
}
