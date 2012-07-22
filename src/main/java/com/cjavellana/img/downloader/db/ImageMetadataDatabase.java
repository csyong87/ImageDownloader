package com.cjavellana.img.downloader.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Christian
 * 
 */
public class ImageMetadataDatabase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3013637184924085601L;

	private static final Log logger = LogFactory
			.getLog(ImageMetadataDatabase.class);

	private static final String IMG_DB_FILENAME = "/img.dat";
	private Map<String, String> map;
	private String destDirectory;

	public ImageMetadataDatabase(String imgDirectory) {
		try {
			this.destDirectory = imgDirectory;
			FileInputStream fis = new FileInputStream(imgDirectory
					+ IMG_DB_FILENAME);
			ObjectInputStream ios = new ObjectInputStream(fis);

			logger.info(String.format(
					"img.dat found at %s; Loading the database now",
					imgDirectory));

			map = (Map<String, String>) ios.readObject();
			ios.close();
		} catch (FileNotFoundException fnfe) {
			String msg = "Unable to find img.dat at %s, creating new Map now.";
			logger.info(String.format(msg, imgDirectory));

			// ok, file isn't there. It's the first time this application is run
			// on the specified output directory

			// create the map object as our serialized database which we later
			// persist to disk
			map = new HashMap<String, String>();
		} catch (IOException ioe) {
			// don't know what to do with ioe, give it back to the caller
			throw new RuntimeException(ioe);
		} catch (ClassNotFoundException cnfe) {
			// shouldn't happen as we are only persisting a HashMap; All JVMs
			// have that
			throw new RuntimeException(cnfe);
		}
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, String value) {
		synchronized (map) {
			map.put(key, value);
		}
	}

	/**
	 * Returns the object identified by the parameter <tt>key</tt>. Returns null
	 * of there's no object identified by <tt>key</tt>
	 * 
	 * @param key
	 *            The object's key
	 * @return
	 */
	public String get(String key) {
		return map.get(key);
	}

	/**
	 * @return the destDirectory
	 */
	public String getDestDirectory() {
		return destDirectory;
	}

	/**
	 * @param destDirectory
	 *            the destDirectory to set
	 */
	public void setDestDirectory(String destDirectory) {
		this.destDirectory = destDirectory;
	}

	public void persistToDisk() {
		try {
			File f = new File(destDirectory + IMG_DB_FILENAME);

			if (!f.exists()) {
				f.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(f);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(map);
			os.flush();
			os.close();
		} catch (IOException ioe) {
			throw new RuntimeException(
					"Unable to write Image Database to disk", ioe);
		}
	}
}
