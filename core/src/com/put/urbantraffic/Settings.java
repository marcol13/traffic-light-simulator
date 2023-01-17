package com.put.urbantraffic;

import com.badlogic.gdx.graphics.Color;

public class Settings {

    final static float CAMERA_MOVE_SPEED = 300f;
    final static boolean IS_DEBUG = false;
    final static boolean IS_OPTIMIZATION = true;

//    25 hours -> first and last one must match
    final static int STARTING_HOUR = 0;
    final static int ENDING_HOUR = 24;
    final static double[] TRAFFIC_LEVEL_BY_HOUR = new double[]{1.6, 1.1, 1.1, 0.9, 1.1, 1.2, 2.4, 4.4, 6.8, 7.8, 5.7, 5.3, 5.7, 5.8, 6.3, 7.4, 8.2, 6.9, 5.2, 4.2, 3.2, 2.8, 2.3, 1.9, 1.6};

    final static int CARS_QUANTITY = 70000;


///////////////////////////////////
//    Generating city
///////////////////////////////////
    //        Crossing amount < 70 -> *1
    //        Crossing amount < 300 -> *2
    //        Crossing amount < 600 -> *3
    final static int CROSSING_AMOUNT = 80;
    final static int GRID_MULTIPLIER = 2;
    final static int MESH_DISTANCE = 100;



///////////////////////////////////
//    Car, Crossing, Lane characteristics
///////////////////////////////////
    final static int CROSSING_RADIUS = 4;
    final static int CORNER_RADIUS = 4;
    final static int NODE_LANE_OFFSET = 4;
    final static int CAR_RADIUS = 2;
    final static int DISTANCE_BETWEEN_CARS_IN_JAM = 1;
    final static Color CAR_CIRCLE_COLOR = new Color(0.1f, 0.7f, 0.1f, 1);
    final static Color CAR_CIRCLE_COLOR_WAITING = new Color(0.7f, 0.1f, 0.1f, 1);
    final static float CAR_SPEED_MULTIPLIER = 1.0f;

///////////////////////////////////
//    Yellow, red light length
    final static int ALL_RED_LIGHT_LENGTH = 2;
    final static int YELLOW_LIGHT_LENGTH = 2;
    final static int MAX_RED_LIGHT_LENGTH = 300;
    final static int MAX_GREEN_LIGHT_LENGTH = 300;
    final static int MAX_OFFSET_LENGTH = MAX_RED_LIGHT_LENGTH + MAX_GREEN_LIGHT_LENGTH;

///////////////////////////////////


////////////////////////////////////
//    Genetic Algorithm
////////////////////////////////////
    final static int EPOCHS = 10;
    final static int POPULATION = 10;
    final static int MUTATION_SCALE = 100;
    final static int TOURNAMENT_SELECTION_CONTESTANT = 2;
//    TODO Needs to be change to min_delta & max_delta
    final static int INITIAL_DELTA_RANGE = 1000;

    final static int TIME_PRECISION = 3; //One second is divided into TIME_PRECISION frames so e.g. TIME_PRECISION = 30 => one day is 24 x 60 x 60 x 30 frames
    final static int HEATMAP_PRECISION = 2;
}
