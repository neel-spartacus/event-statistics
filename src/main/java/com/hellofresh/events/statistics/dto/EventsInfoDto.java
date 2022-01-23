package com.hellofresh.events.statistics.dto;

import lombok.*;

import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode
public class EventsInfoDto {

    Instant timeStamp;
    Double valueX;
    Integer valueY;
}
