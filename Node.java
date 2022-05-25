// Node class for travelling salesman problem
// CSC 242-01
// Liam O'Connor, Luis Silva Carillo, Tarinderjit Singh
// December 7, 2021

public class Node {

	private int x; // X coordinate
	private int y;	// Y coordinate

	public Node(int x, int y) { // Constructor using fields
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Node [x=" + x + ", y=" + y + "]";
	}



}
