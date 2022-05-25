// Nearest Neighbour Algorithm for Traveling Salesman Problem
// CSC 242-01
// Liam O'Connor, Tarinderjit Singh

import java.util.ArrayList;

public class TSPNearestNeighbour {
    ArrayList<Node> nodes;
    ArrayList<Integer> path;

    public TSPNearestNeighbour(ArrayList<Node> nodes) {
        setNodes(nodes);
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<Integer> calcPath() {
        path = new ArrayList<>();
        int currentNode = 0;
        path.add(currentNode);

        int[] visited = new int[nodes.size()];
        visited[0] = 1;
        for (int i = 1; i < visited.length; i++)
            visited[i] = 0;

        int counter = 1; // counts number of nodes added to the path
        int minIndex = -1; // keeps track of the index of the closest node
        double minValue, currentEdge; //

        // While the number of nodes in the path is less than each node that needs to be reached
        while (counter < nodes.size()) {
            minValue = Integer.MAX_VALUE;
            for(int i = 0; i < nodes.size(); i++) {
                if (visited[i] == 0) {
                    currentEdge = calcEdgeLength(nodes.get(currentNode), nodes.get(i));
                     if (currentEdge < minValue) {
                         minValue = currentEdge;
                         minIndex = i;
                     }
                }
            }
            path.add(minIndex);
            currentNode = minIndex;
            visited[minIndex] = 1;
            counter++;
        }

        path.add(0);

        return path;
    }
    /*
    Have an array of all the nodes already visited.
    Visit each node only one time.
    Two options -
    (1) Have a recursive method.
    (2) Have a while loop.

    (1) What would be returned? Is that really necessary?
    (2) Have a variable, currentNode.
    Start with any node. Check the edge length of the other nodes that are unvisited, and keep track
        of the one with the lowest value.
    Add that node to the path.
    Change currentNode to that minimum node, and go through the unvisited nodes again, until all nodes are reached.
    */

    // Calculate the length of an edge
    public double calcEdgeLength(Node nodeA, Node nodeB) {
        return Math.hypot(nodeA.getX() - nodeB.getX(), nodeA.getY() - nodeB.getY());
    }
}
