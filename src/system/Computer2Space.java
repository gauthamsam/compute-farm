/*
 * @author gautham
 */
package system;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The remote interface that the ComputeServers use to register themselves with the ComputeSpace
 */
public interface Computer2Space extends Remote{
		
	/**
	 * Registers the Computer and creates a ComputerProxy which runs as a separate thread to process the submitted Tasks and to return the Results back to the ComputeSpace
	 *
	 * @param computer the Computer to be registered
	 * @throws RemoteException the remote exception
	 */
	void register(Computer computer) throws RemoteException;
}
