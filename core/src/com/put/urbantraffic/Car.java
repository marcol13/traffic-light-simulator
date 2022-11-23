package com.put.urbantraffic;

import com.badlogic.gdx.utils.Array;
import lombok.Data;

import javax.sound.sampled.Line;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.put.urbantraffic.SETTINGS.CAR_SPEED_MULTIPLIER;


@Data
public class Car {

    @Override
    public String toString() {
        return "Car{" +
                "startLane=" + startLane +
                '}';
    }

    private final Lane startLane;
    private final Lane endLane;
    private Node startNode;
    private Node endNode;

    private List<Node> path;

    private Node currentNode;
    private Node nextNode;
    private Node carPosition;
    private Lane currentLane;
    private float nodePercentage = 0.0f;

    private RideStatus status = RideStatus.RIDING;

    private List<Crossing> crossingList;
    private Crossing nextCrossing;

    private List<Lane> lanesList;
    CityGraph.PathWithTime calculatedPath;

    private Way way;
    private Way nextWay;

    private Direction direction;

    private boolean onCrossing = false;

    public enum Way {
        TOP, RIGHT, BOTTOM, LEFT
    }

    public Car(Lane startLane, Lane endLane) {

        this.startLane = startLane;
        this.endLane = endLane;

        this.startNode = this.startLane.getMiddlePoint();
        this.endNode = this.endLane.getMiddlePoint();

        this.path = generatePath(startLane, endLane);

        this.currentNode = new Node(path.get(0).getX(), path.get(0).getY());
        this.currentLane = lanesList.get(0);
        this.carPosition = new Node(path.get(0).getX(), path.get(0).getY());
        this.nextNode = path.get(1);
        this.nextCrossing = crossingList.get(0);

        this.way = calculateWay(this.currentNode, this.nextNode);

        int laneCenter = (int)(startLane.getLength()/(Lane.AVERAGE_CAR_LENGTH + Lane.DISTANCE_BETWEEN_CARS))/2;
        if(laneCenter > startLane.getCarsList().size()){
            startLane.getCarsList().add(this);
        } else{
            startLane.getCarsList().add(laneCenter, this);
        }
//        System.out.println(this.path);

    }

    private List<Node> generatePath(Lane startLane, Lane endLane) {
        calculatedPath = UrbanTrafficFlowSimulation.paths[startLane.getId()][endLane.getId()];
        this.crossingList = new ArrayList<>(calculatedPath.getCrossings());

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

    public void moveCar() {
        if (status != RideStatus.FINISH) {

            status = RideStatus.RIDING;

            List<Node> nodeList = currentLane.getNodeList();
            float speed = (float) currentLane.getSpeedLimit() / (float) getNodeLength(currentNode, nextNode);

            int xVector = nextNode.getX() - currentNode.getX();
            int yVector = nextNode.getY() - currentNode.getY();

            if (currentNode.equals(startNode) || nextNode.equals(endNode)) {
                speed = (float) currentLane.getSpeedLimit() / (float) getNodeLength(nodeList.get(0), nodeList.get(nodeList.size() - 1));
                speed *= 2;
            }
            int carPositionInTrafficJam = currentLane.getCarsList().indexOf(this);

            if(currentLane.getCarsList().size() > 1 && carPositionInTrafficJam > 0){

                Car previousCar = currentLane.getCarsList().get(carPositionInTrafficJam-1);

                //check, some car is in front of you
                if(calculateDistance(carPosition.getX(),
                        carPosition.getY(),
                        previousCar.carPosition.getX(),
                        previousCar.carPosition.getY()) <= Lane.DISTANCE_BETWEEN_CARS + Lane.AVERAGE_CAR_LENGTH * 2){
                    status = RideStatus.WAITING;
                    return;
                }
            }
            else if(carPositionInTrafficJam == 0 && !onCrossing){
                double distance = calculateDistance(carPosition.getX(),
                        carPosition.getY(),
                        nextCrossing.getX(),
                        nextCrossing.getY());
                if(distance <= Crossing.NODE_CIRCLE_RADIUS + Lane.AVERAGE_CAR_LENGTH){

                    if (path.size() > 2) {
                        Node currentCrossing = path.get(1);
                        Node nodeAfterCrossing = path.get(2);
                        nextWay = calculateWay(currentCrossing, nodeAfterCrossing);
                        direction = calculateDirection(way, nextWay);

                        if(crossingList.get(0).isGoOnCrossingPossible(this)){
                            onCrossing = true;
                        }
                        else{
                            status = RideStatus.WAITING;
                            return;
                        }

                    }
                }
            }

            nodePercentage += speed * CAR_SPEED_MULTIPLIER;

            if (nodePercentage >= 100) {

                if(onCrossing && direction == Direction.LEFT){
                    if(!crossingList.get(0).isTurnLeftPossible(this)){
                        status = RideStatus.WAITING;
                        return;
                    }
                    else{
                        if(lanesList.size() > 1){
                            if(!lanesList.get(1).isLaneFull()){
                                status = RideStatus.RIDING;
                            }
                            else{
                                status = RideStatus.WAITING;
                                return;
                            }
                        }
                    }
                }

                if(status == RideStatus.RIDING){

                    nodePercentage %= 100;
                    path.remove(0);

                    onCrossing = false;
                    nextCrossing.goOutFromCrossing(this);


                    if (currentLane.getNodeList().size() == 2 || (currentLane.getNodeList().size() > 2 && nextNode == currentLane.getNodeList().get(2))) {

                        lanesList.get(0).getCarsList().remove(0);
                        lanesList.remove(0);


                        if(lanesList.size() > 0)
                            lanesList.get(0).getCarsList().add(this);

                        if(crossingList.size() > 1){
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
            }

            if(status == RideStatus.RIDING || onCrossing) {
                carPosition.setX((int) (currentNode.getX() + xVector * nodePercentage / 100));
                carPosition.setY((int) (currentNode.getY() + yVector * nodePercentage / 100));
            }

//            System.out.println("Car cords:" + actualNode.getX() + " " + actualNode.getY() + " Node percentage " + nodePercentage + " xVec " + xVector + " yVec " + yVector);

        }
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
//            else if(destination == Way.RIGHT){
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
//            else if(destination == Way.LEFT){
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
//            else if(destination == Way.BOTTOM){
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
//            else if(destination == Way.TOP ){
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
