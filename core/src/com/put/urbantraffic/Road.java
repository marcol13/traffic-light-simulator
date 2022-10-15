package com.put.urbantraffic;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Road {
    private final int speedLimit;
    private final List<Lane> laneList;
    private final List<Node> nodeList;
}
