package com.put.urbantraffic;

import lombok.Data;

import java.util.List;

@Data
public class Road {
    private final int id;
    private int speedLimit;
    private final int length;
    private final List<Lane> laneList;
    private final List<Node> nodeList;
    public Node getMiddlePoint(){
        if(nodeList.size() % 2 == 1)
            return nodeList.get(1);
        return new Node((nodeList.get(0).getX() + nodeList.get(1).getX()) / 2, (nodeList.get(0).getY() + nodeList.get(1).getY()) / 2);
    }
}
