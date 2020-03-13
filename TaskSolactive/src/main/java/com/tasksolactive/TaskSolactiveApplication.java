package com.tasksolactive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.tasksolactive.enumtype.ServiceStatus;
import com.tasksolactive.model.SchedulerService;
import com.tasksolactive.model.TickCalculationManager;
import com.tasksolactive.model.TickMessageManager;
import com.tasksolactive.model.TickStatisticsManager;

/*
 * Main Application class
 * 
 * */
@SpringBootApplication
public class TaskSolactiveApplication {

	public static ServiceStatus SERVICE_STATUS = ServiceStatus.INITIALIZING;
	
	public static void main(String[] args) 
	{
		System.out.println(MessageFormat.format("{0} Started", Constants.APP_NAME));
		
		loadArguments(args);
		init();
		SpringApplication.run(TaskSolactiveApplication.class, args);
		TaskSolactiveApplication.SERVICE_STATUS = ServiceStatus.RUNNING;
		System.out.println(MessageFormat.format("{0} {1}", Constants.APP_NAME, 
				TaskSolactiveApplication.SERVICE_STATUS.getMessage()));
	}
	
	private static void init()
	{
		System.out.println(MessageFormat.format("{0} {1}", Constants.APP_NAME, 
				TaskSolactiveApplication.SERVICE_STATUS.getMessage()));
		
		TickMessageManager.getInstance().init();
		TickStatisticsManager.getInstance().init();
		TickCalculationManager.getInstance().init();
		SchedulerService.getInstance().start();
		
		System.out.println(MessageFormat.format("{0} Initialized", Constants.APP_NAME));
	}
	
	private static void loadArguments(String[] args)
	{
		if(args != null && args.length > 0)
		{
			System.out.print("Arguments: ");
			for(String arg : args)
			{
				System.out.print(arg + " ");
			}
			System.out.println("");
			
			try
			{
				AppData.CFG_PATH = args[0];
				
				if(!AppData.CFG_PATH.endsWith(File.separator))
					AppData.CFG_PATH=AppData.CFG_PATH.concat(File.separator);	
			}
			catch (Exception e) 
			{
				System.err.println(MessageFormat.format("Failed to parse arguments. Error: {0}", e.getMessage()));
			}
		}

		loadLog4j();
		loadAppProperties();
	}
	
	private static void loadLog4j()
	{
		try {
			PropertyConfigurator.configure(MessageFormat.format("{0}{1}", AppData.CFG_PATH, Constants.LOG4J_FILE));
			
			System.out.println(MessageFormat.format("Log4j properties is loaded from {0}{1}",
					AppData.CFG_PATH, Constants.LOG4J_FILE));
		} 
		catch (Exception e) 
		{
			System.err.println(MessageFormat.format("Cannot initialize log4j from {0}{1} :{2}",
					AppData.CFG_PATH, Constants.LOG4J_FILE, e.getMessage()));
		}
	}
	
	private static void loadAppProperties()
	{
		Properties props;
		try {
			
			props = loadPropertiesFile(MessageFormat.format("{0}{1}", AppData.CFG_PATH,
					Constants.TASK_SOLACTIVE_CFG));
			
			AppData.TICK_SLIDING_TIME_INTERVAL = parseLongProps(props, 
						Constants.TICK_SLIDING_TIME_INTERVAL,
						AppData.TICK_SLIDING_TIME_INTERVAL);
			
			double valueDouble = parseDoubleProps(props, Constants.STATISTICS_LAMBDA, AppData.STATISTICS_LAMBDA);
			if(valueDouble > 0 && valueDouble <= 1.0)
				AppData.STATISTICS_LAMBDA = valueDouble;
			
			valueDouble = parseDoubleProps(props, Constants.STATISTICS_PERCENTILE, AppData.STATISTICS_PERCENTILE);
			if(valueDouble >= 0 && valueDouble <= 1.0)
				AppData.STATISTICS_PERCENTILE = valueDouble;
			
			AppData.TICK_PROCESS_POOLER_SIZE = parseIntegerProps(props, 
					Constants.TICK_PROCESS_POOLER_SIZE, 
					AppData.TICK_PROCESS_POOLER_SIZE);
			
			AppData.TICK_CALCULATION_POOLER_SIZE = parseIntegerProps(props, 
					Constants.TICK_CALCULATION_POOLER_SIZE, 
					AppData.TICK_CALCULATION_POOLER_SIZE);
			
			AppData.CALCULATION_AGGREGATED_TIMER = parseLongProps(props, 
						Constants.CALCULATION_AGGREGATED_TIMER,
						AppData.CALCULATION_AGGREGATED_TIMER);
			
			AppData.TICK_CLEANER_INTERVAL = parseLongProps(props, 
					Constants.TICK_CLEANER_INTERVAL,
					AppData.TICK_CLEANER_INTERVAL);
		
			AppData.STATISTICS_SCALE = parseIntegerProps(props, 
					Constants.STATISTICS_SCALE, 
					AppData.STATISTICS_SCALE);
			
			AppData.STATISTICS_PRICE_SCALE = parseIntegerProps(props, 
					Constants.STATISTICS_PRICE_SCALE, 
					AppData.STATISTICS_PRICE_SCALE);
			
			System.out.println(MessageFormat.format("Configuration parameters are loaded from file {0}{1}.", 
					AppData.CFG_PATH, Constants.TASK_SOLACTIVE_CFG));
			
		} catch (Exception e) {
			System.err.println(MessageFormat.format("Failed to read configuration file {0}{1}. Default parameters will be used. Error: {2}", 
					AppData.CFG_PATH, Constants.TASK_SOLACTIVE_CFG, e.getMessage()));
		}
	}
	
	private static Properties loadPropertiesFile(String path) throws IOException
	{
		InputStream inputStream = null;

		try
		{
			Properties props = new Properties();

			File file = new File(path);
			inputStream = FileUtils.openInputStream(file);

			props.load(inputStream);
			return props;
		}
		finally
		{
			IOUtils.closeQuietly(inputStream);
		}
	}
	
	private static long parseLongProps(Properties props, String parameter, long defaultValue)
	{
		if(props.getProperty(parameter) != null &&
				!props.getProperty(parameter).isEmpty())
		{
			long value = Long.parseLong(props.getProperty(parameter));
			if(value > 0)
				return value;
		}
		
		return defaultValue;
	}
	
	private static double parseDoubleProps(Properties props, String parameter, double defaultValue)
	{
		if(props.getProperty(parameter) != null &&
				!props.getProperty(parameter).isEmpty())
		{
			double value = Double.parseDouble(props.getProperty(parameter));
			return value;
		}
		
		return defaultValue;
	}
	
	private static int parseIntegerProps(Properties props, String parameter, int defaultValue)
	{
		if(props.getProperty(parameter) != null &&
				!props.getProperty(parameter).isEmpty())
		{
			int value = Integer.parseInt(props.getProperty(parameter));
			if(value > 0)
				return value;
		}
		
		return defaultValue;
	}

}
