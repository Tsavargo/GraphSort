package hu.tsavargo.graphsort.parser;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import hu.tsavargo.graphsort.model.FunctionNode;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class GraphMLParser {

    public MutableGraph<FunctionNode> parseGraph(InputStream inputStream)
        throws Exception {
        MutableGraph<FunctionNode> graph = GraphBuilder.directed().build();

        Map<String, FunctionNode> nodeRegistry = new HashMap<>();

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT) {
                switch (reader.getLocalName()) {
                    case "node" -> {
                        String nodeId = reader.getAttributeValue(null, "id");
                        FunctionNode node = new FunctionNode(nodeId, nodeId);
                        nodeRegistry.put(nodeId, node);
                        graph.addNode(node);
                    }
                    case "edge" -> {
                        String sourceId = reader.getAttributeValue(
                            null,
                            "source"
                        );
                        String targetId = reader.getAttributeValue(
                            null,
                            "target"
                        );

                        FunctionNode sourceNode = nodeRegistry.get(sourceId);
                        FunctionNode targetNode = nodeRegistry.get(targetId);

                        if (sourceNode != null && targetNode != null) {
                            graph.putEdge(sourceNode, targetNode);
                        }
                    }
                    default -> {
                    }
                }
            }
        }
        reader.close();
        return graph;
    }
}
