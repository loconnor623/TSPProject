// Class for implementing the genetic algorithm solution to the Traveling Salesman Problem
// CSC 242-01
// Luis Silva Carillo
// December 7, 2021

import java.util.ArrayList;
import java.util.Random;

public class GeneticAlgorithm {

	private int populationSize; // The size of the population
	private ArrayList<Node> nodes = new ArrayList<Node>(); // Nodes
	private int population[][]; // Matrix to hold different permutations of possible paths (populations)
	private double fitness[]; // fitness score for each population set

	private double bestRecordedDistance = 0; // variable to hold the best recorded distance of a path in the population
	private double relativeBestDistance = 0;
	private int bestPath[]; // the best recorded path
	private int relativePath[];
	private double mutationRate = 1;
	private int populationCounter = 0;

	public GeneticAlgorithm(int populationSize, ArrayList<Node> nodes) { // Constructor using fields, needs a population size and the array list of nodes

		super();
		this.populationSize = populationSize;
		this.nodes = nodes;

		population = new int[populationSize][nodes.size()];
		fitness = new double[populationSize];
		bestPath = new int[nodes.size()];
		relativePath = new int[nodes.size()];

		generatePopulation();
	}

	public void modifyParameters(int populationSize, ArrayList<Node> nodes) {
		this.populationSize = populationSize;
		this.nodes = nodes;

		population = new int[populationSize][nodes.size()];
		fitness = new double[populationSize];
		bestPath = new int[nodes.size()];
		relativePath = new int[nodes.size()];

		generatePopulation();
	}



	public int[] runGeneticAlgorithm() {

		calculateFitness();
		normalizeFitness();
		nextGeneration();

		return bestPath.clone();

	}


	private void generatePopulation() {
		Random rand = new Random();

		for(int i = 0; i < population.length; i++) // Setting up population arrays with initial values in sequence 0,1,2,3,4.... etc
			for(int j = 0; j < population[i].length; j++)
				population[i][j] = j;

		for(int i = 0; i < population.length; i++) { // Shuffle population
			for(int j = 0; j < population[i].length; j++) {
				int randomIndex = rand.nextInt(population[i].length);
				swap(population[i], randomIndex, j);
			}
		}

	}

	private void swap(int[] arr, int x, int y) { // Swaps elements in a integer array
		int temp = arr[x];
		arr[x] = arr[y];
		arr[y] = temp;
	}

	private void calculateFitness() {

		if(bestRecordedDistance == 0) {
			bestRecordedDistance = calcDistance(nodes, population[0]); // Record a starting best distance
			for(int j = 0; j < population[0].length; j++) // best starting Path
				bestPath[j] = population[0][j];
		}


		relativeBestDistance = calcDistance(nodes, population[0]); // Record a starting best distance
		for(int j = 0; j < population[0].length; j++) // best starting Path
			relativePath[j] = population[0][j];

		// Calculating fitness for each population
		for(int i = 0; i < populationSize; i++) {
			double totalDistance = calcDistance(nodes, population[i]);

			if(totalDistance < bestRecordedDistance) { // If new best recorded distance is found the update the best path and the best recorded distance value.
				bestRecordedDistance = totalDistance;
				for(int j = 0; j < population[i].length; j++)
					bestPath[j] = population[i][j];
			}

			if(totalDistance < relativeBestDistance) { // If new best recorded distance is found the update the relative path and the best recorded distance value.
				relativeBestDistance = totalDistance;
				for(int j = 0; j < population[i].length; j++)
					relativePath[j] = population[i][j];
			}

			fitness[i] = 1 / (totalDistance + 1);
		}
	}

	private double calcDistance(ArrayList<Node> nodes, int[] order) {
		double totalDistance = 0;
		Node cityA;
		Node cityB;

		for(int i = 0; i < order.length - 1; i++) {

			int indexCityA = order[i];
			cityA = nodes.get(indexCityA);

			int indexCityB = order[i + 1];
			cityB = nodes.get(indexCityB);

			double edgeDistance = Math.hypot(cityA.getX() - cityB.getX(), cityA.getY() - cityB.getY());
			totalDistance += edgeDistance;
		}

		return totalDistance;
	}

	private void normalizeFitness() {
		double sumFitness = 0;

		for(int i = 0; i < fitness.length; i++) {
			sumFitness += fitness[i];
		}

		for(int i = 0; i < fitness.length; i++) {
			fitness[i] = fitness[i] / sumFitness;
		}
	}

	private void nextGeneration() {

		int newPopulation[][] = new int[populationSize][nodes.size()];

		for(int i = 0; i < population.length; i++) {

			int orderA[] = pickPopulation(population, fitness);
			int orderB[] = pickPopulation(population, fitness);

			int order[] = crossOver(orderA, orderB);

 			mutate(order, mutationRate);
			newPopulation[i] = order.clone();

		}

		population = newPopulation.clone();
		populationCounter++;

	}

	private int[] pickPopulation(int [][] population, double[] fitness) {

		int index = 0;

		double portion = Math.random();

		while(portion > 0) {
			portion = portion - fitness[index];
			index++;
		}
		index--;

		return population[index].clone();
	}

	private int[] crossOver(int[] orderA, int[] orderB) {

		Random rand = new Random();

		int start = rand.nextInt(orderA.length);
		int end = rand.nextInt(orderA.length - start) + start; // int end = rand.nextInt(start, orderA.length);

		ArrayList<Integer> newOrder = new ArrayList<Integer>();

		for(int i = start; i < end; i++)
			newOrder.add(orderA[i]);


		for(int i = 0; i < orderA.length; i++) {
			if(!newOrder.contains(orderB[i])) {
				newOrder.add(orderB[i]);
			}
		}

		int[] intNewOrder = new int[orderA.length];

		for(int i = 0; i < newOrder.size(); i++)
			intNewOrder[i] = newOrder.get(i);

		return intNewOrder;
	}



	private void mutate(int[] order, double mutationRate) {

		Random rand = new Random();

		for(int i = 0; i < order.length; i++) {
			if(Math.random() < mutationRate) {
				int indexA = rand.nextInt(order.length);
				int indexB;
				if((indexA + 1) >= order.length) {
					indexB = indexA -1;
				}
				else
					indexB = indexA + 1;

				swap(order, indexA, indexB);
			}
		}
		reduceMutationRate();
	}

	public double getBestRecordedDistance() {
		return bestRecordedDistance;
	}

	public double getRelativeBestDistance() {
		return relativeBestDistance;
	}

	public int[] getRelativePath() {
		return relativePath.clone();
	}

	private void reduceMutationRate() {
		if(this.mutationRate > 0.1) {
			this.mutationRate -= 0.001;
		}
	}

	public int getPopulationCount() {
		return populationCounter;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public int getNumberOfNodes() {
		return nodes.size();
	}

	public ArrayList<Node> getNodes() {
		return nodes;
	}

	public void resetBestRecordedDistance() {
		bestRecordedDistance = 0;
		populationCounter = 0;
	}
}
