package com.tasksolactive.model.worker;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.tasksolactive.Constants;
import com.tasksolactive.entity.Tick;
import com.tasksolactive.enumtype.CalculationType;
import com.tasksolactive.model.TickCalculationManager;
import com.tasksolactive.model.TickStatisticsManager;
import com.tasksolactive.model.calculation.StatisticsCalculator;

/*
 * Calculation thread listens instrument queue and take instrument-ticks pair and run statistic calculator
 * If calculation is an Aggregated calculation, it fetches all the ticks from TickStatisticsManager
 *
 * */
public class TickCalculationThread extends AbstractThread
{
	private static final Logger LOG = Logger.getLogger(TickCalculationThread.class);
	
	private CalculationType calculationType;
	private StatisticsCalculator calculator;
	
	public TickCalculationThread(CalculationType calculationType)
	{
		super();
		this.calculator = new StatisticsCalculator();
		this.calculationType = calculationType;
		
		this.setDaemon(!isAggregatedCalculationProcess());
	}
	
	@Override
	public void run()
	{
		boolean isRunning = true;
		while(isRunning)
		{
			Entry<String, List<Tick>> instrumentTicksEntry = null;
			try
			{
				setProcessing(true);
				
				instrumentTicksEntry = getInstrument();
				
				startStopWatch();
				if(instrumentTicksEntry.getValue() != null && !instrumentTicksEntry.getValue().isEmpty())
				{
					TickStatisticsManager.getInstance().refreshStatistics(instrumentTicksEntry.getKey(),
							calculator.calculate(instrumentTicksEntry.getKey(), instrumentTicksEntry.getValue()));
				}
			}
			catch (Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
			finally 
			{
				isRunning = !isAggregatedCalculationProcess();
				setProcessing(false);
				stopStopWatch();
				LOG.trace(MessageFormat.format("Instrument {0} calculation is completed in {1}", 
						instrumentTicksEntry != null ? instrumentTicksEntry.getKey() : "", 
						getElapsedTime()));
			}
		}
	}
	
	/*
	 * It listens and takes instrument-ticks pair from instrument queue
	 * If calculation is an Aggregated calculation, it fetches all the ticks from TickStatisticsManager
	 * 
	 * Returns String, List<Tick> entry : instrument as String and its ticks as list
	 *
	 * */
	private Entry<String, List<Tick>> getInstrument()
	{
		if(isAggregatedCalculationProcess())
		{
			try
			{
				TickStatisticsManager.getInstance().setAggrCalculationInProgress(true);
				return TickStatisticsManager.getInstance().getFilteredTickList(Constants.ALL_INSTRUMENTS);
			}
			finally 
			{
				TickStatisticsManager.getInstance().setAggrCalculationInProgress(false);
			}
		}
		else
		{
			return TickCalculationManager.getInstance().takeInstrument();
		}
	}
	
	private boolean isAggregatedCalculationProcess()
	{
		return calculationType == CalculationType.AGGREGATED;
	}
}
