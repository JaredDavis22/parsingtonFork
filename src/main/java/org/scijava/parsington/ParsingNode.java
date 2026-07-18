package org.scijava.parsington;

import java.util.*;

/**
 * A simple deterministic finite automaton for parsing characters
 *
 * @author Jared Davis
 */


public class ParsingNode<T> {
    // transitions to the next state, in value order
    private final List<ParsingNode<T>> next = new ArrayList<>();
    // when payload is non-null, this node is equivalent to an accept state
    private List<T> payload = null;
    // starting ParsingNode value is not used
    private char value = 0;
    // Use an int comparator to keep the next list in char order.
    private final Comparator<ParsingNode<T>> vComparator = Comparator.comparingInt(ParsingNode::getValue);

    /**
     * Get the next matching state.
     *
     * @param v The character to find in the next list.
     * @return next ParsingNode that matches v or null when v is not found.
     */
    ParsingNode<T> hasValueNext(char v) {
        int ndx = getIndex(v);
        if (ndx >= 0) {
            return next.get(ndx);
        } else {
            return null;
        }
    }

    /**
     * Get index value of next list for char v.
     * Performs a binarySearch of list next looking for v.
     *
     * @param v The character to index in the next list
     * @return The index of v in the next list, if it is contained in the next list;
     * otherwise, (-(insertion point) - 1).
     */
    int getIndex(char v) {
        ParsingNode<T> pn = new ParsingNode<>();
        pn.value = v;
        return Collections.binarySearch(next, pn, vComparator);
    }

    /**
     * Add/update a ParsingNode
     *
     * @param v      The character to add/update.
     * @param action when non-null this defines the character to have an accept state.
     * @return The ParsingNode that was added.
     */

    ParsingNode<T> addNextValue(char v, T action) {
        int ndx = getIndex(v);
        ParsingNode<T> n;
        if (ndx >= 0) {
            n = next.get(ndx);
        } else {
            n = new ParsingNode<>();
            n.value = v;
            next.add(-ndx - 1, n);
        }
        if (action != null) {
            if (n.payload == null) {
                n.payload = new ArrayList<>(6);
            }
            n.payload.add(action);
        }
        return n;
    }

    /**
     * Get the current value
     *
     * @return value of this ParsingNode
     */
    char getValue() {
        return value;
    }

    /**
     * Get the current payload list
     *
     * @return payload of this ParsingNode
     */
    public List<T> getPayload() {
        return payload;
    }


    /**
     * Generate a Graphviz dot file starting at this node
     *
     * @return text for a dot graph
     */
    public String emitDotGraph() {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph {\n ");
        Set<ParsingNode<T>> visited = new HashSet<>();
        Queue<ParsingNode<T>> queue = new LinkedList<>();
        // visit all the following nodes and their children
        queue.add(this);
        visited.add(this);
        while (!queue.isEmpty()) {
            ParsingNode<T> currentNode = queue.poll();
            for (ParsingNode<T> nextState : currentNode.next) {
                if (!visited.contains(nextState)) {
                    visited.add(nextState);
                    queue.add(nextState);
                }
            }
        }
        // define the graph labels for the nodes
        for (ParsingNode<T> node : visited) {
            sb.append("n");
            sb.append(node.hashCode());
            sb.append(" [label=\"");
            sb.append((node.value == 0) ? "START" : nodeValueEscaped(node.value));
            sb.append("\n");
            sb.append((node.payload == null) ? "" : "pay=" + node.payload.size());
            sb.append("\"];\n");
        }

        sb.append("\n");
        // define the connections between nodes
        for (ParsingNode<T> node : visited) {
            for (ParsingNode<T> nextState : node.next) {
                sb.append("n");
                sb.append(node.hashCode());
                sb.append(" -> ");
                sb.append("n");
                sb.append(nextState.hashCode());
                sb.append(";\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * node char formatting helper for the Graphviz graph
     *
     * @param v char to format for graph usage
     * @return Graphviz safe String.
     */
    protected String nodeValueEscaped(char v) {
        if (v == 0) return "";
        if (v == '\\') return "\\\\";
        return String.valueOf(v);
    }

}
