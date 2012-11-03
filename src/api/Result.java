/*
 * @author gautham
 */
package api;

import java.io.Serializable;

/**
 * The class represents the Result objects of the Tasks that are executed in the Compute Space.
 * Each Task in the ComputeSpace is turned into a result by one of the Compute Servers.
 * Results of the executed Tasks are read from the Compute Space by the respective Jobs and combined into an overall result for the Client.
 *
 * @param <T> A task execute method's return value of type T.
 */
public class Result<T> implements Serializable{
		
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The task id. */
	private int taskId;
	
	/** The elapsed time. */
	private long elapsedTime;		
	
	/** The return value. */
	private T returnValue;
	
	/**
	 * Gets the task return value.
	 *
	 * @return the task return value
	 */
	public T getTaskReturnValue(){
		return this.returnValue;
	}
	
	/**
	 * Sets the task return value.
	 *
	 * @param returnValue the new task return value
	 */
	public void setTaskReturnValue(T returnValue){
		this.returnValue = returnValue;
	}
	
	/**
	 * Sets the task run time.
	 *
	 * @param elapsedTime the new task run time
	 */
	public void setTaskRunTime(long elapsedTime) {
		this.elapsedTime = elapsedTime;
	}
	
	/**
	 * Gets the task run time.
	 *
	 * @return the task run time
	 */
	public long getTaskRunTime() {
		return elapsedTime;
	}	

	/**
	 * Sets the task id.
	 *
	 * @param taskId the new task id
	 */
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	/**
	 * Gets the task id.
	 *
	 * @return the task id
	 */
	public int getTaskId() {
		return taskId;
	}
	
	
}
