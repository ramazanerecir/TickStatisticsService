package com.tasksolactive.enumtype;

/*
 * Statistics Calculation Type
 * 
 * */
public enum CalculationType 
{
	SINGLE("Single"),
	AGGREGATED("Recovery Mode");
	
	private String message;
	
	private CalculationType(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
}