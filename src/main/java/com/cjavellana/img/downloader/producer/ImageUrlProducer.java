package com.cjavellana.img.downloader.producer;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cjavellana.img.downloader.mediator.MessageMediator;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * Single Producer Model. This class does not support multiple producers acting
 * on a single <tt>nodeListPool</tt>
 * 
 * @author Christian
 * 
 */
public class ImageUrlProducer implements Runnable {

	// ~ Field constants ===========================================

	private static final Log logger = LogFactory.getLog(ImageUrlProducer.class);
	private static final String HTML_IMG_TAG = "img";
	private static final String HTML_SRC_ATTR = "src";

	// ~ Field instances ==========================================

	// A webpage can have multiple img tags pointing to a single image
	// Therefore it is important that we only process unique image urls
	private Set<String> uniqueUrls = Collections
			.synchronizedSet(new HashSet<String>());
	private MessageMediator mediator;
	private HtmlPage htmlPage;

	/**
	 * 
	 * @param htmlPage
	 *            The <tt>htmlPage</tt> where <tt>nodeList</tt> comes from
	 * @param nodeList
	 * @param taskQueue
	 */
	public ImageUrlProducer(HtmlPage htmlPage, MessageMediator mediator) {
		this.htmlPage = htmlPage;
		this.mediator = mediator;
	}

	public void run() {
		logger.info(String.format("Thread %s running.", Thread.currentThread()
				.getId()));
		try {
			DomNodeList<HtmlElement> nodeList = htmlPage
					.getElementsByTagName(HTML_IMG_TAG);

			for (HtmlElement imgTask : nodeList) {
				URL pageUrl = htmlPage.getUrl();
				String imgSrc = imgTask.getAttribute(HTML_SRC_ATTR);

				String imageUrl = "";

				if (imgSrc.startsWith("/")) {
					// src attribute is an absolute url
					// prepend it with the protocol / host name / post number
					imageUrl = String.format("%s://%s%s",
							pageUrl.getProtocol(), pageUrl.getHost(), imgSrc);
				} else if (imgSrc.startsWith("http")) {
					// src attribute is a full http url; do nothing
					imageUrl = imgSrc;
				} else {
					// src attribute is a relative url; Prepend it with the
					// page's virtual dir location.

					// i.e src='email_icon.jpg' from
					// http://google.com/images/july2012/images.html
					// The image url would be interpreted as
					// http://google.com/images/july2012/email_icon.jpg
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
		} catch (InterruptedException ie) {
			logger.info("Current thread has been interrupted");
			return;
		}
	}
}
