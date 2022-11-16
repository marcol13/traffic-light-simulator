package com.put.urbantraffic;

import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

public class City {
    private final List<Crossing> crossings;
    private final List<Road> roads;
    private final List<Lane> lanes = new ArrayList<>();

    private static final int MESH_OFFSET = 100;

    public City(List<Crossing> crossings, List<Road> roads) {
        this.crossings = crossings;
        this.roads = roads;
    }

    public City(int width, int height, int crossingAmount) {

        int[][] grid = new CityGenerator().generate(width, height, crossingAmount);
        int counter = 0;
        for (int[] x : grid) {
            for (int y : x) {
//                System.out.print(y + " ");
                if (y == 8) {
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
        while (startIndex == endIndex) {
            startIndex = rand.nextInt(lanes.size());
            endIndex = rand.nextInt(lanes.size());
            if(lanes.get(startIndex).getNodeList().size() > 2 || lanes.get(endIndex).getNodeList().size() > 2){
                startIndex = endIndex;
            }
        }
        Lane startLane = lanes.get(startIndex);
        Lane endLane = lanes.get(endIndex);

        return new Car(startLane, endLane);
    }

    private <T> T getRandomListElement(List<T> elementsList, Random rand) {
        return elementsList.get(rand.nextInt(elementsList.size()));
    }


    private void parseGridToClasses(int[][] grid) {
        int crossingId = 0;
        for (int y = 1; y < grid.length; y += 2) {
            for (int x = 1; x < grid[0].length; x += 2) {
                if (grid[y][x] == 9) {
                    crossings.add(new Crossing(crossingId, x * MESH_OFFSET, y * MESH_OFFSET));
                    crossingId++;
                }
            }
        }


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
                            if (x == grid[0].length) {
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
                            addNewRoad(nodes, startCrossing, endCrossing);
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
                            if (y == grid.length) {
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
                            addNewRoad(nodes, startCrossing, endCrossing);
                            y--;
                        }
                    }
                }
            }
        }


//        Checking Right up/down
        for (int y = 1; y < grid.length; y += 2) {
            for (int x = 1; x < grid[0].length; x += 2) {
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
                            if (tempx == grid[0].length) {
                                tempx--;
                                break;
                            }
                        }
                        Crossing endCrossing = null;
                        if (grid[tempy][tempx] == 8) {
                            nodes.add(new Node(tempx * MESH_OFFSET, tempy * MESH_OFFSET));
                            int directionAfterTurn;
                            if (grid[tempy + 1][tempx] == 1) {
                                directionAfterTurn = 1;
                            } else {
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
                            addNewRoad(nodes, startCrossing, endCrossing);
                        }
                    }
                }
            }
        }


//        Checking Left up/down
        for (int y = 1; y < grid.length; y += 2) {
            for (int x = 1; x < grid[0].length; x += 2) {
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
                            if (tempx < 0) {
                                tempx++;
                                break;
                            }
                        }
                        Crossing endCrossing = null;
                        if (grid[tempy][tempx] == 8) {
                            nodes.add(new Node(tempx * MESH_OFFSET, tempy * MESH_OFFSET));
                            int directionAfterTurn;
                            if (grid[tempy + 1][tempx] == 1) {
                                directionAfterTurn = 1;
                            } else {
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

                            addNewRoad(nodes, startCrossing, endCrossing);
                        }
                    }
                }
            }
        }

