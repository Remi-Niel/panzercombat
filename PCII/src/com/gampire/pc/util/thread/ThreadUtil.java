package com.gampire.pc.util.thread;

public class ThreadUtil {

	static public void waitAWhile(int milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			// nothing to do
		}
	}
}
