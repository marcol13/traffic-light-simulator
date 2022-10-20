package com.put.urbantraffic;

import lombok.val;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.Math.max;

public class CityGenerator {
    private List<Point> potentialCrossingsCoordinates = new ArrayList<Point>(); ;

    int[][] generate(int width, int height, int crossingAmount) {
//        Creating array for randomizing numbers with weights
        int scaler=10;

        int longest_distance = (int)Math.sqrt(Math.pow(((width + 1) / 2. - 2)/2, 2) + Math.pow(((height + 1) / 2. - 2)/2, 2));
        int sumOfProbabilities = 0;
        for(int i=0; i<((width + 1) / 2. - 2) ; i++){
            for(int j=0; j<((height + 1) / 2. - 2); j++){
                sumOfProbabilities += Math.pow(max((longest_distance - (int)Math.sqrt(Math.pow(((width + 1) / 2. - 2)/2 - i, 2) + Math.pow(((height + 1) / 2. - 2)/2 - j, 2))) - longest_distance*0/2., 0) , 4);
                System.out.print(Math.pow(max((longest_distance - (int)Math.sqrt(Math.pow(((width + 1) / 2. - 2)/2 - i, 2) + Math.pow(((height + 1) / 2. - 2)/2 - j, 2))) - longest_distance*0/2., 0), 4) + " ");
            }
            System.out.println();
        }


        int[][] tilesProbabilities= new int[sumOfProbabilities][2];
        int previousTilesCounter=0;
        for(int i=0; i<((width + 1) / 2. - 2); i++){
            for(int j=0; j<((height + 1) / 2. - 2); j++){
                int k;
                for(k=0; k < Math.pow(max(longest_distance - (int)Math.sqrt(Math.pow(((width + 1) / 2. - 2)/2 - i, 2) + Math.pow(((height + 1) / 2. - 2)/2 - j, 2)) - longest_distance*0/2., 0), 4); k++){
                    tilesProbabilities[previousTilesCounter+k][0] = i;
                    tilesProbabilities[previousTilesCounter+k][1] = j;
                }
                previousTilesCounter += k;
            }
        }


        System.out.println("Longest straight line to center: " + longest_distance);
        System.out.println("Array length: " + sumOfProbabilities);

        val grid = new int[height][width];
        for (int i = 0; i < width; i++) {
            grid[height / 4 * 2 + 1][i] = 1;
        }
        for (int i = 0; i < height; i++) {
            grid[i][width / 4 * 2 - 1] = 1;
        }
        grid[height / 4 * 2 + 1][width / 4 * 2 - 1] = 9;

        int crossingCounter = 0;
        while(crossingCounter < crossingAmount){
            int x = width / 4 * 2 - 1;
            int y = height / 4 * 2 + 1;
            while (grid[y][x] == 9 || grid[y][x] == 1) {
//                x = 2 * generateRandomInt(0, (width + 1) / 2 - 2) + 1;
                x = 2 * tilesProbabilities[(int) (tilesProbabilities.length*Math.random())][0] + 1;
//                y = 2 * generateRandomInt(0, (height + 1) / 2 - 2) + 1;
                y = 2 * tilesProbabilities[(int) (tilesProbabilities.length*Math.random())][1] + 1;
            }
            grid[y][x] = 9;
            potentialCrossingsCoordinates.add(new Point(x, y));

            // right
            for (int i = x + 1; i < width; i++) {
                if (grid[y][i] == 1 || grid[y][i] == 9) {
                    fillArray(grid[y], x + 1, i, 1);
                    if (grid[y][i] != 9){
                        grid[y][i] = 9;
                        potentialCrossingsCoordinates.add(new Point(i, y));
                    }
                    break;
                }
            }
            // left
            for (int i = x - 1; i > -1; i--) {
                if (grid[y][i] == 1 || grid[y][i] == 9) {
                    fillArray(grid[y], i + 1, x, 1);
                    if (grid[y][i] != 9){
                        grid[y][i] = 9;
                        potentialCrossingsCoordinates.add(new Point(i, y));
                    }
                    break;
                }
            }
            // down
            for (int i = y + 1; i < height; i++) {
                //noinspection DuplicatedCode
                if (grid[i][x] == 1 || grid[i][x] == 9) {
                    for (int j = y + 1; j < i; j++) {
                        grid[j][x] = 1;
                    }
                    if (grid[i][x] != 9){
                        grid[i][x] = 9;
                        potentialCrossingsCoordinates.add(new Point(x, i));
                    }
                    break;
                }
            }
            // up
            for (int i = y - 1; i > -1; i--) {
                //noinspection DuplicatedCode
                if (grid[i][x] == 1 || grid[i][x] == 9) {
                    for (int j = i + 1; j < y; j++) {
                        grid[j][x] = 1;
                    }
                    if (grid[i][x] != 9){
                        grid[i][x] = 9;
                        potentialCrossingsCoordinates.add(new Point(x, i));
                    }
                    break;
                }
            }

            crossingCounter = 0;
            for(int i=1; i<grid.length; i+=2){
                for(int j=1; j<grid[0].length; j+=2){
                    if(grid[i][j] == 9){
                        crossingCounter++;
                    }
                }
            }
        }

        for(Point point: potentialCrossingsCoordinates){
            grid[point.y][point.x] = checkIfShouldStayCrossing(grid, point.y, point.x);
        }
        return grid;
    }

    private int checkIfShouldStayCrossing(int[][] grid, int i, int j) {
        val sumOfNeighbours = grid[i + 1][j] + grid[i - 1][j] + grid[i][j + 1] + grid[i][j - 1];
        if (sumOfNeighbours == 2) {
            return 8;
        }
        return 9;
    }

    /**
     * @param min - minimum value
     * @param max - maximum value
     * @return random int number from range [min, max]
     */
    private int generateRandomInt(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * @param array - the array to modify
     * @param start - start index inclusive
     * @param end   - end index exclusive
     * @param value - the value to fill the array
     */
    private void fillArray(int[] array, int start, int end, int value) {
        for (int i = start; i < end; i++) {
            array[i] = value;
        }
    }

    public static void main(String[] args) {
//        for (int[] x : new CityGenerator().generate())
//        {
//            for (int y : x)
//            {
//                System.out.print(y + " ");
//            }
//            System.out.println();
//        }
    }
}
