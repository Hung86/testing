package com.entrypoint;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.devices.IR1055_0;
import com.devices.PhidgetsDevice;
import com.devices.VINTHub0000_0;
import com.java.utility.FuncUtil;

public class Phidgets {	
	private VINTHub0000_0 Hub0000_0 = null;
	private IR1055_0 ir1055_0 = null;
	private Properties phidget_temp;
	private Properties phidget_ir;
	private Logger mLogger = LoggerFactory.getLogger("Phidget Thread");
	ScheduledExecutorService actionThread =  Executors.newSingleThreadScheduledExecutor();
	public Phidgets() {
		phidget_temp = FuncUtil.getPropertiesFile("config/temp.prop");
		phidget_ir = FuncUtil.getPropertiesFile("config/ir.prop");
		if (!phidget_temp.isEmpty() && !phidget_ir.isEmpty()) {
			Hub0000_0 = new VINTHub0000_0("0000", 1);
			ir1055_0 = new IR1055_0("1055", 2);
			Hub0000_0.setConfiguration(phidget_temp);
			ir1055_0.setConfiguration(phidget_ir);
			actionThread.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {	
					action();
				}
			}, 15, 10, TimeUnit.SECONDS);
		} else {
			mLogger.info("[Phidgets] There is not configuration file from config/temp.prop or config/ir.prop");
		}
	}
	public void start () {
		Hub0000_0.start();
		ir1055_0.start();
	}
	
	public void stop () {
		Hub0000_0.stop();
		ir1055_0.stop();
	}
	
	public void action() {
		mLogger.info("[action] getting action");
		int act = Hub0000_0.getAction();
		if (act != PhidgetsDevice.ACTION_NONE) {
			
		} else {
			mLogger.info("[action] do nothing");
		}
	}
	
	
	
}
