package com.put.urbantraffic;


import lombok.val;

public class SimulatorChild extends Thread{
    public int[] lightDeltas;
    public float valueOfGoalFunction;

    public void run(){
//        We need to simulate each individual here and after simulation update valueOfGoalFunction !!!!
//        Now our goal function sum of all Crossing deltas
        int sumator=0;
        for(int delta :lightDeltas){
            sumator+=delta;
        }
        int N = 10000;
        val array = new int[N];
        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        // random time consuming operation
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (array[i] > array[j]) {
                    int temp = array[j];
                    array[j] = array[i];
                    array[i] = temp;
                }
            }
        }
        valueOfGoalFunction = sumator;
    }


}
