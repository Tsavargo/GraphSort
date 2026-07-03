package hu.tsavargo.graphsort.model;

public record FunctionNode(String id, String name) {
    public String toString() {
        return name;
    }
}
