package com.cjavellana.img.downloader.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

public class ImgMetadataDatabase {

	private static final String IMG_DB_FILENAME = "img.dat";
	private Map<String, String> map;

	public ImgMetadataDatabase(String imgDirectory) {
		try {
			FileInputStream fis = new FileInputStream(imgDirectory
					+ IMG_DB_FILENAME);
			ObjectInputStream ios = new ObjectInputStream(fis);
			map = (Map<String, String>) ios.readObject();
			ios.close();
		} catch (FileNotFoundException fnfe) {
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
}
