package com.tudou.userstat.web;

 
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class TaskThread implements Runnable {
	private Long id;
	private CountDownLatch threadSignal;
	public static List<Long> resultList = new LinkedList<Long>();

	TaskThread(Long id, CountDownLatch threadSignal) {
		this.id = id;
		this.threadSignal = threadSignal;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized(TaskThread.class) {
			if(id % 2 != 0) {
				resultList.add(id);
			}
		}
		threadSignal.countDown();
	}
}
