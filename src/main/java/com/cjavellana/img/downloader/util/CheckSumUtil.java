package com.cjavellana.img.downloader.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Class used to calculate the image's check sum. Check sum is used to determine
 * whether a file has changed since the previous run.
 * 
 * @author Christian
 * @see http://stackoverflow.com/questions/304268/getting-a-files
 *      -md5-checksum-in-java
 */
public class CheckSumUtil {

	private static byte[] createChecksum(byte[] imageData) throws Exception {
		InputStream fis = new ByteArrayInputStream(imageData);

		byte[] buffer = new byte[1024];

		// Changed algorithm to SHA-256 due to vulnerabilities in MD5
		// http://en.wikipedia.org/wiki/MD5
		MessageDigest complete = MessageDigest.getInstance("SHA-256");
		int numRead;

		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);

		fis.close();
		return complete.digest();
	}

	// see this How-to for a faster way to convert
	// a byte array to a HEX string
	public static String getMD5Checksum(byte[] imageData) throws Exception {
		byte[] b = createChecksum(imageData);
		String result = "";

		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

}
