package com.put.urbantraffic;

import com.badlogic.gdx.utils.Array;
import lombok.Data;

import javax.print.attribute.standard.Destination;
import java.util.ArrayList;

@Data
public class Crossing {
    static final int NODE_CIRCLE_RADIUS = 15;
    private final int id;
    private final int x;
    private final int y;

    private final ArrayList<Car> forwardTopCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnRightTopCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnLeftTopCardField = new ArrayList<Car>();

    private final ArrayList<Car> forwardBottomCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnRightBottomCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnLeftBottomCardField = new ArrayList<Car>();

    private final ArrayList<Car> forwardLeftCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnRightLeftCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnLeftLeftCardField = new ArrayList<Car>();

    private final ArrayList<Car> forwardRightCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnRightRightCardField = new ArrayList<Car>();
    private final ArrayList<Car> turnLeftRightCardField = new ArrayList<Car>();

    private TrafficLightsSupervisor trafficLightsSupervisor = new TrafficLightsSupervisor();


//    boolean mayTurnLeft(Car.Way current, Car.Way next){
//
//        return true;
//    }
//
//    boolean mayEnterCrossing(Car car){
//
//        if(x < car.getCarPosition().getX()){
//            if(trafficLightsSupervisor.getRightTrafficLight().getCurrentColor() == Light.GREEN){
//                rightCardField = car;
//                return true;
//            }
//            return false;
//        }
//
//        if( x > car.getCarPosition().getX()){
//            if(trafficLightsSupervisor.getLeftTrafficLight().getCurrentColor() == Light.GREEN){
//                leftCardField = car;
//                return true;
//            }
//            return false;
//        }
//
//        if(y < car.getCarPosition().getY()){
//            if(trafficLightsSupervisor.getTopTrafficLight().getCurrentColor() == Light.GREEN){
//                topCardField = car;
//                return true;
//            }
//            return false;
//        }
//
//        if(y > car.getCarPosition().getY()){
//            if(trafficLightsSupervisor.getBottomTrafficLight().getCurrentColor() == Light.GREEN){
//                bottomCardField = car;
//                return true;
//            }
//            return false;
//        }
//
//        return false;
//    }

    public void goOutFromCrossing(Car car) {

        switch (car.getWay()) {
            case TOP:
                if(turnLeftBottomCardField.contains(car)) {
                    turnLeftBottomCardField.remove(car);
                }
                else if(turnRightBottomCardField.contains(car)) {
                    turnRightBottomCardField.remove(car);
                }
                else if(forwardBottomCardField.contains(car)) {
                    forwardBottomCardField.remove(car);
                }
                break;
            case RIGHT:
                if(turnLeftLeftCardField.contains(car)) {
                        turnLeftLeftCardField.remove(car);
                }
                else if(turnRightLeftCardField.contains(car)) {
                    turnRightLeftCardField.remove(car);
                }
                else if(forwardLeftCardField.contains(car)) {
                        forwardLeftCardField.remove(car);
                }
                break;
            case BOTTOM:
                if(turnLeftTopCardField.contains(car)) {
                    turnLeftTopCardField.remove(car);
                }
                else if(turnRightTopCardField.contains(car)) {
                    turnRightTopCardField.remove(car);
                }
                else if(forwardTopCardField.contains(car)) {
                        forwardTopCardField.remove(car);
                }
                break;
            case LEFT:
                if(turnLeftRightCardField.contains(car)) {
                    turnLeftRightCardField.remove(car);
                }
                else if(turnRightRightCardField.contains(car)) {
                        turnRightRightCardField.remove(car);
                }
                else if(forwardRightCardField.contains(car)) {
                        forwardRightCardField.remove(car);

                }
                break;
        }
    }

    public boolean isGoOnCrossingPossible(Car car) {
        Car.Way way = car.getWay();
        Direction direction = car.getDirection();

        if (way == Car.Way.TOP) {
            if(trafficLightsSupervisor.getBottomTrafficLight().getCurrentColor() == Light.GREEN){
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
        }


        else if (way == Car.Way.BOTTOM) {
            if(trafficLightsSupervisor.getTopTrafficLight().getCurrentColor() == Light.GREEN) {
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
        }

        else if (way == Car.Way.LEFT) {
            if(trafficLightsSupervisor.getRightTrafficLight().getCurrentColor() == Light.GREEN) {
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
        }


        else if (way == Car.Way.RIGHT){
            if(trafficLightsSupervisor.getLeftTrafficLight().getCurrentColor() == Light.GREEN) {
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

//    public boolean isGoForwardPossible(Car car) {
//        if(car.getNextWay() == Car.Way.TOP)
//            if(forwardBottomCardField.size() == 0 && turnLeftBottomCardField.size() < 2){
//                forwardBottomCardField.add(car);
//                return true;
//            }
//
//        if(car.getNextWay() == Car.Way.BOTTOM)
//            if(forwardTopCardField.size() == 0 && turnLeftTopCardField.size() < 2){
//                forwardTopCardField.add(car);
//                return true;
//            }
//
//        if(car.getNextWay() == Car.Way.LEFT)
//            if(forwardRightCardField.size() == 0 && turnLeftRightCardField.size() < 2){
//                forwardRightCardField.add(car);
//                return true;
//            }
//
//        if(car.getNextWay() == Car.Way.RIGHT)
//            if(forwardLeftCardField.size() == 0 && turnLeftLeftCardField.size() < 2){
//                forwardLeftCardField.add(car);
//                return true;
//            }
//
//        return false;
//    }
//
//    public boolean isTurnRightPossible(Car car) {
//        if(car.getNextWay() == Car.Way.TOP)
//            if(turnRightBottomCardField.size() == 0 && turnLeftBottomCardField.size() < 2){
//                turnRightBottomCardField.add(car);
//                return true;
//            }
//
//        if(car.getNextWay() == Car.Way.BOTTOM)
//            if(turnRightTopCardField.size() == 0 && turnLeftTopCardField.size() < 2){
//                turnRightTopCardField.add(car);
//                return true;
//            }
//
//        if(car.getNextWay() == Car.Way.LEFT)
//            if(turnRightRightCardField.size() == 0 && turnLeftRightCardField.size() < 2){
//                turnRightTopCardField.add(car);
//                return true;
//            }
//
//        if(car.getNextWay() == Car.Way.RIGHT)
//            if(turnRightLeftCardField.size() == 0 && turnLeftLeftCardField.size() < 2){
//                turnRightTopCardField.add(car);
//                return true;
//            }
//
//        return false;
//    }

    public boolean isTurnLeftPossible(Car car) {
        switch (car.getWay()){
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
