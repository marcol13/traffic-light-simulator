package com.put.urbantraffic;

import lombok.Data;

import java.util.*;

@Data
public class City {
    private static List<Crossing> crossings;
    private static List<Road> roads;

    private static final int MESH_OFFSET = 100;

    public City(List<Crossing> crossings, List<Road> roads){
        City.crossings = crossings;
        City.roads = roads;
    }

    public City(int width, int height, int crossingAmount){

        int[][] grid = new CityGenerator().generate(width, height, crossingAmount);
        int counter = 0;
        for (int[] x : grid)
        {
            for (int y : x)
            {
//                System.out.print(y + " ");
                if(y == 8){
                    counter++;
                }
            }
//            System.out.println();
        }
        System.out.println("Quantity of Turns: " + counter);
        crossings = new ArrayList<>();
        roads = new ArrayList<>();
        parseGridToClasses(grid);
        calculateRoadSpeedLimit();
    }

    public Car spawnCar(){
        Random rand = new Random();
        int startIndex = 0, endIndex = 0;
        while(startIndex == endIndex){
            startIndex = rand.nextInt(roads.size());
            endIndex = rand.nextInt(roads.size());
        }
        Road startRode = roads.get(startIndex);
        Road endRode = roads.get(endIndex);

        return new Car(startRode, endRode);
    }

    private <T> T getRandomListElement(List<T> elementsList, Random rand){
        return elementsList.get(rand.nextInt(elementsList.size()));
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
                            if (crossing.getX() == x * MESH_OFFSET && crossing.getY() == y * MESH_OFFSET) {
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
                                if (crossing.getX() == x * MESH_OFFSET && crossing.getY() == y * MESH_OFFSET) {
                                    endCrossing = crossing;
                                    break;
                                }
                            }
                            roads.add(new Road(roads.size(), Math.abs(nodes.get(0).getX() - nodes.get(nodes.size()-1).getX()),Arrays.asList(new Lane(laneId, startCrossing, endCrossing, new ArrayList<Direction>()), new Lane(laneId + 1, endCrossing, startCrossing, new ArrayList<Direction>())), nodes));
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
                            if (crossing.getX() == x * MESH_OFFSET && crossing.getY() == y * MESH_OFFSET) {
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
                                if (crossing.getX() == x * MESH_OFFSET && crossing.getY() == y * MESH_OFFSET) {
                                    endCrossing = crossing;
                                    break;
                                }
                            }
                            roads.add(new Road(roads.size(), Math.abs(nodes.get(0).getY() - nodes.get(nodes.size()-1).getY()) , Arrays.asList(new Lane(laneId, startCrossing, endCrossing, new ArrayList<Direction>()), new Lane(laneId + 1, endCrossing, startCrossing, new ArrayList<Direction>())), nodes));
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
                            if (crossing.getX() == tempx * MESH_OFFSET && crossing.getY() == tempy * MESH_OFFSET) {
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
                                if (crossing.getX() == tempx * MESH_OFFSET && crossing.getY() == tempy * MESH_OFFSET) {
                                    endCrossing = crossing;
                                    break;
                                }
                            }
                            roads.add(new Road(roads.size(), Math.abs(nodes.get(0).getX() - nodes.get(nodes.size()-1).getX()) + Math.abs(nodes.get(0).getY() - nodes.get(nodes.size()-1).getY()) , Arrays.asList(new Lane(laneId, startCrossing, endCrossing, new ArrayList<Direction>()), new Lane(laneId + 1, endCrossing, startCrossing, new ArrayList<Direction>())), nodes));
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
                            if (crossing.getX() == tempx * MESH_OFFSET && crossing.getY() == tempy * MESH_OFFSET) {
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
                                if (crossing.getX() == tempx * MESH_OFFSET && crossing.getY() == tempy * MESH_OFFSET) {
                                    endCrossing = crossing;
                                    break;
                                }
                            }
                            
                            roads.add(new Road(roads.size(), Math.abs(nodes.get(0).getX() - nodes.get(nodes.size()-1).getX()) + Math.abs(nodes.get(0).getY() - nodes.get(nodes.size()-1).getY()), Arrays.asList(new Lane(laneId, startCrossing, endCrossing, new ArrayList<Direction>()), new Lane(laneId + 1, endCrossing, startCrossing, new ArrayList<Direction>())), nodes));
                            laneId += 2;
                        }
                    }
                }
            }
        }

//        Adding driveways
        int x = 0;
        int y = grid.length / 4 * 2 + 1;
        addDriveway(grid, -1, grid.length / 4 * 2 + 1, 1, 0, crossingId, laneId);
        addDriveway(grid, grid[0].length, grid.length / 4 * 2 + 1, -1, 0, crossingId + 1, laneId + 2);
        addDriveway(grid, grid[0].length / 4 * 2 - 1, -1, 0, 1, crossingId + 2, laneId + 4);
        addDriveway(grid, grid[0].length / 4 * 2 - 1, grid.length, 0, -1, crossingId + 3, laneId + 6);
        crossingId += 4;
        laneId += 8;
    }

    private static void addDriveway(int[][] grid, int x, int y, int addX, int addY, int crossingId, int laneId){
        List<Node> nodes = new ArrayList<>();
        Crossing crossing2 = new Crossing(crossingId, x * MESH_OFFSET, y * MESH_OFFSET,  new ArrayList<Light>());
        crossings.add(crossing2);
        nodes.add(new Node(x * MESH_OFFSET, y * MESH_OFFSET));
        x+=addX;
        y+=addY;
        while(grid[y][x] == 1){
            x+=addX;
            y+=addY;
        }
        nodes.add(new Node(x * MESH_OFFSET, y * MESH_OFFSET));
        for (Crossing crossing : crossings) {
            if (crossing.getX() == x * MESH_OFFSET && crossing.getY() == y * MESH_OFFSET) {
                roads.add(new Road(roads.size(), Math.abs(nodes.get(0).getX() - nodes.get(nodes.size()-1).getX()) + Math.abs(nodes.get(0).getY() - nodes.get(nodes.size()-1).getY()), Arrays.asList(new Lane(laneId, crossing2, crossing, new ArrayList<Direction>()), new Lane(laneId + 1, crossing, crossing2, new ArrayList<Direction>())), nodes));
                break;
            }
        }
    }

    private void calculateRoadSpeedLimit(){
        roads.sort(Comparator.comparing(Road::getLength));

        int i = 0;

       do{
            roads.get(i).setSpeedLimit(40);
            i++;
        }while((i < (roads.size() * 3/10)) ||  roads.get(i-1).getLength() == roads.get(i).getLength());
//        }while((i < (roads.size() * 3/10)));


        while((i < (roads.size() * 9/10)) ||  roads.get(i-1).getLength() == roads.get(i).getLength()){
            roads.get(i).setSpeedLimit(50);
            i++;
        }

        while(i < roads.size()){
            roads.get(i).setSpeedLimit(70);
            i++;
        }

    }

    public List<Crossing> getCrossings() {
        return crossings;
    }
    public List<Road> getRoads() {
        return roads;
    }
}
