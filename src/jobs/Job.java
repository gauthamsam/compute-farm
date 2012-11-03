/*
 * @author gautham
 */
package jobs;

import java.util.HashMap;
import java.util.Map;

import api.Space;

/**
 * The Job represents the work to be done from the Client's perspective.
 *
 * @param <T> a type parameter, T, which represents the result type of the job computation.
 */
public interface Job<T> {

	/**
	 * Generates multiple tasks from this job. The client decomposes the problem (job), constructing a set of Task objects
	 *
	 * @param space the space
	 */
	public void generateTasks(Space space);
	
	/**
	 * Collects results from the Space, composing them into a solution to the original problem.
	 *
	 * @param space the space
	 * @return t
	 */
	public T collectResults(Space space);
	
	/** Mapping between a taskId and its start time. It is used for experimentation purposes. */
	public Map<Integer, Long> taskStartTimeMap = new HashMap<Integer, Long>();
	
}
