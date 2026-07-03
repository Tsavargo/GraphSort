# GraphSort

GraphSort partitions the nodes of a **directed acyclic graph (DAG)** into **execution layers** - groups of nodes that can all run in parallel because every dependency they need has already been satisfied by nodes in earlier layers.

Think of it as answering: *"Given a set of tasks with dependencies, what is the maximum parallelism I can achieve, and in what order must the layers proceed?"*

## What problem does it solve?

Suppose you have a build pipeline, a data-processing workflow, or a circuit where each node represents a unit of work, and each directed edge `X → Y` means *"X must finish before Y can start."* You want to schedule the work to finish as quickly as possible by running independent tasks concurrently.

GraphSort reads a graph in [GraphML](http://graphml.graphdrawing.org/) format and prints the **layered topological ordering** - each layer is a set of nodes that are mutually independent at that point in the execution.

## Algorithm

The algorithm runs in **O(V + E)** time and has three phases:

### 1. Find the entry point - O(V)

Scan every node and find the one with **in-degree 0** (no incoming edges). This is the root - the only node that can start immediately.

The algorithm requires **exactly one** entry point. If zero or multiple are found, it halts with an error.

### 2. Validate reachability - O(V + E)

Run a classic **BFS** from the entry point and confirm that every node in the graph is visited. If any nodes are unreachable, the graph contains disconnected components and the algorithm reports them.

### 3. Compute execution layers - O(V + E)

This is a **layered variant of Kahn's algorithm** for topological sorting:

1. Record the in-degree of every node.
2. Seed the **first layer** with the entry point.
3. While the current layer is non-empty:
   - Add the layer to the result.
   - For each node in the layer, **decrement the in-degree** of all its successors.
   - A successor whose in-degree drops to **zero** has all its dependencies fulfilled - it joins the **next layer**.
   - Advance to the next layer and repeat.
4. If the total number of processed nodes does not match the graph size, the graph contains a **cycle** - the algorithm reports it.

This produces a list `[Layer₁, Layer₂, …, Layerₙ]` where every node in Layerₖ depends only on nodes from earlier layers, and all nodes within a single layer are independent of each other (they can execute in parallel).

## Project structure

```
src/main/java/hu/tsavargo/graphsort/
├── Main.java                          # Entry point: loads a graph, runs the algorithm, prints layers
├── model/
│   └── FunctionNode.java              # Immutable node record (id, name)
├── parser/
│   └── GraphMLParser.java             # Streaming XML parser for GraphML → Guava MutableGraph
└── algorithm/
    └── ExecutionGrouping.java         # The layered topological sort algorithm
src/main/resources/
├── graph1.graphml                     # Simple 4-node DAG
├── graph2.graphml                     # Linear 5-node chain
└── graph3.graphml                     # Complex 10-node DAG with multiple branches
```

## Dependencies

- **Java 26**
- **[Guava](https://github.com/google/guava)** - for the `MutableGraph` data structure
- **JUnit Jupiter** - for testing
- **Maven** - build system (run with `exec-maven-plugin`)

## Build & Run

```bash
# Compile
mvn compile

# Run (uses graph3.graphml by default)
mvn exec:java

# To change the graph, edit the GRAPH_FILE constant in Main.java
```
