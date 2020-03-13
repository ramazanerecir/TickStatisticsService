package com.tasksolactive.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.tasksolactive.TaskSolactiveApplication;
import com.tasksolactive.entity.EmptyJsonResponse;
import com.tasksolactive.entity.Tick;
import com.tasksolactive.enumtype.ServiceStatus;
import com.tasksolactive.model.TickMessageManager;

/*
 * TickServiceController to handle incoming ticks
 *
 * */
@RestController
public class TickServiceController 
{
	@ResponseBody
	@PostMapping(path = "/ticks", 
		consumes = MediaType.APPLICATION_JSON_VALUE,
		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmptyJsonResponse> tick(@RequestBody Tick tick) 
	{
		if(TaskSolactiveApplication.SERVICE_STATUS == ServiceStatus.RUNNING
				&& tick != null && tick.validateTick())
		{
			new Thread(() -> TickMessageManager.getInstance().putTick(tick)).start();
			
			return new ResponseEntity<>(new EmptyJsonResponse(), HttpStatus.CREATED);
		}

		return new ResponseEntity<>(new EmptyJsonResponse(), HttpStatus.NO_CONTENT);
    }

}
