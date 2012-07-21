package com.cjavellana.img.downloader.processor;

import java.util.concurrent.atomic.AtomicInteger;

public class IndexPointer {
	private AtomicInteger val = new AtomicInteger(0);

	public int getAndIncrement(){
		return val.getAndIncrement(); 
	}
}
