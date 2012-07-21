package com.cjavellana.img.downloader;

import java.util.concurrent.BlockingQueue;

/**
 * 
 * @author Christian
 * 
 */
public class MessageMediator {

	/**
	 * The poison flag; Causes a consumer thread to terminate if it reads this
	 * message
	 */
	public static final String POISON_TASK = "shutdown";

	private BlockingQueue<String> queue;
	private boolean hasMoreTask = true;

	public MessageMediator(BlockingQueue<String> blockingQueue) {
		this.queue = blockingQueue;
	}

	public void put(String task) throws InterruptedException {
		queue.put(task);
	}

	public String take() throws InterruptedException {
		return queue.take();
	}

	public BlockingQueue<String> getBackingQueue() {
		return queue;
	}

	/**
	 * @return the hasMoreTask
	 */
	public boolean isHasMoreTask() {
		return hasMoreTask;
	}

	/**
	 * @param hasMoreTask
	 *            the hasMoreTask to set
	 */
	public void setHasMoreTask(boolean hasMoreTask) {
		this.hasMoreTask = hasMoreTask;
	}

}
