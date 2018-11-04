package org.ai.carp.controller.util.SolutionChecker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class Node {

    private int name;
    private List<Node> shortestPath = new LinkedList<>();
    private Integer weight = Integer.MAX_VALUE;
    private Map<Node, Integer> adjacentNodes = new HashMap<>();
    private Map<Node, Integer> demandMap = new HashMap<>();

    void addEdge(Node destination, int weight, int demand) {
        adjacentNodes.put(destination, weight);
        demandMap.put(destination, weight);
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }

    Node(int name) {
        this.name = name;
    }

    Map<Node, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    Map<Node, Integer> getDemandMap() {
        return demandMap;
    }
}
