package com.put.urbantraffic;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.put.urbantraffic.Settings.CAR_SPEED_MULTIPLIER;


@Data
public class Car {

    @Override
    public String toString() {
        return "Car{" +
                "startLane=" + startLane +
                '}';
    }

    private Lane startLane;
    private Lane endLane;
    private Node startNode;
    private Node endNode;

    private List<Node> path;

    private Node currentNode;
    private Node nextNode;
    private Node predictedCarPosition;
    private Node carPosition;
    private Lane currentLane;
    private float nodePercentage = 0.0f;

    private RideStatus status = RideStatus.STARTING;

    private List<Crossing> crossingList;
    private Crossing nextCrossing;

    private List<Lane> lanesList;
    CityGraph.PathWithTime calculatedPath;

    private Way way;
    private Way nextWay;

    private Direction direction;

    private boolean onCrossing = false;

    private int carPositionInTrafficJam;
    private boolean changedPositionInTrafficJam;

    public enum Way {
        TOP, RIGHT, BOTTOM, LEFT
    }

    public Car(Road startRoad, Road endRoad, CityGraph.PathWithTime[][] paths) {
        this.path = generatePathAndInitializeLanes(startRoad, endRoad, paths);

        this.currentNode = path.get(0);
        this.currentLane = lanesList.get(0);

//        ???
//        this.carPosition = path.get(0);
//        this.predictedCarPosition = path.get(0);
        this.carPosition = new Node(path.get(0).getX(), path.get(0).getY());
        this.predictedCarPosition = new Node(path.get(0).getX(), path.get(0).getY());

        this.nextNode = path.get(1);
        this.nextCrossing = crossingList.get(0);

        this.way = calculateWay(this.currentNode, this.nextNode);

        List<Node> nodeList = startLane.getNodeList();
        Way laneWay = calculateWay(nodeList.get(0), nodeList.get(1));
        List<Car> carsList = startLane.getCarsList();
        int i;
        loop:
        for (i = 0; i < carsList.size(); i++) {
            switch (laneWay) {
                case TOP:
                    if (carsList.get(i).getCarPosition().getY() < currentNode.getY()) break loop;
                    break;
                case RIGHT:
                    if (carsList.get(i).getCarPosition().getX() < currentNode.getX()) break loop;
                    break;
                case BOTTOM:
                    if (carsList.get(i).getCarPosition().getY() > currentNode.getY()) break loop;
                    break;
                case LEFT:
                    if (carsList.get(i).getCarPosition().getX() > currentNode.getX()) break loop;
                    break;
            }
        }
        startLane.getCarsList().add(i, this);
        for(Car car: startLane.getCarsList()){
            car.setChangedPositionInTrafficJam(true);
        }
        carPositionInTrafficJam = i;
        direction = calculateDirection(way, calculateWay(path.get(1), path.get(2)));
//        System.out.println(this.path);

    }

    private List<Node> generatePathAndInitializeLanes(Road startRoad, Road endRoad, CityGraph.PathWithTime[][] paths) {

        List<Lane> possibleStartLanes = startRoad.getLaneList();
        List<Lane> possibleEndLanes = endRoad.getLaneList();
        calculatedPath = paths[possibleStartLanes.get(0).getId()][possibleEndLanes.get(0).getId()];
        this.crossingList = new ArrayList<>(calculatedPath.getCrossings());
        this.startLane = possibleStartLanes.get(0).getEndCrossing() == crossingList.get(0) ? possibleStartLanes.get(0) : possibleStartLanes.get(1);
        this.endLane = possibleEndLanes.get(0).getStartCrossing() == crossingList.get(crossingList.size() - 1) ? possibleEndLanes.get(0) : possibleEndLanes.get(1);
        this.startNode = this.startLane.getMiddlePoint();
        this.endNode = this.endLane.getMiddlePoint();

        this.lanesList = new ArrayList<>(Collections.singletonList(this.startLane));
        this.lanesList.addAll(calculatedPath.getLanes());
        this.lanesList.add(endLane);

        List<Node> path = new ArrayList<>(Collections.singletonList(this.startNode));
        if (this.crossingList.size() == 1) {
            path.add(new Node(this.crossingList.get(0).getX(), this.crossingList.get(0).getY()));
        } else {
            for (Lane lane : calculatedPath.getLanes()) {
                for (Node node : lane.getNodeList()) {
                    if (!node.equals(path.get(path.size() - 1))) {
                        path.add(node);
                    }
                }
            }
        }

        path.add(this.endNode);

        return path;
    }

