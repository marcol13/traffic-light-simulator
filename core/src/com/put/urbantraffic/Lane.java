package com.put.urbantraffic;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Lane {
    private static final double AVERAGE_CAR_LENGTH = 2.5;
    private static final double DISTANCE_BETWEEN_CARS = 0.5;

    private final long id;
    private final Crossing startCrossing;
    private final Crossing endCrossing;
    private final int speedLimit;
    private final List<Direction> directions;

    private final List<Car> carsList = new ArrayList<>();

//    private final int maxCarAmount =
//            (int) (Math.sqrt(
//                    Math.pow(startCrossing.getX() - endCrossing.getX(), 2) +
//                            Math.pow(startCrossing.getY() - endCrossing.getY(), 2))
//                    / (AVERAGE_CAR_LENGTH + DISTANCE_BETWEEN_CARS));
}
