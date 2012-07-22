package com.cjavellana.img.downloader;

import java.util.Calendar;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cjavellana.img.downloader.util.DateFormatter;

public class Main {

	private static final Log logger = LogFactory.getLog(Main.class);

	@SuppressWarnings("static-access")
	public static void main(String... args) {

		logger.info(String.format("Image Downloader Started at: %s",
				DateFormatter.format(Calendar.getInstance().getTime())));

		try {
			Option url = OptionBuilder.withArgName("url").hasArg()
					.withDescription("The Url of the webpage to process")
					.create("url");

			Option destFolder = OptionBuilder
					.withArgName("dest")
					.hasArg()
					.withDescription(
							"The location where grabbed images will be stored")
					.create("dest");

			Options options = new Options();
			options.addOption(url);
			options.addOption(destFolder);

			CommandLineParser parser = new PosixParser();
			CommandLine commandLine = parser.parse(options, args);
			logger.info("Url: " + commandLine.getOptionValue("url"));

			ImageDownloadManager idm = new ImageDownloadManager();
			idm.downloadImages(commandLine.getOptionValue("url"),
					commandLine.getOptionValue("dest"));
		} catch (ParseException pe) {
			logger.error("Unable to parse arguments", pe);
		}
		logger.info(String.format("Image Downloader Ended at: %s",
				DateFormatter.format(Calendar.getInstance().getTime())));
	}
}