    public void predictMoveCar() {
        float speed = (float) currentLane.getSpeedLimit() / (float) getNodeLength(currentNode, nextNode);

        int xVector = nextNode.getX() - currentNode.getX();
        int yVector = nextNode.getY() - currentNode.getY();
        int predictedX = predictXandYPosition(xVector, yVector)[0];
        int predictedY = predictXandYPosition(xVector, yVector)[1];

        if(changedPositionInTrafficJam){
            carPositionInTrafficJam = currentLane.getCarsList().indexOf(this);
            changedPositionInTrafficJam = false;
        }



        if(carPositionInTrafficJam > 0){
            Car previousCar = currentLane.getCarsList().get(carPositionInTrafficJam - 1);

//            ??? TODO I would say we have to check it after the potential move, not now. BTW it is probably necessary with sequentional movement
            //check, some car is in front of you

            if (calculateDistance(predictedX,
                    predictedY,
                    previousCar.carPosition.getX(),
                    previousCar.carPosition.getY()) <= Settings.DISTANCE_BETWEEN_CARS_IN_JAM + Settings.CAR_RADIUS * 2){
                status = status == RideStatus.STARTING ? RideStatus.STARTING : RideStatus.WAITING;
                return;
            }
        } else if (carPositionInTrafficJam == 0 && !onCrossing) {

            double distance = calculateDistance(predictedX,
                    predictedY,
                    nextCrossing.getX(),
                    nextCrossing.getY());
            if (distance <= Settings.CROSSING_RADIUS + Settings.CAR_RADIUS) {

                if (path.size() > 2) {
//                    System.out.println("Pierogi z serem");
//                    Node currentCrossing = path.get(1);
//                    Node nodeAfterCrossing = path.get(2);
//                    nextWay = calculateWay(path.get(1), nodeAfterCrossing);
                    nextWay = calculateWay(path.get(1), path.get(2));
                    direction = calculateDirection(way, nextWay);

//                    TODO CHECK IS GO ON CROSSING POSSIBLE
                    if (nextCrossing.isGoOnCrossingPossible(this)) {
//                        TODO REMEMBER Potential onCrossing
                        onCrossing = true;
                    } else {
                        status = RideStatus.WAITING;
                        return;
                    }
                }
            }
        }

        status = RideStatus.RIDING;

        nodePercentage += speed * CAR_SPEED_MULTIPLIER * 100 / Settings.TIME_PRECISION / 300 * 83;

        if (nodePercentage >= 100) {

            if(onCrossing){
                if(direction == Direction.LEFT){
                    if (!crossingList.get(0).isTurnLeftPossible(this)) {
                        status = RideStatus.WAITING;
                        return;
                    } else {
                        if (!lanesList.get(1).isLaneFull()) {
                            status = RideStatus.RIDING;
                        } else {
                            status = RideStatus.WAITING;
                            return;
                        }
                    }
                } else{
                    if(!lanesList.get(1).isLaneFull()){
                        status = RideStatus.RIDING;
                    }
                    else{
                        status = RideStatus.WAITING;
                        return;
                    }
                }
            }

            //IF GO TO NEXT NODE

            nodePercentage %= 100;
            path.remove(0);

            onCrossing = false;

            if(path.size() > 2){
                direction = calculateDirection(calculateWay(path.get(0), path.get(1)), calculateWay(path.get(1), path.get(2)));
            }

            if (currentLane.getNodeList().size() == 2 || (currentLane.getNodeList().size() > 2 && nextNode == currentLane.getNodeList().get(2))) {

                nextCrossing.goOutFromCrossing(this);

                nodePercentage = 0;

                lanesList.get(0).getCarsList().remove(this);
                for(Car car: lanesList.get(0).getCarsList()){
                    car.setChangedPositionInTrafficJam(true);
                }
                lanesList.remove(0);


                if (!lanesList.isEmpty()){
                    lanesList.get(0).getCarsList().add(this);
                    for(Car car: lanesList.get(0).getCarsList()){
                        car.setChangedPositionInTrafficJam(true);
                    }
                }

                if (crossingList.size() > 1) {
                    crossingList.remove(0);
                    nextCrossing = crossingList.get(0);
                }
            }

            if (path.size() > 1) {
                currentNode = path.get(0);
                currentLane = lanesList.get(0);
                nextNode = path.get(1);
                way = calculateWay(currentNode, nextNode);
            } else {
                status = RideStatus.FINISH;
                carPosition = nextNode;
                return;
            }
        }

        if (status == RideStatus.RIDING || onCrossing) {
            predictedCarPosition.setX((int) (currentNode.getX() + xVector * nodePercentage / 100));
            predictedCarPosition.setY((int) (currentNode.getY() + yVector * nodePercentage / 100));
        }
    }

