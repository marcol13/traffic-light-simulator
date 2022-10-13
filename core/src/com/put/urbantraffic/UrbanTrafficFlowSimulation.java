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

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();
        img = new Texture("badlogic.jpg");

        val crossing1 = new Crossing(0, 100, 100, new ArrayList<Light>());
        val crossing2 = new Crossing(1, 200, 200, new ArrayList<Light>());
        val lane = new Lane(0, crossing1, crossing2, 50, new ArrayList<Direction>());

        city = new City(
                new ArrayList<>(Arrays.asList(crossing1, crossing2)),
                new ArrayList<>(Collections.singletonList(lane))
        );
    }

    @Override
    public void render() {
        ScreenUtils.clear(1, 0, 0, 1);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Crossing crossing : city.getCrossings()) {
            shapeRenderer.circle(crossing.getX(), crossing.getY(), 2);
        }
        shapeRenderer.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }
}
