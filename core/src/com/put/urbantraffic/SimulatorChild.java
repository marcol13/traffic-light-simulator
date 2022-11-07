package com.put.urbantraffic;


public class SimulatorChild extends Thread {
    public int[] lightDeltas;
    public float valueOfGoalFunction;

    public void run() {
//        We need to simulate each individual here and after simulation update valueOfGoalFunction !!!!
//        Now our goal function sum of all Crossing deltas
        int sumator = 0;
        for (int delta : lightDeltas) {
            sumator += delta;
        }
        valueOfGoalFunction = sumator;
    }


}
