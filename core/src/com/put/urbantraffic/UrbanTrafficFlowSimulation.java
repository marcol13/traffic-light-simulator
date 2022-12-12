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
import com.put.urbantraffic.drawablemodels.DrawableCrossingTrafficLight;
import com.put.urbantraffic.drawablemodels.DrawableCar;
import com.put.urbantraffic.drawablemodels.Frame;
import lombok.val;

import java.util.List;

import java.util.Random;
import java.util.Set;

public class UrbanTrafficFlowSimulation extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private float playerX;
    private float playerY;
    ExtendViewport extendViewport;
    BitmapFont font;
    BitmapFont clockFont;
    SpriteBatch batch;

    private City city;
    private Frame frameToRender = null;
    private int frameIndex = 0;
    private int speed = 1;
//    private final int scalePositionX = 2500;
    private final int scalePositionX = -100;
    private final int scalePositionY = 0;
    private final int scaleSpace = 20;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        extendViewport = new ExtendViewport(1200, 1200);
        Random rand = new Random(0);
//        long seed = rand.nextLong();
        long seed = -4962768465676381896L;
        rand.setSeed(seed);
        System.out.println("Seed is " + seed);

        city = new City(Settings.GRID_MULTIPLIER * 2 * 16, Settings.GRID_MULTIPLIER * 2 * 9, Settings.CROSSING_AMOUNT, rand);

        // runs whole simulation on different thread
        // so we can still render next frames
        new Thread(() -> {
            for (int i = Settings.STARTING_HOUR * 3600; i < Settings.ENDING_HOUR * 3600 - 1; i++) {
                city.makeStep();
//                System.out.println("Rendering frame: " + city.frame.size());
            }
        }).start();

        setupInitialCameraPositionAndZoom(Settings.GRID_MULTIPLIER);

        System.out.println("Quantity of Crossings: " + city.getCrossings().size());
        System.out.println("Quantity of Roads: " + city.getRoads().size());

//        List<Crossing> crossings = new ArrayList<Crossing>(Arrays.asList(new Crossing(1, 0, 200, new ArrayList<>()), new Crossing(2, 0,  0, new ArrayList<>()), new Crossing(3, 0, 400, new ArrayList<>()), new Crossing(4, 200, 200, new ArrayList<>())));
//        List<Road> roads = new ArrayList<Road>(
//                Arrays.asList(
//                        new Road(200,
//                                new ArrayList<Lane>(Arrays.asList(
//                                        new Lane(1, crossings.get(1), crossings.get(0), new ArrayList<Direction>()),
//                                        new Lane(1, crossings.get(0), crossings.get(1), new ArrayList<Direction>()))),
//                                new ArrayList<Node>(Arrays.asList(
//                                        new Node(0, 0),
//                                        new Node(0, 100),
//                                        new Node(0, 200)))),
//                        new Road(200,
//                                new ArrayList<Lane>(Arrays.asList(
//                                        new Lane(1, crossings.get(0), crossings.get(2), new ArrayList<Direction>()),
//                                        new Lane(4, crossings.get(2), crossings.get(0), new ArrayList<Direction>()))),
//                                new ArrayList<Node>(Arrays.asList(
//                                        new Node(0, 200),
//                                        new Node(0, 300),
//                                        new Node(0, 400)))),
//                        new Road(200,
//                                new ArrayList<Lane>(Arrays.asList(
//                                        new Lane(5, crossings.get(0), crossings.get(3), new ArrayList<Direction>()),
//                                        new Lane(6, crossings.get(3), crossings.get(0), new ArrayList<Direction>()))),
//                                new ArrayList<Node>(Arrays.asList(
//                                        new Node(0, 200),
//                                        new Node(100, 200),
//                                        new Node(200, 200)
//                                )))
//                        ));
//
//        city = new City(crossings, roads);
//
//        car = new Car(new Node(0, 0), new Node(200, 200), new ArrayList<Node>(Arrays.asList(new Node(0, 0), new Node(0, 100), new Node(0, 200), new Node(100, 200), new Node(200, 200))));



