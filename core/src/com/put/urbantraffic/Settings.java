package com.put.urbantraffic;

import com.badlogic.gdx.graphics.Color;

public class Settings {

    final static float CAMERA_MOVE_SPEED = 300f;

//    25 hours -> first and last one must match
    final static int STARTING_HOUR = 0;
    final static int ENDING_HOUR = 24;
    static int TIME = STARTING_HOUR*3600;
    final static double[] TRAFFIC_LEVEL_BY_HOUR = new double[]{1.6, 1.1, 1.1, 0.9, 1.1, 1.2, 2.4, 4.4, 6.8, 7.8, 5.7, 5.3, 5.7, 5.8, 6.3, 7.4, 8.2, 6.9, 5.2, 4.2, 3.2, 2.8, 2.3, 1.9, 1.6};
    final static int CARS_QUANTITY = 10000;


///////////////////////////////////
//    Generating city
///////////////////////////////////
    //        Crossing amount < 70 -> *1
    //        Crossing amount < 300 -> *2
    //        Crossing amount < 600 -> *3
    final static int CROSSING_AMOUNT = 25;
    final static int GRID_MULTIPLIER = 1;
    final static int MESH_DISTANCE = 100;



///////////////////////////////////
//    Car, Crossing, Lane characteristics
///////////////////////////////////
    final static int CROSSING_RADIUS = 15;
    final static int CORNER_RADIUS = 7;
    final static int NODE_LANE_OFFSET = 4;
    final static int CAR_RADIUS = 10;
    final static Color CAR_CIRCLE_COLOR = Color.YELLOW;
    final static float CAR_SPEED_MULTIPLIER = 1.0f;
//    final static Color CAR_CIRCLE_COLOR = Color.GREEN;



////////////////////////////////////
//    Genetic Algorithm
////////////////////////////////////
    final static int EPOCHS = 100;
    final static int POPULATION = 100;
    final static int MUTATION_SCALE = 100;
    public static int TOURNAMENT_SELECTION_CONTESTANT = 2;
//    Needs to be change to min_delta & max_delta
    final static int INITIAL_DELTA_RANGE = 1000;


}
