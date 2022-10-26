package com.put.urbantraffic;

import lombok.Data;

@Data
public class Car {
    private final Node startNode;
    private final Node endNode;
    private Node currentNode;
    private Node nextNode;
//    private final Lane startLane;
//    private final Lane endLane;
    private int nodePercentage;
    private final List<Node> path = new ArrayList();
    private final RideStatus status = RideStatus.WAITING;

    public void moveCar(){
        nodePercentage += 10;
        if(nodePercentage >= 100){
            nodePercentage %= 100;
            path.remove(0);
            if(path.size() > 1){
                currentNode = path.get(0);
                nextNode = path.get(1);
            }
            else{
                //FINISH
            }

        }
    }
}
