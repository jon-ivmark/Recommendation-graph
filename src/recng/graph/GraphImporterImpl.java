package recng.graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import recng.cache.Cache;
import recng.cache.CacheBuilder;

/**
 * Imports graphs from csv files.
 *
 * @author jon
 */
public abstract class GraphImporterImpl<T> implements GraphImporter<T> {

    private final GraphBuilder<T> builder;
    private final Map<Integer, EdgeType> edgeTypes =
        new HashMap<Integer, EdgeType>();
    private final Map<Integer, NodeType> nodeTypes =
        new HashMap<Integer, NodeType>();


    /**
     * Avoid using different instances for equivalent keys
     */
    private final Cache<String, NodeID<T>> keyCache =
        new CacheBuilder<String, NodeID<T>>()
        .concurrencyLevel(1).maxSize(50000).build();

    public GraphImporterImpl(GraphBuilder<T> builder, GraphMetadata metadata) {
        this.builder = builder;
        for (EdgeType edgeType : metadata.getEdgeTypes())
            edgeTypes.put(edgeType.ordinal(), edgeType);
        for (NodeType nodeType : metadata.getNodeTypes())
            nodeTypes.put(nodeType.ordinal(), nodeType);
    }

    public Graph<T> importGraph(String file) {
        try {
            return importCSV(file);
        } catch (IOException e) {
            throw new RuntimeException("Import failed", e);
        }
    }

    private Graph<T> importCSV(String file) throws IOException {
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String line = null;
            int nodeCount = 0;
            int edgeCount = 0;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#"))
                    continue;
                String[] fields = line.split(";");
                if (fields.length == 3) {
                    int i = 0;
                    int index = Integer.parseInt(fields[i++]);
                    String node = fields[i++];
                    int type = Integer.parseInt(fields[i++]);
                    NodeType nodeType = nodeTypes.get(type);
                    if (nodeType == null)
                        throw new IllegalArgumentException(
                                                           "Illegal node type for line: "
                                                               + line);

                    int nodeIndex = builder.addNode(getNodeID(node, nodeType));
                    if (nodeIndex != index)
                        throw new IllegalStateException("Invalid node index: " +
                            index);
                    if (nodeCount % 10000 == 0)
                        System.out.println("Imported " + nodeCount + " nodes");
                    nodeCount++;

                } else if (fields.length == 4) {
                    int i = 0;
                    int startNode = Integer.parseInt(fields[i++]);
                    int endNode = Integer.parseInt(fields[i++]);
                    int edgeTypeOrdinal = Integer.parseInt(fields[i++]);
                    EdgeType edgeType = edgeTypes.get(edgeTypeOrdinal);
                    if (edgeType == null)
                        throw new IllegalArgumentException(
                                                           "Illegal edge type for line: "
                                                               + line);
                    float weight =
                        fields.length > i ? Float.parseFloat(fields[i++]) : -1f;
                    builder.addEdge(startNode, endNode, edgeType,
                                    weight);
                    if (edgeCount % 10000 == 0)
                        System.out.println("Imported " + edgeCount + " edges");
                    edgeCount++;
                }
            }
        } finally {
            if (br != null)
                br.close();
            if (fr != null)
                fr.close();
        }
        return builder.build();
    }

    private NodeID<T> getNodeID(String id, NodeType nodeType) {
        if (keyCache.contains(id))
            return keyCache.get(id);
        NodeID<T> key = getNodeKey(id, nodeType);
        keyCache.cache(id, key);
        return key;
    }

    /**
     * Creates a node key from a string.
     *
     */
    protected abstract NodeID<T> getNodeKey(String id, NodeType nodeType);
}
