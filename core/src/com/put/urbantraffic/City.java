package com.put.urbantraffic;

import com.put.urbantraffic.drawablemodels.DrawableCrossingTrafficLight;
import com.put.urbantraffic.drawablemodels.DrawableCar;
import com.put.urbantraffic.drawablemodels.Frame;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

import static com.put.urbantraffic.Settings.IS_DEBUG;
import static com.put.urbantraffic.Settings.MESH_DISTANCE;

public class City {
    private final List<Crossing> crossings;
    private final List<Road> roads;
    private final List<Lane> lanes = new ArrayList<>();
    public List<Integer> spawnCarArray = new ArrayList<>();
    public int time = Settings.STARTING_HOUR*3600*Settings.TIME_PRECISION;
    public long waitingTime = 0;
    Random rand;
    public CityGraph.PathWithTime[][] paths;
    final List<Car> cars = new ArrayList<>();
    List<TrafficLightsSettings> trafficLightsSettingsList;
    private final boolean shouldGenerateLights;

    public City(Random rand) {
        this(rand, new ArrayList<>());
    }

    public City(Random rand, List<TrafficLightsSettings> trafficLightsSettingsList) {
        int width = Settings.GRID_MULTIPLIER * 2 * 16;
        int height = Settings.GRID_MULTIPLIER * 2 * 9;
        int crossingAmount = Settings.CROSSING_AMOUNT;

        this.trafficLightsSettingsList = trafficLightsSettingsList;
        this.shouldGenerateLights = trafficLightsSettingsList.isEmpty();
        this.rand = rand;

        int[][] grid = new CityGenerator(rand).generate(width, height, crossingAmount);
        int counter = 0;
        for (int[] x : grid) {
            for (int y : x) {
                if (y == 8) {
                    counter++;
                }
            }
        }
        if (IS_DEBUG) System.out.println("Quantity of Turns: " + counter);
        crossings = new ArrayList<>();
        roads = new ArrayList<>();
        parseGridToClasses(grid);
        calculateRoadSpeedLimit();
        createSpawnCarArray();

        for (Crossing crossing : getCrossings()) {
            crossing.getTrafficLightsSupervisor().turnOnLights();
        }
        paths = new CityGraph().generate(this);
    }

    public List<Frame> frame = new ArrayList<>();

    public void makeStep() {
        while (spawnCarArray.size() > 0 && time == spawnCarArray.get(0)) {
            cars.add(spawnCar());
            spawnCarArray.remove(0);
        }
        for (Crossing crossing : getCrossings()) {
            crossing.getTrafficLightsSupervisor().changeAllLights();
        }
        carHandler();
        time += 1;

        // TODO: Disable if rendering is not enabled
        List<DrawableCar> drawableCars = cars.stream()
                .map(DrawableCar::fromCar)
                .collect(Collectors.toList());
        List<DrawableCrossingTrafficLight> drawableLights = crossings.stream()
                .map(DrawableCrossingTrafficLight::fromCrossing)
                .collect(Collectors.toList());

        frame.add(new Frame(drawableCars, drawableLights, waitingTime, time));
    }

    public void startSimulation() {
        for (int i = Settings.STARTING_HOUR * Settings.TIME_PRECISION * 3600; i < Settings.ENDING_HOUR * Settings.TIME_PRECISION * 3600 - 1; i++) {
            makeStep();
            if (IS_DEBUG) System.out.println("Rendering frame: " + frame.size());
        }
    }

    public Car spawnCar() {
        Road startRoad = null;
        Road endRoad = null;

        while (startRoad == endRoad) {

            startRoad = roads.get(rand.nextInt(roads.size()));
            endRoad = roads.get(rand.nextInt(roads.size()));

            boolean doesAnyContainTurn = startRoad.getLaneList().get(0).doesContainTurn() || endRoad.getLaneList().get(0).doesContainTurn();
            if (doesAnyContainTurn) {
                startRoad = null;
                endRoad = null;
            }
        }
        return new Car(startRoad, endRoad, paths);
    }

    public void carHandler(){
        List<Car> removeCars = new ArrayList<>();

        for(Car car: cars){
            if(car.getStatus() == RideStatus.WAITING)
                waitingTime++;
            else if(car.getStatus() == RideStatus.FINISH){
                removeCars.add(car);
                continue;
            }
            car.predictMoveCar();
        }

        for(Car removeCar: removeCars){
            cars.remove(removeCar);
        }

        for( Car car: cars){
            car.moveCar();
        }
    }

