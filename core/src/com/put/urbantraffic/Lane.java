package com.put.urbantraffic;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Lane {
    public static final double AVERAGE_CAR_LENGTH = 10;
    public static final double DISTANCE_BETWEEN_CARS = 0.5;

    private final int id;
    private final Crossing startCrossing;
    private final Crossing endCrossing;
    private final List<Direction> directions;
    private final List<Node> nodeList;
    private final int length;
    private int speedLimit;

    public Lane(int id, Crossing startCrossing, Crossing endCrossing, List<Direction> directions, List<Node> nodeList) {
        this.id = id;
        this.startCrossing = startCrossing;
        this.endCrossing = endCrossing;
        this.directions = directions;
        this.nodeList = nodeList;
        this.length = calculateLength(nodeList);
    }

    private final List<Car> carsList = new ArrayList<>();

    private int calculateLength(List<Node> nodeList) {
        int xDiff = Math.abs(nodeList.get(0).getX() - nodeList.get(nodeList.size() - 1).getX());
        int yDiff = Math.abs(nodeList.get(0).getY() - nodeList.get(nodeList.size() - 1).getY());
        return xDiff + yDiff;
    }

    //    private final int maxCarAmount =
//            (int) (Math.sqrt(
//                    Math.pow(startCrossing.getX() - endCrossing.getX(), 2) +
//                            Math.pow(startCrossing.getY() - endCrossing.getY(), 2))
//                    / (AVERAGE_CAR_LENGTH + DISTANCE_BETWEEN_CARS));

    public Node getMiddlePoint() {
        if (nodeList.size() > 2) {
            return nodeList.get(1);
        }
        return new Node((nodeList.get(0).getX() + nodeList.get(1).getX()) / 2,
                (nodeList.get(0).getY() + nodeList.get(1).getY()) / 2);
    }

    public int getId() {
        return id;
    }

    public Crossing getStartCrossing() {
        return startCrossing;
    }

    public Crossing getEndCrossing() {
        return endCrossing;
    }

    public List<Direction> getDirections() {
        return directions;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public int getLength() {
        return length;
    }

    public List<Car> getCarsList() {
        return carsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lane lane = (Lane) o;
        return id == lane.id && speedLimit == lane.speedLimit && length == lane.length && Objects.equals(startCrossing, lane.startCrossing) && Objects.equals(endCrossing, lane.endCrossing) && Objects.equals(directions, lane.directions) && Objects.equals(nodeList, lane.nodeList) && Objects.equals(carsList, lane.carsList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startCrossing, endCrossing, directions, speedLimit, nodeList, length, carsList);
    }

    public boolean isLaneFull(){
        return length - carsList.size()* (AVERAGE_CAR_LENGTH + DISTANCE_BETWEEN_CARS) < AVERAGE_CAR_LENGTH + DISTANCE_BETWEEN_CARS;
    }
}
