package com.put.urbantraffic;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class Car {
    private final Lane startLane;
    private final Lane endLane;
    private Node startNode;
    private Node endNode;

    private List<Node> path;

    private Node currentNode;
    private Node nextNode;
    private Node actualNode;
    private Lane currentLane;
    private float nodePercentage = 0.0f;

    private RideStatus status = RideStatus.RIDING;

    private List<Crossing> crossingList;
    private Crossing nextCrossing;

    private List<Lane> lanesList;
    CityGraph.PathWithTime calculatedPath;

    private Way way;
    private Way nextWay;

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
        this.actualNode = new Node(path.get(0).getX(), path.get(0).getY());
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

            nodePercentage += speed;


            if (nodePercentage >= 100) {

                if(status == RideStatus.RIDING){

                    nodePercentage %= 100;
                    path.remove(0);
                    onCrossing = false;

                    if (path.size() > 1) {
                        currentNode = path.get(0);
                        currentLane = lanesList.get(0);
                        nextNode = path.get(1);
                        nextWay = calculateWay(currentNode, nextNode);
                        if(crossingList.get(0).mayTurnLeft(way,nextWay)){
                            way = nextWay;
                        }else{
                            status = RideStatus.WAITING;
                            return;
                        }
                    } else {
                        status = RideStatus.FINISH;
                        actualNode = nextNode;
                        return;
                    }

                    if (currentLane.getNodeList().size() == 2 || currentLane.getNodeList().size() > 2 && currentNode == currentLane.getNodeList().get(2)) {

                        lanesList.get(0).getCarsList().remove(0);
                        lanesList.remove(0);

                        nextCrossing.goOutFromCrossing(this);

                        if(lanesList.size() > 0)
                            lanesList.get(0).getCarsList().add(this);

                        if(crossingList.size() > 1){
                            crossingList.remove(0);
                            nextCrossing = crossingList.get(0);
                        }

                    }
                }
            }
            else if (nodePercentage >= 100 - 15 - (lanesList.get(0).getCarsList().indexOf(this) * (Lane.AVERAGE_CAR_LENGTH + Lane.DISTANCE_BETWEEN_CARS))){
                if(nextNode.equals(new Node(nextCrossing.getX(),nextCrossing.getY()))){
                    if(!nextCrossing.mayEnterCrossing(this) && !onCrossing){
                        status = RideStatus.WAITING;
                        nodePercentage -= speed;
                    }
                    else{
                        onCrossing = true;
                    }
                }
            }

            if(status == RideStatus.RIDING || onCrossing) {
                actualNode.setX((int) (currentNode.getX() + xVector * nodePercentage / 100));
                actualNode.setY((int) (currentNode.getY() + yVector * nodePercentage / 100));
            }

//            System.out.println("Car cords:" + actualNode.getX() + " " + actualNode.getY() + " Node percentage " + nodePercentage + " xVec " + xVector + " yVec " + yVector);

        }
    }

    private int getNodeLength(Node currentNode, Node nextNode) {
        return Math.abs(currentNode.getX() - nextNode.getX()) + Math.abs(currentNode.getY() - nextNode.getY());
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
