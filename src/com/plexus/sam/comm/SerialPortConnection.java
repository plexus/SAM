/*
 * Created on 11-okt-2005
 *
 */
package com.plexus.sam.comm;

import gnu.io.CommPortIdentifier;
import gnu.io.CommPortOwnershipListener;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.TooManyListenersException;

import com.plexus.sam.SAM;
import com.plexus.sam.config.Configuration;

public class SerialPortConnection implements SerialPortEventListener {
	private boolean DEBUG = false;
	private InputStream	is;
	private SerialPort sPort;
	private CommPortIdentifier portId;
	private boolean open;
	private List<ByteListener> listeners;
	
	public SerialPortConnection() {
		open = false;
		listeners = new LinkedList<ByteListener>();
	}
	
	public void openConnection() {
		debug("openConnection");
		try {
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(Configuration.get("comm","serialport"));
			int timeout;
			try {
				timeout = Integer.parseInt(Configuration.get("comm", "timeoutms"));
			} catch (Exception e) {
				timeout = 30000;
			}
			sPort = (SerialPort) portId.open("SAM", timeout);
			if (sPort != null) {
				open = true;
				debug("openConnection >> port opened");
			} else
				debug("openConnection >> opening port failed");
		} catch (NoSuchPortException e) {
			SAM.error("invalid_serial_port", Configuration.get("comm","serialport"), e);
			debug("openConnection >> NoSuchPortException");
		} catch (PortInUseException e) {
			SAM.error("serial_port_in_use", Configuration.get("comm","serialport"), e);
			debug("openConnection >> PortInUseException");
		} catch (UnsatisfiedLinkError e) {
			SAM.error("serial_port_in_use", Configuration.get("comm","serialport"), e);
			debug("openConnection >> UnsatisfiedLinkError "+e.getMessage());
		}
		
		
		setConnectionParameters();
		
		
		if (open) {
			debug("openConnection >> parameters set, getting inputstream");
			try {
				is = sPort.getInputStream();
			} catch (IOException e) {
				SAM.error("couldnt_open_serial_for_input", e);
				debug("openConnection >> IOException while opening stream");
			}
			
			try {
				sPort.addEventListener(this);
			} catch (TooManyListenersException e) {
				SAM.error("couldnt_open_serial_for_input", e);
				sPort.close();
				open = false;
			}
		}
		
		if (open) {	
			//Set notifyOnDataAvailable to true to allow event driven input.
			sPort.notifyOnDataAvailable(true);
			
			//Set notifyOnBreakInterrup to allow event driven break handling.
			sPort.notifyOnBreakInterrupt(true);
			
			//Set receive timeout to allow breaking out of polling loop during
			//input handling.
			try {
				sPort.enableReceiveTimeout(30);
			} catch (UnsupportedCommOperationException e) {}
		}
	}
	
	private void debug(String string) {
		if (DEBUG)
			System.out.println("SerialPortConnection "+string);
		
	}

	public void setConnectionParameters() {
		debug("setConnectionParameters");
		if (open) {
			try {
				int baudRate = Integer.parseInt(Configuration.get("comm", "baudRate"));
				int databits = Integer.parseInt(Configuration.get("comm", "databits"));
				int stopbits = Integer.parseInt(Configuration.get("comm", "stopbits"));
				int parity = Integer.parseInt(Configuration.get("comm", "parity"));
				int flowControl = Integer.parseInt(Configuration.get("comm", "flowcontrol"));
				
				debug("setConnectionParameters >> setting baudrate:"+baudRate+" databits:"+databits+" stopbits:"+" parity:"+parity+" flowcontrol:"+flowControl);
				sPort.setSerialPortParams(baudRate, databits, stopbits, parity);
				sPort.setFlowControlMode(flowControl);
				
				
			} catch (UnsupportedCommOperationException e) {
				debug("setConnectionParameters >> UnsupportedCommOperationException "+e.getMessage());
				SAM.error("unsupported_serial_params", e.getMessage(), e);
				sPort.close();
				open = false;
			} catch (NumberFormatException e) {
				debug("setConnectionParameters >> NumberFormatException");
				SAM.error("no_number_serial_params", e.getMessage(), e);
				sPort.close();
				open = false;
			}
		}
	}
	
	public void closeConnection() {
		if (!open) {
			return;
		} 
		
		if (sPort != null) {
			try {
				is.close();
			} catch (IOException e) {
				System.err.println(e);
			} 
			
			sPort.close();
			
		} 
		
		open = false;
	} 
	
	
	
	public void serialEvent(SerialPortEvent e) {
		int	     newData = 0;
		
		if (e.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			while (newData != -1) {
				try {
					newData = is.read();
					
					if (newData != -1) 
						fireByteReceived(newData);
					
					
					
				} catch (IOException ex) {
					System.err.println(ex);
					
					return;
				} 
			} 
		}
	}

	private void fireByteReceived(int newData) {
		for (ByteListener bl : listeners) {
			bl.byteReceived((byte)newData);
		}
		
	}
	
	public void addByteListener(ByteListener list) {
		listeners.add(list);
	}
	
	public void removeByteListener(ByteListener list) {
		listeners.remove(list);
	}
	
	public boolean isOpen() {
		return open;
	}
	
}

