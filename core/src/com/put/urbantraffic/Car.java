package com.put.urbantraffic;

import lombok.Data;

@Data
public class Car {
    private final Node startNode;
    private final Node endNode;
    private final Lane startLane;
    private final Lane endLane;
    private final RideStatus status = RideStatus.WAITING;
}
