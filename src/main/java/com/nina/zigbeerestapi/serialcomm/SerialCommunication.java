package com.nina.zigbeerestapi.serialcomm;

import com.nina.zigbeerestapi.core.Light;
import com.nina.zigbeerestapi.core.Lights;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;	
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.TooManyListenersException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class SerialCommunication{

	private String portName;
	private SerialPort serialPort;
	private OutputStream outStream;
	private InputStream inStream;
	private byte[] readBuffer = new byte[400];
	private Lights lights;

	public SerialCommunication (String portName, Lights lights){
		this.portName = portName;
		this.lights = lights;
	}

	public void init (){
		try {
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
	  		serialPort = (SerialPort) portId.open("Zigbee Rest Api", 5000);

	  		serialPort.setSerialPortParams(
				    38400,
				    SerialPort.DATABITS_8,
				    SerialPort.STOPBITS_1,
				    SerialPort.PARITY_NONE);

	  		serialPort.setFlowControlMode(
       				SerialPort.FLOWCONTROL_NONE);

	  		outStream = serialPort.getOutputStream();
			inStream = serialPort.getInputStream();
  		}

  		catch(Exception exc){
  			exc.printStackTrace();
  		}
	}

	public void startReading(){
		new Thread(new ReadThread()).start();
	}

	public void writeCommand(String command){
		try {
			outStream.write((command + "\r\n").getBytes());
		}
		catch (IOException exc){
			exc.printStackTrace();
		}
	}

 	private StringBuilder report = new StringBuilder(	);
 	private boolean waitLF = false;


	private void readSerial() {
	    try {
	        int availableBytes = inStream.available();
	        if (availableBytes > 0) {
	            // Read the serial port
	            inStream.read(readBuffer, 0, availableBytes);
	 			
	 			for(int i=0; i<availableBytes; i++) {
	 				byte character = readBuffer[i];
	 				//System.out.print((char)(character & 0xFF));
	 				if(waitLF) {
	 					if(character == '\n') {
		 					parseReport(report.toString());
		 					report.setLength(0);
		 					waitLF = false;
	 					}
	 					else if(character == '\r') {
	 						report.append("\r");
	 					}
	 					else {
	 						report.append("\r");
	 						report.append((char)character);
	 						waitLF = false;
	 					}	 					
	 				}
	 				else {
						if(character == '\r') {
	 						waitLF = true;
	 					}
	 					else {
	 						report.append((char)character);
	 					}	 					
	 				}
	 			}
	        }
	    } 
	    catch (IOException e) {
		}
	}

	private void parseReport(String report){
		System.out.println(report);
		String[] rep = report.split(";");
		try{
			String type = rep[0];

			if (type.equals("onOff") || type.equals("levelControl") 
					|| type.equals("modelId") || type.equals("stackVersion")){
				String shortNwkAddr = rep[1];
				String endpointId = rep[2];
				String value = rep[3];

				Light light = lights.getLightByAddress(shortNwkAddr);
				if (light == null) {
					light = lights.createLight();
					light.setEndpointId(endpointId);
					light.setShortNwkAddress(shortNwkAddr);
				}

				if(type.equals("onOff")){
					light.setOn(Integer.parseInt(value) > 0);
				}
				else if(type.equals("levelControl")){
					light.setBrightness(Integer.parseInt(value));
				}
				else if(type.equals("modelId")) {
					light.setModelId(value.equals("0")?"not specified":value);
				}
				else if (type.equals("stackVersion")) {
					light.setStackVersion(value.equals("0")?"not specified":value);
				}
			}
		}
		catch (Exception exc){
			exc.printStackTrace();
		}
	}
	
    private void setSerialEventHandler(SerialPort serialPort) {
	    try {
	        // Add the serial port event listener
	        serialPort.addEventListener(new SerialEventHandler());
	        serialPort.notifyOnDataAvailable(true);
	    } catch (TooManyListenersException ex) {
	        System.err.println(ex.getMessage());
	    }
	}

	private class SerialEventHandler implements SerialPortEventListener {
	    public void serialEvent(SerialPortEvent event) {
	        switch (event.getEventType()) {
	            case SerialPortEvent.DATA_AVAILABLE:
	                readSerial();
	                break;
	        }
	    }
	}

	private class ReadThread implements Runnable {
	    public void run() {
	        while(true) {
	            readSerial();
	        }
	    }
	}

}