/*
 * @author gautham
 */
package jobs;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Map.Entry;

import tasks.EuclideanTspTask;
import api.Result;
import api.Space;

/**
 * This class represents the entire work involved in solving a Traveling Salesman Problem (TSP), where the cities are points in the 2D Euclidean plane.
 * The job is split into multiple tasks by the clients and then passed to the Compute Space for computation whose results are later obtained and composed to form the solution to the original problem.
 */
public class EuclideanTspJob implements Job<int[]>{

	/** The cities in 2D Euclidean plane that are part of the TSP. */
	private double[][] cities;
	
	/**
	 * Instantiates a new Euclidean TSP task.
	 *
	 * @param cities the cities in 2D Euclidean plane that are part of the TSP; it codes the x and y coordinates of city[i]: cities[i][0] is the x-coordinate of city[i] and cities[i][1] is the y-coordinate of city[i]
	 */
	public EuclideanTspJob(double[][] cities){
		this.cities = cities;		
	}	
	
	/**
	 * Generates multiple tasks from this job. The client decomposes the problem (job), constructing a set of Task objects
	 * The EuclideanTsp job is split into n-1 tasks (n corresponds to the number of cities), representing the (n-1)! factorial permutations that are needed to find the minimal tour
	 * 
	 */
	@Override
	public void generateTasks(Space space) {
		int[] permutation = new int[cities.length - 1];
		for(int i = 0; i < permutation.length; i++){
			permutation[i] = i + 1;
		}
		for(int i = 0; i < cities.length - 1; i++){
			if(i != 0){
				swap(permutation, 0, i);
			}
			EuclideanTspTask task = new EuclideanTspTask(cities, permutation, i);
			long startTime = System.currentTimeMillis();
			taskStartTimeMap.put(i, startTime);
			try{
				space.put(task);
			}
			catch(RemoteException e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Collects results from the Space, composing them into a solution to the original problem.
	 * Each result in the EuclideanTSP job is a map of the minimal tour among the permutations computed by this task and the cost involved for that tour. 	
	 */
	@Override
	public int[] collectResults(Space space) {
		System.out.println("Collect Results");
		int minTour[] = null;
		double minDistance = Double.MAX_VALUE;
		
		long totalElapsedTime_computer = 0;
		long taskElapsedTime_computer = 0;
		long taskElapsedTime_client = 0;
		long totalElapsedTime_client = 0;
		int numTasks = cities.length - 1;
		
		for(int i = 0; i < numTasks; i++){
			try{
				Result<Map<int[], Double>> result = (Result<Map<int[], Double>>) space.take();
				int taskId = result.getTaskId();
				taskElapsedTime_client = System.currentTimeMillis() - taskStartTimeMap.get(taskId);
				totalElapsedTime_client += taskElapsedTime_client;
				//System.out.println("task " + (taskId + 1) + ": " + taskElapsedTime_client + " ms");
				
				taskElapsedTime_computer = result.getTaskRunTime();
				totalElapsedTime_computer += taskElapsedTime_computer;
				System.out.println("task " + (taskId + 1) + ": " + taskElapsedTime_computer + " ms");
				
				Map<int[], Double> minTourMap = result.getTaskReturnValue();
				
				for(Entry<int[], Double> entry : minTourMap.entrySet()){
					double distance = entry.getValue();
					if(distance < minDistance){
						minDistance = distance;
						minTour = entry.getKey();						
					}
				}
			}
			catch(RemoteException re){
				re.printStackTrace();
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
			}
		}
		//System.out.println("Average elapsed time as seen by the Client: " + totalElapsedTime_client / numTasks + " ms");
		System.out.println("Average elapsed time as seen by the Computer: " + totalElapsedTime_computer / numTasks + " ms");
		return minTour;
	}

	/**
	 * Swap.
	 *
	 * @param arr the arr
	 * @param i the i
	 * @param j the j
	 */
	private void swap(int[] arr, int i, int j){
		int temp = arr[i];
		arr[i] = arr[j];
		arr[j] = temp;
	}
	
	/**
	 * Prints the array.
	 *
	 * @param arr the arr
	 */
	private void printArray(int[] arr){
		for(int i : arr){
			System.out.print(i + ", ");
		}
		System.out.println("");
	}
	
}
