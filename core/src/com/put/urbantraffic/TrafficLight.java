package com.put.urbantraffic;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TrafficLight {
    private Lane startLane;
    private Light currentColor;
}
