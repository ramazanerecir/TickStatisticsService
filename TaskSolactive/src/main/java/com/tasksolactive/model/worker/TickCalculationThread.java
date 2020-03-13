package com.tasksolactive.model.worker;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.tasksolactive.Constants;
import com.tasksolactive.entity.Tick;
import com.tasksolactive.enumtype.CalculationType;
import com.tasksolactive.model.TickCalculationManager;
import com.tasksolactive.model.TickStatisticsManager;
import com.tasksolactive.model.calculation.StatisticsCalculator;

/*
 * Calculation thread that fetches tick list by instrument and run statistic calculator
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
			String instrument = null;
			try
			{
				instrument = getInstrument();
				
				startStopWatch();
				List<Tick> calcTickList = null;
				Lock lock = null;
				try
				{
					lock = TickStatisticsManager.getInstance().getInstrumentLock(instrument);
					lock.lock();
					setProcessing(true);
					
					processByAggregation(true);
					
					calcTickList = getFilteredTickList(instrument);
				}
				catch (Exception e1)
				{
					LOG.error(e1.getMessage(), e1);
				}
				finally 
				{
					isRunning = processByAggregation(false);
					
					unlock(instrument, lock);
				}
				
				TickStatisticsManager.getInstance().refreshStatistics(instrument,
						calculator.calculate(instrument, calcTickList));
			}
			catch (Exception e)
			{
				LOG.error(e.getMessage(), e);
			}
			finally 
			{
				setProcessing(false);
				stopStopWatch();
				LOG.trace(MessageFormat.format("Instrument {0} calculation is completed in {1}", instrument, getElapsedTime()));
			}
		}
	}
	
	private String getInstrument()
	{
		if(isAggregatedCalculationProcess())
		{
			return Constants.ALL_INSTRUMENTS;
		}
		else
		{
			String instrument = TickCalculationManager.getInstance().takeInstrument();
			if(instrument.equals(Constants.ALL_INSTRUMENTS))
				calculationType = CalculationType.AGGREGATED;
			return instrument;
		}
	}
	
	private boolean isAggregatedCalculationProcess()
	{
		return calculationType == CalculationType.AGGREGATED;
	}
	
	/*
	 * If Calculation Type is aggregation, On/Off Aggregation calculation in progress
	 * according to status parameter.
	 * Returns running status
	 * */
	private boolean processByAggregation(boolean aggregationStatus)
	{
		if(aggregationStatus)
		{
			if(isAggregatedCalculationProcess())
			{
				TickStatisticsManager.getInstance().setAggrCalculationInProgress(aggregationStatus);
			}
			else
			{
				checkAggregationCalculationProcess();
			}
			return true;
		}
		else
		{
			if(isAggregatedCalculationProcess())
			{
				TickStatisticsManager.getInstance().setAggrCalculationInProgress(aggregationStatus);
				return false;
			}
			else
				return true;
		}
	}
	
	/*
	 * Returns filtered light copy tick list by instrument
	 * */
	public List<Tick> getFilteredTickList(String instrument)
	{
		if(instrument.equals(Constants.ALL_INSTRUMENTS))
		{
			return TickStatisticsManager.getInstance().getInstrumentMap()
					.values()
					.parallelStream()
					.flatMap(List::stream)
					.filter(Tick::validateTimestamp)
					.map(Tick::lightCopy)
			        .collect(Collectors.toList());
		}
		else
		{
			if(TickStatisticsManager.getInstance().getInstrumentMap().containsKey(instrument))
				return TickStatisticsManager.getInstance().getInstrumentMap()
						.get(instrument)
						.parallelStream()
						.filter(Tick::validateTimestamp)
						.map(Tick::lightCopy)
						.collect(Collectors.toList());
			else
				return new ArrayList<>();
		}
	}
}
