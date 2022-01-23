package com.hellofresh.events.statistics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

//@Profile("test")
@Component
public class TestHelper {
	
	@Autowired
	private ObjectMapper objectMapper;


	public long generateRandomTimeStamp(long startTimestamp, long endTimestamp) {
		long timestamp = ThreadLocalRandom.current().nextLong(startTimestamp, endTimestamp);
		return timestamp;
	}

	public double generateRandomDouble(long minDouble, long maxDouble) {
		Random r = new Random();
		double randomDouble = minDouble + (maxDouble - minDouble) * r.nextDouble();
		return randomDouble;
	}

	public int generateRandomInteger(int minInteger, int maxInteger) {
		int randomInteger = ThreadLocalRandom.current().nextInt(minInteger, maxInteger);

		return randomInteger;
	}

}
