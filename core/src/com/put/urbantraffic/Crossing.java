package com.put.urbantraffic;

import lombok.Data;

import java.util.List;

@Data
public class Crossing {
    private final long id;
    private final int x;
    private final int y;

    private final List<Light> lights;
}
