package com.put.urbantraffic;

import java.util.List;

//public class SimulatorChild extends Thread{
public class SimulatorChild{
    public int[] lightDeltas;
    public float valueOfGoalFunction;

    public void run(){
//        System.out.println("Simulated!");
        int sumator=0;
        for(int delta :lightDeltas){
            sumator+=delta;
//            System.out.print(delta + " ");
        }
//        System.out.println("== " + sumator);
        valueOfGoalFunction = sumator;
    }


}
