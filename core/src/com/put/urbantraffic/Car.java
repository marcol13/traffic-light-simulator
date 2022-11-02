package com.put.urbantraffic;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
public class Car {
//    private final Node startNode;
//    private final Node endNode;
    private final Road startRode;
    private final Road endRode;
    private Node startPoint;
    private Node endPoint;
    private List<Node> path;
    private Node currentNode;
    private Node actualPoint;
    private Node nextNode;
    private int nodePercentage = 0;
    private RideStatus status = RideStatus.WAITING;

    public Car(Road startRode, Road endRode){
        this.startRode = startRode;
        this.endRode = endRode;

        this.startPoint = this.startRode.getMiddlePoint();
        this.endPoint = this.endRode.getMiddlePoint();

        this.path = generatePath(startRode, endRode);

        System.out.println(path);

        this.currentNode = path.get(0);
        this.nextNode = path.get(1);
        this.actualPoint = path.get(0);


//        return new ArrayList<Node>(Arrays.asList(this.startPoint, this.endPoint));
//        this.path = path;
//        this.currentNode = path.get(0);
//        this.nextNode = path.get(1);
//        this.xPos = startNode.getX();
//        this.yPos = startNode.getY();
    }

    private List<Node> generatePath(Road startRode, Road endRode){
        CityGraph.PathWithTime calculatedPath = UrbanTrafficFlowSimulation.paths[startRode.getId()][endRode.getId()];
        System.out.println(startRode.getId());
        System.out.println(endRode.getId());
        System.out.println(calculatedPath);
        List<Node> path = new ArrayList<>(Collections.singletonList(this.startPoint));
        for(Road road: calculatedPath.roads){
            path.addAll(road.getNodeList());
        }
//        path.remove(path.size() - 1);
        path.add(this.endPoint);
        return path;
//        System.out.print
    }

    public void moveCar(){
        if(status != RideStatus.FINISH){
            nodePercentage += 1;

            if(nodePercentage >= 100){
                nodePercentage %= 100;
                path.remove(0);
                if(path.size() > 1){
                    currentNode = new Node(path.get(0).getX(), path.get(0).getY());
                    nextNode = new Node(path.get(1).getX(), path.get(1).getY());
                }
                else{
                    status = RideStatus.FINISH;
                    actualPoint = nextNode;
                    return;
                }
            }

//            System.out.println(currentNode);
//            System.out.println(nextNode);

            actualPoint.setX(currentNode.getX() + (nextNode.getX() - currentNode.getX()) * nodePercentage / 100);
            actualPoint.setY(currentNode.getY() + (nextNode.getY() - currentNode.getY()) * nodePercentage / 100);
        }

    }
}
