package org.ai.carp.controller.util.SolutionChecker;

import java.util.*;
import java.util.stream.Collectors;

class UndirWeightedGraph {
    private int numVertices = 0;
    private int capacity = 0;
    private int deport = 0;
    private Set<Node> nodes = new HashSet<>();
    private Map<Integer, Node> nodeMap = new HashMap<>();
    private Set<Edge> edges = new HashSet<>();
    private Set<Edge> tasks = new HashSet<>();
    private Map<Edge, Integer> demandMap = new HashMap<>();
    private Map<Edge, Integer> costMap = new HashMap<>();

    void setNumVertices(int numVertices) {
        this.numVertices = numVertices;
    }

    void addEdge(List<Integer> params) {
        /*
        src, dest, cost, demand
         */
        int cost = params.get(2);
        int demand = params.get(3);
        Node sourceNode = checkNode(params.get(0));
        Node destNode = checkNode(params.get(1));
        addDirectedEdge(sourceNode, destNode, cost, demand);
        addDirectedEdge(destNode, sourceNode, cost, demand);
        Edge newEdge = new Edge(sourceNode, destNode, cost, demand);
        edges.add(newEdge);
        if (demand > 0) {
            demandMap.put(newEdge, demand);
            tasks.add(newEdge);
        }
        costMap.put(newEdge, cost);
    }

    int getTaskDemand(Edge edge) {
        return demandMap.getOrDefault(edge, 0);
    }

    int getEdgeCost(Edge edge) {
        return costMap.getOrDefault(edge, 0);
    }

    private void addDirectedEdge(Node sourceNode, Node destNode, int weight, int demand) {
        sourceNode.addEdge(destNode, weight, demand);
    }

    private Node checkNode(int nodeId) {
        Node node;
        if (nodeMap.containsKey(nodeId)) {
            if (nodeMap.get(nodeId) != null) {
                node = nodeMap.get(nodeId);
            } else {
                node = addNewNode(nodeId);
            }
        } else {
            node = addNewNode(nodeId);
        }
        return node;
    }

    Node getNode(int nodeId) {
        return nodeMap.getOrDefault(nodeId, null);
    }

    private Node addNewNode(int nodeId) {
        Node node = new Node(nodeId, this);
        nodeMap.put(nodeId, node);
        nodes.add(node);
        numVertices += 1;
        return node;
    }


    @Override
    public String toString() {
        String nodesStr = nodes.toString();
        List<String> edgeInfo = nodes.stream().map(node -> {
            StringBuilder edgeStr = new StringBuilder();
            edgeStr.append(String.format("%s: ", node.toString()));
            Map<Node, Integer> adjMap = node.getAdjacentNodes();
            Map<Node, Integer> demandMap = node.getDemandMap();
            for (Map.Entry<Node, Integer> entry : adjMap.entrySet()) {
                Node adjNode = entry.getKey();
                int cost = entry.getValue();
                int demand = demandMap.get(adjNode);
                edgeStr.append(String.format("[%s %d %d]", adjNode.toString(), cost, demand));
            }
            edgeStr.append("\n");
            return edgeStr.toString();
        }).collect(Collectors.toList());
        String result = edgeInfo.toString();

        return String.format("%s\n%s", nodesStr, result);
    }

    void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    int getCapacity() {
        return capacity;
    }

    int getDeport() {
        return deport;
    }

    void setDeport(int deport) {
        this.deport = deport;
        this.checkNode(deport);
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    Set<Edge> getTasks() {
        return tasks;
    }

    public Set<Node> getNodes() {
        return nodes;
    }
}

