/*
 * @author gautham
 */
package system;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import api.Result;
import api.Space;
import api.Task;

/**
 * This acts as a channel for passing messages between Client and ComputeServers.
   It defines mechanisms to hold Tasks that are created by the Client jobs, to assign it to the ComputeServers and then process the Result objects.
   
 */
public class SpaceImpl extends UnicastRemoteObject implements Space,
		Computer2Space {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** A blocking queue that stores the Tasks submitted by the Clients. */
	private BlockingQueue<Task<?>> taskQueue; 
	
	/** A blocking queue that stores the Results submitted by the ComputeServers. */
	private BlockingQueue<Result<?>> resultQueue;
	
	/** A mapping between the computerId and the actual Computer Object. */
	private Map<Integer, Computer> computerMap;
	
	/** The computer id. */
	private int computerId;
	
	/**
	 * Instantiates a new space impl.
	 *
	 * @throws RemoteException the remote exception
	 */
	protected SpaceImpl() throws RemoteException {
		super();
		taskQueue = new LinkedBlockingQueue<Task<?>>();
		resultQueue = new LinkedBlockingQueue<Result<?>>();
		computerMap = new HashMap<Integer, Computer>();
	}

	
	/*
	 * Registers the Computer and creates a ComputerProxy which runs as a separate thread to process the submitted Tasks and to return the Results back to the ComputeSpace
	 */
	/* (non-Javadoc)
	 * @see system.Computer2Space#register(system.Computer)
	 */
	@Override
	public void register(Computer computer) throws RemoteException {		
		computerId ++;
		computerMap.put(computerId, computer);
		System.out.println("Registering computer " + computerId);
		ComputerProxy proxy = new ComputerProxy(computer, computerId);
		proxy.start();
	}

	/* (non-Javadoc)
	 * @see api.Space#put(api.Task)
	 */
	@Override
	public void put(Task<?> task) throws RemoteException {
		taskQueue.add(task);

	}

	/* (non-Javadoc)
	 * @see api.Space#take()
	 */
	@Override
	public Result<?> take() throws RemoteException, InterruptedException {
		return resultQueue.take();
	}
	
		
	/* (non-Javadoc)
	 * @see api.Space#stop()
	 */
	@Override
	public void stop() {
		System.out.println("Stopping all the registered Computers.");
		System.out.println("--------------------------------------");
		for(Entry<Integer, Computer> entry : computerMap.entrySet()){
			System.out.println("Stopping computer " + entry.getKey());
			Computer computer = entry.getValue();
			try{
				computer.stop();
			}
			catch(RemoteException e){
				continue;
			}
			computerMap.remove(computer);
		}
		
		System.out.println("--------------------------------------");
		System.out.println("Stopping Space.");
		System.exit(0);
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {
		// Construct & set a security manager to allow downloading of classes
		// from a remote codebase
		System.setSecurityManager(new RMISecurityManager());
		// instantiate a space object
		Space space = new SpaceImpl();
		// construct an rmiregistry within this JVM using the default port
		Registry registry = LocateRegistry.createRegistry(1099);
		// bind space in rmiregistry.
		registry.rebind(Space.SERVICE_NAME, space);
		System.out.println("Space is ready.");

	}

	/*
	 * This thread's run method loops forever, removing tasks from the task queue,
	 * invoking the associated Computer's execute method with the task as its
	 * argument, and putting the returned Result object in the result queue for
	 * retrieval by the client.
	 */
	/**
	 * It represents the remote proxy to the ComputeServer
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
							+ t.getClass().getName() + " from Computer " + this.computerId);
					// Adding the task back to the task queue
					System.out.println("Adding the task back to the task queue to be assigned to another Computer");
					taskQueue.add(t);
					//Thread.currentThread().interrupt();					
					break;
				} catch (InterruptedException e) {
					System.out.println("Interrupted Exception");					
				}
			}		
		}
	}
}
