package com.cjavellana.img.downloader;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cjavellana.img.downloader.consumer.ImageUrlConsumer;
import com.cjavellana.img.downloader.processor.ImageUrlProducer;
import com.cjavellana.img.downloader.processor.ImageUrlProducerParameter;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This class processes a extracts all img tags from a given url to build a
 * shared task pool which feeds the consumer threads
 * 
 * @author Christian
 * 
 */
public class HtmlPageParser {

	private static final Log logger = LogFactory.getLog(HtmlPageParser.class);

	/**
	 * Max of 50 image downloader and resizer workers
	 */
	private static final int MAX_MAIN_THREAD_POOL_COUNT = 50;

	private static final int MAX_QUEUE_TASK = 100;

	private static final int MAX_CONSUMER_COUNT = 10;

	private String urlToProcess;
	private String destination;

	public HtmlPageParser(String urlToProcess, String destination) {
		this.urlToProcess = urlToProcess;
		this.destination = destination;
	}

	public void process() {

		try {
			ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors
					.newFixedThreadPool(MAX_MAIN_THREAD_POOL_COUNT);

			WebClient webClient = new WebClient();
			HtmlPage page = webClient.getPage(urlToProcess);
			DomNodeList<HtmlElement> nodeList = page
					.getElementsByTagName("img");

			logger.info(String.format("Initializing MessageMediator."));

			MessageMediator mediator = new MessageMediator(
					new ArrayBlockingQueue<String>(MAX_QUEUE_TASK));

			// initialize the producer's input parameter
			ImageUrlProducerParameter param = new ImageUrlProducerParameter();
			param.setHtmlPage(page);
			param.setNodeList(nodeList);

			logger.info(String.format("Initializing Producers."));

			// Currently, only one producer is supported. Having multiple
			// producer will mess up the task pool
			pool.submit(new ImageUrlProducer(param, mediator));

			logger.info(String.format("Initializing Consumers."));

			// initialize our consumers
			for (int i = 0; i < MAX_CONSUMER_COUNT; i++) {
				pool.submit(new ImageUrlConsumer(mediator, destination));
			}

			while (mediator.isHasMoreTask()) {
				// we have not received the 'shutdown' flag yet, suspend current
				// thread for a little while
				Thread.sleep(100);
			}

			logger.info(String
					.format("Producer stopped producing, placing poison pill into the task queue"));

			// Producer has stopped producing task, poison the task queue to
			// make consumers shut down.

			// Since we have MAX_CONSUMER_COUNT and each consumer would have
			// popped the task, insert MAX_CONSUMER_COUNT of poison pills into
			// the task queue
			for (int i = 0; i < MAX_CONSUMER_COUNT; i++) {
				mediator.put(MessageMediator.POISON_TASK);
			}

			logger.info(String.format("Shutting down task pool"));

			pool.shutdown();

			webClient.closeAllWindows();

			for (String url : mediator.getBackingQueue()) {
				logger.info(String.format("Image at: %s", url));
			}

		} catch (Exception e) {
			logger.fatal("Unable to complete operation due to: ", e);
		}

	}
}
