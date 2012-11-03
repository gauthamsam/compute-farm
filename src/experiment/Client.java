/*
 * @author gautham
 */
package experiment;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import api.Result;
import api.Task;

import client.Visualizer;




/**
 * This class represents an RMI client that can execute tasks on a remote computer.
 * The RMI client requests a reference to a named remote object. The reference (the remote object's stub instance) is what the client will use to make remote method calls to the remote object.
 * The client also encompasses the functionality of visualizing the results of the different tasks that are executed on a remote machine.
 */
public class Client {
	
	/** The remote server url. */
	private static String serverURL;
		
	/** The task queue. */
	private BlockingQueue<Task<?>> taskQueue;
	
	/** The result queue. */
	private BlockingQueue<Result<?>> resultQueue;

	/**
	 * Instantiates a new client.
	 */
	public Client(){
		taskQueue = new LinkedBlockingQueue<Task<?>>();
		resultQueue = new LinkedBlockingQueue<Result<?>>();
	}
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		// Construct & set a security manager to allow downloading of classes from a remote codebase
		System.setSecurityManager(new RMISecurityManager());
		String serverDomainName = args[0];
		serverURL = "//" + serverDomainName + "/" + Computer.SERVICE_NAME;
		
		Job<int[][]> mJob = new MandelbrotSetJob(new double[] {-0.7510975859375, 0.1315680625}, 0.01611,
				1024, 512);		
		
		double[][] cities = { { 1, 1 }, { 8, 1 }, { 8, 8 }, { 1, 8 }, { 2, 2 },
				{ 7, 2 }, { 7, 7 }, { 2, 7 }, { 3, 3 }, { 6, 3 }, {6, 6}, {3, 6} };
		// t2 is an instance of the EuclideanTspTask
		Job<int[]> tspJob = new EuclideanTspJob(cities);
		
		// The RMI client requests a reference to a named remote object. The reference (the remote object's stub instance) is what the client will use to make remote method calls to the remote object.
		Computer computer = (Computer) Naming.lookup(serverURL);
		Client client = new Client();
		client.registerComputer(computer);
		
		int[][] counts = (int[][]) client.runTask(mJob);
		int[] tour = (int[]) client.runTask(tspJob);
		
		// Visualize the results using Java graphics
		long startTime = System.currentTimeMillis();
		Visualizer.visualizeMandelbrotSetTask(counts, 512, 1024);
		long endTime = System.currentTimeMillis();
		System.out.println("Elapsed time for Mandelbrot Set visualization: " + (endTime - startTime) + " ms");
		startTime = System.currentTimeMillis();
		Visualizer.visualizeEuclideanTspTask(tour, cities, 512);
		endTime = System.currentTimeMillis();
		System.out.println("Elapsed time for EuclideanTSP Task visualization: " + (endTime - startTime) + " ms");
		

	}
	
	/**
	 * Register computer.
	 *
	 * @param computer the computer
	 */
	public void registerComputer(Computer computer) {
		ComputerProxy proxy = new ComputerProxy(computer, 1);
		proxy.start();
	}
	
	/**
	 * Runs the given task by calling the execute method on the remote server.
	 * The execution is repeated five times and the round trip time involved in the remote execution is calculated.
	 *
	 * @param job the job
	 * @return object
	 * @throws RemoteException the remote exception
	 * @throws MalformedURLException the malformed url exception
	 * @throws NotBoundException the not bound exception
	 */
	private Object runTask(Job<?> job) throws RemoteException, MalformedURLException, NotBoundException
	{		
		/* print task class name;
		* run task 5 times
		* collect/print the execution times
		* compute the average time		
		*/
		System.out.println("Job: " + job.getClass().getName());
		long startTime = System.currentTimeMillis();
		
		job.generateTasks(taskQueue);
		Object obj = job.collectResults(resultQueue);
		long endTime = System.currentTimeMillis();
		System.out.println("Elapsed Time: " + (endTime - startTime) + " ms");
		return obj;
	}
	
	/*
	 * This thread's run method loops forever, removing tasks from a queue,
	 * invoking the associated Computer's execute method with the task as its
	 * argument, and putting the returned Result object in a data structure for
	 * retrieval by the client.
	 */
	/**
	 * The Class ComputerProxy.
	 */
	private class ComputerProxy extends Thread {
		
		/** The computer. */
		private Computer computer;
		
		/** The computer id. */
		private int computerId;

		/**
		 * Instantiates a new computer proxy.
		 *
		 * @param c the c
		 * @param computerId the computer id
		 */
		public ComputerProxy(Computer c, int computerId) {
			this.computer = c;
			this.computerId = computerId;
		}

		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			Task<?> t = null;
			while (true) {
				try {
					t = taskQueue.take();
					Result<?> result = computer.execute(t);
					resultQueue.add(result);
				} catch (RemoteException e) {
					/*
					 * The Space accommodates faulty computers: If a computer
					 * that is running a task returns a RemoteException, the
					 * task is assigned to another computer.
					 */
					System.out.println("Remote Exception while executing task "
							+ t.getClass().getName() + " from Computer "
							+ this.computerId);
					// Adding the task back to the task queue
					System.out
							.println("Adding the task back to the task queue to be assigned to another Computer");
					taskQueue.add(t);
					// Thread.currentThread().interrupt();
					break;
				} catch (InterruptedException e) {
					System.out.println("Interrupted Exception");
				}
			}
		}
	}
	
}
