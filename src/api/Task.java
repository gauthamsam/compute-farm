/*
 * @author gautham
 */

package api;

import java.io.Serializable;

/**
 * This defines the interface between the Computer implementation and the work that it needs to do, providing the way to start the work.
 * The client decomposes the original problem into a set of Task objects and they therefore represent the unit of work that is to be done by the Computers.  
 *  
 * @param <T> a type parameter, T, which represents the result type of the task's computation.
 */
public interface Task<T> extends Serializable{
	
	/**
	 * Executes a given task.
	 * This method returns the result of the implementing task's computation and thus its return type is T.
	 *
	 * @return Object of type T
	 */
	Result<T> execute();
}