    private void createSpawnCarArray() {
        double[] trapezeArea = new double[Settings.ENDING_HOUR];
        double[] carsPerHour = new double[Settings.ENDING_HOUR];

        for(int i = Settings.STARTING_HOUR; i< Settings.ENDING_HOUR; i++){
            trapezeArea[i] = (Settings.TRAFFIC_LEVEL_BY_HOUR[i] + Settings.TRAFFIC_LEVEL_BY_HOUR[i+1])/2;
        }

        double trapeze_area_sum = Arrays.stream(trapezeArea).sum();
        for(int i = Settings.STARTING_HOUR; i< Settings.ENDING_HOUR; i++){
            carsPerHour[i] = trapezeArea[i]/trapeze_area_sum* Settings.CARS_QUANTITY;
        }

        double leftCars=0;
        for(int hour = Settings.STARTING_HOUR; hour< Settings.ENDING_HOUR; hour++){
            double carsEverySecond = carsPerHour[hour]/Settings.TIME_PRECISION/3600;
            for(int frame=0; frame<3600 * Settings.TIME_PRECISION; frame++){
                leftCars += carsEverySecond;
                while(leftCars >= 1){
                    spawnCarArray.add(hour * 3600 * Settings.TIME_PRECISION + frame);
                    leftCars -= 1;
                }

            }
        }
    }



