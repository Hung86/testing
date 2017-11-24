package com.entrypoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	static {
		System.setProperty("log4j.configurationFile", "config/log4j2.xml");
	}
	static Logger mLogger = LoggerFactory.getLogger("Main Thread");
	public static boolean isRunning = false;

	public static void main(String[] args) {
		mLogger.info("[MAIN]==============START");
		try {
			Phidgets ph = new Phidgets();
			ph.start();
			isRunning = true;
			while (isRunning) {
				Thread.sleep(600000);
			}
			isRunning = false;
		} catch (Exception e) {
			mLogger.error("Exception : ", e);
		}
		mLogger.info("[MAIN]==============END");
	}

}
