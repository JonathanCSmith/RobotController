/*
 * This script is a part of the robot controller package. To run, you must have the phidget
 * libraries already installed on your computer
 * 
 * Author: Jonathan Smith, Imperial College London
 * 
 * Liscence: LGPL
 */

package org.robotcommunicator;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

public class OlimexCommunicator
{
	
	private static OlimexCommunicator instance;
	private static SerialPort port;
	private static InputStream input;
	private static OutputStream output;
	private static boolean connected;
	private static boolean busy;
	private static boolean debug = false;
	
	//Singleton constructer - ensures there is only one object
	private OlimexCommunicator() {
		OlimexCommunicator.connected = false;
		OlimexCommunicator.busy = false;
	}
	
	//Allows objects to reference this object instead of instantiate a new one
	public static synchronized OlimexCommunicator link() {
		if (instance == null) {
			instance = new OlimexCommunicator();
		}
		return instance;
	}

	//Connect option if no other variables are given
	public boolean connect(String portName) {
		return connect(portName, 4800);
	}
	
	//Connect protocol with debug catches
	public boolean connect(String portName, int speed) {
		CommPortIdentifier portID;
		if (debug) {
			System.out.println("We are in debug mode");
		}
		boolean connected = false;
		try {
			//Try to connect to the given comm port
			portID = CommPortIdentifier.getPortIdentifier(portName);
			if (portID.isCurrentlyOwned()) {
				if (debug) {
					System.out.println("The port is already owned");
				}
				return connected;
			} else {
				//Setup the comm port - use preset settings for the robot
				port = (SerialPort) portID.open("RobotController", 2000);
				port.setSerialPortParams(speed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
				port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				input = port.getInputStream();
				output = port.getOutputStream();
				
				try {
					port.addEventListener(new SerialReader(new BufferedInputStream(input)));
				} catch (TooManyListenersException e) {
					return connected;
				}
				
				port.notifyOnDataAvailable(true);
				connected = true;
			}
		} catch (NoSuchPortException e) {
			e.printStackTrace();
			if (debug) {
				System.out.println("No such port was found");
			}
			return connected;
		} catch (PortInUseException e) {
			if (debug) {
				System.out.println("The port was in use");
			}
			return connected;
		} catch (UnsupportedCommOperationException e) {
			if (debug) {
				System.out.println("An unsupported comm operation was triggered");
			}
			return connected;
		} catch (IOException e) {
			if (debug) {
				System.out.println("An IOException occured");
			}
			return connected;
		}
		
		OlimexCommunicator.connected = true;
		if (debug) {
			System.out.println("Connection status:" + OlimexCommunicator.connected);
		}
		return connected;
		
	}

	//Disconnect method, prevents port from hanging on the program's exit
	public boolean disconnect() {
		boolean disconnected = false;
		try {
			output.close();
		} catch (IOException e) {
			return disconnected;
		}
		
		try {
			input.close();
		} catch (IOException e) {
			return disconnected;
		}
		
		port.close();
		
		OlimexCommunicator.connected = false;
		return disconnected = true;
	}
	
	//Write using byte streams
	public boolean write(String[] instructions) {
		boolean sent = false;
		
		// Ensure device is connected and not writing already
		if (isConnected()) {
			if (busy) {
				if (debug) {
					System.out.println("The device is regestered as busy");
				}
				return sent;
				
			} else {
				try {
					//Prefix and suffix are constant for this type of robot
					busy = true;
					byte[] prefix = "2".getBytes("ASCII");
					byte[] suffix = "\r\n\r\n".getBytes("ASCII");
					
					output.write(prefix);
					if (debug) {
						System.out.println("The prefix was written");
					}
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						if (debug) {
							System.out.println("There was an interruption");
						}
					}

					//Iteratively send bytes, without time delays the process hangs
					for (int i = 0; i < instructions.length; i++) {
						byte[] content = instructions[i].getBytes("ASCII");
						
						for (int j = 0; j < content.length; j ++) {
							System.out.println("The byte values are: " + content[j]);
						}
						
						output.write(content);
						
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							if (debug) {
								System.out.println("There was an interruption");
							}
						}
						
						if (debug) {
							System.out.println("These instructions were written: " + instructions[i]);
						}
					}

					SerialReader.instructionsRecieved = false;
					SerialReader.stringStore = null;
					
					output.write(suffix);
					if (debug) {
						System.out.println("Suffix was written");
					}
					
					boolean data = waitForData();
					if (data) {
						sent = true;
					} else {
						sent = false;
					}

				} catch (IOException e){
				}
				busy = false;
				return sent;
			}
		} else {
			busy = false;
			return sent;
		}
	}

	//Ensures that the data has finished writing before we start sending the next signal
	// also ensures that the solenoid has finished
	private boolean waitForData() {
		
		boolean success = false;
		boolean loop = true;
		String store = null;
		
		while (loop) {
			if (SerialReader.instructionsRecieved) {
				String str = SerialReader.stringStore;
				if (store == null) {
					store = str;
				} else {
					store = store + str;
				}
				
				if (str.contains("1READY") || store.contains("1READY")) {
					SerialReader.instructionsRecieved = false;
					success = true;
					loop = false;
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
		
		return success;
	}
	
	//Methods for checking status
	public boolean isConnected() {
		return connected;
	}
	
	public boolean isBusy() {
		return busy;
	}
	
	public void setDebug(boolean b) {
		OlimexCommunicator.debug = b;
	}
	
}