/*
 * @author gautham
 */
package jobs;

import java.rmi.RemoteException;

import tasks.MandelbrotSetTask;
import api.Result;
import api.Space;

/**
 * This class helps to produce a visualization of the some part of the Mandelbrot set which is probably one of the most well known fractals, and probably one of the most widely implemented fractal in fractal plotting programs.
 */
public class MandelbrotSetJob implements Job<int[][]> {

	/** The constant represents the number of rows that each task gets to process */
	public static final int NUM_ROWS_PER_TASK = 32;
	
	/** The left corner coordinates of the square in the complex plane. */
	private double[] leftCornerCoordinates;
	
	/** The edge length of the square in the complex plane. */
	private double edgeLength;
	
	/** The numSquares denotes the number of pixels (n x n squares) representing the square region in the complex plane. */
	private int numSquares;
	
	/** The iteration limit that defines when the representative point of a region is considered to be in the Mandelbrot set. */
	private int iterationLimit;
	
	/**
	 * Instantiates a new Mandelbrot set task.
	 *
	 * @param leftCornerCoordinates the left corner coordinates of the square in the complex plane
	 * @param edgeLength the edge length of the square in the complex plane
	 * @param numSquares the number denoting the number of pixels (n x n squares) inside the square region in the complex plane
	 * @param iterationLimit the iteration limit that denotes the number of iterations to do before deciding that the representative point of a region is considered to be in the Mandelbrot set
	 */
	public MandelbrotSetJob(double[] leftCornerCoordinates, double edgeLength, int numSquares, int iterationLimit){
		this.leftCornerCoordinates = leftCornerCoordinates;
		this.edgeLength = edgeLength;
		this.numSquares = numSquares;
		this.iterationLimit = iterationLimit;		
	}
	
	/**
	 * Generates multiple tasks from this job. The client decomposes the problem (job), constructing a set of Task objects
	 * The MandelbrotSet job is decomposed into 'n' number of tasks, with each task taking care of a fixed number of rows as defined by NUM_ROWS_PER_TASK
	 */
	@Override
	public void generateTasks(Space space) {
		System.out.println("Generate Tasks");
		
		// the number of tasks. The below computation works even if the number of rows is not a power of 2.
		int numTasks = (numSquares / NUM_ROWS_PER_TASK) + ((numSquares % NUM_ROWS_PER_TASK) == 0 ? 0 : 1);
		for(int i = 0; i < numTasks; i++){			
			MandelbrotSetTask task = new MandelbrotSetTask(this.leftCornerCoordinates, this.edgeLength, this.numSquares, this.iterationLimit, i);
			long startTime = System.currentTimeMillis();
			taskStartTimeMap.put(i, startTime);
			try{
				space.put(task);
			}
			catch(RemoteException re){
				re.printStackTrace();
			}			
		}		
	}

	/**
	 * Collects results from the Space, composing them into a solution to the original problem.
	 * Each result in the MandelbrotSet job is a one-dimensional array that contains the 'k' values of multiple rows (which correspond to the number of rows that each task works upon)
	 */
	@Override
	public int[][] collectResults(Space space) {
		System.out.println("Collect Results");
		int[][] count = new int[numSquares][numSquares];	
		int numTasks = (numSquares / NUM_ROWS_PER_TASK) + ((numSquares % NUM_ROWS_PER_TASK) == 0 ? 0 : 1);
		
		long totalElapsedTime_computer = 0;
		long taskElapsedTime_computer = 0;
		long taskElapsedTime_client = 0;
		long totalElapsedTime_client = 0;
		
		for(int k = 0; k < numTasks; k++){
			try {				
				Result<int[]> result = (Result<int[]>)space.take();
				int taskId = result.getTaskId();
				taskElapsedTime_client = System.currentTimeMillis() - taskStartTimeMap.get(taskId);
				totalElapsedTime_client += taskElapsedTime_client;
				
				//System.out.println("task " + (taskId + 1) + ": " + taskElapsedTime_client + " ms");
				int[] returnValue = result.getTaskReturnValue();
				//Entry<Integer, int[]> entry = rowMap.entrySet().iterator().next();
				//int taskNum = entry.getKey();
				int rowNum = NUM_ROWS_PER_TASK * taskId;
				//int[] returnValue = entry.getValue();
				
				taskElapsedTime_computer = result.getTaskRunTime();
				System.out.println("task " + (taskId + 1) + ": " + taskElapsedTime_computer + " ms");
				totalElapsedTime_computer += taskElapsedTime_computer;
				
				
				for(int j = 0; j < returnValue.length; j++){
					if(rowNum >= numSquares){
						System.out.println("i " + rowNum + " Breaking");
						break;
					}
					count[rowNum][(numSquares - (j % numSquares) - 1)] = returnValue[j];
					if((j + 1) % numSquares == 0){
						rowNum++ ;
					}
				}				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			catch(InterruptedException ie){
				ie.printStackTrace();
			}
		}		
		//System.out.println("Average elapsed time as seen by the Client: " + totalElapsedTime_client / numTasks + " ms");
		System.out.println("Average elapsed time as seen by the Computer: " + totalElapsedTime_computer / numTasks + " ms");
		
		return count;
	}
	
}
