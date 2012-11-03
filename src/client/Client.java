/*
 * 
 */
package client;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;

import jobs.EuclideanTspJob;
import jobs.Job;
import jobs.MandelbrotSetJob;

import api.Space;

/**
 * This class represents an RMI client that creates a Job and decomposes it, constructing a set of Task objects
 * The client retrieves results from the Space that are computed for each Task by the Compute Servers, composing them into a solution to the original problem.
 * The RMI client requests a reference to a named remote object of the Compute Space. The reference (the remote object's stub instance) is what the client will use to make remote method calls to the remote object.
 * The client also encompasses the functionality of visualizing the results of the different tasks that are executed on a remote machine.
 */
public class Client {
	
	/** The remote server url. */
	private static String spaceURL;
	
	/** The space. */
	private static Space space;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws MalformedURLException the malformed url exception
	 * @throws RemoteException the remote exception
	 * @throws NotBoundException the not bound exception
	 */
	public static void main(String[] args) throws MalformedURLException, RemoteException, NotBoundException{
		// Construct & set a security manager to allow downloading of classes from a remote codebase
		System.setSecurityManager(new RMISecurityManager());
		String serverDomainName = args[0];
		spaceURL = "//" + serverDomainName + "/" + Space.SERVICE_NAME;
		
		// mJob is an instance of MandelbrotSet Job.
		Job<int[][]> mJob = new MandelbrotSetJob(new double[] {-0.7510975859375, 0.1315680625}, 0.01611,
				1024, 512);		
		
		double[][] cities = { { 1, 1 }, { 8, 1 }, { 8, 8 }, { 1, 8 }, { 2, 2 },
				{ 7, 2 }, { 7, 7 }, { 2, 7 }, { 3, 3 }, { 6, 3 }, {6, 6}, {3, 6} };
		// tspJob is an instance of the EuclideanTsp Job.
		Job<int[]> tspJob = new EuclideanTspJob(cities);
		
		// The RMI client requests a reference to a named remote object. The reference (the remote object's stub instance) is what the client will use to make remote method calls to the remote object.
		space = (Space) Naming.lookup(spaceURL);
		int[][] counts = (int[][]) runTask(mJob);
		int[] tour = (int[]) runTask(tspJob);
		
		// Visualize the results using Java graphics
		long startTime = System.currentTimeMillis();
		Visualizer.visualizeMandelbrotSetTask(counts, 512, 1024);
		long endTime = System.currentTimeMillis();
		System.out.println("Elapsed time for Mandelbrot Set visualization: " + (endTime - startTime) + " ms");
		startTime = System.currentTimeMillis();
		Visualizer.visualizeEuclideanTspTask(tour, cities, 512);
		endTime = System.currentTimeMillis();
		System.out.println("Elapsed time for EuclideanTSP Task visualization: " + (endTime - startTime) + " ms");
		
		stopExecution();

	}
	
	/**
	 * Stop execution of the Space which would in turn stop all the registered Compute Servers and then stop itself.
	 */
	private static void stopExecution(){
		try{
			space.stop();
		}
		catch(RemoteException re){
			System.out.println("Space successfully stopped.");
		}
	}
	
	/**
	 * Runs the given Job by first decomposing it into smaller Tasks and then composes the results of each of the tasks.
	
	 * @param job the Job to be performed. It is either a MandelbrotSet job or EuclideanTSP job
	 * @return object representing the overall result of the job.
	 * @throws RemoteException the remote exception
	 * @throws MalformedURLException the malformed url exception
	 * @throws NotBoundException the not bound exception
	 */
	private static Object runTask(Job<?> job) throws RemoteException, MalformedURLException, NotBoundException
	{		
		/* print task class name;
		* run task 5 times
		* collect/print the execution times
		* compute the average time		
		*/
		System.out.println("Job: " + job.getClass().getName());
		long startTime = System.currentTimeMillis();
		job.generateTasks(space);
		Object obj = job.collectResults(space);
		long endTime = System.currentTimeMillis();
		System.out.println("Elapsed Time: " + (endTime - startTime) + " ms");
		return obj;
	}
	
}
