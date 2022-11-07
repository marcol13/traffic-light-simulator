package com.put.urbantraffic;

import lombok.Data;

import java.io.Serializable;

@Data
public class Node implements Serializable {
    private final int x;
    private final int y;
}
