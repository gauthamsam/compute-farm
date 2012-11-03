/*
 * 
 */
package tasks;

import jobs.MandelbrotSetJob;
import api.Result;
import api.Task;

/**
 * This class represents a unit of work that helps to produce a visualization of the some part of the Mandelbrot set which is probably one of the most well known fractals, and probably one of the most widely implemented fractal in fractal plotting programs.
 */
public final class MandelbrotSetTask implements Task<int[]>{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The left corner coordinates of the square in the complex plane. */
	private double[] leftCornerCoordinates;
	
	/** The edge length of the square in the complex plane. */
	private double edgeLength;
	
	/** The numSquares denotes the number of pixels (n x n squares) inside the square region in the complex plane. */
	private int numSquares;
	
	/** The iteration limit that defines when the representative point of a region is considered to be in the Mandelbrot set. */
	private int iterationLimit;
	
	/** The task id. */
	private int taskId;
		
	/**
	 * Instantiates a new Mandelbrot set task.
	 *
	 * @param leftCornerCoordinates the left corner coordinates of the square in the complex plane
	 * @param edgeLength the edge length of the square in the complex plane
	 * @param numSquares the number denoting the number of pixels (n x n squares) inside the square region in the complex plane
	 * @param iterationLimit the iteration limit that denotes the number of iterations to do before deciding that the representative point of a region is considered to be in the Mandelbrot set
	 * @param taskId the task id
	 */
	
	public MandelbrotSetTask(double[] leftCornerCoordinates, double edgeLength, int numSquares, int iterationLimit, int taskId){
		this.leftCornerCoordinates = leftCornerCoordinates;
		this.edgeLength = edgeLength;
		this.numSquares = numSquares;
		this.iterationLimit = iterationLimit;
		this.taskId = taskId;		
	}
	/**
	 * This method uses a simple algorithm for drawing a picture of the Mandelbrot set. 
	 * The region of the complex plane that one is considering is subdivided into a certain number of squares (pixels).
     * To color any such pixel, let c be the lower leftmost point of that pixel. We now iterate till we reach the iteration limit, checking at each step whether the orbit point has modulus larger than 2.
       When this is the case, we know that  does not belong to the Mandelbrot set, and we color our pixel according to the number of iterations used to find out. Otherwise, we keep iterating up to a fixed number of steps, after which we decide that our parameter is "probably" in the Mandelbrot set and color the pixel black.
	 * 
	 * @return count array, where count[i] = k, where k defines whether the representative point in the region is part of the Mandelbrot set or not.
	 * The array contains the k values of multiple rows which correspond to the number of rows that each task works upon.
	 * 
	 */
	@Override
	public Result<int[]> execute() {
		Result<int[]> result = new Result<int[]>();
		result.setTaskId(this.taskId);
		
		int[] count = new int[MandelbrotSetJob.NUM_ROWS_PER_TASK * numSquares];
		int countIndex = 0;
		int index = taskId * MandelbrotSetJob.NUM_ROWS_PER_TASK;
		for(int i = 0; i < MandelbrotSetJob.NUM_ROWS_PER_TASK; i++){
			for(int j = 0; j < numSquares; j++){
				//System.out.println("{" + rowNum + ", " + j + "}");
				if((index + i) >= numSquares){
					System.out.println("(index + i) " + (index + i) + " Breaking");
					break;
				}
				int k = getK(index + i, j);				
				count[countIndex++] = k;			
			}
		}
		result.setTaskReturnValue(count);
		return result;
	}
	
	/**
	 * Gets the value of k for the representative point in the region[i][j].
	 *
	 * @param i the i
	 * @param j the j
	 * @return k
	 */
	private int getK(int i, int j){
		int k = 1;
		
		double c_real = leftCornerCoordinates[0] + (i * edgeLength / numSquares);
		double c_imag = leftCornerCoordinates[1] + (j * edgeLength / numSquares);		
		double real = 0, imag = 0;
		
		while( (real * real + imag * imag < 4) && k < iterationLimit){		
			double temp = real * real - imag * imag + c_real;
			imag = 2 * real * imag + c_imag;
			real = temp;
			k++;
		}
		return k;
	}
		
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception{
		MandelbrotSetTask task = new MandelbrotSetTask(new double[] {  -0.7510975859375, 0.1315680625 }, 0.01611,
				1024, 512, 1);
		//int[][] counts = task.execute();
		int[] tour = {1,2,3,4,5,6,7,8,9,0};
		double[][] cities = { { 6, 3 }, { 2, 2 }, { 5, 8 }, { 1, 5 }, { 1, 6 },
				{ 2, 7 }, { 2, 8 }, { 6, 5 }, { 1, 3 }, { 6, 6 } };
		//Visualizer.visualize(counts, 1024, tour, cities);
	}
	
}
