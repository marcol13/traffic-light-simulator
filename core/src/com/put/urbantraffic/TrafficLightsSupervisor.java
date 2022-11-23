package com.put.urbantraffic;

import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrafficLightsSupervisor {
    private static final int YELLOW_DURATION = 15; // TODO: Move to settings
    private final int greenDuration;
    private final int redDuration;
    private final int offset;
    private final Random rand;

    private int timeToChangeLights;
    private int changeCount = 0;

    public TrafficLightsSupervisor(int greenDuration, int redDuration, int offset, Random rand) {
        this.greenDuration = greenDuration;
        this.redDuration = redDuration;
        this.offset = offset;
        this.rand = rand;
        this.timeToChangeLights = offset + greenDuration + YELLOW_DURATION;
    }

    @Override
    public String toString() {

        String top = topTrafficLight != null ? "TOP" : "BRAK";
        String bottom = bottomTrafficLight != null ? "BOTTOM" : "BRAK";
        String left = leftTrafficLight != null ? "LEFT" : "BRAK";
        String right = rightTrafficLight != null ? "RIGHT" : "BRAK";


        return "TrafficLightsSupervisor{" +
                "topTrafficLight=" + top +
                ", bottomTrafficLight=" + bottom +
                ", leftTrafficLight=" + left +
                ", rightTrafficLight=" + right +
                '}';
    }

    private TrafficLight topTrafficLight = null;
    private TrafficLight bottomTrafficLight = null;
    private TrafficLight leftTrafficLight = null;
    private TrafficLight rightTrafficLight = null;

    void turnOnLights(){
        if (offset == 0) {
            changeLight(topTrafficLight, Light.GREEN);
            changeLight(bottomTrafficLight, Light.GREEN);
            changeLight(leftTrafficLight, Light.RED);
            changeLight(rightTrafficLight, Light.RED);
        } else {
            changeLight(topTrafficLight, Light.RED);
            changeLight(bottomTrafficLight, Light.RED);
            changeLight(leftTrafficLight, Light.GREEN);
            changeLight(rightTrafficLight, Light.GREEN);
        }
    }

    private void changeLight(TrafficLight trafficLight) {
        if (trafficLight != null) {
            Light newLight = trafficLight.getCurrentColor() == Light.RED ? Light.GREEN : Light.RED;
            trafficLight.setCurrentColor(newLight);
        }
    }

    private void changeLight(TrafficLight trafficLight, Light light) {
        if (trafficLight != null) {
            trafficLight.setCurrentColor(light);
        }
    }

    private void switchYellow(TrafficLight trafficLight, boolean turnOn) {
        if (trafficLight != null) {
            trafficLight.setYellow(turnOn);
        }
    }

    void changeAllLights() {
        timeToChangeLights--;
        if (timeToChangeLights == 0) {
            switchYellow(topTrafficLight, false);
            switchYellow(bottomTrafficLight, false);
            switchYellow(leftTrafficLight, false);
            switchYellow(rightTrafficLight, false);

            changeLight(topTrafficLight);
            changeLight(bottomTrafficLight);
            changeLight(leftTrafficLight);
            changeLight(rightTrafficLight);

            changeCount++;
            timeToChangeLights = YELLOW_DURATION + (changeCount % 2 == 0 ? greenDuration : redDuration);
        } else if (timeToChangeLights < YELLOW_DURATION) {
            switchYellow(topTrafficLight, true);
            switchYellow(bottomTrafficLight, true);
            switchYellow(leftTrafficLight, true);
            switchYellow(rightTrafficLight, true);
        }
    }
}
