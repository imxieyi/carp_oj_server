package org.ai.carp.controller.util.SolutionChecker;

class Edge {
    private Node[] nodePair = new Node[2];
    private int cost;
    private int demand;

    Edge(Node a, Node b, int cost, int demand) {
        this.nodePair[0] = a;
        this.nodePair[1] = b;
        this.cost = cost;
        this.demand = demand;
    }

    Edge(Node a, Node b) {
        this.nodePair[0] = a;
        this.nodePair[1] = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge edge = (Edge) o;
        Node[] nodePair2 = edge.nodePair;
        if (nodePair[0] == nodePair2[0] && nodePair[1] == nodePair2[1]) {
            return true;
        } else {
            return (nodePair[0] == nodePair2[1] && nodePair[1] == nodePair2[0]);
        }
    }

    @Override
    public int hashCode() {
        int result;
        result = nodePair[0].hashCode() * nodePair[1].hashCode();
        return result;
    }

}
