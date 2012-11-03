/*
 * @author gautham
 */
package experiment;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import api.Result;
import api.Task;

/**
 * The Interface Job.
 *
 * @param <T> the generic type
 */
public interface Job<T> {

	/**
	 * Generate tasks.
	 *
	 * @param taskQueue the task queue
	 */
	public void generateTasks(BlockingQueue<Task<?>> taskQueue);
	
	/**
	 * Collect results.
	 *
	 * @param resultQueue the result queue
	 * @return t
	 */
	public T collectResults(BlockingQueue<Result<?>> resultQueue);
		
	/** The task start time map. */
	public Map<Integer, Long> taskStartTimeMap = new HashMap<Integer, Long>();
	
}
