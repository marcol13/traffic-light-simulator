package com.put.urbantraffic;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class Car {
    private final Node startNode;
    private final Node endNode;
    private final List<Node> path;
    private Node currentNode;
    private Node nextNode;
    private int xPos;
    private int yPos;
    private int nodePercentage = 0;
    private RideStatus status = RideStatus.WAITING;

    public Car(Node startNode, Node endNode, List<Node> path) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.path = path;
        this.currentNode = path.get(0);
        this.nextNode = path.get(1);
        this.xPos = startNode.getX();
        this.yPos = startNode.getY();
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
                    xPos = nextNode.getX();
                    yPos = nextNode.getY();
                    return;
                }
            }

            xPos = currentNode.getX() + (nextNode.getX() - currentNode.getX()) * nodePercentage / 100;
            yPos = currentNode.getY() + (nextNode.getY() - currentNode.getY()) * nodePercentage / 100;
        }

    }
}
