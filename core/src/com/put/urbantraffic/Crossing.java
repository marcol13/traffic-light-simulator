package com.put.urbantraffic;

import lombok.Data;

import java.util.List;

@Data
public class Crossing {
    private final int id;
    private final int x;
    private final int y;
    private boolean topCardField = true;
    private boolean bottomCardField = true;
    private boolean leftCardField = true;
    private boolean rightCardField = true;
    private TrafficLightsSupervisor trfficLightsSupervisor = new TrafficLightsSupervisor();
}
