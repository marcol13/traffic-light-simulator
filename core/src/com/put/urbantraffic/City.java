package com.put.urbantraffic;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class City {
    private static List<Crossing> crossings;
    private static List<Road> roads;

    private static final int MESH_OFFSET = 100;

    public City(){

        int[][] grid = new CityGenerator().generate(2*16, 2*9, 15);
        int counter = 0;
        for (int[] x : grid)
        {
            for (int y : x)
            {
                System.out.print(y + " ");
                if(y == 8){
                    counter++;
                }
            }
            System.out.println();
        }
        System.out.println(counter);
        crossings = new ArrayList<>();
        roads = new ArrayList<>();
        parseGridToClasses(grid);
    }


    private static void parseGridToClasses(int[][] grid) {
        int crossingId = 0;
        for (int y = 1; y < grid.length; y+=2) {
            for (int x = 1; x < grid[0].length; x+=2) {
                if (grid[y][x] == 9) {
                    crossings.add(new Crossing(crossingId, x * MESH_OFFSET, y * MESH_OFFSET, new ArrayList<Light>()));
                    crossingId++;
                }
            }
        }

        int laneId = 0;

//        Checking horizontal roads
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[0].length; x++) {
                if (grid[y][x] == 9) {
                    if (grid[y][x + 1] == 1) {
                        Crossing startCrossing = null;
                        for (Crossing crossing : crossings) {
                            if (crossing.getX() == x && crossing.getY() == y) {
                                startCrossing = crossing;
                                break;
                            }
                        }
                        List<Node> nodes = new ArrayList<>();
                        nodes.add(new Node(x * MESH_OFFSET, y * MESH_OFFSET));
                        x += 2;
                        while (grid[y][x] == 1) {
                            x++;
                            if(x == grid[0].length){
                                x--;
                                break;
                            }
                        }
                        Crossing endCrossing = null;
                        if (grid[y][x] == 9) {
                            nodes.add(new Node(x * MESH_OFFSET, y * MESH_OFFSET));
//                            Search for that crossing
                            for (Crossing crossing : crossings) {
                                if (crossing.getX() == x && crossing.getY() == y) {
                                    endCrossing = crossing;
                                }
                            }
                            roads.add(new Road(50, Arrays.asList(new Lane(laneId, startCrossing, endCrossing, new ArrayList<Direction>()), new Lane(laneId + 1, endCrossing, startCrossing, new ArrayList<Direction>())), nodes));
                            laneId += 2;
                            x--;
                        }
                    }
                }
            }
        }


//        Checking vertical roads
        for (int x = 0; x < grid[0].length; x++) {
            for (int y = 0; y < grid.length; y++) {
                if (grid[y][x] == 9) {
                    if (grid[y + 1][x] == 1) {
                        Crossing startCrossing = null;
                        for (Crossing crossing : crossings) {
                            if (crossing.getX() == x && crossing.getY() == y) {
                                startCrossing = crossing;
                                break;
                            }
                        }
                        List<Node> nodes = new ArrayList<>();
                        nodes.add(new Node(x * MESH_OFFSET, y * MESH_OFFSET));
                        y += 2;
                        while (grid[y][x] == 1) {
                            y++;
                            if(y == grid.length){
                                y--;
                                break;
                            }
                        }
                        Crossing endCrossing = null;
                        if (grid[y][x] == 9) {
                            nodes.add(new Node(x * MESH_OFFSET, y * MESH_OFFSET));
//                            Search for that crossing
                            for (Crossing crossing : crossings) {
                                if (crossing.getX() == x && crossing.getY() == y) {
                                    endCrossing = crossing;
                                }
                            }
                            roads.add(new Road(50, Arrays.asList(new Lane(laneId, startCrossing, endCrossing, new ArrayList<Direction>()), new Lane(laneId + 1, endCrossing, startCrossing, new ArrayList<Direction>())), nodes));
                            laneId += 2;
                            y--;
                        }
                    }
                }
            }
        }



