package com.put.urbantraffic;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    private int nodePercentage = 0;

    private RideStatus status = RideStatus.WAITING;

    private List<Crossing> crossingList;
    private List<Lane> lanesList;
    CityGraph.PathWithTime calculatedPath;

    public Car(Lane startLane, Lane endLane) {
        this.startLane = startLane;
        this.endLane = endLane;

        this.startNode = this.startLane.getMiddlePoint();
        this.endNode = this.endLane.getMiddlePoint();

        this.path = generatePath(startLane, endLane);

        this.currentNode = path.get(0);
        this.actualNode = path.get(0);
        this.nextNode = path.get(1);

        System.out.println(this.path);
    }
    private List<Node> generatePath(Lane startLane, Lane endLane){
        calculatedPath = UrbanTrafficFlowSimulation.paths[startLane.getId()][endLane.getId()];
        this.crossingList = calculatedPath.getCrossings();
        this.lanesList = calculatedPath.getLanes();

        System.out.println(calculatedPath);
        System.out.println("CROSSINGS: " + this.crossingList.size() + " ROADS: " + this.lanesList.size());

        List<Node> path = new ArrayList<>(Collections.singletonList(this.startNode));
        if(this.crossingList.size() == 1)
            path.add(new Node(this.crossingList.get(0).getX(), this.crossingList.get(0).getY()));
        else
            for(Lane lane: this.lanesList){
                path.addAll(lane.getNodeList());
            }
        path.add(this.endNode);

        return path;
    }

    public void moveCar() {
        if (status != RideStatus.FINISH) {
            nodePercentage += 1;

            if (nodePercentage >= 100) {
                nodePercentage %= 100;
                path.remove(0);
                if (path.size() > 1) {
                    currentNode = path.get(0);
                    nextNode = path.get(1);

                } else {
                    status = RideStatus.FINISH;
                    actualNode = nextNode;
                    return;
                }
            }

            actualNode.setX(currentNode.getX() + (nextNode.getX() - currentNode.getX()) * nodePercentage / 100);
            actualNode.setY(currentNode.getY() + (nextNode.getY() - currentNode.getY()) * nodePercentage / 100);
        }

    }
}
