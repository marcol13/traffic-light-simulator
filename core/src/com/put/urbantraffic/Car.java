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
    private List<Crossing> crossingList;
    CityGraph.PathWithTime calculatedPath;

    public Car(Road startRode, Road endRode){
        this.startRode = startRode;
        this.endRode = endRode;

        this.startPoint = this.startRode.getMiddlePoint();
        this.endPoint = this.endRode.getMiddlePoint();

        this.path = generatePath(startRode, endRode);

        System.out.println(this.path);

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

        calculatedPath = UrbanTrafficFlowSimulation.paths[startRode.getId()][endRode.getId()];
        this.crossingList = calculatedPath.getCrossings();
        int pathSize = calculatedPath.crossings.size() - 1;
        System.out.println(startRode.getId());
        System.out.println(endRode.getId());
        System.out.println(calculatedPath);
        List<Node> path = new ArrayList<>(Collections.singletonList(this.startPoint));
        System.out.println("CROSSINGS: " + calculatedPath.crossings.size() + " ROADS: " + calculatedPath.roads.size());

        int i = 0;
        if(checkIfIsInMiddle(calculatedPath.crossings.get(0), calculatedPath.crossings.get(1), this.startPoint)){
            path.add(new Node(calculatedPath.crossings.get(0).getX(), calculatedPath.crossings.get(0).getY()));
            i++;
        }

        for(; i < calculatedPath.crossings.size() - 1; i++){
            if(calculatedPath.roads.get(i).getNodeList().size() > 2){
                System.out.println(calculatedPath.crossings.get(i));
                List<Node> temp = new ArrayList<>();
                for(Node node: calculatedPath.roads.get(i).getNodeList()){
                    System.out.println(node);
                    if(node.getX() != calculatedPath.crossings.get(i).getX() || node.getY() != calculatedPath.crossings.get(i).getY()){
                        temp.add(node);
                    }
                }
                Node tempNode = new Node(calculatedPath.crossings.get(i).getX(), calculatedPath.crossings.get(i).getY());
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
                Node tempNode = new Node(calculatedPath.crossings.get(i).getX(), calculatedPath.crossings.get(i).getY());
                if(!Objects.equals(tempNode,path.get(path.size() - 1)))
                    path.add(tempNode);
            }

        }

        if(checkIfIsInMiddle(calculatedPath.crossings.get(pathSize - 1), calculatedPath.crossings.get(pathSize), this.endPoint)){
            path.add(new Node(calculatedPath.crossings.get(pathSize).getX(), calculatedPath.crossings.get(pathSize).getY()));
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
