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
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.put.urbantraffic.drawablemodels.DrawableCrossingTrafficLight;
import com.put.urbantraffic.drawablemodels.DrawableCar;
import com.put.urbantraffic.drawablemodels.Frame;
import lombok.SneakyThrows;
import lombok.val;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.put.urbantraffic.Settings.*;

public class UrbanTrafficFlowSimulation extends ApplicationAdapter {
    private ShapeRenderer shapeRenderer;
    private float playerX;
    private float playerY;
    ExtendViewport extendViewport;
    BitmapFont font;
    BitmapFont clockFont;
    SpriteBatch batch;

    public static Gson gson = new Gson();
    private City city;
    private Frame frameToRender = null;
    private int frameIndex = 0;
    private int speed = 1;
//    private final int scalePositionX = 2500;
    private final int scalePositionX = -100;
    private final int scalePositionY = 0;
    private final int scaleSpace = 20;
    SimulationCore simulation = new SimulationCore(new Random());
    List<Frame> frames = new ArrayList<>();
    private BufferedReader reader;
    Random rand = new Random(0);
    //        long seed = rand.nextLong();
    long seed = 0;

    private int worstDistrict;

    @SneakyThrows
    @Override
    public void create() {
        String filename = "plik.txt";
        shapeRenderer = new ShapeRenderer();
        extendViewport = new ExtendViewport(1200, 1200);

        rand.setSeed(seed);
        System.out.println("Seed is " + seed);

        if (IS_OPTIMIZATION) {
            simulation.seed = seed;
            simulation.epochs = Settings.EPOCHS;
            simulation.population = Settings.POPULATION;
            simulation.mutationScale = Settings.MUTATION_SCALE;
            simulation.initialDeltaRange = Settings.INITIAL_DELTA_RANGE;
            simulation.tournamentSelectionContestants = Settings.TOURNAMENT_SELECTION_CONTESTANT;
            val resultsStream = new BufferedWriter(new FileWriter("results_skrz_" + CROSSING_AMOUNT + "_cars_" + CARS_QUANTITY + "_" + System.currentTimeMillis() +".txt"));
            resultsStream.write("aut " + CARS_QUANTITY + "\n");
            resultsStream.write("skrzyzowan " + CROSSING_AMOUNT + "\n");
            for (int i = 0; i < 1; i++) {
                long startTime = System.currentTimeMillis();
                simulation.startSimulation();
                long time = System.currentTimeMillis() - startTime;
                System.out.println("Total time " + time);
                resultsStream.write("time " + time + "\n");
                city = simulation.best;
                city = new City(new Random(seed), city.trafficLightsSettingsList, filename);
                System.out.println(city.getCrossings().stream().sorted(Comparator.comparingInt(Crossing::getX)).collect(Collectors.toList()));
                System.out.println(city.getCrossings().stream().sorted(Comparator.comparingInt(Crossing::getY)).collect(Collectors.toList()));
                worstDistrict = Stream.of(city.carsInDistricts).flatMapToInt(IntStream::of).summaryStatistics().getMax();
                System.out.println(city.waitingTime);
                resultsStream.write("Worst " + simulation.worst.waitingTime + "\n");
                resultsStream.write("Best " + simulation.best.waitingTime + "\n\n");
                resultsStream.flush();
            }
            resultsStream.close();
        } else {
            city = new City(new Random(seed), filename);
        }
//        System.exit(0);

        System.out.println("zaczynam wyswietlanie");
        city.startSimulation();
        System.out.println("zaczynam wyswietlanie2");
        reader = new BufferedReader(new FileReader(filename));
        loadMoreFrames();

        font = new BitmapFont(Gdx.files.internal("bahnschrift.fnt"));
        clockFont = new BitmapFont(Gdx.files.internal("clock-font.fnt"));
        batch = new SpriteBatch();
        if (IS_DEBUG) {
            System.out.println("Quantity of Crossings: " + city.getCrossings().size());
            System.out.println("Quantity of Roads: " + city.getRoads().size());
        }
        setupInitialCameraPositionAndZoom(Settings.GRID_MULTIPLIER);
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

        ScreenUtils.clear(1, 1, 1, 1);
        drawHeatmap();

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
            drawCircle(light.getX(), light.getY(), Settings.CROSSING_RADIUS, Color.BLACK);
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
            drawCircle(car.getX() + offsetX, car.getY() + offsetY, Settings.CAR_RADIUS, car.getStatus() == RideStatus.STARTING ? Color.ORANGE : (car.getStatus() == RideStatus.RIDING ? Settings.CAR_CIRCLE_COLOR : CAR_CIRCLE_COLOR_WAITING));
        }

        drawLinearScale();

