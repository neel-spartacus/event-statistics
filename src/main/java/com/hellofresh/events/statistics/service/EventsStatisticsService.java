package com.hellofresh.events.statistics.service;


import com.hellofresh.events.statistics.dto.EventsInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.hellofresh.events.statistics.constants.Constants.EMPTY_STRING;

@Service
@Slf4j
public class EventsStatisticsService {


    @Value("${events.cutoffDuration}")
    private long cutoffDuration;

    private ConcurrentHashMap<Instant, List<EventsInfoDto>> eventsMap = new ConcurrentHashMap();

    public ConcurrentHashMap<Instant, List<EventsInfoDto>> getEventsMap() {
        return eventsMap;
    }

    public void setEventsMap(ConcurrentHashMap<Instant, List<EventsInfoDto>> eventsMap) {
        this.eventsMap = eventsMap;
    }

    public long getCutoffDuration() {
        return cutoffDuration;
    }

    public void setCutoffDuration(long cutoffDuration) {
        this.cutoffDuration = cutoffDuration;
    }

    public EventsInfoDto processEvents(EventsInfoDto eventsInfoDto) {

        eventsMap.computeIfAbsent(eventsInfoDto.getTimeStamp(), key -> new ArrayList<>()).add(eventsInfoDto);
        return eventsInfoDto;
    }

    public EventsInfoDto createDtoFromEvent(String event) {

        String[] eventParameters = event.split(",");
        Long time = Long.parseLong(eventParameters[0]);
        Double valueX = Double.parseDouble(eventParameters[1]);
        Integer valueY = Integer.parseInt(eventParameters[2]);

        return EventsInfoDto.builder().timeStamp(Instant.ofEpochMilli(time)).valueX(valueX).valueY(valueY).build();
    }


    public String getEventStatistics(ZonedDateTime now) {

        List<EventsInfoDto> eventsInfo = eventsMap.values().stream().flatMap(List::stream).collect(Collectors.toList());
        List<EventsInfoDto> eventsInfoDtoList = getEventsSmallerThanCutOffTime(eventsInfo, now);
        int eventsStatsSize = eventsInfoDtoList.size();
        if (eventsStatsSize > 0) {
            LongSummaryStatistics valuesYEventsStatistics = eventsInfoDtoList.stream()
                    .collect(Collectors.summarizingLong(EventsInfoDto::getValueY));
            DoubleSummaryStatistics valuesXEventsStatistics = eventsInfoDtoList.stream()
                    .collect(Collectors.summarizingDouble(EventsInfoDto::getValueX));
            Double sumX = BigDecimal.valueOf(valuesXEventsStatistics.getSum())
                    .setScale(10, BigDecimal.ROUND_HALF_DOWN)
                    .doubleValue();
            Double avgX = BigDecimal.valueOf(valuesXEventsStatistics.getAverage())
                    .setScale(10, BigDecimal.ROUND_HALF_DOWN)
                    .doubleValue();
            BigDecimal avgY = BigDecimal.valueOf(valuesYEventsStatistics.getAverage());
            return String.join(",", String.valueOf(eventsStatsSize), String.valueOf(sumX)
                    , String.valueOf(avgX),
                    String.valueOf(valuesYEventsStatistics.getSum()), String.format("%.3f", avgY));
        } else {
            return EMPTY_STRING;
        }

    }

    /**
     * The API does allow past and future transactions to be registered. But, for statistical purposes, we are
     * only taking the transactions that have occurred before the cutoff duration(in this case 60s).
     * So, we have to first truncate the future transactions, and after that filter out
     * the transactions that are younger than the cutoff duration
     *
     * @param eventsInfoList
     * @param now
     * @return
     */
    private List<EventsInfoDto> getEventsSmallerThanCutOffTime(List<EventsInfoDto> eventsInfoList, ZonedDateTime now) {

        ZonedDateTime cutoffTime = now.minusSeconds(cutoffDuration);
        // This will contain will the events in the future
        List<EventsInfoDto> futureEventsInfoList = new ArrayList<>();
        List<EventsInfoDto> eventsListLesserThan60s = new ArrayList<>();

        eventsInfoList.stream().forEach(p -> {
            Duration duration = Duration.between(now, ZonedDateTime.ofInstant(p.getTimeStamp(), ZoneId.of("UTC")));
            if (!duration.isNegative()) {
                futureEventsInfoList.add(p);

            }

        });

        // Now we have only events that are in the past
        eventsInfoList.removeAll(futureEventsInfoList);

        // We need only the events that are younger than cutoff duration
        eventsInfoList.stream().forEach(p -> {
            Duration duration = Duration.between(cutoffTime, ZonedDateTime.ofInstant(p.getTimeStamp(), ZoneId.of("UTC")));
            if (!duration.isNegative()) {
                eventsListLesserThan60s.add(p);

            }

        });

        return eventsListLesserThan60s;

    }
}
