package com.cjavellana.img.downloader;

import java.util.Calendar;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cjavellana.img.downloader.util.DateFormatter;

public class Main {

	private static final Log logger = LogFactory.getLog(Main.class);

	public static void main(String... args) {

		logger.info(String.format("Image Downloader Started at: %s",
				DateFormatter.format(Calendar.getInstance().getTime())));

		Option url = OptionBuilder.withArgName("url").hasArg()
				.withDescription("The Url of the webpage to process")
				.create("url");

		Option destFolder = OptionBuilder
				.withArgName("dest")
				.hasArg()
				.withDescription(
						"The location where grabbed images will be stored")
				.create("dest");

		HtmlPageParser parser = new HtmlPageParser(
				"http://javarevisited.blogspot.sg/2012/02/producer-consumer-design-pattern-with.html",
				"D:\\Temp\\downloaded_images");
		parser.process();

		logger.info(String.format("Image Downloader Ended at: %s",
				DateFormatter.format(Calendar.getInstance().getTime())));
	}
}
