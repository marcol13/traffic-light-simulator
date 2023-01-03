package com.put.urbantraffic;

import java.util.Random;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrafficLightsSupervisor {
    static final int YELLOW_DURATION = Settings.YELLOW_LIGHT_LENGTH * Settings.TIME_PRECISION;
    static final int ALL_RED_DURATION = Settings.ALL_RED_LIGHT_LENGTH * Settings.TIME_PRECISION;
    private TrafficLightsSettings trafficLightsSettings;
    private final Random rand;

    private int timeToChangeLights;
    private int changeCount = 0;

    public TrafficLightsSupervisor(TrafficLightsSettings trafficLightsSettings, Random rand) {
        this.trafficLightsSettings = trafficLightsSettings;
        this.rand = rand;
        this.timeToChangeLights = trafficLightsSettings.getOffset() + trafficLightsSettings.getGreenDuration() + 2 * YELLOW_DURATION + ALL_RED_DURATION;
    }

    @Override
    public String toString() {
// TODO Change BRAK XDDD to EMPTY / NULL whatever
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

    enum Orientation {
        HORIZONTAL,
        VERTICAL;

        Orientation opposite() {
            if (this == HORIZONTAL) return VERTICAL;
            else return HORIZONTAL;
        }
    }

    Orientation nextGreen = null;

    void turnOnLights() {
        if (trafficLightsSettings.getOffset() <= 0) {
            changeLight(topTrafficLight, Light.GREEN);
            changeLight(bottomTrafficLight, Light.GREEN);
            changeLight(leftTrafficLight, Light.RED);
            changeLight(rightTrafficLight, Light.RED);
            nextGreen = Orientation.HORIZONTAL;
        } else {
            changeLight(topTrafficLight, Light.RED);
            changeLight(bottomTrafficLight, Light.RED);
            changeLight(leftTrafficLight, Light.GREEN);
            changeLight(rightTrafficLight, Light.GREEN);
            nextGreen = Orientation.VERTICAL;
        }
    }

    private void changeLight(TrafficLight trafficLight, boolean changeToGreen) {
        if (trafficLight != null) {
            Light newLight = changeToGreen ? Light.GREEN : Light.RED;
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
        if (timeToChangeLights <= 0) {
            if (nextGreen == Orientation.VERTICAL) {
                switchYellow(topTrafficLight, false);
                switchYellow(bottomTrafficLight, false);
            } else {
                switchYellow(leftTrafficLight, false);
                switchYellow(rightTrafficLight, false);
            }

            changeLight(topTrafficLight, nextGreen == Orientation.VERTICAL);
            changeLight(bottomTrafficLight, nextGreen == Orientation.VERTICAL);
            changeLight(leftTrafficLight, nextGreen == Orientation.HORIZONTAL);
            changeLight(rightTrafficLight, nextGreen == Orientation.HORIZONTAL);
            nextGreen = nextGreen.opposite();

            changeCount++;
            int currentLightDuration = changeCount % 2 == 0 ?
                    trafficLightsSettings.getGreenDuration() : trafficLightsSettings.getRedDuration();
            timeToChangeLights = 2 * YELLOW_DURATION + currentLightDuration + ALL_RED_DURATION;
        } else if (timeToChangeLights < YELLOW_DURATION) {
            if (nextGreen == Orientation.VERTICAL) {
                switchYellow(topTrafficLight, true);
                switchYellow(bottomTrafficLight, true);
            } else {
                switchYellow(leftTrafficLight, true);
                switchYellow(rightTrafficLight, true);
            }
        } else if (timeToChangeLights < ALL_RED_DURATION + YELLOW_DURATION) {
            nextGreen = nextGreen.opposite();
            if (nextGreen.opposite() /* current green */ == Orientation.VERTICAL) {
                switchYellow(topTrafficLight, false);
                switchYellow(bottomTrafficLight, false);
            } else {
                switchYellow(leftTrafficLight, false);
                switchYellow(rightTrafficLight, false);
            }
            changeLight(topTrafficLight, Light.RED);
            changeLight(bottomTrafficLight, Light.RED);
            changeLight(leftTrafficLight, Light.RED);
            changeLight(rightTrafficLight, Light.RED);
        } else if (timeToChangeLights < ALL_RED_DURATION + 2 * YELLOW_DURATION) {
            if (nextGreen.opposite() /* current green */ == Orientation.VERTICAL) {
                switchYellow(topTrafficLight, true);
                switchYellow(bottomTrafficLight, true);
            } else {
                switchYellow(leftTrafficLight, true);
                switchYellow(rightTrafficLight, true);
            }
        }
    }
}
