/*
 * @author gautham
 */
package system;

import java.rmi.Remote;
import java.rmi.RemoteException;

import api.Result;
import api.Task;

/**
 * This is the remote interface through which different tasks can be executed by the ComputeSpace.
 * These tasks are run using the task's implementation of the execute method and the results are returned to the ComputeSpace
 * Any object that implements this interface can be a remote object.
 */
public interface Computer extends Remote{
	
	/**
	 * A remote method to which different tasks can be submitted by the remote clients.
	 * These tasks are run using the task's implementation of the execute method and the results are returned to the remote client.
	 *
	 * @param <T> the generic type
	 * @param t the t
	 * @return result
	 * @throws RemoteException the remote exception
	 */
	public <T> Result<?> execute(Task<T> t) throws RemoteException;
	
	/**
	 * Stop.
	 *
	 * @throws RemoteException the remote exception
	 */
	public void stop() throws RemoteException;
}
