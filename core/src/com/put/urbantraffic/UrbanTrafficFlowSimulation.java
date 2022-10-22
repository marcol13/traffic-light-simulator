package com.put.urbantraffic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import lombok.val;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UrbanTrafficFlowSimulation extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private float playerX;
    private float playerY;
    ExtendViewport extendViewport;

    private City city;

    private static final float MOVE_SPEED = 150f;
    private static final int NODE_CIRCLE_RADIUS = 15;
    private static final int CORNER_CIRCLE_RADIUS = 7;
    private static final int NODE_OFFSET_LANE = 4;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        extendViewport = new ExtendViewport(1200, 1200);

//        Crossing amount < 70 -> *2
//        Crossing amount < 300 -> *4
//        Crossing amount < 600 -> *6
        int gridMultiplier = 4;
        int crossingAmount = 100;
        city = new City(gridMultiplier * 16, gridMultiplier * 9, crossingAmount);
        System.out.println("Quantity of Crossings: " + city.getCrossings().size());
        System.out.println("Quantity of Roads: " + city.getRoads().size());

        SimulationCore simulation = new SimulationCore();
        simulation.city = city;
        simulation.epochs = 40;
        simulation.numberOfChildren = 10;
        simulation.numberOfCrossings = city.getCrossings().size();
        simulation.mutationScale = 50;
        simulation.initialDeltaRange = 1000;
        simulation.startSimulation();

        for (Road road : city.getRoads()) {
            System.out.println("ROAD LENGTH: " + road.getLength() + " Speed: " + road.getSpeedLimit());
        }

    }

    @Override
    public void render() {

        moveCamera();

        ScreenUtils.clear(0, 0, 0, 1);

        extendViewport.apply();
        shapeRenderer.setProjectionMatrix(extendViewport.getCamera().combined);


        for (Crossing crossing : city.getCrossings()) {
            drawCircle(crossing.getX(), crossing.getY(), NODE_CIRCLE_RADIUS, Color.WHITE);
        }

        //Draw roads where max speed
        for (Road road : city.getRoads()) {
            int lanesAmount = road.getLaneList().size();
            int startX = road.getNodeList().get(0).getX();
            int startY = road.getNodeList().get(0).getY();
            int endX = road.getNodeList().get(0).getX();
            int endY = road.getNodeList().get(0).getY();

            for (Node node : road.getNodeList()) {
                if (node.getX() != startX && node.getY() != startY) {
                    drawCircle(endX, endY, CORNER_CIRCLE_RADIUS, Color.WHITE);
                    if(road.getSpeedLimit() == 40){
                        drawLanes(startX, startY, endX, endY, lanesAmount, Color.DARK_GRAY);
                    }else if(road.getSpeedLimit() == 50){
                        drawLanes(startX, startY, endX, endY, lanesAmount, Color.LIGHT_GRAY);
                    }
                    else{
                        drawLanes(startX, startY, endX, endY, lanesAmount, Color.RED);
                    }

                    startX = endX;
                    startY = endY;
                }
                endX = node.getX();
                endY = node.getY();
            }
            if(road.getSpeedLimit() == 40){
                drawLanes(startX, startY, endX, endY, lanesAmount, Color.DARK_GRAY);
            }else if(road.getSpeedLimit() == 50){
                drawLanes(startX, startY, endX, endY, lanesAmount, Color.LIGHT_GRAY);
            }
            else{
                drawLanes(startX, startY, endX, endY, lanesAmount, Color.RED);
            }
        }
    }

    public void moveCamera() {
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            ((OrthographicCamera) extendViewport.getCamera()).zoom += .5f * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            ((OrthographicCamera) extendViewport.getCamera()).zoom -= .5f * delta;
        }


        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerX -= MOVE_SPEED * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerX += MOVE_SPEED * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerY -= MOVE_SPEED * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerY += MOVE_SPEED * delta;
        }

        extendViewport.getCamera().position.set(playerX, playerY, 0);

    }

    public void drawLanes(int startX, int startY, int endX, int endY, int lanesAmount, Color color) {
        int offsetX, offsetY, shiftX, shiftY;
        if (startX == endX) {
            offsetX = NODE_OFFSET_LANE;
            offsetY = 0;
            shiftX = NODE_OFFSET_LANE / 2 * (lanesAmount - 1);
            shiftY = 1;
        } else {
            offsetX = 0;
            offsetY = NODE_OFFSET_LANE;
            shiftX = 1;
            shiftY = NODE_OFFSET_LANE / 2 * (lanesAmount - 1);
        }
        for (int i = 0; i < lanesAmount; i++) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(color);
            shapeRenderer.line(startX + offsetX * i - shiftX, startY + offsetY * i - shiftY, endX + offsetX * i - shiftX, endY + offsetY * i - shiftY);
            shapeRenderer.end();
        }
    }

    public void drawCircle(int x, int y, int radius, Color color) {
        shapeRenderer.setColor(color);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(x, y, radius);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        extendViewport.update(width, height);
    }

    @Override
    public void dispose() {
    }
}
