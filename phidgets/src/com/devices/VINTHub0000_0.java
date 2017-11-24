package com.devices;

import java.util.Properties;

import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.DetachEvent;
import com.phidget22.DetachListener;
import com.phidget22.DeviceClass;
import com.phidget22.ErrorEvent;
import com.phidget22.ErrorListener;
import com.phidget22.PhidgetException;
import com.phidget22.TemperatureSensor;

public class VINTHub0000_0 extends PhidgetsDevice{
	//serail 495527
	private TemperatureSensor temp;
	private double temperature = -999;
	
	private double threshold_low_min=23;
    private double threshold_low_max=25;
    private double threshold_high_min=27;
    private double threshold_high_max=30;
	private int waiting_time_1c_in_ms=120000;
	private int waiting_time_power_in_ms=600000;
	
	private int lastAction = ACTION_NONE;
	private long waitignTime = 0;
	
	public VINTHub0000_0(String category, int addr) {
		super(category, addr);
		initialize();
	}

	@Override
	public void initialize() {
		try {
			temp = new TemperatureSensor();
			//temp.setDataInterval(1000);

			temp.addAttachListener(new AttachListener() {
				public void onAttach(AttachEvent ae) {
					TemperatureSensor phid = (TemperatureSensor) ae.getSource();
					try {
						if(phid.getDeviceClass() != DeviceClass.VINT){
							mLogger.info("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " attached");
						}
						else{
							mLogger.info("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " hub port " + phid.getHubPort() + " attached");
						}
					} catch (PhidgetException ex) {
						mLogger.error("PhidgetException : " , ex);
					}
				}
	        });
        
			temp.addDetachListener(new DetachListener() {
				public void onDetach(DetachEvent de) {
					TemperatureSensor phid = (TemperatureSensor) de.getSource();
					try {
						if (phid.getDeviceClass() != DeviceClass.VINT) {
							mLogger.info("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " detached");
						} else {
							mLogger.info("channel " + phid.getChannel() + " on device " + phid.getDeviceSerialNumber() + " hub port " + phid.getHubPort() + " detached");
						}
					} catch (PhidgetException ex) {
						mLogger.error("PhidgetException : " , ex);
					}
				}
	        });
                
			temp.addErrorListener(new ErrorListener() {
				public void onError(ErrorEvent ee) {
					mLogger.info("Error: " + ee.getDescription());
				}
	        });
	
//			temp.addTemperatureChangeListener(new TemperatureSensorTemperatureChangeListener() {
//				public void onTemperatureChange(TemperatureSensorTemperatureChangeEvent e) {
//					mLogger.info("Temperature Changed:  : " + e.getTemperature());
//				}
//	        });
		} catch (PhidgetException e1) {
			mLogger.error("PhidgetException : " , e1);
		}
	}
	// the first time of bootup, lastAction = ACTION_NONE
	public int getAction() {
		try {
			temperature = temp.getTemperature();
			mLogger.info("[getAction] current temperature : " + temperature + " C");
			if (temperature < threshold_low_min) {
				if (lastAction == ACTION_OFF) {
					mLogger.debug("[getAction] temperature  " + temperature + " in threshold_low_min case with ACTION_OFF");
					// waiting temperature rise up, if it is not , that aircon can be not turn off
					// yet
					if ((System.currentTimeMillis() - waitignTime) < waiting_time_power_in_ms) {
						return ACTION_NONE;
					}
					// continue
					// try to turn off aircon
				}
				mLogger.debug("[getAction] temperature  " + temperature + " in threshold_low_min case set to ACTION_OFF");
				lastAction = ACTION_OFF;
				waitignTime = System.currentTimeMillis();
				return lastAction;
			}

			if (temperature < threshold_low_max) {
				if (lastAction == ACTION_OFF) {
					mLogger.debug("[getAction] temperature  " + temperature + " in threshold_low_max case with ACTION_OFF");
					return ACTION_NONE;
				}
				if (lastAction == ACTION_UP) {
					mLogger.debug("[getAction] temperature  " + temperature + " in threshold_low_max case with ACTION_UP");
					// waiting temperature rise up
					if ((System.currentTimeMillis() - waitignTime) < waiting_time_1c_in_ms) {
						return ACTION_NONE;
					}
					// continue
					// try to increase temperature
				}
				mLogger.debug("[getAction] temperature  " + temperature + " in threshold_low_max case set to ACTION_UP");
				waitignTime = System.currentTimeMillis();
				lastAction = ACTION_UP;
				return lastAction;
			}

			if ((temperature > threshold_high_min) && (temperature < threshold_high_max)) {
				if (lastAction == ACTION_DOWN) {
					mLogger.debug("[getAction] temperature  " + temperature + " in threshold_high_min case with ACTION_DOWN");
					// waiting temperature decrease down
					if ((System.currentTimeMillis() - waitignTime) < waiting_time_1c_in_ms) {
						return ACTION_NONE;
					}
					// continue
					// try to decrease temperature
				}
				mLogger.debug("[getAction] temperature  " + temperature + " in threshold_high_min case set to ACTION_DOWN");
				waitignTime = System.currentTimeMillis();
				lastAction = ACTION_DOWN;
				return lastAction;
			}

			if (temperature > threshold_high_max) {
				if (lastAction == ACTION_OFF) {
					mLogger.debug("[getAction] temperature  " + temperature + " in threshold_high_max case with ACTION_OFF , force to ACTION_ON");
					return ACTION_ON;
				}

				if (lastAction == ACTION_ON) {
					mLogger.debug("[getAction] temperature  " + temperature + " in threshold_high_max case with ACTION_ON");
					// waiting temperature decrease down
					if ((System.currentTimeMillis() - waitignTime) < waiting_time_power_in_ms) {
						return ACTION_NONE;
					}
					// continue
					// try to decrease temperature
				}
				mLogger.debug("[getAction] temperature  " + temperature + " in threshold_high_max case set to ACTION_ON");
				waitignTime = System.currentTimeMillis();
				lastAction = ACTION_ON;
				return lastAction;
			}
		} catch (PhidgetException e) {
			mLogger.error("PhidgetException : " , e);
		}
		return ACTION_NONE;
	}
	
	public double getTempValue() {
		try {
			if (temp.getAttached()) {
				temperature = temp.getTemperature();
			} else {
				mLogger.error("[getTempValue] waiting temperature attached !");
			}
		} catch (PhidgetException e) {
			mLogger.error("[getTempValue] Temperature exception", e);
		}
		return temperature;
	}

	@Override
	public boolean start() {
		boolean result = false;
		try {
			temp.open(5000);
			result = true;
		} catch (PhidgetException e) {
			mLogger.error("PhidgetException : " , e);
		}
		return result;
	}
	
	@Override
	public boolean stop() {
		boolean result = false;
		try {
			temp.close();
			result = true;
		} catch (PhidgetException e) {
			mLogger.error("PhidgetException : " , e);
		}
		return result;
	}
	
	@Override
	public void setConfiguration(Properties config) {
		if (config != null) {
			threshold_low_min = Double.valueOf(config.getProperty("threshold_low_min").trim());
			threshold_low_max = Double.valueOf(config.getProperty("threshold_low_max").trim());
			threshold_high_min = Double.valueOf(config.getProperty("threshold_high_min").trim());
			threshold_high_max = Double.valueOf(config.getProperty("threshold_high_max").trim());
			waiting_time_1c_in_ms = Integer.valueOf(config.getProperty("waiting_time_1c_in_minute").trim()) * 60000;
			waiting_time_power_in_ms = Integer.valueOf(config.getProperty("waiting_time_power_in_minute").trim()) * 60000;
			mLogger.info("[setConfiguration] threshold_low_min = " + threshold_low_min);
			mLogger.info("[setConfiguration] threshold_low_max = " + threshold_low_max);
			mLogger.info("[setConfiguration] threshold_high_min = " + threshold_high_min);
			mLogger.info("[setConfiguration] threshold_high_max = " + threshold_high_max);
			mLogger.info("[setConfiguration] waiting_time_1c_in_ms = " + waiting_time_1c_in_ms);
			mLogger.info("[setConfiguration] waiting_time_power_in_ms = " + waiting_time_power_in_ms);
		}
	}
}
