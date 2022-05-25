// Class for implementing the Greedy Heuristic Algorithm for the Traveling Salesman Problem
// CSC 242-01
// Liam O'Connor
// December 7, 2021

import java.util.ArrayList;
import java.util.Comparator;

public class GreedyAlgorithm {
    private ArrayList<Node> nodes;
    private ArrayList<Edge> edges;
    private ArrayList<Edge> tspPath;

    public GreedyAlgorithm(ArrayList<Node> nodes) {
        setNodes(nodes);

        makeCompleteGraph();
    }

    public void modifyNodes(ArrayList<Node> nodes) {
    	setNodes(nodes);

        makeCompleteGraph();
    }


    // Setters and getters

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    // Make connections between every node created in the Main Class. Put all edges in an array
    public void makeCompleteGraph() {

        edges = new ArrayList<>();

        // Nested for loops will create only one connection between every possible combination of nodes
        for (int i = 0; i < nodes.size() - 1; i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                edges.add(new Edge(i, j));
            }
        }

        // Sort all edges in the array.
        edges.sort(new EdgeComparator());
    }

    // Create ordered list of the edges to be added to the graph, from shortest to longest
    public ArrayList<Edge> calcPath() {
        tspPath = new ArrayList<>();

        // 2D array to keep track of node connections.
        // Second index holds a maximum of two other nodes that the node represented by the first index is connected to.
        int[][] connectedNodes = new int[nodes.size()][2];

        for (int a = 0; a < nodes.size(); a++)
            for (int b = 0; b < 2; b++)
                connectedNodes[a][b] = -1;


        // Go through sorted list of edges, add them to path from shortest to longest
        //   provided each node is only visited twice (path to the node, path from the node).
        //   Stop the for loop when tspPath has accounted for every node.
        for (int i = 0; i < edges.size() && tspPath.size() < nodes.size(); i++) {
            int index1 = edges.get(i).getNode1Index();
            int index2 = edges.get(i).getNode2Index();

            if (connectedNodes[index1][1] < 0 && connectedNodes[index2][1] < 0) {

                // Check to make sure this edge wouldn't make a cycle without every node included
                int checkCycle = hasCycle(index2, index1, connectedNodes);

                if (checkCycle == 0 || checkCycle == nodes.size() - 1){
                    tspPath.add(edges.get(i));

                    // Conditional operator: if the index 0 is empty, fill index 0, else fill index 1
                    connectedNodes[index1][connectedNodes[index1][0] < 0 ? 0 : 1] = index2;
                    connectedNodes[index2][connectedNodes[index2][0] < 0 ? 0 : 1] = index1;
                }
            }

        }

        return tspPath;
    }


    // Calculate the length of an edge
    public double calcEdgeLength(Node nodeA, Node nodeB) {
        return Math.hypot(nodeA.getX() - nodeB.getX(), nodeA.getY() - nodeB.getY());
    }

    public class Edge {
        private int node1Index, node2Index;
        private double length;

        public Edge(int node1Index, int node2Index) {
            this.node1Index = node1Index;
            this.node2Index = node2Index;
            length = calcEdgeLength(nodes.get(node1Index), nodes.get(node2Index));
        }

        public int getNode1Index() {
            return node1Index;
        }

        public int getNode2Index() {
            return node2Index;
        }

        public double getLength() {
            return length;
        }

        @Override
        public String toString() {
            return "From node " + node1Index + " to node " + node2Index + ": " + length;
        }
    }

    public class EdgeComparator implements Comparator<Edge> {
        @Override
        public int compare(Edge edge1, Edge edge2) {
            double length1 = edge1.getLength();
            double length2 = edge2.getLength();

            if (length1 < length2)
                return -1;
            if (length1 > length2)
                return 1;
            return 0;
        }
    }

    /*
     * Recursive function similar to a Depth-First Search.
     * For a given edge (with two nodes) to be added to tspPath, input one node as nodeIndex, another as targetNode.
     * If there is another edge connected to nodeIndex, follow it.
     * If the path leads back to targetNode, then there would be a cycle if we added that edge.
     * The number matters:
     *  If the number of nodes in the cycle is less than the total number of nodes, that's a problem.
     *  If all nodes are included in the cycle, then we have our Hamiltonian path, and we should add the edge.
     *
     * Returns 0 if there is no cycle. If there is a cycle, return the number of nodes in the cycle.
     */
    public int hasCycle(int nodeIndex, int targetNode, final int[][] connectedNodes) {
        // Note:
        // If connectedNodes[nodeIndex][1] had a value, that would mean this node already has two connections,
        //    meaning we wouldn't be in this function to begin with.

        if (connectedNodes[nodeIndex][0] == -1)
            return 0;

        int counter = 1;// For counting how many nodes we pass by.
        return hasCycle(connectedNodes[nodeIndex][0], nodeIndex, targetNode, connectedNodes, counter);
    }

    // Continuing the above function, this time with parentNodeIndex, representing the node that was just handled.
    public int hasCycle(int nodeIndex, int parentNodeIndex, int targetNode, final int[][] connectedNodes, int counter) {
        int nextNode = -1;
        for (int i = 0; i < 2; i++) {
            int connection = connectedNodes[nodeIndex][i]; // for simplifying the references
            // If this node has a connection other than the node we just came from:
            if (connection != parentNodeIndex && connection != -1){
                nextNode = connection;
                if (nextNode == targetNode)
                    return ++counter;
            }
        }

        if (nextNode == -1)
            return 0;
        counter++;
        return hasCycle(nextNode, nodeIndex, targetNode, connectedNodes, counter);
    }
}