//        Adding driveways
        int x = 0;
        int y = grid.length / 4 * 2 + 1;
        addDriveway(grid, -1, grid.length / 4 * 2 + 1, 1, 0, crossingId);
        addDriveway(grid, grid[0].length, grid.length / 4 * 2 + 1, -1, 0, crossingId + 1);
        addDriveway(grid, grid[0].length / 4 * 2 - 1, -1, 0, 1, crossingId + 2);
        addDriveway(grid, grid[0].length / 4 * 2 - 1, grid.length, 0, -1, crossingId + 3);
        crossingId += 4;
    }

    private void addDriveway(int[][] grid, int x, int y, int addX, int addY, int crossingId) {
        List<Node> nodes = new ArrayList<>();
        Crossing crossing2 = new Crossing(crossingId, x * MESH_OFFSET, y * MESH_OFFSET);
        crossings.add(crossing2);
        nodes.add(new Node(x * MESH_OFFSET, y * MESH_OFFSET));
        x += addX;
        y += addY;
        while (grid[y][x] == 1) {
            x += addX;
            y += addY;
        }
        nodes.add(new Node(x * MESH_OFFSET, y * MESH_OFFSET));
        for (Crossing crossing : crossings) {
            if (crossing.getX() == x * MESH_OFFSET && crossing.getY() == y * MESH_OFFSET) {
                addNewRoad(nodes, crossing2, crossing);
                break;
            }
        }
    }

    private void addNewRoad(List<Node> nodes, Crossing crossing1, Crossing crossing2) {
        if (nodes.get(0).getX() == crossing2.getX() && nodes.get(0).getY() == crossing2.getY()) {
            Collections.reverse(nodes);
        }

        Lane lane1 = new Lane(lanes.size(), crossing1, crossing2, new ArrayList<>(), nodes);
        lanes.add(lane1);

        ArrayList<Node> reversedNodes = new ArrayList<>(nodes);
        Collections.reverse(reversedNodes);
        Lane lane2 = new Lane(lanes.size(), crossing2, crossing1, new ArrayList<>(), reversedNodes);
        lanes.add(lane2);

        List<Lane> laneList = Arrays.asList(lane1, lane2);
        roads.add(new Road(roads.size(), laneList));


        // TODO add with 3 nodes

        //Creation TrafficLight
        if(nodes.size() == 2){
            if(crossing1.getX() < crossing2.getX()){
                crossing1.getTrafficLightsSupervisor().setRightTrafficLight(new TrafficLight(lane2, Light.RED));
                crossing2.getTrafficLightsSupervisor().setLeftTrafficLight(new TrafficLight(lane1, Light.RED));
            }
            else if(crossing1.getX() > crossing2.getX()) {
                crossing2.getTrafficLightsSupervisor().setRightTrafficLight(new TrafficLight(lane1, Light.RED));
                crossing1.getTrafficLightsSupervisor().setLeftTrafficLight(new TrafficLight(lane2, Light.RED));
            }
            else if(crossing1.getY() < crossing2.getY()){
                crossing1.getTrafficLightsSupervisor().setTopTrafficLight(new TrafficLight(lane2, Light.RED));
                crossing2.getTrafficLightsSupervisor().setBottomTrafficLight(new TrafficLight(lane1, Light.RED));
            }
            else{
                crossing2.getTrafficLightsSupervisor().setTopTrafficLight(new TrafficLight(lane1, Light.RED));
                crossing1.getTrafficLightsSupervisor().setBottomTrafficLight(new TrafficLight(lane2, Light.RED));
            }
        } else{
            //first crossing to the curve

            if(crossing1.getX() < nodes.get(1).getX()){
                crossing1.getTrafficLightsSupervisor().setRightTrafficLight(new TrafficLight(lane2, Light.RED));
            }
            else if(crossing1.getX() > nodes.get(1).getX()) {
                crossing1.getTrafficLightsSupervisor().setLeftTrafficLight(new TrafficLight(lane2, Light.RED));
            }
            else if(crossing1.getY() < nodes.get(1).getY()){
                crossing1.getTrafficLightsSupervisor().setTopTrafficLight(new TrafficLight(lane2, Light.RED));
            }
            else{
                crossing1.getTrafficLightsSupervisor().setBottomTrafficLight(new TrafficLight(lane2, Light.RED));
            }


            //curve to the crossing2
            if(nodes.get(1).getX() < crossing2.getX()){
                crossing2.getTrafficLightsSupervisor().setLeftTrafficLight(new TrafficLight(lane1, Light.RED));
            }
            else if(nodes.get(1).getX() > crossing2.getX()) {
                crossing2.getTrafficLightsSupervisor().setRightTrafficLight(new TrafficLight(lane1, Light.RED));
            }
            else if(nodes.get(1).getY() < crossing2.getY()){
                crossing2.getTrafficLightsSupervisor().setBottomTrafficLight(new TrafficLight(lane1, Light.RED));
            }
            else{
                crossing2.getTrafficLightsSupervisor().setTopTrafficLight(new TrafficLight(lane1, Light.RED));
            }
        }

    }

    private void calculateRoadSpeedLimit() {
        val roadsLengths = lanes.stream().map(Lane::getLength).sorted().collect(Collectors.toList());
        val lowerBoundFraction = 3.0 / 10;
        val upperBoundFraction = 9.0 / 10;
        val lowerBoundLength = roadsLengths.get((int) (roadsLengths.size() * lowerBoundFraction));
        val upperBoundLength = roadsLengths.get((int) (roadsLengths.size() * upperBoundFraction));
        for (Lane lane : lanes) {
            if (lane.getLength() <= lowerBoundLength) {
                lane.setSpeedLimit(40);
            } else if (lane.getLength() <= upperBoundLength) {
                lane.setSpeedLimit(50);
            } else {
                lane.setSpeedLimit(70);
            }
        }
    }

    public List<Crossing> getCrossings() {
        return crossings;
    }

    public List<Road> getRoads() {
        return roads;
    }

    public List<Lane> getLanes() {
        return lanes;
    }
}
