package com.put.urbantraffic;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
public class Car {
//    private final Node startNode;
//    private final Node endNode;
    private final Lane startLane;
    private final Lane endLane;
    private Node startPoint;
    private Node endPoint;
    private List<Node> path;
    private Node currentNode;
    private Node actualPoint;
    private Node nextNode;
    private int nodePercentage = 0;
    private RideStatus status = RideStatus.WAITING;
    private List<Crossing> crossingList;
    CityGraph.PathWithTime calculatedPath;

    public Car(Lane startLane, Lane endLane) {
        this.startLane = startLane;
        this.endLane = endLane;

        this.startPoint = this.startLane.getMiddlePoint();
        this.endPoint = this.endLane.getMiddlePoint();

        this.path = generatePath(startLane, endLane);

        System.out.println(this.path);

//    public Car(Node startNode, Node endNode, List<Node> path) {
//        this.startNode = startNode;
//        this.endNode = endNode;
//        this.path = path;
//        this.currentNode = path.get(0);
//        this.nextNode = path.get(1);
//        this.actualPoint = path.get(0);
//
//
////        return new ArrayList<Node>(Arrays.asList(this.startPoint, this.endPoint));
////        this.path = path;
////        this.currentNode = path.get(0);
////        this.nextNode = path.get(1);
////        this.xPos = startNode.getX();
////        this.yPos = startNode.getY();
//    }

    }
    private List<Node> generatePath(Lane startLane, Lane endLane){

        calculatedPath = UrbanTrafficFlowSimulation.paths[startLane.getId()][endLane.getId()];
        this.crossingList = calculatedPath.getCrossings();
        int pathSize = crossingList.size() - 1;
        System.out.println(startLane.getId());
        System.out.println(endLane.getId());
        System.out.println(calculatedPath);
        List<Node> path = new ArrayList<>(Collections.singletonList(this.startPoint));
        System.out.println("CROSSINGS: " + crossingList.size() + " ROADS: " + calculatedPath.getLanes().size());

        int i = 0;
        if(checkIfIsInMiddle(crossingList.get(0), crossingList.get(1), this.startPoint)){
            path.add(new Node(crossingList.get(0).getX(), crossingList.get(0).getY()));
            i++;
        }

        for(; i < crossingList.size() - 1; i++){
            if(calculatedPath.getLanes().get(i).getNodeList().size() > 2){
                System.out.println(crossingList.get(i));
                List<Node> temp = new ArrayList<>();
                for(Node node: calculatedPath.getLanes().get(i).getNodeList()){
                    System.out.println(node);
                    if(node.getX() != crossingList.get(i).getX() || node.getY() != crossingList.get(i).getY()){
                        temp.add(node);
                    }
                }
                Node tempNode = new Node(crossingList.get(i).getX(), crossingList.get(i).getY());
                if(!Objects.equals(tempNode,path.get(path.size() - 1)))
                    path.add(tempNode);
                if(tempNode.getX() == temp.get(0).getX() || tempNode.getY() == temp.get(0).getY()){
                    if(!Objects.equals(temp.get(0),path.get(path.size() - 1)))
                        path.add(temp.get(0));
                    if(!Objects.equals(temp.get(1),path.get(path.size() - 1)))
                        path.add(temp.get(1));
                }else{
                    if(!Objects.equals(temp.get(1),path.get(path.size() - 1)))
                        path.add(temp.get(1));
                    if(!Objects.equals(temp.get(0),path.get(path.size() - 1)))
                        path.add(temp.get(0));
                }
            }
            else{
                Node tempNode = new Node(crossingList.get(i).getX(), crossingList.get(i).getY());
                if(!Objects.equals(tempNode,path.get(path.size() - 1)))
                    path.add(tempNode);
            }

        }

        if(checkIfIsInMiddle(crossingList.get(pathSize - 1), crossingList.get(pathSize), this.endPoint)){
            path.add(new Node(crossingList.get(pathSize).getX(), crossingList.get(pathSize).getY()));
        }

//        for(Road road: calculatedPath.roads){
//            path.addAll(road.getNodeList());
//        }
//        path.remove(path.size() - 1);
        path.add(this.endPoint);
        System.out.println(path);
        return path;
//        System.out.print
    }

//    private boolean checkNodes(Node firstNode, Node secondNode){
//        if(firstNode.e)
//    }

    private boolean checkIfIsInMiddle(Crossing firstCrossing, Crossing secondCrossing, Node checkPoint){
        return ((firstCrossing.getX() + secondCrossing.getX()) / 2 != checkPoint.getX() ||
                (firstCrossing.getY() + secondCrossing.getY()) / 2 != checkPoint.getY());
//                &&
//                ((firstCrossing.getX() != checkPoint.getX() && secondCrossing.getX() != checkPoint.getX()) ||
//                (firstCrossing.getY() != checkPoint.getY() && secondCrossing.getY() != checkPoint.getY()));
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