        batch.setProjectionMatrix(extendViewport.getCamera().combined);
        batch.begin();
        font.getData().setScale(1.0f);
        font.setColor(Color.BLACK);
        font.draw(batch, "0", scalePositionX - 5,scalePositionY + 2 * scaleSpace);
        font.draw(batch, "100", scalePositionX + Settings.MESH_DISTANCE - 15,scalePositionY + 2 * scaleSpace);
        font.draw(batch, "200 [m]", scalePositionX + 2 * Settings.MESH_DISTANCE - 15,scalePositionY + 2 * scaleSpace);
        font.getData().setScale(4.0f);
        font.draw(batch, "Value of goal function: " + Long.toString(frameToRender.getWaitingTime()), -100,-300);

        long currentSimulationTime = frameToRender.getCurrentTime();
        String hour = String.format("%02d", currentSimulationTime / Settings.TIME_PRECISION / 3600 );
        String minute = String.format("%02d", currentSimulationTime / Settings.TIME_PRECISION / 60 % 60);
        String second = String.format("%02d", currentSimulationTime / Settings.TIME_PRECISION % 60);
        font.draw(batch, "Current time: " + hour + ":" + minute + ":" + second, -100,-100);

        font.draw(batch, "Amount of cars: " + frameToRender.getCars().size(), -100,-200);
        batch.end();
    }

    private void drawHeatmap() {
        if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            if(!IS_OPTIMIZATION){
                worstDistrict = Stream.of(city.carsInDistricts).flatMapToInt(IntStream::of).summaryStatistics().getMax();
            }
            for (int i = 0; i < 9 * Settings.HEATMAP_PRECISION * Settings.GRID_MULTIPLIER; i++) {
                for (int j = 0; j < 16 * Settings.HEATMAP_PRECISION * Settings.GRID_MULTIPLIER; j++) {
                    //Uncomment for white to red transition
                    shapeRenderer.setColor(new Color(1, 1 - city.carsInDistricts[i][j] / (float) worstDistrict, (float)(1 - city.carsInDistricts[i][j]/worstDistrict), 1));
                    //Uncomment for black to red transition
//                    shapeRenderer.setColor(new Color(city.carsInDistricts[i][j] / (float) worstDistrict, 0, 0, 1));
                    shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                    shapeRenderer.rect((float) (j - 1 - (HEATMAP_PRECISION-1)/2.) * Settings.GRID_MULTIPLIER * MESH_DISTANCE / HEATMAP_PRECISION, (float) (i - 1 - (HEATMAP_PRECISION-1)/2.) * Settings.GRID_MULTIPLIER * MESH_DISTANCE / HEATMAP_PRECISION, MESH_DISTANCE/ HEATMAP_PRECISION * GRID_MULTIPLIER, MESH_DISTANCE/ HEATMAP_PRECISION * GRID_MULTIPLIER);
                    shapeRenderer.end();
                }
            }
        }
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
            drawLanes(startX, startY, endX, endY, lanesAmount, new Color(0.9f, 0.9f, 0.9f, 1));
        } else if (lane.getSpeedLimit() == 50) {
            drawLanes(startX, startY, endX, endY, lanesAmount, new Color(0.6f, 0.6f, 0.6f, 1));
        } else {
            drawLanes(startX, startY, endX, endY, lanesAmount, Color.BLACK);
        }
    }

    public void moveCamera() {
        float delta = Gdx.graphics.getDeltaTime();
        String filename = "plikBest.txt";

        if (Gdx.input.isKeyPressed(Input.Keys.O)) {
            ((OrthographicCamera) extendViewport.getCamera()).zoom += .5f * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.P)) {
            ((OrthographicCamera) extendViewport.getCamera()).zoom -= .5f * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (frameIndex < frames.size()) {
                frameToRender = frames.get(frameIndex);
                for (int i = 0; i < frameIndex; i++) {
                    frames.remove(0);
                }
                if (frames.size() < 100) {
                    loadMoreFrames();
                }
                frameIndex = 0;
                frameIndex += speed;
            } else {
                frameIndex = frames.size() - 1;
                frameToRender = frames.get(frameIndex);
                if (false) {
                    city = simulation.best;
                    rand.setSeed(seed);
                    city = new City(rand, city.trafficLightsSettingsList, filename);
                    city.startSimulation();
                    try {
                        reader = new BufferedReader(new FileReader(filename));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    frameIndex = 0;
                }
                frameIndex = 0;
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

    private void loadMoreFrames() {
        try {
            for (int i = 0; i < 100; i++) {
                String json = reader.readLine();
                if (json == null) return;

                frames.add(gson.fromJson(json, Frame.class));
            }
        } catch (JsonSyntaxException | IOException e) {
            e.printStackTrace();
        }
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
        if (Gdx.input.isKeyPressed(Input.Keys.NUM_0)) {
            speed = 1;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS)) {
            speed = Math.max(speed - 1, 1);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.EQUALS)) {
            speed += 1;
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