    private void parseGridToClasses(int[][] grid) {
        int crossingId = 0;
        for (int y = 1; y < grid.length; y += 2) {
            for (int x = 1; x < grid[0].length; x += 2) {
                if (grid[y][x] == 9) {
                    addNewCrossing(x, y, crossingId);
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
                            if (crossing.getX() == x * MESH_DISTANCE / 2 && crossing.getY() == y * MESH_DISTANCE / 2) {
                                startCrossing = crossing;
                                break;
                            }
                        }
                        List<Node> nodes = new ArrayList<>();
                        nodes.add(new Node(x * MESH_DISTANCE / 2, y * MESH_DISTANCE / 2));
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
                            nodes.add(new Node(x * MESH_DISTANCE / 2, y * MESH_DISTANCE / 2));
//                            Search for that crossing
                            for (Crossing crossing : crossings) {
                                if (crossing.getX() == x * MESH_DISTANCE / 2 && crossing.getY() == y * MESH_DISTANCE / 2) {
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
                            if (crossing.getX() == x * MESH_DISTANCE / 2 && crossing.getY() == y * MESH_DISTANCE / 2) {
                                startCrossing = crossing;
                                break;
                            }
                        }
                        List<Node> nodes = new ArrayList<>();
                        nodes.add(new Node(x * MESH_DISTANCE / 2, y * MESH_DISTANCE / 2));
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
                            nodes.add(new Node(x * MESH_DISTANCE / 2, y * MESH_DISTANCE / 2));
//                            Search for that crossing
                            for (Crossing crossing : crossings) {
                                if (crossing.getX() == x * MESH_DISTANCE / 2 && crossing.getY() == y * MESH_DISTANCE / 2) {
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
                            if (crossing.getX() == tempx * MESH_DISTANCE / 2 && crossing.getY() == tempy * MESH_DISTANCE / 2) {
                                startCrossing = crossing;
                                break;
                            }
                        }
                        List<Node> nodes = new ArrayList<>();
                        nodes.add(new Node(tempx * MESH_DISTANCE / 2, tempy * MESH_DISTANCE / 2));
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
                            nodes.add(new Node(tempx * MESH_DISTANCE / 2, tempy * MESH_DISTANCE / 2));
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

                            nodes.add(new Node(tempx * MESH_DISTANCE / 2, tempy * MESH_DISTANCE / 2));
//                            Search for that crossing
                            for (Crossing crossing : crossings) {
                                if (crossing.getX() == tempx * MESH_DISTANCE / 2 && crossing.getY() == tempy * MESH_DISTANCE / 2) {
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
                            if (crossing.getX() == tempx * MESH_DISTANCE / 2 && crossing.getY() == tempy * MESH_DISTANCE / 2) {
                                startCrossing = crossing;
                                break;
                            }
                        }

                        List<Node> nodes = new ArrayList<>();
                        nodes.add(new Node(tempx * MESH_DISTANCE / 2, tempy * MESH_DISTANCE / 2));
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
                            nodes.add(new Node(tempx * MESH_DISTANCE / 2, tempy * MESH_DISTANCE / 2));
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

                            nodes.add(new Node(tempx * MESH_DISTANCE / 2, tempy * MESH_DISTANCE / 2));
//                            Search for that crossing
                            for (Crossing crossing : crossings) {
                                if (crossing.getX() == tempx * MESH_DISTANCE / 2 && crossing.getY() == tempy * MESH_DISTANCE / 2) {
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
        Crossing crossing2 = addNewCrossing(x, y, crossingId);
        nodes.add(new Node(x * MESH_DISTANCE / 2, y * MESH_DISTANCE / 2));
        x += addX;
        y += addY;
        while (grid[y][x] == 1) {
            x += addX;
            y += addY;
        }
        nodes.add(new Node(x * MESH_DISTANCE / 2, y * MESH_DISTANCE / 2));
        for (Crossing crossing : crossings) {
            if (crossing.getX() == x * MESH_DISTANCE / 2 && crossing.getY() == y * MESH_DISTANCE / 2) {
                addNewRoad(nodes, crossing2, crossing);
                break;
            }
        }
    }

    private Crossing addNewCrossing(int x, int y, int crossingId) {
        final int greenDuration = 10 * Settings.TIME_PRECISION;
        final int redDuration = 10  * Settings.TIME_PRECISION;
        final int offset = 0 * Settings.TIME_PRECISION;
        TrafficLightsSettings trafficLightsSettings;
        if (shouldGenerateLights) {
            trafficLightsSettings = new TrafficLightsSettings(greenDuration, redDuration, offset);
            trafficLightsSettingsList.add(trafficLightsSettings);
        } else {
            trafficLightsSettings = trafficLightsSettingsList.get(crossingId);
        }
        Crossing crossing = new Crossing(crossingId, x * MESH_DISTANCE / 2, y * MESH_DISTANCE / 2, trafficLightsSettings, rand);
        crossings.add(crossing);
        return crossing;
    }

    public void setTrafficLightsSettingsList(List<TrafficLightsSettings> trafficLightsSettingsList) {
        // changing lights must happen before starting simulation
        assert time == Settings.STARTING_HOUR * 3600;

        this.trafficLightsSettingsList = trafficLightsSettingsList;
        for (int i = 0; i < crossings.size(); i++) {
            crossings.get(i).setTrafficLightsSettings(trafficLightsSettingsList.get(i));
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
                crossing1.getTrafficLightsSupervisor().setRightTrafficLight(new TrafficLight(lane2, Light.RED, false));
                crossing2.getTrafficLightsSupervisor().setLeftTrafficLight(new TrafficLight(lane1, Light.RED, false));
            }
            else if(crossing1.getX() > crossing2.getX()) {
                crossing2.getTrafficLightsSupervisor().setRightTrafficLight(new TrafficLight(lane1, Light.RED, false));
                crossing1.getTrafficLightsSupervisor().setLeftTrafficLight(new TrafficLight(lane2, Light.RED, false));
            }
            else if(crossing1.getY() < crossing2.getY()){
                crossing1.getTrafficLightsSupervisor().setTopTrafficLight(new TrafficLight(lane2, Light.RED, false));
                crossing2.getTrafficLightsSupervisor().setBottomTrafficLight(new TrafficLight(lane1, Light.RED, false));
            }
            else{
                crossing2.getTrafficLightsSupervisor().setTopTrafficLight(new TrafficLight(lane1, Light.RED, false));
                crossing1.getTrafficLightsSupervisor().setBottomTrafficLight(new TrafficLight(lane2, Light.RED, false));
            }
        } else{
            //first crossing to the curve

            if(crossing1.getX() < nodes.get(1).getX()){
                crossing1.getTrafficLightsSupervisor().setRightTrafficLight(new TrafficLight(lane2, Light.RED, false));
            }
            else if(crossing1.getX() > nodes.get(1).getX()) {
                crossing1.getTrafficLightsSupervisor().setLeftTrafficLight(new TrafficLight(lane2, Light.RED, false));
            }
            else if(crossing1.getY() < nodes.get(1).getY()){
                crossing1.getTrafficLightsSupervisor().setTopTrafficLight(new TrafficLight(lane2, Light.RED, false));
            }
            else{
                crossing1.getTrafficLightsSupervisor().setBottomTrafficLight(new TrafficLight(lane2, Light.RED, false));
            }


            //curve to the crossing2
            if(nodes.get(1).getX() < crossing2.getX()){
                crossing2.getTrafficLightsSupervisor().setLeftTrafficLight(new TrafficLight(lane1, Light.RED, false));
            }
            else if(nodes.get(1).getX() > crossing2.getX()) {
                crossing2.getTrafficLightsSupervisor().setRightTrafficLight(new TrafficLight(lane1, Light.RED, false));
            }
            else if(nodes.get(1).getY() < crossing2.getY()){
                crossing2.getTrafficLightsSupervisor().setBottomTrafficLight(new TrafficLight(lane1, Light.RED, false));
            }
            else{
                crossing2.getTrafficLightsSupervisor().setTopTrafficLight(new TrafficLight(lane1, Light.RED, false));
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
                lane.setSpeedLimit(30);
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
