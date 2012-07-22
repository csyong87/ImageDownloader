package com.cjavellana.img.downloader.mediator;

import java.util.concurrent.BlockingQueue;

/**
 * Facilitates the message passing between the producer and the consumer
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

	/**
	 * <p>
	 * Constructs a {@link MessageMediator} object using the specified
	 * {@link BlockingQueue} instance.
	 * </p>
	 * 
	 * @param blockingQueue
	 */
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

}
