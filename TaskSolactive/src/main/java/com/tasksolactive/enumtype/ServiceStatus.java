package com.tasksolactive.enumtype;

/*
 * Application Service status
 * 
 * */
public enum ServiceStatus 
{
	INITIALIZING("Initializing"),
	RECOVERY("Recovery Mode"),	
	RUNNING("Service is Running"),
	CLOSED("Service is Closed");
	
	private String message;
	
	private ServiceStatus(String message)
	{
		this.message = message;
	}
	
	public String getMessage()
	{
		return message;
	}
}
