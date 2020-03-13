package com.tasksolactive.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.tasksolactive.Constants;
import com.tasksolactive.entity.Statistics;
import com.tasksolactive.entity.Tick;

/*
 * Managing instrument statistics collections
 *
 * */
public class TickStatisticsManager 
{
	private static final Logger LOG = Logger.getLogger(TickStatisticsManager.class);
	
	private static TickStatisticsManager instance;
	
	//Tick List by instrument
	private Map<String, List<Tick>> instrumentMap;
	//Statistics by instrument
	private Map<String, Statistics> statisticsMap;
	//Lock by instrument
	private Map<String, Lock> instrumentLockMap;
	
	
	//Holding Aggregated Calculation is in progress or not
	//instrumentMap should be locked while collecting all tick list for aggregated statistics calculation
	private volatile boolean aggrCalculationInProgress = false; 
	
	private TickStatisticsManager()
	{
		//
	}
	
	public static TickStatisticsManager getInstance()
	{
		if (instance == null)
		{
			instance = new TickStatisticsManager();
		}
		return instance;
	}
	
	public void init()
	{
		instrumentMap = new ConcurrentHashMap<>();
		statisticsMap = new ConcurrentHashMap<>();
		instrumentLockMap = new ConcurrentHashMap<>();
		
		LOG.debug("TickStatisticsManager initialized");
	}
	
	public boolean isAggrCalculationInProgress() {
		return aggrCalculationInProgress;
	}

	public void setAggrCalculationInProgress(boolean aggrCalculationInProgress) {
		this.aggrCalculationInProgress = aggrCalculationInProgress;
	}

	/*
	 * Getting lock by instrument to be able to manage concurrent access on instrumentMap
	 * */
	public Lock getInstrumentLock(String instrument)
	{
		if(!instrumentLockMap.containsKey(instrument))
		{
			instrumentLockMap.put(instrument, new ReentrantLock());
		}
		return instrumentLockMap.get(instrument);
	}
	
	/*
	 * Releasing lock by instrument
	 * */
	public void releaseInstrumentLock(String instrument, Lock lock)
	{
		instrumentLockMap.put(instrument, lock);
	}
	
	/*
	 * Returns instrumentMap keySet:instrument set
	 * */
	public Set<String> getInstrumentMapKeySet()
	{
		return instrumentMap.keySet()
				.parallelStream()
				.collect(Collectors.toSet());
	}
	
	/*
	 * Returns instrumentMap keySet:instrument set
	 * */
	public void putInstrumentMap(String instrument, List<Tick> tickList)
	{
		instrumentMap.put(instrument, tickList);
	}

	/*
	 * Adds ticks by instrument
	 * */
	public void putTick(Tick tick)
	{
		if(!instrumentMap.containsKey(tick.getInstrument()))
		{
			instrumentMap.put(tick.getInstrument(), new ArrayList<>());
		}
		
		instrumentMap.get(tick.getInstrument()).add(tick);
	}
	
	/*
	 * Returns aggregated statistics results
	 * */
	public Statistics getAggregatedStatistics()
	{
		LOG.debug("Aggregated Statistics is requested");
		return getStatistics(Constants.ALL_INSTRUMENTS);
	}
	
	/*
	 * Returns statistics results by instrument
	 * */
	public Statistics getStatistics(String instrument)
	{
		if(!instrument.equals(Constants.ALL_INSTRUMENTS))
			LOG.debug(MessageFormat.format("Instrument {0} statistics is requested", instrument));
		
		if(!instrument.isEmpty() && 
				hasStatistics(instrument))
			return statisticsMap.get(instrument);
		else
			return new Statistics();
	}
	
	/*
	 * Check whether instruments has Statistics
	 * */
	public boolean hasStatistics(String instrument)
	{
		return statisticsMap.containsKey(instrument);
	}
	
	/*
	 * Updates recalculated statistics by instrument
	 * */
	public void refreshStatistics(String instrument, Statistics statistics)
	{
		statisticsMap.put(instrument, statistics);
	}
	
	/*
	 * Remove instrument which has no ticks in sliding time interval
	 * */
	public void removeInstrument(String instrument)
	{
		statisticsMap.remove(instrument);
		instrumentMap.remove(instrument);
	}
	
	/*
	 * Returns tick list by instrument
	 * */
	public List<Tick> getTickList(String instrument)
	{
		return instrumentMap.get(instrument);
	}
	
	/*
	 * Returns instrumentMap
	 * */
	public Map<String, List<Tick>> getInstrumentMap() {
		return instrumentMap;
	}
}
