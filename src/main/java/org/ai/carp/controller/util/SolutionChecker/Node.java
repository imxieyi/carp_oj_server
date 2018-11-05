package org.ai.carp.controller.util.SolutionChecker;

import java.util.*;

class Node {
    private int name;
    private UndirWeightedGraph graph;
    private Map<Node, Integer> shortestCost = new HashMap<>();
    private boolean alreadyDijkstra = false;
    private Map<Node, Integer> adjacentNodes = new HashMap<>();
    private Map<Node, Integer> demandMap = new HashMap<>();

    void addEdge(Node destination, int weight, int demand) {
        adjacentNodes.put(destination, weight);
        demandMap.put(destination, demand);
    }

    int getShortestPathCost(Node dest) {
        // for outside invoke
        if (!alreadyDijkstra) {
            runDijkstra();
        }
        // System.out.println(this.shortestCost);
        return this.shortestCost.get(dest);
    }

    private void runDijkstra() {
        shortestCost = new HashMap<>(adjacentNodes);
        Set<Node> unsettledNodes = new HashSet<>();
        Set<Node> settledNodes = new HashSet<>();
        this.shortestCost.put(this, 0);
        unsettledNodes.add(this);
        while (!unsettledNodes.isEmpty()) {
            Node curNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(curNode);
            for (Map.Entry<Node, Integer> adjacencyPair :
                    curNode.getAdjacentNodes().entrySet()) {
                Node adjacentNode = adjacencyPair.getKey();
                Integer edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    int newCost = this.getShortestDistance(curNode) + edgeWeight;
                    if (newCost < this.getShortestDistance(adjacentNode)) {
                        this.shortestCost.put(adjacentNode, newCost);
                        adjacentNode.shortestCost.put(this, newCost);
                    }
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(curNode);
        }
        alreadyDijkstra = true;
    }

    private Node getLowestDistanceNode(Set<Node> unsettledNodes) {
        // for dijkstra use only
        Node lowestDistanceNode = null;
        int lowestDistance = Integer.MAX_VALUE;
        for (Node node : unsettledNodes) {
            int nodeDistance = node.getShortestDistance(this);
            if (nodeDistance <= lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }


    Node(int name, UndirWeightedGraph graph) {
        this.name = name;
        this.graph = graph;
    }

//    private int getDistance(Node src){
//        if (this.adjacentNodes.keySet().contains(src)){
//            return this.adjacentNodes.get(src);
//        }else if (src.equals(this)){
//            return 0;
//        }else{
//            return Integer.MAX_VALUE;
//        }
//    }

    private int getShortestDistance(Node src) {
        if (this.shortestCost.keySet().contains(src)) {
            return this.shortestCost.get(src);
        } else if (src.equals(this)) {
            return 0;
        } else {
            return Integer.MAX_VALUE;
        }
    }

    Map<Node, Integer> getAdjacentNodes() {
        return adjacentNodes;
    }

    Map<Node, Integer> getDemandMap() {
        return demandMap;
    }
}
