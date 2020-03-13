package com.tasksolactive;

/*
 * Application Configuration parameters
 * 
 * */
public class AppData 
{
	private AppData()
	{
		//
	}
	
	//Configuration File Path
	public static String CFG_PATH="./";
	
	//sliding time interval parameter in milliseconds
	public static long TICK_SLIDING_TIME_INTERVAL = 60000;
	
	//Delay time interval in milliseconds between each cleaner process for out of sliding time interval ticks
	public static long TICK_CLEANER_INTERVAL = 1000;
	
	//Thread Pooler size to process Tick Message BlockingQueue
	public static int TICK_PROCESS_POOLER_SIZE = 3;
	
	//Thread Pooler size to calculate Instrument statistics BlockingQueue
	public static int TICK_CALCULATION_POOLER_SIZE = 3;

	//Timer Thread period in milliseconds to calculate Aggregated Statistics
	public static long CALCULATION_AGGREGATED_TIMER = 1000;
	
	//decay parameter lambda for twap2 calculation
	public static double STATISTICS_LAMBDA = 0.94;
	
	//lower percentile parameter for quantile calculation
	public static double STATISTICS_PERCENTILE = 0.05;
	
	//Scale parameter for other statistics
	public static int STATISTICS_SCALE = 6;
	
	//Scale parameter related to price statistics
	public static int STATISTICS_PRICE_SCALE = 2;
}
