package com.cjavellana.img.downloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cjavellana.img.downloader.consumer.ImageUrlConsumer;
import com.cjavellana.img.downloader.db.ImageMetadataDatabase;
import com.cjavellana.img.downloader.html.HtmlPageParser;
import com.cjavellana.img.downloader.mediator.MessageMediator;
import com.cjavellana.img.downloader.producer.ImageUrlProducer;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * <p>
 * The class the binds the {@link HtmlPageParser}, {@link ImageUrlProducer} and
 * {@link ImageUrlConsumer} together
 * </p>
 * 
 * @author Christian
 * 
 */
public class ImageDownloadManager {

	private static final Log logger = LogFactory
			.getLog(ImageDownloadManager.class);

	/**
	 * Max of 50 image downloader and resizer workers
	 */
	private static final int MAX_MAIN_THREAD_POOL_COUNT = 50;

	private static final int MAX_QUEUE_TASK = 100;

	private static final int MAX_CONSUMER_COUNT = 10;

	public ImageDownloadManager() {
	}

	public void downloadImages(String url, String localRepo) {
		try {
			ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors
					.newFixedThreadPool(MAX_MAIN_THREAD_POOL_COUNT);

			// check if local repo exist. If not, then create folder structure
			File destinationFolder = new File(localRepo);
			if (!destinationFolder.exists()) {
				destinationFolder.mkdirs();
			}

			HtmlPageParser parser = new HtmlPageParser(url);
			HtmlPage htmlPage = parser.parse();

			logger.info(String.format("Initializing MessageMediator."));

			MessageMediator mediator = new MessageMediator(
					new ArrayBlockingQueue<String>(MAX_QUEUE_TASK));

			logger.info(String.format("Initializing Producers."));

			// Currently, only one producer is supported. Having multiple
			// producer will mess up the task pool
			Future<?> producerFuture = pool.submit(new ImageUrlProducer(
					htmlPage, mediator));

			logger.info(String.format("Initializing Consumers."));

			ImageMetadataDatabase imgDatabase = new ImageMetadataDatabase(
					localRepo);

			// initialize our consumers
			List<Future<?>> consumerFutures = new ArrayList<Future<?>>();
			for (int i = 0; i < MAX_CONSUMER_COUNT; i++) {
				consumerFutures.add(pool.submit(new ImageUrlConsumer(mediator,
						imgDatabase)));
			}

			// wait for the producer to finish
			producerFuture.get();

			logger.info(String
					.format("Producer has stopped, placing poison pill into the task queue"));

			// We delegate the 'poisoning' of the task queue here since the
			// producer does not know how many consumers we have

			// Producer has stopped producing task, poison the task queue to
			// shut down consumers.

			// Since we have MAX_CONSUMER_COUNT and each consumer would have
			// popped the task, insert MAX_CONSUMER_COUNT of poison pills into
			// the task queue
			for (int i = 0; i < MAX_CONSUMER_COUNT; i++) {
				mediator.put(MessageMediator.POISON_TASK);
			}

			logger.info(String.format("Waiting for consumers to shutdown"));

			// wait for all consumers to finish
			for (Future<?> future : consumerFutures) {
				future.get();
			}

			logger.info(String.format("Shutting down task pool"));

			// clean up; Shutdown task pool, web client and store img database
			// to disk
			pool.shutdown();

			// ensure that pool is terminated before
			// we write our database otherwise we would end up
			// writing a partially filled database or an even an empty database
			// this is because we write the database to disk before the consumer
			// has placed the file's hash there
			while (true) {
				if (pool.isTerminated()) {
					imgDatabase.persistToDisk();
					break;
				} else {
					Thread.sleep(100);
				}
			}

		} catch (Exception e) {
			logger.error("Unable to process request due to: ", e);
		}
	}
}
