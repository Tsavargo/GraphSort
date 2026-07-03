package hu.tsavargo.graphsort;

import com.google.common.graph.MutableGraph;
import hu.tsavargo.graphsort.algorithm.ExecutionGrouping;
import hu.tsavargo.graphsort.model.FunctionNode;
import hu.tsavargo.graphsort.parser.GraphMLParser;
import java.io.InputStream;
import java.util.*;

public class Main {

    private static final String GRAPH_FILE = "graph3.graphml";

    public static void main(String[] args) {
        System.out.println("Graphsort");

        MutableGraph<FunctionNode> graph = null;
        try {
            graph = loadGraph(GRAPH_FILE);
            System.out.println("Graph successfully loaded!");
            System.out.println("Number of nodes: " + graph.nodes().size());
            System.out.println("Number of edges: " + graph.edges().size());
        } catch (Exception exception) {
            System.err.println(
                "An error occurred while loading the graph file:"
            );
            exception.printStackTrace();
            return;
        }

        try {
            System.out.println(
                "\n--- Running Execution Grouping Algorithm ---"
            );
            ExecutionGrouping<FunctionNode> grouper = new ExecutionGrouping<>();
            List<Set<FunctionNode>> executionLayers = grouper.computeSets(
                graph
            );

            System.out.println("Execution layers calculated successfully:\n");
            for (int i = 0; i < executionLayers.size(); i++) {
                System.out.println("Layer " + (i + 1) + ":");
                for (FunctionNode node : executionLayers.get(i)) {
                    System.out.println(node);
                }
            }
        } catch (Exception exception) {
            System.err.println(
                "An error occurred during the algorithm execution:"
            );
            exception.printStackTrace();
        }
    }

    private static MutableGraph<FunctionNode> loadGraph(String resourceName)
        throws Exception {
        InputStream graphStream = Main.class
            .getClassLoader()
            .getResourceAsStream(resourceName);

        if (graphStream == null) {
            throw new IllegalArgumentException(
                "Error: " +
                    resourceName +
                    " file not found in the resources folder!"
            );
        }

        GraphMLParser parser = new GraphMLParser();
        return parser.parseGraph(graphStream);
    }
}
