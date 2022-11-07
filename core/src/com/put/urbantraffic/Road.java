package com.put.urbantraffic;

import lombok.Data;

import java.util.List;

@Data
public class Road {
    private final int id;
    private final List<Lane> laneList;
}
