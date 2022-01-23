package com.hellofresh.events.statistics.service.unit;

import com.hellofresh.events.statistics.dto.EventsInfoDto;
import com.hellofresh.events.statistics.service.EventsStatisticsService;
import com.hellofresh.events.statistics.service.TestHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static com.hellofresh.events.statistics.constants.Constants.MAX_VALUE_X;
import static com.hellofresh.events.statistics.constants.Constants.MIN_VALUE_X;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class EventStatisticsServiceJunitTest {

    private long currentTimeInMillis;

    /**
     * The application takes into consideration all events that were executed
     * in the last 60 seconds
     */

    private final long cutoffDurationInMillis = 60000L;
    private long leastAllowedEventsTimestamp;
    private long timeStampOlderThan60s;
    private long futureTimeStamp;
    private int numberOfEventsWithCutoffTime = 98;


    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @InjectMocks
    private TestHelper testHelper;


    @InjectMocks
    private EventsStatisticsService service;


    @Before
    public void setup() {

        currentTimeInMillis = System.currentTimeMillis();
        leastAllowedEventsTimestamp = currentTimeInMillis - cutoffDurationInMillis;
        timeStampOlderThan60s = leastAllowedEventsTimestamp - cutoffDurationInMillis;
        futureTimeStamp = currentTimeInMillis + cutoffDurationInMillis;
        service.setEventsMap(createEventsInfoMap());
        service.setCutoffDuration(60L);
    }


    @Test
    public void testGetStatistics() {
        ZonedDateTime now = ZonedDateTime
                .ofInstant(Instant.ofEpochMilli(currentTimeInMillis), ZoneId.of("UTC"));

        String statistics = service.getEventStatistics(now);
        Assert.assertEquals(5, statistics.split(",").length);

    }


    private ConcurrentHashMap<Instant, List<EventsInfoDto>> createEventsInfoMap() {
        ConcurrentHashMap<Instant, List<EventsInfoDto>> eventsDtoMap = new ConcurrentHashMap<>();

        IntStream.range(0, 80).forEach(i -> {
            long timestamp = testHelper.generateRandomTimeStamp(timeStampOlderThan60s, leastAllowedEventsTimestamp);
            Instant instant = Instant.ofEpochMilli(timestamp);
            eventsDtoMap.computeIfAbsent(instant, k -> new ArrayList<>()).add(EventsInfoDto.builder().timeStamp(instant)
                    .valueX(testHelper.generateRandomDouble(0, 1)).valueY(testHelper.generateRandomInteger(MIN_VALUE_X, MAX_VALUE_X)).build());

        });

        IntStream.range(0, numberOfEventsWithCutoffTime).forEach(i -> {

            long timestamp = testHelper.generateRandomTimeStamp(leastAllowedEventsTimestamp, currentTimeInMillis);
            Instant instant = Instant.ofEpochMilli(timestamp);
            eventsDtoMap.computeIfAbsent(instant, k -> new ArrayList<>()).add(EventsInfoDto.builder().timeStamp(instant)
                    .valueX(testHelper.generateRandomDouble(0, 1)).valueY(testHelper.generateRandomInteger(MIN_VALUE_X, MAX_VALUE_X)).build());

        });


        IntStream.range(0, 50).forEach(i -> {
            long timestamp = testHelper.generateRandomTimeStamp(currentTimeInMillis, futureTimeStamp);
            Instant instant = Instant.ofEpochMilli(timestamp);
            eventsDtoMap.computeIfAbsent(instant, k -> new ArrayList<>()).add(EventsInfoDto.builder().timeStamp(instant)
                    .valueX(testHelper.generateRandomDouble(0, 1)).valueY(testHelper.generateRandomInteger(MIN_VALUE_X, MAX_VALUE_X)).build());

        });
        return eventsDtoMap;
    }


    @Test
    public void whenBigDecimalCreatedFromDouble_thenValueMayNotMatch() {
        BigDecimal bdFromDouble = BigDecimal.valueOf(Double.POSITIVE_INFINITY);
        System.out.println(bdFromDouble);
    }

    @Test
    public void whenStringArraySize() {
       // for (int i = 2; i >= 0; i--) {
            try {
                String[] arr = new String[Integer.MAX_VALUE - 8];
                System.out.println("Max-Size : " + arr.length);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
   // }

}
