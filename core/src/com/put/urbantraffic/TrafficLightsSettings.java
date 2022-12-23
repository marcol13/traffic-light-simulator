package com.put.urbantraffic;

import lombok.Data;

@Data
public class TrafficLightsSettings {
    private final int greenDuration;
    private final int redDuration;
    private final int offset;
}
