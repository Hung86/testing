package com.devices;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class PhidgetsDevice {
	public final static int ACTION_NONE = 0;
	public final static int ACTION_UP = 1;
	public final static int ACTION_UP_2 = 2;
	public final static int ACTION_DOWN = 3;
	public final static int ACTION_DOWN_2 = 4;
	public final static int ACTION_ON = 5;
	public final static int ACTION_OFF = 6;

	protected Logger mLogger = LoggerFactory.getLogger(getClass().getName());
	protected DecimalFormat vformat = new DecimalFormat("#########0.0000");
	protected String category;
	protected int deviceid;
	protected long timestamp;
	protected int errorCount;
	protected List<Map<String, String>> real_time_data;;
		
	public PhidgetsDevice(String cat, int addr) {
		deviceid = addr;
		category = cat;
		timestamp = 0;
		errorCount = 0;
		real_time_data = new ArrayList<Map<String, String>>();
	}

	public String getDeviceMetaData() {
		return "DEVICEID=" + getId() + ",STATUS=2;";
	}

	public int modbusId() {
		return deviceid;
	}

	public String getId() {
		return category + "-" + deviceid;
	}
	
	abstract public void initialize();
	abstract public boolean start();
	abstract public boolean stop();
	abstract public void setConfiguration(Properties config);
	
}