    void moveCar(){
        carPosition = predictedCarPosition;
    }

    private int getNodeLength(Node currentNode, Node nextNode) {
        return Math.abs(currentNode.getX() - nextNode.getX()) + Math.abs(currentNode.getY() - nextNode.getY());
    }

    private int[] predictXandYPosition(int xVector, int yVector){
            int newX = (int) (currentNode.getX() + xVector * nodePercentage / 100);
            int newY = (int) (currentNode.getY() + yVector * nodePercentage / 100);
            return new int[] {newX,newY};
    }

    private Direction calculateDirection(Way start, Way destination){
        if(start == Way.BOTTOM ){
            if(destination == Way.LEFT){
                return Direction.RIGHT;
            }
            else if(destination == Way.BOTTOM){
                return Direction.FORWARD;
            }
            else if(destination == Way.RIGHT || destination == Way.TOP){
                return Direction.LEFT;
            }
        }
        else if(start == Way.TOP){
            if(destination == Way.RIGHT){
                return Direction.RIGHT;
            }
            else if(destination == Way.TOP){
                return Direction.FORWARD;
            }
            else if(destination == Way.LEFT || destination == Way.BOTTOM){
                return Direction.LEFT;
            }
        }
        else if(start == Way.LEFT){
            if(destination == Way.TOP){
                return Direction.RIGHT;
            }
            else if(destination == Way.LEFT){
                return Direction.FORWARD;
            }
            else if(destination == Way.BOTTOM || destination == Way.RIGHT){
                return Direction.LEFT;
            }
        }
        else if(start == Way.RIGHT){
            if(destination == Way.BOTTOM){
                return Direction.RIGHT;
            }
            else if(destination == Way.RIGHT){
                return Direction.FORWARD;
            }
            else if(destination == Way.TOP || destination == Way.LEFT){
                return Direction.LEFT;
            }
        }
        return Direction.NONE;
    }


    private double calculateDistance(int x1 , int y1, int x2, int y2){
        return Math.sqrt(Math.pow((x1-x2),2) + Math.pow((y1-y2),2));
    }

    private Way calculateWay(Node startNode, Node endNode) {
        if (startNode.getX() == endNode.getX()) {
            if (startNode.getY() > endNode.getY())
                return Way.BOTTOM;
            return Way.TOP;
        }

        if (startNode.getX() > endNode.getX())
            return Way.LEFT;
        return Way.RIGHT;
    }
}