//        SimulationCore simulation = new SimulationCore(rand);
//        simulation.city = city;
//        simulation.epochs = Settings.EPOCHS;
//        simulation.population = Settings.POPULATION;
//        simulation.numberOfCrossings = city.getCrossings().size();
//        simulation.mutationScale = Settings.MUTATION_SCALE;
//        simulation.initialDeltaRange = Settings.INITIAL_DELTA_RANGE;
//        simulation.tournamentSelectionContestants = Settings.TOURNAMENT_SELECTION_CONTESTANT;
//        simulation.startSimulation();

        font = new BitmapFont(Gdx.files.internal("bahnschrift.fnt"));
        clockFont = new BitmapFont(Gdx.files.internal("clock-font.fnt"));
        batch = new SpriteBatch();
    }


    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void setupInitialCameraPositionAndZoom(int gridMultiplier) {
        val minX = city.getCrossings().stream().map(Crossing::getX).min(Integer::compareTo).get();
        val minY = city.getCrossings().stream().map(Crossing::getY).min(Integer::compareTo).get();
        val maxX = city.getCrossings().stream().map(Crossing::getX).max(Integer::compareTo).get();
        val maxY = city.getCrossings().stream().map(Crossing::getY).max(Integer::compareTo).get();
        playerX = (minX + maxX) / 2.0f;
        playerY = (minY + maxY) / 2.0f;
        ((OrthographicCamera) extendViewport.getCamera()).zoom = gridMultiplier + 0.5f;
    }

    @Override
    public void render() {

        moveCamera();

        ScreenUtils.clear(0, 0, 0, 1);

        extendViewport.apply();
        shapeRenderer.setProjectionMatrix(extendViewport.getCamera().combined);

        //Draw roads where max speed
        for (Road road : city.getRoads()) {
            int lanesAmount = road.getLaneList().size();
            Lane lane = road.getLaneList().get(0);
            List<Node> nodeList = lane.getNodeList();
            int startX = nodeList.get(0).getX();
            int startY = nodeList.get(0).getY();
            int endX = nodeList.get(0).getX();
            int endY = nodeList.get(0).getY();

            for (Node node : nodeList) {
                if (node.getX() != startX && node.getY() != startY) {
                    drawCircle(endX, endY, Settings.CORNER_RADIUS, Color.WHITE);
                    drawLaneWithSpeedLimit(lanesAmount, lane, startX, startY, endX, endY);
                    startX = endX;
                    startY = endY;
                }
                endX = node.getX();
                endY = node.getY();
            }
            drawLaneWithSpeedLimit(lanesAmount, lane, startX, startY, endX, endY);
        }

        if (frameToRender == null) return;

        for (final DrawableCrossingTrafficLight light :frameToRender.getLights()) {
            drawCircle(light.getX(), light.getY(), Settings.CROSSING_RADIUS, Color.WHITE);
            drawTrafficLight(light);
        }

        //Draw cars

        for(DrawableCar car: frameToRender.getCars()){
            int offsetX = 0, offsetY = 0;

            if(car.getWay() == Car.Way.TOP)
                offsetX = Settings.NODE_LANE_OFFSET;
            if(car.getWay() == Car.Way.BOTTOM)
                offsetX = -Settings.NODE_LANE_OFFSET;
            if(car.getWay() == Car.Way.LEFT)
                offsetY = Settings.NODE_LANE_OFFSET;
            if(car.getWay() == Car.Way.RIGHT)
                offsetY = -Settings.NODE_LANE_OFFSET;
            drawCircle(car.getX() + offsetX, car.getY() + offsetY, Settings.CAR_RADIUS, car.getStatus() == RideStatus.RIDING ? Settings.CAR_CIRCLE_COLOR : Color.BLUE);
        }

        drawLinearScale();

        batch.setProjectionMatrix(extendViewport.getCamera().combined);
        batch.begin();
        font.getData().setScale(1.0f);
        font.draw(batch, "0", scalePositionX - 5,scalePositionY + 2 * scaleSpace);
        font.draw(batch, "100", scalePositionX + Settings.MESH_DISTANCE - 15,scalePositionY + 2 * scaleSpace);
        font.draw(batch, "200 [m]", scalePositionX + 2 * Settings.MESH_DISTANCE - 15,scalePositionY + 2 * scaleSpace);
        font.getData().setScale(4.0f);
        font.draw(batch, "Value of goal function: " + Long.toString(frameToRender.getWaitingTime()), -100,-300);

        long currentSimulationTime = frameToRender.getCurrentTime();
        String hour = String.format("%02d", currentSimulationTime / 3600);
        String minute = String.format("%02d", currentSimulationTime / 60 % 60);
        String second = String.format("%02d", currentSimulationTime % 60);
        font.draw(batch, "Current time: " + hour + ":" + minute + ":" + second, -100,-100);

        font.draw(batch, "Amount of cars: " + frameToRender.getCars().size(), -100,-200);
        batch.end();
    }

    private void drawTrafficLight(DrawableCrossingTrafficLight crossing) {
        if (crossing.getTopTrafficLight() != null) {
            Color color;
            if (crossing.getTopTrafficLight().isYellow()) {
                color = Color.YELLOW;
            } else {
                color = crossing.getTopTrafficLight().getCurrentColor() == Light.GREEN ? Color.GREEN : Color.RED;
            }

            drawCircle(crossing.getX(), crossing.getY() + Settings.CAR_RADIUS, Settings.CAR_RADIUS / 2, color);
        }

        if (crossing.getBottomTrafficLight() != null) {
            Color color;
            if (crossing.getBottomTrafficLight().isYellow()) {
                color = Color.YELLOW;
            } else {
                color = crossing.getBottomTrafficLight().getCurrentColor() == Light.GREEN ? Color.GREEN : Color.RED;
            }

            drawCircle(crossing.getX(), crossing.getY() - Settings.CAR_RADIUS, Settings.CAR_RADIUS / 2, color);
        }

        if (crossing.getRightTrafficLight() != null) {
            Color color;
            if (crossing.getRightTrafficLight().isYellow()) {
                color = Color.YELLOW;
            } else {
                color = crossing.getRightTrafficLight().getCurrentColor() == Light.GREEN ? Color.GREEN : Color.RED;
            }

            drawCircle(crossing.getX() + Settings.CAR_RADIUS, crossing.getY(), Settings.CAR_RADIUS / 2, color);
        }

        if (crossing.getLeftTrafficLight() != null) {
            Color color;
            if (crossing.getLeftTrafficLight().isYellow()) {
                color = Color.YELLOW;
            } else {
                color = crossing.getLeftTrafficLight().getCurrentColor() == Light.GREEN ? Color.GREEN : Color.RED;
            }

            drawCircle(crossing.getX() - Settings.CAR_RADIUS, crossing.getY(), Settings.CAR_RADIUS / 2, color);
        }
    }

    private void drawLaneWithSpeedLimit(int lanesAmount, Lane lane, int startX, int startY, int endX, int endY) {
        if (lane.getSpeedLimit() == 30) {
            drawLanes(startX, startY, endX, endY, lanesAmount, Color.DARK_GRAY);
        } else if (lane.getSpeedLimit() == 50) {
            drawLanes(startX, startY, endX, endY, lanesAmount, Color.LIGHT_GRAY);
        } else {
            drawLanes(startX, startY, endX, endY, lanesAmount, Color.RED);
        }
    }

    public void moveCamera() {
        float delta = Gdx.graphics.getDeltaTime();

        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            ((OrthographicCamera) extendViewport.getCamera()).zoom += .5f * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            ((OrthographicCamera) extendViewport.getCamera()).zoom -= .5f * delta;
        }

        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)){
            if (frameIndex < city.frame.size()) {
                frameToRender = city.frame.get(frameIndex);
                frameIndex += speed;
            }
            else{
                frameIndex = city.frame.size()-1;
                frameToRender = city.frame.get(frameIndex);
            }
        }

        handleSpeedControl();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            playerX -= Settings.CAMERA_MOVE_SPEED * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            playerX += Settings.CAMERA_MOVE_SPEED * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            playerY -= Settings.CAMERA_MOVE_SPEED * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            playerY += Settings.CAMERA_MOVE_SPEED * delta;
        }

        extendViewport.getCamera().position.set(playerX, playerY, 0);
    }

    private void handleSpeedControl() {
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_1)) {
            speed = 10;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_2)) {
            speed = 20;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_3)) {
            speed = 30;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_4)) {
            speed = 40;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_5)) {
            speed = 50;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_6)) {
            speed = 60;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_7)) {
            speed = 70;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_8)) {
            speed = 80;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_9)) {
            speed = 999999999;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)) {
            speed = 1;
        }
    }

    public void drawLanes(int startX, int startY, int endX, int endY, int lanesAmount, Color color) {
        int offsetX, offsetY, shiftX, shiftY;
        if (startX == endX) {
            offsetX = Settings.NODE_LANE_OFFSET;
            offsetY = 0;
            shiftX = Settings.NODE_LANE_OFFSET / 2 * (lanesAmount - 1);
            shiftY = 1;
        } else {
            offsetX = 0;
            offsetY = Settings.NODE_LANE_OFFSET;
            shiftX = 1;
            shiftY = Settings.NODE_LANE_OFFSET / 2 * (lanesAmount - 1);
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

    /**
     * draws path between two lanes with given id
     * green is road
     * magenta is start and end line
     */
    private void drawPathFor2LanesId(int id1, int id2) {
        List<Lane> lanes = city.getLanes();
        Lane startLane = lanes.get(id1);
        Lane endLane = lanes.get(id2);
        drawPath(city.paths[startLane.getId()][endLane.getId()]);
        drawLanes(startLane.getStartCrossing().getX(), startLane.getStartCrossing().getY(), startLane.getEndCrossing().getX(), startLane.getEndCrossing().getY(), 1, Color.MAGENTA);
        drawLanes(endLane.getStartCrossing().getX(), endLane.getStartCrossing().getY(), endLane.getEndCrossing().getX(), endLane.getEndCrossing().getY(), 1, Color.MAGENTA);
    }

    /**
     * draws path for given CityGraph#PathWithTime
     */
    private void drawPath(CityGraph.PathWithTime pathWithTime) {
        for (Crossing crossing : pathWithTime.getCrossings()) {
            drawCircle(crossing.getX(), crossing.getY(), 20, Color.BLUE);
        }
        drawCircle(pathWithTime.getCrossings().get(0).getX(), pathWithTime.getCrossings().get(0).getY(), 20, Color.RED);
        drawCircle(pathWithTime.getCrossings().get(pathWithTime.getCrossings().size() - 1).getX(), pathWithTime.getCrossings().get(pathWithTime.getCrossings().size() - 1).getY(), 20, Color.RED);
        for (Lane lane : pathWithTime.getLanes()) {
            drawLanes(lane.getStartCrossing().getX(), lane.getStartCrossing().getY(), lane.getEndCrossing().getX(), lane.getEndCrossing().getY(), 1, Color.LIME);
        }
    }

    private void drawLinearScale(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rectLine(scalePositionX, scalePositionY, scalePositionX + 2 * Settings.MESH_DISTANCE, scalePositionY, 5);
        shapeRenderer.rectLine(scalePositionX, scalePositionY - scaleSpace, scalePositionX, scalePositionY + scaleSpace, 5);
        shapeRenderer.rectLine(scalePositionX + 2 * Settings.MESH_DISTANCE, scalePositionY - scaleSpace, scalePositionX + 2 * Settings.MESH_DISTANCE, scalePositionY + scaleSpace, 5);
        shapeRenderer.rectLine(scalePositionX + Settings.MESH_DISTANCE, (float) (scalePositionY - scaleSpace * 0.75), scalePositionX + Settings.MESH_DISTANCE, (float) (scalePositionY + scaleSpace * 0.75), 5);
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
