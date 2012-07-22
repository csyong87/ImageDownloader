package com.cjavellana.img.downloader.consumer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cjavellana.img.downloader.db.ImgMetadataDatabase;
import com.cjavellana.img.downloader.mediator.MessageMediator;
import com.cjavellana.img.downloader.util.CheckSumUtil;
import com.cjavellana.img.downloader.util.ImageWriter;

/**
 * The Consumer thread of Image Url.
 * 
 * @author Christian
 * 
 */
public class ImageUrlConsumer implements Runnable {

	private static final Log logger = LogFactory.getLog(ImageUrlConsumer.class);

	private MessageMediator mediator;
	private String destination;
	private ImgMetadataDatabase imgDatabase;

	public ImageUrlConsumer(MessageMediator mediator,
			ImgMetadataDatabase imgDatabase) {
		this.mediator = mediator;
		this.destination = imgDatabase.getDestDirectory();
		this.imgDatabase = imgDatabase;
	}

	public void run() {

		File destLocation = new File(destination);
		while (true) {
			try {
				// Will block if there are no task in the queue.
				// If manager receives a 'no more task' message from producer,
				// manager will then proceed to inject poisoned pill into the
				// task queue to terminate consumers
				String imageUrl = mediator.take();

				// Check if task queue is poisoned. If so, break out of this
				// loop and exit gracefully
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

						// Preserve the file extension
						String ext = imageUrl.substring(imageUrl.length() - 3,
								imageUrl.length());

						// save the image using the origin url, but replace /
						// and : with _
						String newFilename = imageUrl.replace("/", "_")
								.replace(":", "_").replace(".", "_");
						newFilename = newFilename.substring(0,
								newFilename.length() - 3)
								+ "." + ext;

						ByteArrayOutputStream os = new ByteArrayOutputStream();
						ImageIO.write(img, ext, os);

						String md5Hash = CheckSumUtil.getMD5Checksum(os
								.toByteArray());

						logger.info(String.format("File: %s; SHA-256 Hash: %s",
								newFilename, md5Hash));

						// Get checksum of the image from the previous run
						String previousChecksum = imgDatabase.get(newFilename);

						// if this is a new image or image has changed
						// update the image in the dest directory
						if (previousChecksum == null
								|| !previousChecksum.equalsIgnoreCase(md5Hash)) {
							logger.info(String.format(
									"File %s has changed; Updating Images...",
									newFilename));

							// Create image with sizes of 100px, 220px, and
							// 320px
							ImageWriter.writeImage(img, destination,
									newFilename);
							// store the image
							imgDatabase.put(newFilename, md5Hash);
						} else {
							logger.info(String.format(
									"File %s has not changed", newFilename));
						}

					}
				} catch (IOException ioException) {
					// if there's an img that points to an invalid url, log
					// only. not being able to find an img file is not a
					// catasthropic error
					logger.warn(String.format(
							"Unable to download image from %s", imageUrl),
							ioException);
				} catch (Exception e) {
					// could be problems in retrieving the md5 hash
					logger.warn(String.format(
							"Unable to download image from %s", imageUrl), e);
				}
			} catch (InterruptedException ie) {
				// Set the interrupt flag
				Thread.currentThread().interrupt();
			}

		}
	}
}
