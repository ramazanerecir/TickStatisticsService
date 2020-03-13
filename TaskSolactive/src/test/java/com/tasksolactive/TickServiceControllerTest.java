package com.tasksolactive;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasksolactive.entity.Tick;

public class TickServiceControllerTest {
	
	private static final Logger LOG = Logger.getLogger(TickServiceControllerTest.class);
	
	@Test
	public static void testTick(MockMvc mockMvc) throws Exception 
	{
		LOG.debug("testTick is started");
		
		ResultActions resultActions = mockMvc.perform(post("/ticks").contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(new Tick("IBM.NE", 143.82, new Timestamp(new Date().getTime()).getTime()))))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
		
		MvcResult result = resultActions.andReturn();
		LOG.debug(MessageFormat.format("/ticks response status (expected {1}) : {0}", 
				result.getResponse().getStatus(), HttpStatus.CREATED));
	}
	
	@Test
	public static void testTickNoContent(MockMvc mockMvc) throws Exception 
	{
		LOG.debug("testTickNoContent is started");
		
		long timestamp = new Timestamp(new Date().getTime()).getTime();
		
		timestamp -= 60001l;
		
		ResultActions resultActions = mockMvc.perform(post("/ticks").contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(new Tick("IBM.NE", 143.82, timestamp))))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent());
		
		MvcResult result = resultActions.andReturn();
		LOG.debug(MessageFormat.format("/ticks response status (expected {1}) : {0}", 
				result.getResponse().getStatus(), HttpStatus.NO_CONTENT));
	}
	
	@Test
	public static void testTickPast(MockMvc mockMvc) throws Exception 
	{
		LOG.debug("testTickPast is started");
		
		long timestamp = new Timestamp(new Date().getTime()).getTime();
		timestamp -= 1000l;
		
		ResultActions resultActions = mockMvc.perform(post("/ticks").contentType(MediaType.APPLICATION_JSON)
			.content(asJsonString(new Tick("GOOGL", 1092.09, timestamp))))
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated());
		
		MvcResult result = resultActions.andReturn();
		LOG.debug(MessageFormat.format("/ticks response status (expected {1}) : {0}", 
				result.getResponse().getStatus(), HttpStatus.CREATED));
	}

	private static String asJsonString(final Object obj)
	{
		try
		{
			return new ObjectMapper().writeValueAsString(obj);
		} 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
	}

}
