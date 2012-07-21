package com.cjavellana.img.downloader.consumer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cjavellana.img.downloader.MessageMediator;

/**
 * The Consumer thread of Image Url.
 * 
 * @author Christian
 * 
 */
public class ImageUrlConsumer implements Callable<ConsumerProcessResult> {

	private static final Log logger = LogFactory.getLog(ImageUrlConsumer.class);

	private MessageMediator mediator;
	private String destination;

	public ImageUrlConsumer(MessageMediator mediator, String destination) {
		this.mediator = mediator;
		this.destination = destination;
	}

	public ConsumerProcessResult call() {
		ConsumerProcessResult result = new ConsumerProcessResult();
		File destLocation = new File(destination);
		while (true) {
			try {
				// Will block if there are no task in the queue.
				// If manager receives a 'no more task' message from producer,
				// manager will then proceed to inject poisoned pill into the
				// task queue to terminate consumers
				String imageUrl = mediator.take();

				// Check if poison pill is read from the task queue. If so,
				// break out of this loop and exit gracefully
				if (imageUrl.equalsIgnoreCase(MessageMediator.POISON_TASK)) {
					break;
				}

				try {
					BufferedImage img = ImageIO.read(new URL(imageUrl));

					logger.info(String
							.format("Image Location: %s; Image Height: %s; Image Width: %s",
									imageUrl, img.getHeight(), img.getWidth()));

					// ignore images that where width or height is equal or less
					// than 10px
					if (img.getHeight() > 10 || img.getWidth() > 10) {
						if (!destLocation.exists()) {
							throw new RuntimeException(String.format(
									"Destination %s not found", destination));
						}

						for (String prop : img.getPropertyNames()) {
							logger.info(String.format(
									"Property: %s; Value: %s", prop,
									img.getProperty(prop)));
						}

						// save the file extention
						String ext = imageUrl.substring(imageUrl.length() - 4,
								imageUrl.length());
						// save the image using the origin url, but replace /
						// and : with _
						String newFilename = imageUrl.replace("/", "_")
								.replace(":", "_").replace(".", "_");
						newFilename = newFilename.substring(0,
								newFilename.length() - 5)
								+ ext;

						logger.info(String.format("Storing file %s",
								newFilename));

						File output = new File(destination + "\\" + newFilename);

						ImageIO.write(img, "png", output);
					}
				} catch (IOException ioException) {
					// if there's an img that points to an invalid url, log
					// only. not being able to find an img file is not a
					// catasthropic error
					logger.warn(String.format(
							"Unable to download image from %s", imageUrl),
							ioException);
				}
			} catch (InterruptedException ie) {
				// Set the interrupt flag
				Thread.currentThread().interrupt();
			}

		}

		return result;
	}
}
