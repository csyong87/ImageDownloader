package com.cjavellana.img.downloader.html;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * This class processes a extracts all img tags from a given url to build a
 * shared task pool which feeds the consumer threads
 * 
 * @author Christian
 * 
 */
public class HtmlPageParser {

	private String urlToProcess;

	public HtmlPageParser(String urlToProcess) {
		this.urlToProcess = urlToProcess;
	}

	/**
	 * Parses the page at the specified url
	 * 
	 * @return
	 * @throws MalformedURLException
	 *             Thrown when the given page's url is malformed
	 * @throws IOException
	 *             Thrown when for some reason the page at the specified url
	 *             could not be read
	 */
	public HtmlPage parse() throws MalformedURLException, IOException {
		WebClient webClient = new WebClient();
		HtmlPage page = webClient.getPage(urlToProcess);
		webClient.closeAllWindows();
		return page;
	}
}
