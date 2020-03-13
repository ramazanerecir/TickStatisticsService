package com.tasksolactive.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.tasksolactive.TaskSolactiveApplication;
import com.tasksolactive.entity.Statistics;
import com.tasksolactive.enumtype.ServiceStatus;
import com.tasksolactive.model.TickStatisticsManager;

/*
 * StatisticsServiceController to handle statistics requests
 *
 * */
@RestController
public class StatisticsServiceController 
{
	
	@GetMapping(path = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Statistics> statistics() 
	{
		if(TaskSolactiveApplication.SERVICE_STATUS != ServiceStatus.RUNNING)
			return new ResponseEntity<>(new Statistics(), HttpStatus.TOO_EARLY);
		else
			return new ResponseEntity<>(
					TickStatisticsManager.getInstance().getAggregatedStatistics(),
					HttpStatus.OK);
	}
	
	@GetMapping(path = "/statistics/{instrument}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Statistics> statisticsInstrument(@PathVariable String instrument) 
	{
		if(TaskSolactiveApplication.SERVICE_STATUS != ServiceStatus.RUNNING)
			return new ResponseEntity<>(new Statistics(), HttpStatus.TOO_EARLY);
		else
			return new ResponseEntity<>(
					TickStatisticsManager.getInstance().getStatistics(instrument),
					HttpStatus.OK);
	}
}
