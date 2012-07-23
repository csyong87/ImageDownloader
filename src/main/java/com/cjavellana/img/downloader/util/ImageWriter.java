package com.cjavellana.img.downloader.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageWriter {

	// ~ Target image width constants ===============================

	private static final int IMG_SIZE_100PX = 100;
	private static final int IMG_SIZE_220PX = 220;
	private static final int IMG_SIZE_320PX = 320;

	/**
	 * Creates 3 images in the <tt>destination</tt> of sizes 100px, 220px and
	 * 320px and using the specified <tt>filename</tt>
	 * 
	 * @param originalImage
	 * @param destination
	 * @param filename
	 */
	public static void writeImage(BufferedImage originalImage,
			String destination, String filename) {

		try {
			// resize width to 100px and scale the height
			BufferedImage image100px = (BufferedImage) ImageUtil.resizeImage(
					originalImage, IMG_SIZE_100PX,
					getScaledHeight(originalImage, IMG_SIZE_100PX));
			String filename100px = filename.substring(0, filename.length() - 2)
					+ "_100px";
			ImageIO.write(image100px, "jpg", new File(destination + "/"
					+ filename100px + ".jpg"));
			ImageIO.write(image100px, "png", new File(destination + "/"
					+ filename100px + ".png"));

			// resize width to 220px and scale the height
			BufferedImage image220px = (BufferedImage) ImageUtil.resizeImage(
					originalImage, IMG_SIZE_220PX,
					getScaledHeight(originalImage, IMG_SIZE_220PX));
			String filename220px = filename.substring(0, filename.length() - 2)
					+ "_220px";
			ImageIO.write(image220px, "jpg", new File(destination + "/"
					+ filename220px + ".jpg"));
			ImageIO.write(image220px, "png", new File(destination + "/"
					+ filename220px + ".png"));

			// resize width to 320px and scale the height
			BufferedImage image320px = (BufferedImage) ImageUtil.resizeImage(
					originalImage, IMG_SIZE_320PX,
					getScaledHeight(originalImage, IMG_SIZE_320PX));
			String filename320px = filename.substring(0, filename.length() - 2)
					+ "_320px";
			ImageIO.write(image320px, "jpg", new File(destination + "/"
					+ filename320px + ".jpg"));
			ImageIO.write(image320px, "png", new File(destination + "/"
					+ filename320px + ".png"));
		} catch (IOException ioe) {
			throw new RuntimeException("Unable to save images", ioe);
		}

	}

	/**
	 * Utility method to get the scaled height in order to preserve the width -
	 * height ratio during resizing of the image's width
	 * 
	 * @param originalImage
	 *            The image to resize
	 * @param newWidth
	 *            The target width
	 * @return
	 */
	private static int getScaledHeight(BufferedImage originalImage, int newWidth) {
		int origWidth = originalImage.getWidth();
		int origHeight = originalImage.getHeight();

		return Math.round((newWidth * origHeight) / origWidth);
	}
}
