package com.put.urbantraffic;

import lombok.Data;

import java.util.Random;

@Data
public class TrafficLightsSupervisor {
    private static final int LIGHTS_DURATION = 120 * 2;


    @Override
    public String toString() {

        String top = topTrafficLight != null ? "TOP" : "BRAK";
        String bottom = bottomTrafficLight != null ? "BOTTOM" : "BRAK";
        String left = leftTrafficLight != null ? "LEFT" : "BRAK";
        String right = rightTrafficLight != null ? "RIGHT" : "BRAK";


        return "TrafficLightsSupervisor{" +
                "topTrafficLight=" +  top +
                ", bottomTrafficLight=" + bottom +
                ", leftTrafficLight=" + left +
                ", rightTrafficLight=" + right +
                '}';
    }

    private TrafficLight topTrafficLight = null;
    private TrafficLight bottomTrafficLight = null;
    private TrafficLight leftTrafficLight = null;
    private TrafficLight rightTrafficLight = null;

    private int timeToChangeLights = LIGHTS_DURATION;



    void turnOnLights(){
        if(new Random().nextInt() % 2 == 0){
            changeLight(topTrafficLight, Light.GREEN);
            changeLight(bottomTrafficLight, Light.GREEN);
            changeLight(leftTrafficLight, Light.RED);
            changeLight(rightTrafficLight, Light.RED);
        }
        else{
            changeLight(topTrafficLight, Light.RED);
            changeLight(bottomTrafficLight, Light.RED);
            changeLight(leftTrafficLight, Light.GREEN);
            changeLight(rightTrafficLight, Light.GREEN);
        }
    }

    private void changeLight(TrafficLight trafficLight) {
        if(trafficLight != null){
            Light newLight = trafficLight.getCurrentColor() == Light.RED ? Light.GREEN : Light.RED;
            trafficLight.setCurrentColor(newLight);
        }
    }

    private void changeLight(TrafficLight trafficLight, Light light){
        if(trafficLight != null){
            trafficLight.setCurrentColor(light);
        }
    }

    private void switchYellow(TrafficLight trafficLight, boolean turnOn){
        if(trafficLight != null){
            trafficLight.setYellow(turnOn);
        }
    }

    void changeAllLights(){
        timeToChangeLights--;
        if(timeToChangeLights <  LIGHTS_DURATION/15 && timeToChangeLights != 0){
            switchYellow(topTrafficLight, true);
            switchYellow(bottomTrafficLight, true);
            switchYellow(leftTrafficLight,true );
            switchYellow(rightTrafficLight, true );
        }
        else if(timeToChangeLights == 0){

            switchYellow(topTrafficLight, false);
            switchYellow(bottomTrafficLight, false);
            switchYellow(leftTrafficLight, false);
            switchYellow(rightTrafficLight, false);

            changeLight(topTrafficLight);
            changeLight(bottomTrafficLight);
            changeLight(leftTrafficLight);
            changeLight(rightTrafficLight);
            timeToChangeLights = LIGHTS_DURATION;
        }

    }
}
