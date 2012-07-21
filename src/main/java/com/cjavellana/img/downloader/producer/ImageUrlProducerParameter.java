package com.cjavellana.img.downloader.producer;

import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ImageUrlProducerParameter {

	private DomNodeList<HtmlElement> nodeList;
	private HtmlPage htmlPage;

	/**
	 * @return the nodeList
	 */
	public DomNodeList<HtmlElement> getNodeList() {
		return nodeList;
	}

	/**
	 * @param nodeList
	 *            the nodeList to set
	 */
	public void setNodeList(DomNodeList<HtmlElement> nodeList) {
		this.nodeList = nodeList;
	}

	/**
	 * @return the htmlPage
	 */
	public HtmlPage getHtmlPage() {
		return htmlPage;
	}

	/**
	 * @param htmlPage
	 *            the htmlPage to set
	 */
	public void setHtmlPage(HtmlPage htmlPage) {
		this.htmlPage = htmlPage;
	}

}
