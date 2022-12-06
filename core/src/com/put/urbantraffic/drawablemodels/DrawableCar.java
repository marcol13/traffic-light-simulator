package com.put.urbantraffic.drawablemodels;

import com.put.urbantraffic.Car;
import com.put.urbantraffic.RideStatus;
import lombok.Data;

@Data
public class DrawableCar {
    private final int x;
    private final int y;
    private final Car.Way way;
    private final RideStatus status;

    public static DrawableCar fromCar(Car car) {
        return new DrawableCar(car.getCarPosition().getX(), car.getCarPosition().getY(), car.getWay(), car.getStatus());
    }
}
