package com.put.urbantraffic;

import lombok.Data;

@Data
public class Crossing {
    private final int id;
    private final int x;
    private final int y;
    private Car topCardField = null;
    private Car bottomCardField = null;
    private Car leftCardField = null;
    private Car rightCardField = null;
    private TrafficLightsSupervisor trafficLightsSupervisor = new TrafficLightsSupervisor();


    boolean mayTurnLeft(Car.Way current, Car.Way next){
        if(current == Car.Way.BOTTOM && next == Car.Way.LEFT) {
            return leftCardField == null;
        }else if(current == Car.Way.TOP && next == Car.Way.RIGHT) {
            return rightCardField == null;
        }else if(current == Car.Way.LEFT && next == Car.Way.TOP) {
            return topCardField == null;
        }
        else if(current == Car.Way.RIGHT && next == Car.Way.BOTTOM){
            return bottomCardField == null;
        }

        return true;
    }

    boolean mayEnterCrossing(Car car){

        if(x < car.getActualNode().getX()){
            if(trafficLightsSupervisor.getRightTrafficLight().getCurrentColor() == Light.GREEN){
                rightCardField = car;
                return true;
            }
            return false;
        }

        if( x > car.getActualNode().getX()){
            if(trafficLightsSupervisor.getLeftTrafficLight().getCurrentColor() == Light.GREEN){
                leftCardField = car;
                return true;
            }
            return false;
        }

        if(y < car.getActualNode().getY()){
            if(trafficLightsSupervisor.getTopTrafficLight().getCurrentColor() == Light.GREEN){
                topCardField = car;
                return true;
            }
            return false;
        }

        if(y > car.getActualNode().getY()){
            if(trafficLightsSupervisor.getBottomTrafficLight().getCurrentColor() == Light.GREEN){
                bottomCardField = car;
                return true;
            }
            return false;
        }

        return false;
    }

    public void goOutFromCrossing(Car car) {
        if(topCardField == car){
            topCardField = null;
        }else if(bottomCardField == car){
            bottomCardField = null;
        }else if(leftCardField == car){
            leftCardField = null;
        }else if(rightCardField == car){
            rightCardField = null;
        }
    }
}
