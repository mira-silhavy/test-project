package com.incredible.graph.type;

import java.util.Set;

/** Template JavaDoc for GraphNode */
public interface GraphNode {

    /**
     * Returns all the GraphNodes directly linked to this GraphNode.
     * These are considered to be distance 1 from this node.
     */
    Set<GraphNode> getDirectlyLinkedNodes();
}
