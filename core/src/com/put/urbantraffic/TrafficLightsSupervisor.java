package com.put.urbantraffic;

import lombok.Data;

import java.util.Random;

@Data
public class TrafficLightsSupervisor {
    private static final int LIGHTS_DURATION = 5;


    private TrafficLight topTrafficLight = null;
    private TrafficLight bottomTrafficLight = null;
    private TrafficLight leftTrafficLight = null;
    private TrafficLight rightTrafficLight = null;
    private int timeToChangeLights = LIGHTS_DURATION;


    void turnOnLights(){
        if(new Random().nextInt() % 2 == 0){
            changeLight(topTrafficLight, Light.GREEN);
            changeLight(bottomTrafficLight, Light.GREEN);
        }
        else{
            changeLight(leftTrafficLight, Light.RED);
            changeLight(rightTrafficLight, Light.RED);
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

    void changeAllLights(){
        timeToChangeLights--;
        if(timeToChangeLights == 0){
            changeLight(topTrafficLight);
            changeLight(bottomTrafficLight);
            changeLight(leftTrafficLight);
            changeLight(rightTrafficLight);
            timeToChangeLights = LIGHTS_DURATION;
        }

    }
}
