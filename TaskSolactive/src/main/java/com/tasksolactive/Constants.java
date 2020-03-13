package com.tasksolactive;

/*
 * Constant variable names are stored.
 *  
 * */
public interface Constants 
{
	//Application Name
	public static final String APP_NAME = "SOLACTIVE TASK";
	
	//Configuration File Name
	public static final String TASK_SOLACTIVE_CFG = "taskSolactive.cfg";
	
	//Log4j Properties File Name
	public static final String LOG4J_FILE = "log4j.properties";
	
	//Name for representing All Instruments (Aggregated) in statistics map
	public static final String ALL_INSTRUMENTS = "ALL_INSTRUMENTS";
	
	//Application configuration parameters
	public static final String TICK_SLIDING_TIME_INTERVAL = "tick.sliding.time.interval";
	public static final String TICK_PROCESS_POOLER_SIZE = "tick.process.pooler.size";
	public static final String TICK_CALCULATION_POOLER_SIZE = "tick.calculation.pooler.size";
	public static final String TICK_CLEANER_INTERVAL = "tick.cleaner.interval";
	public static final String CALCULATION_AGGREGATED_TIMER = "calculation.aggregated.timer";
	public static final String STATISTICS_LAMBDA = "statistics.lambda";
	public static final String STATISTICS_PERCENTILE = "statistics.percentile";
	public static final String STATISTICS_SCALE = "statistics.scale";
	public static final String STATISTICS_PRICE_SCALE = "statistics.price.scale";
}
