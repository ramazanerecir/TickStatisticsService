package com.tasksolactive;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.text.MessageFormat;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

public class StatisticsServiceControllerTest 
{
	private static final Logger LOG = Logger.getLogger(StatisticsServiceControllerTest.class);
			
	@Test
	public static void testStatistics(MockMvc mockMvc) throws Exception 
	{
		LOG.debug("testStatistics is started");
		
		//waiting for aggregated calculation thread to complete its calculation
		Thread.sleep(AppData.CALCULATION_AGGREGATED_TIMER);
		
		ResultActions resultActions = mockMvc.perform(get("/statistics"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		
		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		LOG.debug(MessageFormat.format("/statistics response : {0}", contentAsString));
	}
	
	@Test
	public static void testStatisticsInstrument(MockMvc mockMvc, String instrument) throws Exception 
	{
		LOG.debug(MessageFormat.format("testStatisticsInstrument is started for {0}", instrument));
		
		ResultActions resultActions = mockMvc.perform(get("/statistics/"+instrument))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON));
		
		MvcResult result = resultActions.andReturn();
		String contentAsString = result.getResponse().getContentAsString();
		LOG.debug(MessageFormat.format("/statistics/{1} response : {0}", contentAsString, instrument));
	}
	

}
