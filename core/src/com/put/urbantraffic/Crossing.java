package com.put.urbantraffic;

import lombok.Data;

@Data
public class Crossing {
    private final int id;
    private final int x;
    private final int y;
    private boolean topCardField = true;
    private boolean bottomCardField = true;
    private boolean leftCardField = true;
    private boolean rightCardField = true;
    private TrafficLightsSupervisor trafficLightsSupervisor = new TrafficLightsSupervisor();

    boolean canIRide(int carX, int carY){

        System.out.println("Crossings:" + x +" "+ y + " Car X Y: " + carX  + " "+ carY);

        if(x < carX){
            return trafficLightsSupervisor.getRightTrafficLight().getCurrentColor() == Light.GREEN;
        }

        if( x > carX){
            return trafficLightsSupervisor.getLeftTrafficLight().getCurrentColor() == Light.GREEN;
        }

        if(y < carY){
            return trafficLightsSupervisor.getTopTrafficLight().getCurrentColor() == Light.GREEN;
        }

        if(y > carY){
            return trafficLightsSupervisor.getBottomTrafficLight().getCurrentColor() == Light.GREEN;
        }

        return false;
    }
}
