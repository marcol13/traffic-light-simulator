package com.put.urbantraffic;

import lombok.Data;


@Data
public class TrafficLight {
    private Lane startLane;
    private Light currentColor;
    private boolean isYellow = false;
}
