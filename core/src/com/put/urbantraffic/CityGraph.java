package com.put.urbantraffic;

import lombok.Data;
import lombok.val;
import lombok.var;

import java.util.*;

public class CityGraph {
    void generate(City city) {
        val crossings = city.getCrossings();
        val roads = city.getRoads();
        int crossingsAmount = crossings.size();

        val neighbours = new ArrayList<List<CrossingWithTime>>(crossingsAmount);
        for (int i = 0; i < crossingsAmount; i++) {
            neighbours.add(new ArrayList<>());
        }

        // all neighbours
        for (val road : roads) {
            for (val lane : road.getLaneList()) {
                val startCrossingId = lane.getStartCrossing().getId();
                val endCrossing = lane.getEndCrossing();
                float time = getTime(road);
                neighbours.get(startCrossingId).add(new CrossingWithTime(endCrossing.getId(), time));
            }
        }

        // run Dijkstra for each crossing
        for (int i = 0; i < crossingsAmount; i++) {
            dijkstra(crossingsAmount, neighbours, i);
        }

    }

    void dijkstra(int graphSize, List<List<CrossingWithTime>> neighbours, int crossingId) {
        var dist = new float[graphSize];
        Arrays.fill(dist, Float.MAX_VALUE);
        dist[crossingId] = 0;
        var prev = new int[graphSize];
        Arrays.fill(prev, -1);

        val queue = new PriorityQueue<>((Comparator<CrossingWithTime>) (o1, o2) -> Float.compare(o2.time, o1.time));

        for (int i = 0; i < graphSize; i++) {
            queue.add(new CrossingWithTime(i, dist[i]));
        }

        while (!queue.isEmpty()) {
            val u = queue.poll();
            for (CrossingWithTime v : neighbours.get(u.crossingId)) {
                val alt = dist[u.crossingId] + v.time;
                if (alt < dist[v.crossingId]) {
                    dist[v.crossingId] = alt;
                    prev[v.crossingId] = u.crossingId;
                    queue.remove(v);
                    queue.add(new CrossingWithTime(v.crossingId, alt));
                }
            }
        }

        val paths = new ArrayList<List<Integer>>();
        for (int i = 0; i < prev.length; i++) {
            var v = prev[i];
            val list = new ArrayList<Integer>();
            while (v != -1) {
                list.add(0, v);
                v = prev[v];
            }
            list.add(i);
            paths.add(list);
        }
        System.out.println("=== VERTEX " + crossingId + " =====");
        System.out.println(Arrays.toString(dist));
        System.out.println(Arrays.toString(prev));
        System.out.println(paths);

    }

    public static void main(String[] args) {
        new CityGraph().dijkstra(
                4,
                new ArrayList<>(Arrays.asList(
                        new ArrayList<>(Arrays.asList( // 0
                                new CrossingWithTime(1, 2),
                                new CrossingWithTime(2, 5)
                        )),
                        new ArrayList<>(Arrays.asList( // 1
                                new CrossingWithTime(0, 2),
                                new CrossingWithTime(2, 1),
                                new CrossingWithTime(3, 4)
                        )),
                        new ArrayList<>(Arrays.asList( // 2
                                new CrossingWithTime(0, 5),
                                new CrossingWithTime(1, 1),
                                new CrossingWithTime(3, 2)
                        )),
                        new ArrayList<>(Arrays.asList( // 3
                                new CrossingWithTime(1, 4),
                                new CrossingWithTime(2, 2)
                        )))
                ),
                0
        );
    }

    private static float getTime(Road road) {
        return road.getLength() / (float) road.getSpeedLimit();
    }

    @Data
    static class CrossingWithTime {
        final int crossingId;
        final float time;
    }
}
