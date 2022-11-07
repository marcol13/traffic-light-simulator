package com.put.urbantraffic;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Road implements Serializable {
    private final int id;
    private final List<Lane> laneList;

    int getSpeedLimit() {
        return laneList.get(0).getSpeedLimit();
    }

    int getLength() {
        return laneList.get(0).getLength();
    }
}
