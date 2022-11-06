package com.put.urbantraffic;

import java.util.List;

public class Road {
    private final int id;
    private int speedLimit;
    private final List<Lane> laneList;
    private final List<Node> nodeList;
    private final int length;

    public Road(int id, List<Lane> laneList, List<Node> nodeList) {
        this.id = id;
        this.laneList = laneList;
        this.nodeList = nodeList;
        this.length = calculateLength(nodeList);
    }

    private int calculateLength(List<Node> nodeList) {
        int xDiff = Math.abs(nodeList.get(0).getX() - nodeList.get(nodeList.size() - 1).getX());
        int yDiff = Math.abs(nodeList.get(0).getY() - nodeList.get(nodeList.size() - 1).getY());
        return xDiff + yDiff;
    }

    public int getId() {
        return id;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public List<Lane> getLaneList() {
        return laneList;
    }

    public List<Node> getNodeList() {
        return nodeList;
    }

    public int getLength() {
        return length;
    }
}
