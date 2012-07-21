package com.cjavellana.img.downloader.producer;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cjavellana.img.downloader.MessageMediator;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

/**
 * Single Producer Model. This class does not support multiple producers acting
 * on a single <tt>nodeListPool</tt>
 * 
 * @author Christian
 * 
 */
public class ImageUrlProducer implements Runnable {

	private static final Log logger = LogFactory.getLog(ImageUrlProducer.class);

	private Set<String> uniqueUrls = Collections
			.synchronizedSet(new HashSet<String>());

	private ImageUrlProducerParameter parameter;
	private MessageMediator mediator;

	/**
	 * 
	 * @param htmlPage
	 *            The <tt>htmlPage</tt> where <tt>nodeList</tt> comes from
	 * @param nodeList
	 * @param taskQueue
	 */
	public ImageUrlProducer(ImageUrlProducerParameter param,
			MessageMediator mediator) {
		this.parameter = param;
		this.mediator = mediator;
	}

	public void run() {
		logger.info(String.format("Thread %s running.", Thread.currentThread()
				.getId()));
		try {
			for (HtmlElement imgTask : parameter.getNodeList()) {
				URL pageUrl = parameter.getHtmlPage().getUrl();
				String imgSrc = imgTask.getAttribute("src");

				String imageUrl = "";

				if (imgSrc.startsWith("/")) {
					// current path
					imageUrl = String.format("%s://%s%s",
							pageUrl.getProtocol(), pageUrl.getHost(), imgSrc);
				} else if (imgSrc.startsWith("http")) {
					// http is specified
					imageUrl = imgSrc;
				} else {
					// relative path
					imageUrl = String.format("%s%s", pageUrl.toString()
							.substring(0, pageUrl.toString().lastIndexOf("/")),
							imgSrc);
				}

				logger.info(String.format("Image Url: %s", imageUrl));

				synchronized (uniqueUrls) {
					if (!uniqueUrls.contains(imageUrl)) {
						uniqueUrls.add(imageUrl);
						mediator.put(imageUrl);
					}
				}

			}

			mediator.setHasMoreTask(false);
		} catch (InterruptedException ie) {
			logger.info("Current thread has been interrupted");
			return;
		}
	}
}
