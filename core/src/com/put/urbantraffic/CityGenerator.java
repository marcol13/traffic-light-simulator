package com.put.urbantraffic;

import lombok.val;

import java.util.concurrent.ThreadLocalRandom;

public class CityGenerator {
    private final int width = 2 * 16;
    private final int height = 2 * 9;
    private final int crossingAmount = 3;

    int[][] generate() {
        val grid = new int[height][width];
        val heightOffset = (height + 1) % 2;
        for (int i = 0; i < width; i++) {
            grid[height / 2 - heightOffset][i] = 1;
        }
        for (int i = 0; i < height; i++) {
            grid[i][width / 2] = 1;
        }
        for (int iter = 0; iter < crossingAmount; iter++) {
            int x = width / 2;
            int y = heightOffset / 2;
            while (grid[y][x] == 9 || grid[y][x] == 1) {
                x = 2 * generateRandomInt(1, (width - 2) / 2);
                y = 2 * generateRandomInt(1, (height - 2) / 2);
            }
            grid[y][x] = 9;
            // right
            for (int i = x + 1; i < width; i++) {
                if (grid[y][i] == 1 || grid[y][i] == 9) {
                    fillArray(grid[y], x + 1, i, 1);
                    grid[y][i] = 9;
                    break;
                }
            }
            // left
            for (int i = x - 1; i > -1; i--) {
                if (grid[y][i] == 1 || grid[y][i] == 9) {
                    fillArray(grid[y], i + 1, x, 1);
                    grid[y][i] = 9;
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
                    grid[i][x] = 9;
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
                    grid[i][x] = 9;
                    break;
                }
            }
        }

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (grid[i][j] == 9) {
                    grid[i][j] = checkIfShouldStayCrossing(grid, i, j);
                }
            }
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
        for (int[] x : new CityGenerator().generate())
        {
            for (int y : x)
            {
                System.out.print(y + " ");
            }
            System.out.println();
        }
    }
}
