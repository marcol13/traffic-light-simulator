package com.put.urbantraffic.drawablemodels;

import com.badlogic.gdx.utils.Null;
import com.put.urbantraffic.*;
import lombok.Data;

@Data
public class DrawableCrossingTrafficLight {
    private final int x;
    private final int y;
    private final SingleTrafficLight topTrafficLight;
    private final SingleTrafficLight bottomTrafficLight;
    private final SingleTrafficLight leftTrafficLight;
    private final SingleTrafficLight rightTrafficLight;
    private final TrafficLightsSettings trafficLightsSettings;

    @Data
    public static class SingleTrafficLight {
        private final boolean isYellow;
        private final Light currentColor;

        static @Null SingleTrafficLight fromTrafficLight(@Null TrafficLight trafficLight) {
            if (trafficLight == null) return null;
            return new SingleTrafficLight(trafficLight.isYellow(), trafficLight.getCurrentColor());
        }
    }

    public static DrawableCrossingTrafficLight fromCrossing(Crossing crossing) {
        TrafficLightsSupervisor lightsSupervisor = crossing.getTrafficLightsSupervisor();
        return new DrawableCrossingTrafficLight(
                crossing.getX(),
                crossing.getY(),
                SingleTrafficLight.fromTrafficLight(lightsSupervisor.getTopTrafficLight()),
                SingleTrafficLight.fromTrafficLight(lightsSupervisor.getBottomTrafficLight()),
                SingleTrafficLight.fromTrafficLight(lightsSupervisor.getLeftTrafficLight()),
                SingleTrafficLight.fromTrafficLight(lightsSupervisor.getRightTrafficLight()),
                lightsSupervisor.getTrafficLightsSettings()
        );
    }
}
