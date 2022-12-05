package com.put.urbantraffic.drawablemodels;

import lombok.Data;

import java.util.List;

@Data
public class Frame {
    private final List<DrawableCar> cars;
    private final List<DrawableCrossingTrafficLight> lights;
}
