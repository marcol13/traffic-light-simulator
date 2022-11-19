package com.put.urbantraffic;

public class SETTINGS {
    static int TIME = 0;


///////////////////////////////////
//    Generating city
///////////////////////////////////
    //        Crossing amount < 70 -> *1
    //        Crossing amount < 300 -> *2
    //        Crossing amount < 600 -> *3
    final static int crossingAmount = 50;
    final static int gridMultiplier = 1;



////////////////////////////////////
//    Genetic Algorithm
////////////////////////////////////
    final static int epochs = 100;
    final static int population = 100;
    final static int mutationScale = 100;
    public static int tournamentSelectionContestants = 2;
//    Need to be change to min_delta & max_delta
    final static int initialDeltaRange = 1000;




}
