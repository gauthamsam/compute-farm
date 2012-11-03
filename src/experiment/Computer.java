/*
 * @author gautham
 */
package experiment;

import java.rmi.Remote;
import java.rmi.RemoteException;

import api.Result;
import api.Task;

/**
 * The Interface Computer.
 */
public interface Computer extends Remote{
	/** The name under which the RMI registry binds the remote reference. */
	public static final String SERVICE_NAME = "Computer";
	
	/**
	 * Execute.
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
