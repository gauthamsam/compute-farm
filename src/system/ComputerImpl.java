/*
 * @author gautham
 */
package system;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import api.Result;
import api.Space;
import api.Task;

/**
 * This class enables different tasks to be executed by the Compute Space using its remote reference (proxy)
 * These tasks are run using the task's implementation of the execute method and the results are returned to the Compute Space
 *
 * @author gautham
 */
public final class ComputerImpl extends UnicastRemoteObject implements Computer{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new implementation object for the Computer Interface.
	 *
	 * @throws RemoteException the remote exception
	 */
	public ComputerImpl() throws RemoteException{		
	}

	/**
	 * Different tasks can be submitted to this method
	 * These tasks are run using the task's implementation of the execute method and the results are returned to the remote client.
	 *
	 * @param <T> the generic type
	 * @param t the Task object
	 * @return Result the return value of the Task object's execute method
	 * @throws RemoteException the remote exception
	 */	
	@Override
	public <T> Result<?> execute(Task<T> t) throws RemoteException {
		long startTime = System.currentTimeMillis();
		Result<?> result = (Result<?>) t.execute();
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;
		result.setTaskRunTime(elapsedTime);
		//System.out.println("Computer: Elapsed time for task " + (result.getTaskId() + 1) + ": " + elapsedTime + " ms");
		return result;		
	}
	

	/* (non-Javadoc)
	 * @see system.Computer#stop()
	 */
	@Override
	public void stop() throws RemoteException {
		System.out.println("Received command to stop.");
		System.exit(0);		
	}

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws Exception the exception
	 */
	public static void main(String[] args) throws Exception {		
		String spaceDomainName = args[0];
		
		String spaceURL = "//" + spaceDomainName + "/" + Space.SERVICE_NAME;		
		Computer2Space space = (Computer2Space) Naming.lookup(spaceURL);
		
		Computer computer = new ComputerImpl(); // can throw RemoteException
		space.register(computer);
		System.out.println("Computer ready.");
	}

	
	
	
}
