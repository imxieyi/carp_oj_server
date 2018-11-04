package org.ai.carp.controller.util.SolutionChecker;

import org.ai.carp.controller.util.SolutionChecker.Edge;
import org.ai.carp.model.judge.CARPCase;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class Checker {
    private CARPCase carpCase;
    private String solutionOut;
    private String datasetString ;
    private List<String> solutionTrips = new ArrayList<>();
    private int solutionGivenCost = -1;
    private UndirWeightedGraph graph;

    public Checker(CARPCase carpCase) {
        this.carpCase = carpCase;
        this.solutionOut = carpCase.getStdout();
        this.datasetString = carpCase.getDataset().getData();
        if (!this.parseSolution()) {
            this.carpCase.setValid(false);
            this.carpCase.setReason("Invalid Output Format.");
        }
        this.graph = this.parseGraph();
    }

    private UndirWeightedGraph parseGraph() {
        /*
        You need to ensure the graph file format is correct
         */
        UndirWeightedGraph graph = new UndirWeightedGraph();
        Scanner scanner = new Scanner(this.datasetString);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("VERTICES")) {
                graph.setNumVertices(getField(line, "VERTICES"));
            } else if (line.startsWith("CAPACITY")) {
                graph.setCapacity(getField(line, "CAPACITY"));
            } else if (line.startsWith("DEPOT")) {
                graph.setDeport(getField(line, "DEPOT"));
            } else {
                Pattern p = Pattern.compile("^(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)$");
                Matcher m = p.matcher(line);
                if (m.find()) {
                    List<String> numbers = Arrays.asList(m.group(1), m.group(2), m.group(3), m.group(4));
                    List<Integer> params = numbers.stream()
                            .map(Integer::valueOf).collect(Collectors.toList());
                    graph.addEdge(params);
                }
            }
        }
        scanner.close();
        return graph;
    }

    private boolean parseSolution() {
        boolean valid = false;
        Scanner scanner = new Scanner(this.solutionOut);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("s ")) {
                // solution part
                Pattern p = Pattern.compile("0\\s*,\\s*\\(.*?,0");
                Matcher m = p.matcher(line);
                while (m.find()) {
                    this.solutionTrips.add(m.group());
                    valid = true;
                }

            } else if (line.startsWith("q ")) {
                // quality part
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(line);
                if (m.find()) {
                    this.solutionGivenCost = Integer.parseInt(m.group());
                    valid = true;
                }
            }
        }
        return valid;
    }

    private int getField(String line, String field) {
        Pattern p = Pattern.compile("\\d+");
        Matcher m = p.matcher(line);
        assert m.find();
        return Integer.parseInt(m.group(0));

    }

    public boolean checkSolution() {
        // check tasks first
        Set<Edge> remainTasks = new HashSet<>(this.graph.getTasks());
        Pattern taskEdgePattern = Pattern.compile("\\(\\s*'?\\d+'?\\s*,\\s*'?\\d+'?\\s*\\)");
        List<Node> wholeRoute = new ArrayList<>();
        Node depot = graph.getNode(graph.getDeport());
        for (String trip : this.solutionTrips) {
            // for each trip
            int load = 0;
            int count = 0;
            wholeRoute.add(depot);
            Matcher m = taskEdgePattern.matcher(trip);
            while (m.find()) {
                // for each task
                String taskStr = m.group();
                count += 1;
                Pattern taskNodePattern = Pattern.compile("\\d+");
                Matcher m2 = taskNodePattern.matcher(taskStr);
                int count2 = 0;
                int[] nodeId = new int[2];
                while(m2.find()){
                    nodeId[count2++] = Integer.parseInt(m2.group());
                }
                if (count2!=2){
                    this.carpCase.setValid(false);
                    this.carpCase.setReason("Invalid tasks in some trip.");
                    return false;
                }
                Node nodePair[] = {graph.getNode(nodeId[0]), graph.getNode(nodeId[1])};
                Edge taskEdge = new Edge(nodePair[0], nodePair[1]);
                System.out.println(taskEdge.getDemand());
                if (remainTasks.contains(taskEdge)){
                    remainTasks.remove(taskEdge);
                    int demand = graph.getTaskDemand(taskEdge);
                    load += demand;
                    System.out.println(load);
                    if (load>graph.getCapacity()){
                        this.carpCase.setValid(false);
                        this.carpCase.setReason("Exceeding Capacity Detected.");
                        return false;
                    }
                }else{
                    this.carpCase.setValid(false);
                    this.carpCase.setReason("Invalid tasks in some trip or duplicated tasks.");
                    return false;
                }
                wholeRoute.add(nodePair[0]);
                wholeRoute.add(nodePair[1]);
                // System.out.println(remainTasks.size());
            }
            if (count == 0) {
                this.carpCase.setValid(false);
                this.carpCase.setReason("No valid tasks in some trip.");
                return false;
            }
            wholeRoute.add(depot);
        }

        // TODO: Add up the total cost of the route

        return true;
    }
}
