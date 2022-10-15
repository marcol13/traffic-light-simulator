package com.put.urbantraffic;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class UrbanTrafficFlowSimulation extends ApplicationAdapter {
    SpriteBatch batch;
    Texture img;
    City city;
    private ShapeRenderer shapeRenderer;
    private static final int NODE_CIRCLE_RADIUS = 15;
    private static final int CORNER_CIRCLE_RADIUS = 7;
    private static final int NODE_OFFSET_LANE = 4;

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        val crossing1 = new Crossing(0, 100, 100, new ArrayList<Light>());
        val crossing2 = new Crossing(1, 200, 200, new ArrayList<Light>());
        val crossing3 = new Crossing(1, 100, 200, new ArrayList<Light>());
        val crossing4 = new Crossing(1, 200, 100, new ArrayList<Light>());
        val crossings = new ArrayList<>(Arrays.asList(crossing1, crossing2, crossing4));

        val lane1 = new Lane(0, crossing1, crossing3, new ArrayList<Direction>());
        val lane2 = new Lane(0, crossing2, crossing4, new ArrayList<Direction>());
        val lane3 = new Lane(0, crossing1, crossing4, new ArrayList<Direction>());
        val lane4 = new Lane(0, crossing3, crossing1, new ArrayList<Direction>());
        val lane5 = new Lane(0, crossing1, crossing2, new ArrayList<Direction>());
        val lane6 = new Lane(0, crossing1, crossing2, new ArrayList<Direction>());
        val lane7 = new Lane(0, crossing2, crossing1, new ArrayList<Direction>());
//        val lanes = new ArrayList<>(Arrays.asList(lane1, lane2, lane3));

        val node1 = new Node(100, 100);
        val node2 = new Node(100, 200);
        val node3 = new Node(200, 200);


        val road1 = new Road(50, new ArrayList<>(Arrays.asList(lane1, lane4)), new ArrayList<>(Arrays.asList(node1, node2)));
        val road2 = new Road(50, new ArrayList<>(Arrays.asList(lane5, lane6, lane7)), new ArrayList<>(Arrays.asList(node1, node2, node3)));

        val roades = new ArrayList<>(Arrays.asList(road2));

        city = new City(
                crossings,
                roades
        );
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 0, 0, 1);
        for (Crossing crossing : city.getCrossings()) {
            drawCircle(crossing.getX(), crossing.getY(), NODE_CIRCLE_RADIUS, Color.BLACK);
        }
        for (Road road : city.getRoads()) {
            int lanesAmount = road.getLaneList().size();
            int startX =  road.getNodeList().get(0).getX();
            int startY =  road.getNodeList().get(0).getY();
            int endX = road.getNodeList().get(0).getX();
            int endY = road.getNodeList().get(0).getY();

            for(Node node: road.getNodeList()){
                if(node.getX() != startX && node.getY() != startY){
                    drawCircle(endX, endY, CORNER_CIRCLE_RADIUS, Color.BLACK);
                    drawLanes(startX, startY, endX, endY, lanesAmount);
                    startX = endX;
                    startY = endY;
                }
                endX = node.getX();
                endY = node.getY();
            }
            drawLanes(startX, startY, endX, endY, lanesAmount);
        }

    }

    public void drawLanes(int startX, int startY, int endX, int endY, int lanesAmount){
        int offsetX, offsetY;
        if(startX == endX){
            offsetX = NODE_OFFSET_LANE;
            offsetY = 0;
        } else{
            offsetX = 0;
            offsetY = NODE_OFFSET_LANE;
        }
        for(int i = 0; i < lanesAmount; i++){
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.line(startX + offsetX * i, startY + offsetY * i, endX + offsetX * i, endY + offsetY * i);
            shapeRenderer.end();
        }
    }

    public void drawCircle(int x, int y, int radius, Color color){
        shapeRenderer.setColor(color);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(x, y, radius);
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }
}
