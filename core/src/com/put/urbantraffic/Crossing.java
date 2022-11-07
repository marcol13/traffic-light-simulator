package com.put.urbantraffic;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Crossing implements Serializable {
    private final int id;
    private final int x;
    private final int y;

    private final List<Light> lights;
}
