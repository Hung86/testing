package com.devices;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phidget22.AttachEvent;
import com.phidget22.AttachListener;
import com.phidget22.DetachEvent;
import com.phidget22.DetachListener;
import com.phidget22.DeviceClass;
import com.phidget22.ErrorEvent;
import com.phidget22.ErrorListener;
import com.phidget22.IR;
import com.phidget22.IRCodeEvent;
import com.phidget22.IRCodeListener;
import com.phidget22.IRLearnEvent;
import com.phidget22.IRLearnListener;
import com.phidget22.IRRawDataEvent;
import com.phidget22.IRRawDataListener;
import com.phidget22.PhidgetException;

public class IR1055_0 extends PhidgetsDevice{

	private IR ir;
	private int[] key_on;
	private int[] key_off;
	private int[] key_up;
	private int[] key_down;
	
	public IR1055_0(String category, int addr) {
		super(category, addr);
		initialize();
	}

	@Override
	public void initialize() {        
		try {
			ir = new IR();
		} catch (PhidgetException e1) {
			e1.printStackTrace();
		}

		ir.addAttachListener(new AttachListener() {
			public void onAttach(AttachEvent ae) {
				IR phid = (IR) ae.getSource();
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

		ir.addDetachListener(new DetachListener() {
			public void onDetach(DetachEvent de) {
				IR phid = (IR)de.getSource();
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

		ir.addErrorListener(new ErrorListener() {
			public void onError(ErrorEvent ee) {
				mLogger.error("Error: " + ee.getDescription());
			}
        });

		ir.addCodeListener(new IRCodeListener() {
			public void onCode(IRCodeEvent e) {
				mLogger.info("[addCodeListener] In Code Listener");
				mLogger.info("[addCodeListener] Code Read: " + e.getCode());
				mLogger.info("[addCodeListener] Bit Count: " + e.getBitCount());
				mLogger.info("[addCodeListener] Repeat: " + e.getIsRepeat() + "\n");
			}
        });
		
		ir.addRawDataListener(new IRRawDataListener() {
			@Override
			public void onRawData(IRRawDataEvent arg0) {
				mLogger.info("[onRawData]:" + arg0.toString());
			}
		});

		ir.addLearnListener(new IRLearnListener() {
			public void onLearn(IRLearnEvent le) {
				mLogger.info("[addLearnListener] In Learn Handler");
				mLogger.info("[addLearnListener] Code Read: 0x" + le.getCode() + "\n");
				String encodingString, lengthString;
				int toggleMaskBytes;

				switch (le.getCodeInfo().encoding) {
					case UNKNOWN:
						encodingString = "Unknown";
						break;
					case SPACE:
						encodingString = "Space";
						break;
					case PULSE:
						encodingString = "Pulse";
						break;
					case BI_PHASE:
						encodingString = "BiPhase";
						break;
					case RC5:
						encodingString = "RC5";
						break;
					case RC6:
						encodingString = "RC6";
						break;
					default:
						encodingString = "Unknown";
						break;
				}

				switch (le.getCodeInfo().length) {
					case UNKNOWN:
						lengthString = "Unknown";
						break;
					case CONSTANT:
						lengthString = "Constant";
						break;
					case VARIABLE:
						lengthString = "Variable";
						break;
					default:
						lengthString = "Unknown";
						break;
				}

				mLogger.info("[addLearnListener] Learned Code Info");
				mLogger.info("----------------------------------------------------");
				mLogger.info("[addLearnListener] Bit Count: " + le.getCodeInfo().bitCount + "\nEncoding: " + encodingString + "\nLength: " + lengthString + "\nGap: " + le.getCodeInfo().gap + "\nTrail: " + le.getCodeInfo().trail);
				mLogger.info("[addLearnListener] Header: { " + le.getCodeInfo().header[0] + "," + le.getCodeInfo().header[1] + " }\nZero: {" + le.getCodeInfo().one[0] + "," + le.getCodeInfo().one[1] + " }");
				mLogger.info("[addLearnListener] Repeat: {");
				for (int i = 0; i < 26; i++) {
					if (le.getCodeInfo().repeat[i] == 0) {
						break;
					}
					if (i == 0) {
						mLogger.info("" + le.getCodeInfo().repeat[i]);
					} else {
						mLogger.info(" ," + le.getCodeInfo().repeat[i]);
					}
				}
				mLogger.info("}\nMinRepeat: " + le.getCodeInfo().minRepeat);
				mLogger.info("Toggle Mask: ");

				toggleMaskBytes = 0;
				if ((le.getCodeInfo().bitCount % 8) == 0) {
					toggleMaskBytes = (le.getCodeInfo().bitCount / 8) + 0;
				} else {
					toggleMaskBytes = (le.getCodeInfo().bitCount / 8) + 1;
				}

				for (int i = 0; i < toggleMaskBytes; i++) {
					mLogger.info(le.getCodeInfo().toggleMask);
				}
				mLogger.info("[addLearnListener] Carrier Frequency: " + le.getCodeInfo().carrierFrequency + "\nDuty Cycle: " + le.getCodeInfo().dutyCycle + "\n");
				mLogger.info("----------------------------------------------------\n");
			}
		});
	}

	@Override
	public boolean start() {
		boolean result = false;
		try {
			ir.open(5000);
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
			ir.close();
			result = true;
		} catch (PhidgetException e) {
			mLogger.error("PhidgetException : " , e);
		}
		return result;
	}
	
	public void doAction(int code) {
		try {
			switch (code) {
				case ACTION_ON:
					re_transmit_on();
					break;
				case ACTION_OFF:
					re_transmit_off();
					break;
				case ACTION_UP:
					re_transmit_up();
					break;
				case ACTION_DOWN:
					re_transmit_down();
					break;
				default:
					mLogger.error("[doAction] unknown code");
					break;
			}
		} catch (PhidgetException e) {
			mLogger.error("PhidgetException : " , e);
		}
	}
	
	public void re_transmit_on() throws PhidgetException {
		mLogger.info("[re_transmit_on] ===Start");
		ir.transmitRaw(key_on, 0, 0, 0);
		mLogger.info("[re_transmit_on] ===Done");
	}
	
	public void re_transmit_off() throws PhidgetException  {
		mLogger.info("[re_transmit_off] ===Start");
		ir.transmitRaw(key_off, 0, 0, 0);
		mLogger.info("[re_transmit_off] ===Done");
	}

	public void re_transmit_up() throws PhidgetException  {
		mLogger.info("[re_transmit_up] ===Start");
		ir.transmitRaw(key_up, 0, 0, 0);
		mLogger.info("[re_transmit_up] ===Done");
	}

	public void re_transmit_down() throws PhidgetException  {
		mLogger.info("[re_transmit_down] ===Start");
		ir.transmitRaw(key_down, 0, 0, 0);
		mLogger.info("[re_transmit_down] ===Done");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setConfiguration(Properties config) {
		String on_code_string  = config.getProperty("key_on").trim();
		String off_code_string  = config.getProperty("key_off").trim();
		String up_code_string  = config.getProperty("key_up").trim();
		String down_code_string  = config.getProperty("key_down").trim();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			List<Integer> key_on_list = objectMapper.readValue(on_code_string, List.class);
			List<Integer> key_off_list  = objectMapper.readValue(off_code_string, List.class);
			List<Integer> key_up_list  = objectMapper.readValue(up_code_string, List.class);
			List<Integer> key_down_list  = objectMapper.readValue(down_code_string, List.class);
			
			key_on = new int[key_on_list.size()];
			key_off = new int[key_off_list.size()];
			key_up = new int[key_up_list.size()];
			key_down = new int[key_down_list.size()];
			for (int i = 0; i < key_on_list.size(); i++) {
				key_on[i] = key_on_list.get(i);
			}
			for (int i = 0; i < key_off_list.size(); i++) {
				key_off[i] = key_off_list.get(i);
			}
			for (int i = 0; i < key_up_list.size(); i++) {
				key_up[i] = key_up_list.get(i);
			}
			for (int i = 0; i < key_down_list.size(); i++) {
				key_down[i] = key_down_list.get(i);
			}
			mLogger.info("[setConfiguration] key_on = " + Arrays.toString(key_on));
			mLogger.info("[setConfiguration] key_off = " + Arrays.toString(key_off));
			mLogger.info("[setConfiguration] key_up = " + Arrays.toString(key_up));
			mLogger.info("[setConfiguration] key_down = " + Arrays.toString(key_down));
		} catch (JsonParseException e) {
			mLogger.error("JsonParseException :", e);
		} catch (JsonMappingException e) {
			mLogger.error("JsonMappingException :", e);
		} catch (IOException e) {
			mLogger.error("IOException :", e);
		}
	}

}
