/*
 * @author gautham
 */
package experiment;

import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import tasks.MandelbrotSetTask;
import api.Result;
import api.Task;

/**
 * The Class MandelbrotSetJob.
 */
public class MandelbrotSetJob implements Job<int[][]> {

	/** The Constant NUM_ROWS_PER_TASK. */
	public static final int NUM_ROWS_PER_TASK = 32;

	/** The left corner coordinates of the square in the complex plane. */
	private double[] leftCornerCoordinates;

	/** The edge length of the square in the complex plane. */
	private double edgeLength;

	/**
	 * The numSquares denotes the number of pixels (n x n squares) representing
	 * the square region in the complex plane.
	 */
	private int numSquares;

	/**
	 * The iteration limit that defines when the representative point of a
	 * region is considered to be in the Mandelbrot set.
	 */
	private int iterationLimit;

	/**
	 * Instantiates a new Mandelbrot set task.
	 * 
	 * @param leftCornerCoordinates
	 *            the left corner coordinates of the square in the complex plane
	 * @param edgeLength
	 *            the edge length of the square in the complex plane
	 * @param numSquares
	 *            the number denoting the number of pixels (n x n squares)
	 *            inside the square region in the complex plane
	 * @param iterationLimit
	 *            the iteration limit that denotes the number of iterations to
	 *            do before deciding that the representative point of a region
	 *            is considered to be in the Mandelbrot set
	 */
	public MandelbrotSetJob(double[] leftCornerCoordinates, double edgeLength,
			int numSquares, int iterationLimit) {
		this.leftCornerCoordinates = leftCornerCoordinates;
		this.edgeLength = edgeLength;
		this.numSquares = numSquares;
		this.iterationLimit = iterationLimit;		
	}

	

	/* (non-Javadoc)
	 * @see experiment.Job#generateTasks(java.util.concurrent.BlockingQueue)
	 */
	@Override
	public void generateTasks(BlockingQueue<Task<?>> taskQueue) {
		System.out.println("Generate Tasks");

		int numTasks = (numSquares / NUM_ROWS_PER_TASK)
				+ ((numSquares % NUM_ROWS_PER_TASK) == 0 ? 0 : 1);
		for (int i = 0; i < numTasks; i++) {
			MandelbrotSetTask task = new MandelbrotSetTask(
					this.leftCornerCoordinates, this.edgeLength,
					this.numSquares, this.iterationLimit, i);
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
	public int[][] collectResults(BlockingQueue<Result<?>> resultQueue) {
		System.out.println("Collect Results");
		int[][] count = new int[numSquares][numSquares];
		int numTasks = (numSquares / NUM_ROWS_PER_TASK)
				+ ((numSquares % NUM_ROWS_PER_TASK) == 0 ? 0 : 1);

		long totalElapsedTime_computer = 0;
		long taskElapsedTime_computer = 0;
		long taskElapsedTime_client = 0;
		long totalElapsedTime_client = 0;

		for (int k = 0; k < numTasks; k++) {
			try {
				Result<int[]> result = (Result<int[]>) resultQueue.take();
				int taskId = result.getTaskId();
				taskElapsedTime_client = System.currentTimeMillis()
						- taskStartTimeMap.get(taskId);
				totalElapsedTime_client += taskElapsedTime_client;

				//System.out.println("task " + (taskId + 1) + ": " + taskElapsedTime_client + " ms");
				int[] returnValue = result.getTaskReturnValue();
				
				int rowNum = NUM_ROWS_PER_TASK * taskId;
				
				taskElapsedTime_computer = result.getTaskRunTime();
				System.out.println("task " + (taskId + 1) + ": " + taskElapsedTime_computer	+ " ms");
				totalElapsedTime_computer += taskElapsedTime_computer;

				for (int j = 0; j < returnValue.length; j++) {
					if (rowNum >= numSquares) {
						System.out.println("i " + rowNum + " Breaking");
						break;
					}
					count[rowNum][(numSquares - (j % numSquares) - 1)] = returnValue[j];
					if ((j + 1) % numSquares == 0) {
						rowNum++;
					}
				}
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		//System.out.println("Average elapsed time as seen by the Client: " + totalElapsedTime_client / numTasks + " ms");
		System.out.println("Average elapsed time as seen by the Computer: " + totalElapsedTime_computer / numTasks + " ms");

		return count;
	}

	

}
