/*
 * @author gautham
 */
package experiment;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;

import tasks.EuclideanTspTask;
import api.Result;
import api.Task;




/**
 * The Class EuclideanTspJob.
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
	
	/* (non-Javadoc)
	 * @see experiment.Job#generateTasks(java.util.concurrent.BlockingQueue)
	 */
	@Override
	public void generateTasks(BlockingQueue<Task<?>> taskQueue) {
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
			try {
				taskQueue.put(task);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/* (non-Javadoc)
	 * @see experiment.Job#collectResults(java.util.concurrent.BlockingQueue)
	 */
	@Override
	public int[] collectResults(BlockingQueue<Result<?>> resultQueue) {
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
				Result<Map<int[], Double>> result = (Result<Map<int[], Double>>) resultQueue.take();
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