//        Checking Right up/down
        for (int y = 1; y < grid.length; y+=2) {
            for (int x = 1; x < grid[0].length; x+=2) {
                int tempx = x;
                int tempy = y;
                if (grid[tempy][tempx] == 9) {
                    if (grid[tempy][tempx + 1] == 1) {
                        Crossing startCrossing = null;
                        for (Crossing crossing : crossings) {
                            if (crossing.getX() == tempx && crossing.getY() == tempy) {
                                startCrossing = crossing;
                                break;
                            }
                        }
                        List<Node> nodes = new ArrayList<>();
                        nodes.add(new Node(tempx * MESH_OFFSET, tempy * MESH_OFFSET));
                        tempx += 2;
                        while (grid[tempy][tempx] == 1) {
                            tempx++;
                            if(tempx == grid[0].length){
                                tempx--;
                                break;
                            }
                        }
                        Crossing endCrossing = null;
                        if (grid[tempy][tempx] == 8) {
                            nodes.add(new Node(tempx * MESH_OFFSET, tempy * MESH_OFFSET));
                            int directionAfterTurn;
                            if(grid[tempy + 1][tempx] == 1) {
                                directionAfterTurn = 1;
                            }else{
                                directionAfterTurn = -1;
                            }
                            tempy += directionAfterTurn;
                            while (grid[tempy][tempx] == 1) {
                                tempy += directionAfterTurn;
                            }

                            nodes.add(new Node(tempx * MESH_OFFSET, tempy * MESH_OFFSET));
//                            Search for that crossing
                            for (Crossing crossing : crossings) {
                                if (crossing.getX() == tempx && crossing.getY() == tempy) {
                                    endCrossing = crossing;
                                    break;
                                }
                            }
                            roads.add(new Road(50, Arrays.asList(new Lane(laneId, startCrossing, endCrossing, new ArrayList<Direction>()), new Lane(laneId + 1, endCrossing, startCrossing, new ArrayList<Direction>())), nodes));
                            laneId += 2;
                        }
                    }
                }
            }
        }



//        Checking Left up/down
        for (int y = 1; y < grid.length; y+=2) {
            for (int x = 1; x < grid[0].length; x+=2) {
                int tempx = x;
                int tempy = y;
                if (grid[tempy][tempx] == 9) {
                    if (grid[tempy][tempx - 1] == 1) {
                        Crossing startCrossing = null;
                        for (Crossing crossing : crossings) {
                            if (crossing.getX() == tempx && crossing.getY() == tempy) {
                                startCrossing = crossing;
                                break;
                            }
                        }
                        List<Node> nodes = new ArrayList<>();
                        nodes.add(new Node(tempx * MESH_OFFSET, tempy * MESH_OFFSET));
                        tempx--;
                        while (grid[tempy][tempx] == 1) {
                            tempx--;
                            if(tempx < 0){
                                tempx++;
                                break;
                            }
                        }
                        Crossing endCrossing = null;
                        if (grid[tempy][tempx] == 8) {
                            nodes.add(new Node(tempx * MESH_OFFSET, tempy * MESH_OFFSET));
                            int directionAfterTurn;
                            if(grid[tempy + 1][tempx] == 1) {
                                directionAfterTurn = 1;
                            }else{
                                directionAfterTurn = -1;
                            }
                            tempy += directionAfterTurn;
                            while (grid[tempy][tempx] == 1) {
                                tempy += directionAfterTurn;
                            }

                            nodes.add(new Node(tempx * MESH_OFFSET, tempy * MESH_OFFSET));
//                            Search for that crossing
                            for (Crossing crossing : crossings) {
                                if (crossing.getX() == tempx && crossing.getY() == tempy) {
                                    endCrossing = crossing;
                                    break;
                                }
                            }
                            roads.add(new Road(50, Arrays.asList(new Lane(laneId, startCrossing, endCrossing, new ArrayList<Direction>()), new Lane(laneId + 1, endCrossing, startCrossing, new ArrayList<Direction>())), nodes));
                            laneId += 2;
                        }
                    }
                }
            }
        }

    }

    public List<Crossing> getCrossings() {
        return crossings;
    }
    public List<Road> getRoads() {
        return roads;
    }
}
