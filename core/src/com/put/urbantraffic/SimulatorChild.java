package com.put.urbantraffic;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SimulatorChild extends Thread {
    public City city;
    public Long valueOfGoalFunction = Long.MAX_VALUE;
    private final long seed;
    List<TrafficLightsSettings> trafficLightsSettingsList;
    private SimulatorChild[] children;
    private int population;
    private final Random random = new Random();
    private boolean hasEnded = false;

    public SimulatorChild(long seed, List<TrafficLightsSettings> trafficLightsSettingsList, SimulatorChild[] children, int population) {
        this.seed = seed;
        this.trafficLightsSettingsList = trafficLightsSettingsList;
        this.children = children;
        this.population = population;
    }

    public void run() {
        if (trafficLightsSettingsList == null) {
            city = new City(new Random(seed), new ArrayList<>(), null);
            int crossingsSize = city.getCrossings().size();
            trafficLightsSettingsList = generateSettings(crossingsSize);
            city.setTrafficLightsSettingsList(trafficLightsSettingsList);
        } else {
            city = new City(new Random(seed), trafficLightsSettingsList, null);
        }
        city.startSimulation();
        valueOfGoalFunction = city.waitingTime;
        hasEnded = true;
        List<SimulatorChild> ended = Arrays.stream(children).filter(children -> children.hasEnded).collect(Collectors.toList());
        if (ended.size() > population / 2) {
            Long max = ended.stream().map(children -> children.valueOfGoalFunction).sorted().collect(Collectors.toList()).get(population / 2);
            for (SimulatorChild child : children) {
                if (child.isAlive() && !child.isInterrupted() && !child.hasEnded && child.city.waitingTime > max) {
                    child.interrupt();
                    System.out.println("ZABIJAM " + child.getId());
                }
            }
        }
    }

    public List<TrafficLightsSettings> generateSettings(int size) {
        List<TrafficLightsSettings> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            final int greenDuration = random.nextInt(Settings.MAX_GREEN_LIGHT_LENGTH);
            final int redDuration = random.nextInt(Settings.MAX_RED_LIGHT_LENGTH);
            final int offset = random.nextInt(600);
            TrafficLightsSettings trafficLightsSettings = new TrafficLightsSettings(greenDuration, redDuration, offset);
            list.add(trafficLightsSettings);
        }
        return list;
    }
}
