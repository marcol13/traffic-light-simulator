package com.put.urbantraffic;

import lombok.Data;
import java.util.ArrayList;

@Data
public class Crossing {
    static final int NODE_CIRCLE_RADIUS = 15;
    private final int id;
    private final int x;

    @Override
    public String toString() {
        return "Crossing{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    private final int y;

    private final ArrayList<Car> forwardTopCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnRightTopCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnLeftTopCardField = new ArrayList<Car>();

    private final ArrayList<Car> forwardBottomCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnRightBottomCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnLeftBottomCardField = new ArrayList<Car>();

    private final ArrayList<Car> forwardLeftCardField = new ArrayList<>();
    private final ArrayList<Car> turnRightLeftCardField = new ArrayList<>();
    private final ArrayList<Car> turnLeftLeftCardField = new ArrayList<>();

    private final ArrayList<Car> forwardRightCardField = new ArrayList<>();
    private final ArrayList<Car> turnRightRightCardField = new ArrayList<>();
    private final ArrayList<Car> turnLeftRightCardField = new ArrayList<>();

    private TrafficLightsSupervisor trafficLightsSupervisor = new TrafficLightsSupervisor();


    @SuppressWarnings("RedundantCollectionOperation")
    public void goOutFromCrossing(Car car) {

        switch (car.getWay()) {
            case TOP:
                if (turnLeftBottomCardField.contains(car)) {
                    turnLeftBottomCardField.remove(car);
                } else if (turnRightBottomCardField.contains(car)) {
                    turnRightBottomCardField.remove(car);
                } else if (forwardBottomCardField.contains(car)) {
                    forwardBottomCardField.remove(car);
                }
                break;
            case RIGHT:
                if (turnLeftLeftCardField.contains(car)) {
                    turnLeftLeftCardField.remove(car);
                } else if (turnRightLeftCardField.contains(car)) {
                    turnRightLeftCardField.remove(car);
                } else if (forwardLeftCardField.contains(car)) {
                    forwardLeftCardField.remove(car);
                }
                break;
            case BOTTOM:
                if (turnLeftTopCardField.contains(car)) {
                    turnLeftTopCardField.remove(car);
                } else if (turnRightTopCardField.contains(car)) {
                    turnRightTopCardField.remove(car);
                } else if (forwardTopCardField.contains(car)) {
                    forwardTopCardField.remove(car);
                }
                break;
            case LEFT:
                if (turnLeftRightCardField.contains(car)) {
                    turnLeftRightCardField.remove(car);
                } else if (turnRightRightCardField.contains(car)) {
                    turnRightRightCardField.remove(car);
                } else if (forwardRightCardField.contains(car)) {
                    forwardRightCardField.remove(car);

                }
                break;
        }
    }

    public boolean isGoOnCrossingPossible(Car car) {
        Car.Way way = car.getWay();
        Direction direction = car.getDirection();
        System.out.println("Car " + car.getCarPosition() + " crossing " + x + " " + y);
        System.out.println(" way " + way + " direction " + direction);
        System.out.println("LightSupervisor: " + trafficLightsSupervisor.toString());
        if (way == Car.Way.TOP) {
            System.out.println("Traffic Light" + trafficLightsSupervisor.getBottomTrafficLight().getCurrentColor());
            if (trafficLightsSupervisor.getBottomTrafficLight().getCurrentColor() == Light.GREEN) {
                if (direction == Direction.FORWARD) {
                    if (forwardBottomCardField.size() == 0 && turnLeftBottomCardField.size() < 2) {
                        forwardBottomCardField.add(car);
                        return true;
                    }
                } else if (direction == Direction.RIGHT) {
                    if (turnRightBottomCardField.size() == 0 && turnLeftBottomCardField.size() < 2) {
                        turnRightBottomCardField.add(car);
                        return true;
                    }
                } else if (direction == Direction.LEFT) {
                    if (turnLeftBottomCardField.size() < 2) {
                        turnLeftBottomCardField.add(car);
                        return true;
                    }
                }
            }
        } else if (way == Car.Way.BOTTOM) {
            System.out.println("Traffic Light" + trafficLightsSupervisor.getTopTrafficLight().getCurrentColor());

            if (trafficLightsSupervisor.getTopTrafficLight().getCurrentColor() == Light.GREEN) {
                if (direction == Direction.FORWARD) {
                    if (forwardTopCardField.size() == 0 && turnLeftTopCardField.size() < 2) {
                        forwardTopCardField.add(car);
                        return true;
                    }
                } else if (direction == Direction.RIGHT) {
                    if (turnRightTopCardField.size() == 0 && turnLeftTopCardField.size() < 2) {
                        turnRightTopCardField.add(car);
                        return true;
                    }
                } else if (direction == Direction.LEFT) {
                    if (turnLeftTopCardField.size() < 2) {
                        turnLeftTopCardField.add(car);
                        return true;
                    }
                }
            }
        } else if (way == Car.Way.LEFT) {
            System.out.println("Traffic Light" + trafficLightsSupervisor.getRightTrafficLight().getCurrentColor());

            if (trafficLightsSupervisor.getRightTrafficLight().getCurrentColor() == Light.GREEN) {
                if (direction == Direction.FORWARD) {
                    if (forwardRightCardField.size() == 0 && turnLeftRightCardField.size() < 2) {
                        forwardRightCardField.add(car);
                        return true;
                    }
                } else if (direction == Direction.RIGHT) {
                    if (turnRightRightCardField.size() == 0 && turnLeftRightCardField.size() < 2) {
                        turnRightRightCardField.add(car);
                        return true;
                    }
                } else if (direction == Direction.LEFT) {
                    if (turnLeftRightCardField.size() < 2) {
                        turnLeftRightCardField.add(car);
                        return true;
                    }
                }
            }
        } else if (way == Car.Way.RIGHT) {
            System.out.println("Traffic Light" + trafficLightsSupervisor.getLeftTrafficLight().getCurrentColor());

            if (trafficLightsSupervisor.getLeftTrafficLight().getCurrentColor() == Light.GREEN) {
                if (direction == Direction.FORWARD) {
                    if (forwardLeftCardField.size() == 0 && turnLeftLeftCardField.size() < 2) {
                        forwardLeftCardField.add(car);
                        return true;
                    }
                } else if (direction == Direction.RIGHT) {
                    if (turnRightLeftCardField.size() == 0 && turnLeftLeftCardField.size() < 2) {
                        turnRightLeftCardField.add(car);
                        return true;
                    }
                } else if (direction == Direction.LEFT) {
                    if (turnLeftLeftCardField.size() < 2) {
                        turnLeftLeftCardField.add(car);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isTurnLeftPossible(Car car) {
        switch (car.getWay()) {
            case TOP:
                return forwardTopCardField.size() == 0;
            case RIGHT:
                return forwardRightCardField.size() == 0;
            case BOTTOM:
                return forwardBottomCardField.size() == 0;
            case LEFT:
                return forwardLeftCardField.size() == 0;
        }
        return false;
    }
}
