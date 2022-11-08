package com.put.urbantraffic;

import com.put.urbantraffic.util.Pair;
import lombok.Data;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CityGraph {
    PathWithTime[][] generate(City city) {
        val crossings = city.getCrossings();
        val crossingsAmount = crossings.size();
        val roads = city.getRoads().stream().sorted(Comparator.comparingLong(Road::getId)).collect(Collectors.toList());
        val crossingsMap = crossings.stream()
                .collect(Collectors.toMap(Crossing::getId, item -> item));
        final Map<Pair<Integer, Integer>, Road> roadMap = roads.stream()
                .collect(Collectors.toMap(road -> {
                    val startId = road.getLaneList().get(0).getStartCrossing().getId();
                    val endId = road.getLaneList().get(0).getEndCrossing().getId();
                    return new Pair<>(startId, endId);
                }, item -> item));

        val neighboursLists = getNeighboursLists(roads, crossingsAmount);

        // run Dijkstra for each crossing
        final PathWithTime[][] result = new PathWithTime[crossingsAmount][];
        for (int i = 0; i < crossingsAmount; i++) {
            result[i] = convertIntoCityRepresentation(dijkstra(crossingsAmount, neighboursLists, i), crossingsMap, roadMap);
        }

        int roadsSize = roads.size();
        final PathWithTime[][] resultConvertedToRoads = new PathWithTime[roadsSize][roadsSize];
        for (int i = 0; i < roadsSize; i++) {
            for (int j = 0; j < roadsSize; j++) {
                if (i == j) continue; // there's no path from road to road

                val startingLane = roads.get(i).getLaneList().get(0);
                val endingLane = roads.get(j).getLaneList().get(0);
                val startingCrossing1 = startingLane.getStartCrossing().getId();
                val startingCrossing2 = startingLane.getEndCrossing().getId();
                val endingCrossing1 = endingLane.getStartCrossing().getId();
                val endingCrossing2 = endingLane.getEndCrossing().getId();
                val best = Stream.of(
                        result[startingCrossing1][endingCrossing1],
                        result[startingCrossing1][endingCrossing2],
                        result[startingCrossing2][endingCrossing1],
                        result[startingCrossing2][endingCrossing2]
                ).min((a, b) -> Float.compare(a.time, b.time)).get();
                resultConvertedToRoads[i][j] = best;
            }
        }

        return resultConvertedToRoads;
    }

    private static float getTime(Lane lane) {
        return lane.getLength() / (float) lane.getSpeedLimit();
    }

    Pair<float[], List<List<Integer>>> dijkstra(int graphSize, List<List<CrossingWithTime>> neighbours, int crossingId) {
        float[] dist = new float[graphSize];
        Arrays.fill(dist, Float.MAX_VALUE);
        dist[crossingId] = 0;
        int[] prev = new int[graphSize];
        Arrays.fill(prev, -1);

        val queue = new PriorityQueue<>((Comparator<CrossingWithTime>) (o1, o2) -> Float.compare(o1.time, o2.time));

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

        final List<List<Integer>> paths = getIdPaths(prev);

//        System.out.println("=== VERTEX " + crossingId + " =====");
//        System.out.println(Arrays.toString(dist));
//        System.out.println(Arrays.toString(prev));
//        System.out.println(paths);

        return new Pair<>(dist, paths);
    }

    private List<List<Integer>> getIdPaths(int[] prev) {
        val paths = new ArrayList<List<Integer>>();
        for (int i = 0; i < prev.length; i++) {
            int v = prev[i];
            val list = new ArrayList<Integer>();
            while (v != -1) {
                list.add(0, v);
                v = prev[v];
            }
            list.add(i);
            paths.add(list);
        }
        return paths;
    }

    private PathWithTime[] convertIntoCityRepresentation(
            Pair<float[], List<List<Integer>>> distAndPrevArraysPair,
            Map<Integer, Crossing> crossingsMap,
            Map<Pair<Integer, Integer>, Road> roadsMap
    ) {
        float[] dist = distAndPrevArraysPair.getFirst();
        List<List<Integer>> paths = distAndPrevArraysPair.getSecond();
        val crossingsPath = paths.stream().map(path -> path.stream().map(crossingsMap::get).collect(Collectors.toList())).collect(Collectors.toList());
        val roadsPath = new ArrayList<List<Road>>();

        for (List<Crossing> crossing : crossingsPath) {
            val roads = new ArrayList<Road>();
            for (int i = 0; i < crossing.size() - 1; i++) {
                val firstCrossingId = crossing.get(i).getId();
                val secondCrossingId = crossing.get(i + 1).getId();
                Road road = roadsMap.get(new Pair<>(firstCrossingId, secondCrossingId));
                if (road == null) {
                    road = roadsMap.get(new Pair<>(secondCrossingId, firstCrossingId));
                }
                Objects.requireNonNull(road);
                roads.add(road);
            }
            roadsPath.add(roads);
        }

        val result = new PathWithTime[dist.length];
        for (int i = 0; i < dist.length; i++) {
            result[i] = new PathWithTime(dist[i], paths.get(i), crossingsPath.get(i), roadsPath.get(i));
        }
        return result;
    }

    private List<List<CrossingWithTime>> getNeighboursLists(List<Road> roads, int crossingsAmount) {
        val neighbours = new ArrayList<List<CrossingWithTime>>(crossingsAmount);
        for (int i = 0; i < crossingsAmount; i++) {
            neighbours.add(new ArrayList<>());
        }

        for (val road : roads) {
            for (val lane : road.getLaneList()) {
                val startCrossingId = lane.getStartCrossing().getId();
                val endCrossing = lane.getEndCrossing();
                float time = getTime(lane);
                neighbours.get(startCrossingId).add(new CrossingWithTime(endCrossing.getId(), time));
            }
        }
        return neighbours;
    }

    @Data
    static class CrossingWithTime {
        final int crossingId;
        final float time;
    }

    @Data
    static class PathWithTime {
        final float time;
        final List<Integer> path; // path of ids
        final List<Crossing> crossings; // path of crossings
        final List<Road> roads; // path of roads
    }
}
