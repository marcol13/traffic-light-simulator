package com.put.urbantraffic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import lombok.val;

import java.util.*;

public class UrbanTrafficFlowSimulation extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private float playerX;
    private float playerY;
    ExtendViewport extendViewport;

    private City city;
    private Car car;

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
        int gridMultiplier = 2;
        int crossingAmount = 50;
        city = new City(gridMultiplier * 16, gridMultiplier * 9, crossingAmount);
        new CityGraph().generate(city);
        System.out.println("Quantity of Crossings: " + city.getCrossings().size());
        System.out.println("Quantity of Roads: " + city.getRoads().size());

        List<Crossing> crossings = new ArrayList<Crossing>(Arrays.asList(new Crossing(1, 0, 200, new ArrayList<>()), new Crossing(2, 0,  0, new ArrayList<>()), new Crossing(3, 0, 400, new ArrayList<>()), new Crossing(4, 200, 200, new ArrayList<>())));
        List<Road> roads = new ArrayList<Road>(
                Arrays.asList(
                        new Road(200,
                                new ArrayList<Lane>(Arrays.asList(
                                        new Lane(1, crossings.get(1), crossings.get(0), new ArrayList<Direction>()),
                                        new Lane(1, crossings.get(0), crossings.get(1), new ArrayList<Direction>()))),
                                new ArrayList<Node>(Arrays.asList(
                                        new Node(0, 0),
                                        new Node(0, 100),
                                        new Node(0, 200)))),
                        new Road(200,
                                new ArrayList<Lane>(Arrays.asList(
                                        new Lane(1, crossings.get(0), crossings.get(2), new ArrayList<Direction>()),
                                        new Lane(4, crossings.get(2), crossings.get(0), new ArrayList<Direction>()))),
                                new ArrayList<Node>(Arrays.asList(
                                        new Node(0, 200),
                                        new Node(0, 300),
                                        new Node(0, 400)))),
                        new Road(200,
                                new ArrayList<Lane>(Arrays.asList(
                                        new Lane(5, crossings.get(0), crossings.get(3), new ArrayList<Direction>()),
                                        new Lane(6, crossings.get(3), crossings.get(0), new ArrayList<Direction>()))),
                                new ArrayList<Node>(Arrays.asList(
                                        new Node(0, 200),
                                        new Node(100, 200),
                                        new Node(200, 200)
                                )))
                        ));

        city = new City(crossings, roads);

        car = new Car(new Node(0, 0), new Node(200, 200), new ArrayList<Node>(Arrays.asList(new Node(0, 0), new Node(0, 100), new Node(0, 200), new Node(100, 200), new Node(200, 200))));

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

        drawCircle(car.getXPos(), car.getYPos(), 10, Color.YELLOW);
    }

    public void moveCamera() {
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            ((OrthographicCamera) extendViewport.getCamera()).zoom += .5f * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            ((OrthographicCamera) extendViewport.getCamera()).zoom -= .5f * delta;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            car.moveCar();
            System.out.println(car.getXPos());
            System.out.println(car.getYPos());
            drawCircle(car.getXPos(), car.getYPos(), 10, Color.YELLOW);
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
