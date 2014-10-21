package com.coderadar.handler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TimeoutTester {
	// maintains a thread for executing the doWork method
	private final ExecutorService executor = Executors.newFixedThreadPool(1);
	private boolean runed = false;

	public static void main(String... args) {
		new TimeoutTester().doWorkWithTimeout(3);
	}

	private void doWork() {
		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				System.out.println("start run");
				if (!runed) {
					runed = true;
					// perform some long running task here...
					long i = 0L;
					while (i < 100000L) {
						i++;
					}
					System.out.println("Finally ");
				} else {
					t.cancel();
					throw new RuntimeException("aaaaaaaa");
				}
			}
		}, 0, 3000); // can finish in 3 seconds.
	}

	public void doWorkWithTimeout(int timeoutSecs) {
		final Future<?> future = executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					doWork();
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}
