package com.tasksolactive;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tasksolactive.enumtype.ServiceStatus;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest
@RunWith(SpringRunner.class)
class TaskSolactiveApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@BeforeAll
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}
	
	@Test
    @Order(1)
	void contextLoads() 
	{
		TaskSolactiveApplication.main(new String[] {});
		
		assertEquals("Service Status", ServiceStatus.RUNNING, TaskSolactiveApplication.SERVICE_STATUS);
	}
	
	@Test 
    @Order(2)
	void testTick() throws Exception
	{
		TickServiceControllerTest.testTick(mockMvc);
	}
	
	@Test 
    @Order(3)
	void testTickPast() throws Exception
	{
		TickServiceControllerTest.testTickPast(mockMvc);
	}
	
	@Test 
    @Order(4)
	void testTickNoContent() throws Exception
	{
		TickServiceControllerTest.testTickNoContent(mockMvc);
	}
	
	@Test
	@Order(5)
	void testStatisticsInstrument() throws Exception
	{
		StatisticsServiceControllerTest.testStatisticsInstrument(mockMvc, "IBM.NE");
	}
	

	@Test
	@Order(6)
	void testStatisticsInstrumentPast() throws Exception
	{
		StatisticsServiceControllerTest.testStatisticsInstrument(mockMvc, "GOOGL");
	}
	
	@Test
	@Order(7)
	void testStatistics() throws Exception
	{
		StatisticsServiceControllerTest.testStatistics(mockMvc);
	}

}
