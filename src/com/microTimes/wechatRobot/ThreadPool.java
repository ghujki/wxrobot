package com.microTimes.wechatRobot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	private static ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
	
	public static void addRunner(Runnable runner,long delay,TimeUnit unit) {
		pool.schedule(runner, delay, unit);
	}
	
	public static void shutdown() {
		pool.shutdown();
	}
	
	public static void main(String args[]) throws InterruptedException {
		int i = 0;
		while (i < 1000) {
			ThreadPool.addRunner(new Runnable() {
				@Override
				public void run() {
					double rand = Math.random();
					System.out.println(" running " + rand);
				}
			}, 5, TimeUnit.SECONDS);
			i++;
			Thread.sleep(100);
		}
		ThreadPool.shutdown();
	}
}
