package com.put.urbantraffic;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimulationPulseChecker extends Thread {
    volatile boolean run = true;
    private SimulatorChild[] children;
    private int population;

    public SimulationPulseChecker(SimulatorChild[] children, int population) {
        this.children = children;
        this.population = population;
    }

    @Override
    public void run() {
        super.run();
        while (run) {
            List<SimulatorChild> ended = Arrays.stream(children).filter(child -> child.hasEnded).collect(Collectors.toList());
            System.out.println("ENDED " + ended.size());
            if (ended.size() >= population / 2) {
                Long max = ended.stream().map(child -> child.valueOfGoalFunction).sorted().collect(Collectors.toList()).get(population / 2 - 1);
                System.out.println("MAX" + max);
                System.out.flush();
                for (SimulatorChild child : children) {
                    if (child.city == null) continue;

                    System.out.println(child.getId() + ": " + child.city.waitingTime);
                    if (!child.hasEnded && child.city.waitingTime > max) {
                        child.stopSimulation();
                        System.out.println("ZABIJAM " + child.getId());
                        System.out.flush();
                    }
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
//                e.printStackTrace();
            }
        }

    }

}
