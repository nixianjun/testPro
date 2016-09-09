package com.tudou.userstat.web;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestThread {

	public static void main(String[] args) {
		Long start = System.currentTimeMillis();
		CountDownLatch threadSignal = new CountDownLatch(50);
		ExecutorService exe = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 50; i++) {
			exe.execute(new TaskThread(new Long(i + 1) + 1000L, threadSignal));
		}
		try {
			threadSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis() - start);
		System.out.println(TaskThread.resultList);
		exe.shutdown();
	}
	
	
	public   void test(String[] args) {
		Long start = System.currentTimeMillis();
		CountDownLatch threadSignal = new CountDownLatch(50);
		ExecutorService exe = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 50; i++) {
			exe.execute(new TaskThread(new Long(i + 1) + 1000L, threadSignal));
		}
		try {
			threadSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis() - start);
		System.out.println(TaskThread.resultList);
		exe.shutdown();
	}
}
