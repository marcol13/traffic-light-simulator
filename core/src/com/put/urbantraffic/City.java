package com.put.urbantraffic;

import lombok.Data;

import java.util.List;

@Data
public class City {
    private final List<Crossing> crossings;
    private final List<Lane> lanes;
    
}
