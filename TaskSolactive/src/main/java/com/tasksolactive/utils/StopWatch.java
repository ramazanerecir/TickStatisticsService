package com.tasksolactive.utils;

import java.text.MessageFormat;

/*
 * Calculates execution time of the code blocks using start/stop methods
 *
 * */
public class StopWatch 
{
	long enterTime = 0;
	long exitTime = 0;

	public StopWatch()
	{
		//
	}

	public void start()
	{
		this.enterTime = System.currentTimeMillis();
		this.exitTime = this.enterTime;
	}

	public void reset()
	{
		this.enterTime = 0;
		this.exitTime = 0;
	}

	public long stop()
	{
		this.exitTime = System.currentTimeMillis();
		return getElapsedTimeInMilis();
	}

	public long getElapsedTimeInMilis()
	{
		return exitTime - enterTime;
	}
	
	public long getProgressTimeInMilis()
	{
		return System.currentTimeMillis() - enterTime;
	}

	public int getElapsedTimeInSeconds()
	{
		return (int) (getElapsedTimeInMilis() / 1000L);
	}

	public int getElapsedTimeInMinutes()
	{
		return (int) (getElapsedTimeInSeconds() / 60L);
	}

	public String getElapsedTime()
	{
		int seconds = getElapsedTimeInSeconds();

		int mininutesPart = seconds / 60;
		int secondsPart = seconds % 60;

		String minutesPartString = mininutesPart < 10 ? MessageFormat.format("0{0}", mininutesPart) : ""
				+ mininutesPart;
		String secondsPartString = secondsPart < 10 ? "0" + secondsPart : MessageFormat.format("{0}", secondsPart);

		return MessageFormat.format("{0}:{1}", minutesPartString, secondsPartString);
	}
}