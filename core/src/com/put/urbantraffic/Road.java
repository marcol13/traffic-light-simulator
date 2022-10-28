package com.put.urbantraffic;

import lombok.Data;

import java.util.List;

@Data
public class Road {
    private final int id;
    private int speedLimit;
    private final int length;
    private final List<Lane> laneList;
    private final List<Node> nodeList;
}
