package com.put.urbantraffic;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SimulatorChild extends Thread {
    public City city;
    public float valueOfGoalFunction;
    private final long seed;
    List<TrafficLightsSettings> trafficLightsSettingsList;
    private final Random random = new Random();

    public SimulatorChild(long seed, List<TrafficLightsSettings> trafficLightsSettingsList) {
        this.seed = seed;
        this.trafficLightsSettingsList = trafficLightsSettingsList;
    }

    public void run() {
        if (trafficLightsSettingsList == null) {
            city = new City(new Random(seed), new ArrayList<>());
            int crossingsSize = city.getCrossings().size();
            trafficLightsSettingsList = generateSettings(crossingsSize);
            city.setTrafficLightsSettingsList(trafficLightsSettingsList);
        } else {
            city = new City(new Random(seed), trafficLightsSettingsList);
        }
        city.startSimulation();
        valueOfGoalFunction = city.waitingTime;
    }

    public List<TrafficLightsSettings> generateSettings(int size) {
        List<TrafficLightsSettings> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            final int greenDuration = random.nextInt(600);
            final int redDuration = random.nextInt(600);
            final int offset = random.nextInt(600);
            TrafficLightsSettings trafficLightsSettings = new TrafficLightsSettings(greenDuration, redDuration, offset);
            list.add(trafficLightsSettings);
        }
        return list;
    }
}
