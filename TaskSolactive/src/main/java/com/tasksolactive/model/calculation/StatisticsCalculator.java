package com.tasksolactive.model.calculation;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.tasksolactive.AppData;
import com.tasksolactive.entity.Statistics;
import com.tasksolactive.entity.Tick;
import com.tasksolactive.utils.StopWatch;

/*
 * Statistics calculator for given tick list
 *
 * */
public class StatisticsCalculator 
{
	private static final Logger LOG = Logger.getLogger(StatisticsCalculator.class);
			
	public StatisticsCalculator()
	{
		//
	}
	
	public Statistics calculate(String instrument, List<Tick> tickList)
	{
		LOG.trace(MessageFormat.format("Statistics calculate is started for instrument {0}", instrument));
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		
		if(tickList == null || tickList.isEmpty())
			return new Statistics();
		
		//Sort by price to calculate volatility & quantile
		tickList.sort(Comparator.comparing(Tick::getPrice));
		
		Statistics statistics = new Statistics();
		
		statistics.setCount(tickList.size());
		statistics.setMin(tickList.get(0).getPrice());
		statistics.setMax(tickList.get(statistics.getCount()-1).getPrice());
		statistics.setAvg(tickList.parallelStream().mapToDouble(Tick::getPrice).sum()/statistics.getCount());
		
		statistics.setVolatility(calculateVolatility(tickList, statistics.getAvg()));
		
		statistics.setQuantile(calculateQuantile(tickList));
		
		//Sort by timestamp to calculate twap and drawdown
		tickList.sort(Comparator.comparing(Tick::getTimestamp));
		
		double[] resultArray = calculateTwapAndDrawdown(tickList);
		
		statistics.setTwap(resultArray[0]);
		statistics.setTwap2(resultArray[1]);
		statistics.setMaxDrawdown(resultArray[2]);
		
		stopWatch.stop();
		
		LOG.trace(MessageFormat.format("Instrument {0} calculation is completed in {1}", instrument, stopWatch.getElapsedTime()));
		return statistics;
	}
	
	private double calculateVolatility(List<Tick> tickList, double avg)
	{
		LOG.trace("calculateVolatility is started");
		
		double variance = tickList.parallelStream().mapToDouble(t -> Math.pow(t.getPrice() - avg, 2.0)).sum()
				/ tickList.size();
		
		return Math.sqrt(variance);
	}
	
	private double calculateQuantile(List<Tick> tickList)
	{
		LOG.trace("calculateQuantile is started");
		
		int i=0;
		double weight = 1.0/tickList.size();
		double totalWeight = 0.0;
		for(; i<tickList.size(); ++i)
		{
			totalWeight += weight;
			
			if(totalWeight == AppData.STATISTICS_PERCENTILE)
			{
				return tickList.get(i).getPrice();
			}
			else if(totalWeight > AppData.STATISTICS_PERCENTILE)
			{
				break;
			}
		}
		
		if(totalWeight < AppData.STATISTICS_PERCENTILE)
			return tickList.get(tickList.size()-1).getPrice();
		else if(i == 0)
			return tickList.get(0).getPrice();
		
		return tickList.get(i).getPrice() - (tickList.get(i).getPrice() - tickList.get(i-1).getPrice())
					* (totalWeight - AppData.STATISTICS_PERCENTILE) / weight;
	}
	
	private double[] calculateTwapAndDrawdown(List<Tick> tickList)
	{
		LOG.trace("calculateTwapAndDrawdown is started");
		
		long calcTimestamp = new Timestamp(new Date().getTime()).getTime();
		
		double twap = 0.0;
		double twap2 = 0.0;
		double maxDrawdown = 0.0;
		
		
		for(int i=0; i<tickList.size()-1; ++i)
		{
			twap += tickList.get(i).getPrice() 
						* calculateTwapWeight(tickList.get(i).getTimestamp(),
					tickList.get(i+1).getTimestamp());
			 
			twap2 += tickList.get(i).getPrice() 
						* calculateTwap2Weight(tickList.get(i).getTimestamp(), calcTimestamp, 
								tickList.size()-i-1, tickList.size());
					
			
			if(i>0)
			{
				maxDrawdown = getDrawdownByPeak(tickList.get(i-1).getPrice(), tickList.get(i).getPrice(), maxDrawdown);
			}
		}
		 
		twap += tickList.get(tickList.size()-1).getPrice() 
					* calculateTwapWeight(tickList.get(tickList.size()-1).getTimestamp(), calcTimestamp);
		 
		twap2 += tickList.get(tickList.size()-1).getPrice() 
					* calculateTwap2Weight(tickList.get(tickList.size()-1).getTimestamp(), calcTimestamp,
							0, tickList.size());
		
		twap = twap / tickList.size();
		twap2 = twap2 / tickList.size();
		
		if(tickList.size() >= 2)
		{
			maxDrawdown = getDrawdownByPeak(tickList.get(tickList.size()-2).getPrice(),
							tickList.get(tickList.size()-1).getPrice(), maxDrawdown);
		}
		
		return new double[] {twap, twap2, maxDrawdown};
	}
	
	private double getDrawdownByPeak(double prevTick, double nextTick, double maxDrawdown)
	{
		if(prevTick > nextTick)
		{
			double drawdown = prevTick - nextTick;
			if(drawdown > maxDrawdown)
				maxDrawdown = drawdown;
		}
		
		return maxDrawdown;		
	}
	
	private double calculateTwapWeight(long timestamp, long nextTimestamp)
	{
		return ((double)(nextTimestamp - timestamp))/AppData.TICK_SLIDING_TIME_INTERVAL;
	}
	
	private double calculateTwap2Weight(long timestamp, long calcTimestamp, int index, int size)	
	{
		double lambda = AppData.STATISTICS_LAMBDA < 1.0 ?
				(1.0 - AppData.STATISTICS_LAMBDA) / (Math.pow(AppData.STATISTICS_LAMBDA, index))
				/ (Math.pow(AppData.STATISTICS_LAMBDA, size))
				: 1.0;
		return ((double)(calcTimestamp - timestamp))/AppData.TICK_SLIDING_TIME_INTERVAL 
			* lambda;
	}
}
