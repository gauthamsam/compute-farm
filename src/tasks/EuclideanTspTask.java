/*
 * @author gautham
 */
package tasks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import api.Result;
import api.Task;

/**
 * This class represents a unit of task involved in solving a Traveling Salesman Problem (TSP), where the cities are points in the 2D Euclidean plane.
 */
public final class EuclideanTspTask implements Task<Map<int[], Double>>{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The cities in 2D Euclidean plane that are part of the TSP. */
	private double[][] cities;
	
	/** The taskPermutation denotes the permutation of cities given to this task */
	private int[] taskPermutation;
	
	/** The task id. */
	private int taskId;
	
	/**
	 * Instantiates a new Euclidean TSP task.
	 *
	 * @param cities the cities in 2D Euclidean plane that are part of the TSP; it codes the x and y coordinates of city[i]: cities[i][0] is the x-coordinate of city[i] and cities[i][1] is the y-coordinate of city[i]
	 */
	public EuclideanTspTask(double[][] cities){
		this.cities = cities;		
	}
	
	/**
	 * Instantiates a new euclidean tsp task.
	 *
	 * @param cities the cities
	 * @param permutation the original permutation of the cities given to this Task
	 * @param taskId the task id
	 */
	public EuclideanTspTask(double[][] cities, int[] permutation, int taskId){
		this.cities = cities;
		this.taskPermutation = permutation;	
		this.taskId = taskId;
	}
	/**
	 * Executes the Euclidean TSP Task.
	 * The method of finding the minimal distance tour is efficient; the program will fix one point as the starting point and iterate over all the remaining permutations of the cities, and returns a permutation of least cost. 
	 * @return a map of the minimal tour among the permutations computed by this task and the cost involved for that tour. 	
	 */
	@Override
	public Result<Map<int[], Double>> execute() {
		// tour lists the order of the cities of a minimal distance tour.
		Result<Map<int[], Double>> result = new Result<Map<int[], Double>>();		
		result.setTaskId(this.taskId);
		// Variable to hold the minimum distance between all the cities.
		double minDistance = Double.MAX_VALUE;
		// Array to hold the nth permutation. The first element is fixed and the permutation of the remaining elements are computed
		int[] permutation = Arrays.copyOfRange(this.taskPermutation, 1, this.taskPermutation.length);
		// set n = 2 since the first permutation is not needed.
		int n = 2;
		
		// a map of the minimal tour among the permutations computed by this task and the cost involved for that tour.
		Map<int[], Double> minTourMap = new HashMap<int[], Double>(1);
		
		int[] tour = new int[cities.length];
		
		// currentDistance holds the distance traveled for the given permutation of the cities
		double currentDistance = 0; 
		double initDistance = calculateDistance(cities[0], cities[this.taskPermutation[0]]);
		
		while(true){			
			permutation = getPermutation(permutation, n++);
			if(permutation == null){ // All the permutations have been computed. No more left.
				break;
			}
			currentDistance = initDistance;
			currentDistance += calculateDistance(cities[this.taskPermutation[0]], cities[permutation[0]]);
			for(int j = 0; j < permutation.length - 1; j++){
				currentDistance += calculateDistance(cities[permutation[j]], cities[permutation[j + 1]]);				
			}
			currentDistance += calculateDistance(cities[permutation[permutation.length - 1]], cities[0]);
			
			if(minDistance > currentDistance){
				minDistance = currentDistance;
				//minPerm = Arrays.copyOf(permutation, permutation.length);
				copyArray(permutation, tour, 2);
			}
			
		}		
		tour[1] = this.taskPermutation[0];
		minTourMap.put(tour, minDistance);
		result.setTaskReturnValue(minTourMap);
		
		return result;
	}
	
	/**
	 * Copy array.
	 *
	 * @param source the source
	 * @param dest the dest
	 * @param startIndexOfDest the start index of dest
	 */
	private void copyArray(int[] source, int[] dest, int startIndexOfDest){
		for(int i = 0; i < source.length; i++){
			dest[startIndexOfDest++] = source[i];
		}
	}
	/**
	 * Generate the nth permutations in lexicographic order.
	 *
	 * @param permutation the (n-1)th permutation array; when n = 1, it is just 0 to permutation length - 1
	 * @param n the nth permutation to be computed
	 * @return the nth permutation
	 */
	private int[] getPermutation(int[] permutation, int n) {
		if(n == 1){ // the first permutation is just 0 to permutation length - 1 (0 to no. of cities - 1)
			for (int i = 0; i < permutation.length; i++)
		    	permutation[i] = i;
			return permutation;
		}
		int k, l;
        // Find the largest index k such that a[k] < a[k + 1]. If no such index exists, the permutation is the last permutation.
        for (k = permutation.length - 2; k >=0 && permutation[k] >= permutation[k+1]; k--);
        if(k == -1){
        	return null;
        }
        // Find the largest index l such that a[k] < a[l]. Since k + 1 is such an index, l is well defined and satisfies k < l.
        for (l = permutation.length - 1; permutation[k] >= permutation[l]; l--);
        // Swap a[k] with a[l].
        swap(permutation, k, l);
        // Reverse the sequence from a[k + 1] up to and including the final element a[n].
        for (int j = 1; k + j < permutation.length - j; j++){
        	swap(permutation, k + j, permutation.length - j);
        }
        return permutation;
	}
	
	/**
	 * Swap the elements of the array in place.
	 *
	 * @param arr the array
	 * @param i the ith position
	 * @param j the jth position
	 */
	private void swap(int[] arr, int i, int j){
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}
	
	/**
	 * Prints the array.
	 *
	 * @param arr the array
	 */
	private void printArray(int[] arr){
		for(int i : arr){
			System.out.print(i + ", ");
		}
		System.out.println("");
	}
	
	/**
	 * Calculate the Euclidean distance.
	 *
	 * @param pointA the starting point
	 * @param pointB the ending point
	 * @return distance the distance between the points
	 */
	private double calculateDistance(double[] pointA, double[] pointB){		
		double temp1 = Math.pow((pointA[0] - pointB[0]), 2);
		double temp2 = Math.pow((pointA[1] - pointB[1]), 2);
		double distance = Math.sqrt(temp1 + temp2);
		return distance;
	}

	/**
	 * Gets the distance.
	 *
	 * @param permutation the permutation
	 * @return the distance
	 */
	private double getDistance(int[] permutation){
		double currentDistance = 0;
		
		currentDistance = calculateDistance(cities[this.taskPermutation[0]], cities[permutation[0]]);
		for(int j = 0; j < permutation.length - 1; j++){
			currentDistance += calculateDistance(cities[permutation[j]], cities[permutation[j + 1]]);				
		}
		currentDistance += calculateDistance(cities[permutation[permutation.length - 1]], cities[this.taskPermutation[0]]);
		return currentDistance;
	}
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		double[][] cities = { { 1, 1 }, { 8, 1 }, { 8, 8 }, { 1, 8 }, { 2, 2 },
				{ 7, 2 }, { 7, 7 }, { 2, 7 }, { 3, 3 }, { 6, 3 }, {6, 6}, {3, 6} };
		int[] cityOrder = {10, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 11};
		EuclideanTspTask task = new EuclideanTspTask(cities, cityOrder, 0);
		int[] arr = {6, 2, 1, 5, 9, 0, 4, 8, 11, 7, 3};
		System.out.println(task.getDistance(arr));
//		long startTime = System.currentTimeMillis();
//		int[] tour = null;task.execute();
//		System.out.println("Time: " + (System.currentTimeMillis() - startTime));
//		System.out.println("Tour");
//		task.printArray(tour);		
	}
}
