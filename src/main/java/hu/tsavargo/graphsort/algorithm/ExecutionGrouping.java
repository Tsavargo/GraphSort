package hu.tsavargo.graphsort.algorithm;

import com.google.common.graph.Graph;
import java.util.*;

public class ExecutionGrouping<NodeType> {

    // O(V + E)
    public List<Set<NodeType>> computeSets(Graph<NodeType> graph) {
        if (graph.nodes().isEmpty()) {
            return Collections.emptyList();
        }

        NodeType startNode = findEntryPoint(graph);

        validateReachability(graph, startNode);

        return calculateExecutionLayers(graph, startNode);
    }

    // O(V): Iterates through the nodes
    private NodeType findEntryPoint(Graph<NodeType> graph) {
        NodeType startNode = null;
        for (NodeType currentNode : graph.nodes()) {
            if (graph.inDegree(currentNode) == 0) {
                if (startNode == null) {
                    startNode = currentNode;
                } else {
                    throw new IllegalStateException(
                        "Error: Multiple entry points detected in the graph."
                    );
                }
            }
        }

        if (startNode == null) {
            throw new IllegalStateException(
                "Error: No entry point (root node) found in the graph."
            );
        }

        return startNode;
    }

    // O(V + E): Classic BFS
    private void validateReachability(
        Graph<NodeType> graph,
        NodeType startNode
    ) {
        Set<NodeType> visited = new HashSet<>();
        Deque<NodeType> queue = new ArrayDeque<>();

        visited.add(startNode);
        queue.add(startNode);

        while (!queue.isEmpty()) {
            NodeType node = queue.poll();
            for (NodeType successor : graph.successors(node)) {
                if (visited.add(successor)) {
                    queue.add(successor);
                }
            }
        }

        if (visited.size() != graph.nodes().size()) {
            Set<NodeType> unreachable = new HashSet<>(graph.nodes());
            unreachable.removeAll(visited);
            throw new IllegalStateException(
                "Error: Graph is not fully reachable from the entry point. " +
                    "Unreachable or disconnected nodes: " +
                    unreachable
            );
        }
    }

    // O(V + E): Kahn algorithm
    private List<Set<NodeType>> calculateExecutionLayers(
        Graph<NodeType> graph,
        NodeType startNode
    ) {
        List<Set<NodeType>> executionLayers = new ArrayList<>();
        Map<NodeType, Integer> inDegrees = new HashMap<>();

        // O(V): Iterates through the nodes
        for (NodeType node : graph.nodes()) {
            inDegrees.put(node, graph.inDegree(node));
        }

        Set<NodeType> currentLayer = new HashSet<>();
        currentLayer.add(startNode);

        int processedNodesCount = 0;

        // O(V + E)
        while (!currentLayer.isEmpty()) {
            executionLayers.add(currentLayer);
            processedNodesCount += currentLayer.size();
 
            Set<NodeType> nextLayer = new HashSet<>();
            for (NodeType node : currentLayer) {
                // V iteration
                for (NodeType successor : graph.successors(node)) {
                    // E iteration
                    int remainingInDegree = inDegrees.get(successor) - 1;
                    inDegrees.put(successor, remainingInDegree);

                    if (remainingInDegree == 0) {
                        nextLayer.add(successor);
                    }
                }
            }
            currentLayer = nextLayer;
        }

        if (processedNodesCount != graph.nodes().size()) {
            throw new IllegalStateException(
                "Error: Graph contains a cycle or unresolved dependencies."
            );
        }

        return executionLayers;
    }
}
