package com.hellofresh.events.statistics.service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hellofresh.events.statistics.EventsStatisticsApplication;
import com.hellofresh.events.statistics.service.TestHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.hellofresh.events.statistics.constants.Constants.MAX_VALUE_X;
import static com.hellofresh.events.statistics.constants.Constants.MIN_VALUE_X;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventsStatisticsApplication.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class EventsInfoControllerIntegrationTest {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private long currentTimeinMillis;

	/**
	 * The application takes into consideration all transactions that were executed
	 * in the last 60 seconds
	 * 
	 */

	private final long cutoffDurationInMillis = 60000L;
	private long leastAllowedTransactionTimestamp;
	private long timeStampOlderThan60s;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TestHelper testHelper;


	@Before
	public void setup() {
		currentTimeinMillis = System.currentTimeMillis();
		leastAllowedTransactionTimestamp = currentTimeinMillis - cutoffDurationInMillis;
		timeStampOlderThan60s = leastAllowedTransactionTimestamp - cutoffDurationInMillis;
	}
	
	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      logger.info("--------------------------------------------Starting test:--------------------------------------- :" + description.getMethodName());
	   }
	};

	/*
	 * Here we are doing positive test. Meaning all the transactions are younger
	 * 60s, Firstly we are posting transactions, where all the timestamps are within
	 * 60s of currentTimeinMillis Along with that we are also testing
	 * asynchronousness of our end points.Meaning that the endpoints are
	 * non-blocking
	 */
	@Test
	public void testAllTransactionsAreYoungerThan60s() throws Exception {

		List<String> eventsList = new ArrayList<>();

		IntStream.range(0, 5).forEach(i -> {
					StringBuilder sb = new StringBuilder();
					long timestamp = testHelper.generateRandomTimeStamp(leastAllowedTransactionTimestamp, currentTimeinMillis);
					sb.append(timestamp).append(",").append(testHelper.generateRandomDouble(0, 1))
							.append(",").append(testHelper.generateRandomInteger(MIN_VALUE_X, MAX_VALUE_X));
			eventsList.add(sb.toString());
				});

		IntStream.range(0, eventsList.size()).forEach(i -> {

			//ObjectWriter ow = objectMapper.writer().withDefaultPrettyPrinter();
			//String requestJson;
			try {
				//requestJson = ow.writeValueAsString(eventsList.get(i));
				MvcResult mvcResult = this.mockMvc
						.perform(post("/event").content(eventsList.get(i)).contentType(MediaType.APPLICATION_JSON))

						.andExpect(request().asyncStarted()).andExpect(status().is(200))
						.andExpect(request().asyncResult(instanceOf(ResponseEntity.class))).andReturn();
				mvcResult.getAsyncResult();
				this.mockMvc.perform(asyncDispatch(mvcResult)).andExpect(status().isAccepted());
			} catch (Exception e) {
				logger.error("Error -{}", e);
			}

		});

		MvcResult mvcResult = this.mockMvc.perform(get("/stats").contentType(MediaType.APPLICATION_JSON))
				.andExpect(request().asyncStarted())
				.andExpect(request().asyncResult(instanceOf(ResponseEntity.class))).andReturn();
		mvcResult.getAsyncResult();

		this.mockMvc.perform(asyncDispatch(mvcResult)).andExpect(jsonPath("count").value(eventsList.size()));

	}


}
