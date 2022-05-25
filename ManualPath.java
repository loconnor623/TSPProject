// Class for implementing the Manual Path in the Traveling Salesman Problem
// CSC 242-01
// Liam O'Connor
// December 7, 2021

import java.util.ArrayList;

public class ManualPath {
    ArrayList<Node> nodes;
    ArrayList<Integer> path;

    public ManualPath(ArrayList<Node> nodes) {
        setNodes(nodes);
        path = new ArrayList<>();
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Integer> getPath() {
        return path;
    }

    // Calculate the length of an edge
    public double calcEdgeLength(Node nodeA, Node nodeB) {
        return Math.hypot(nodeA.getX() - nodeB.getX(), nodeA.getY() - nodeB.getY());
    }
}
