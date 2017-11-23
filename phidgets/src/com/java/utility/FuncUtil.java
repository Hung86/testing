package com.java.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FuncUtil {
	public static Properties getPropertiesFile(String _configFile) {
		Properties _props = new Properties();
		FileInputStream in = null;
		try {
			in = new FileInputStream(_configFile);
			_props.load(in);
		} catch (FileNotFoundException e) {
			System.out.println("Can't find configuration file: " + _configFile );
		} catch (IOException e) {
			System.out.println("Read failure for configuration file: " + _configFile);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return _props;
	}
	
	public static boolean setPropertiesFile(String _configFile, Properties _properties) {
		FileOutputStream fos = null;
		boolean ok = false;
		try {
			File file = new File(_configFile);
			file.createNewFile();
			file.setReadable(true, false);
			file.setWritable(true, false);
			fos = new FileOutputStream(file);
			_properties.store(fos, "Configuration file");
			ok = true;
		} catch (Exception e1) {
			System.out.println("Fail to create a new configuration file: " + _configFile);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		return ok;
	}
}
