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

import java.util.Enumeration;
import java.util.Vector;

public class RobotHandler {

	public static int baudRate;
	private static OlimexCommunicator olimex = null;
	private static PhidgetCommunicator phidget = null;
	public static boolean connected;
	public static boolean busy;
	private static RobotHandler instance;
	public static boolean debug = false;
	public static boolean fake = false;
	public static RobotControllerGUI parent;
	private static Vector<String> portsList;
	private static Vector<String> deviceList;

	//Instantiate
	private RobotHandler() {
		RobotHandler.baudRate = 4800;
		RobotHandler.connected = false;
		RobotHandler.busy = false;
		RobotHandler.portsList = null;
		RobotHandler.deviceList = null;
	}
	
	//Generate the robot communication modules
	public static void inialize() {
		if (fake) {
			if (debug) {
				System.out.println("No instantiation of phidget/olimex has occured due to fake connection");
			}
		} else {
			RobotHandler.olimex = OlimexCommunicator.link();
			RobotHandler.phidget = PhidgetCommunicator.link();
			if (debug) {
				System.out.println("Attached devices are in debug mode");
				olimex.setDebug(debug);
				phidget.setDebug(debug);
			}
		}
	}

	//Ensure that there is only ever one RobotHandler object
	public static synchronized RobotHandler link() {
		if (instance == null) {
			instance = new RobotHandler();
		} 
		return instance;
	}

	//Connect to the select devices and run a handshake test
	public boolean connect() {
		boolean status = false;
		boolean olimexStatus = false;
		boolean phidgetStatus = false;
		
		//Debug and testing when no robot
		if (fake) {
			if (debug) {
				System.out.println("Fake connection has been setup.");
			}
			ConnectionGUI connectionGUI = new ConnectionGUI(true);
			while(connectionGUI.framePresent) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {

				}
			}
			if (debug) {
				int selectedPort = connectionGUI.getSelectedPort();
				System.out.println("This was the selected port:" + RobotHandler.getPortsList().elementAt(selectedPort));
			}
				
			connectionGUI = new ConnectionGUI(false);
			while(connectionGUI.framePresent) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					
				}
			}
			if (debug) {
				int selectedPort = connectionGUI.getSelectedPort();
				System.out.println("This was the selected device:" + RobotHandler.getDevicesList().elementAt(selectedPort));
			}
			
			return status = true;
		}

		//Connection GUI
		ConnectionGUI connectionGUI = new ConnectionGUI(true);

		//Wait for port select
		while(connectionGUI.framePresent) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {

			}
		}

		//Attempt to connect
		int selectedPort = connectionGUI.getSelectedPort();
		if (selectedPort == -1) {
			return status;
		}
		if (olimex.connect(RobotHandler.getPortsList().elementAt(selectedPort),RobotHandler.baudRate)) {
			// TODO time delay and multiple access may change ports list...
			olimexStatus = true;
		} else {
			// TODO error catch on connection error
			return status;
		}

		//Connection GUI
		connectionGUI = new ConnectionGUI(false);
		
		//Wait for device select
		while(connectionGUI.framePresent) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}

		//Attempt to connect to the port
		selectedPort = connectionGUI.getSelectedPort();
		if (selectedPort == -1) {
			return status;
		}
		if (phidget.connect(selectedPort)) {
			// TODO time delay and multiple access may change ports list...
			phidgetStatus = true;
		}

		//Perform handshake run
		if ((phidgetStatus) & (olimexStatus)) {
			status = handshake();
		} else {
			status = false;
		}

		return status;
	}

	//Disconnect from all devices
	public boolean disconnect() {
		boolean status = false;
		
		if (fake) {
			if (debug) {
				System.out.println("Fake connection has been disconnected.");
			}
			return status = true;
		}
		phidget.disconnect();
		status = olimex.disconnect();
		return status;
	}

	//Write to the robot
	public boolean write(String[] instructions) {
		boolean status = false;
		RobotHandler.busy = true;
		//parent.setIndicatorColor(Color.blue);
		
		int sampleTarget = Integer.parseInt(instructions[0]);
		
		//Move to sample
		if (!fake) {
			int pos = phidget.getIndexLocation();
			if (pos != sampleTarget) {
				phidget.setTargetLocation(sampleTarget);
			}
			if (debug) {
				System.out.println("The target location is: " + sampleTarget);
			}
			boolean data = phidget.waitForData(sampleTarget);
			if (!data) {
				if (debug) {
					System.out.println("Timed out");
				}
				return status;
			} else {
				if (debug) {
					System.out.println("Regestered correct location");
				}
			}
		} else {
			if (debug) {
				System.out.println("The recieved position was:" + sampleTarget);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
		
		//Write to solenoid
		String[] solenoidInstructions = new String[3];
		System.arraycopy(instructions, 1, solenoidInstructions, 0, 3);
		if (!fake) {
			if (debug) {
				System.out.println("The received instructions were:");
				for (int i = 0; i < solenoidInstructions.length; i++) {
					System.out.println("Instruction " + i + " was " + solenoidInstructions[i]);
				}
			}
			boolean data2 = olimex.write(solenoidInstructions);
			if (!data2) {
				if (debug) {
					System.out.println("Olimex write failed");
				}
				return status;
			} else {
				if (debug) {
					System.out.println("Olimex write succeeded");
				}
			}
		} else {
			if (debug) {
				System.out.println("The received instructions were:");
				for (int i = 0; i < solenoidInstructions.length; i++) {
					System.out.println("Instruction " + i + " was " + solenoidInstructions[i]);
				}
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				
			}
		}
		
		while (olimex.isBusy()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				
			}
		}
		
		//Return
		if (!fake) {
			phidget.setTargetLocation(0);
			boolean data3 = phidget.waitForData(0);
			if (!data3) {
				return status;
			}
		}
		
		status = true;
		//parent.setIndicatorColor(Color.green);
		RobotHandler.busy = false;
		return status;
	}
	
	//Handshake test parameters
	private boolean handshake() {
		boolean status = false;
		
		//Simulate Arrays - loc, freq, num, siz
		String[] instructions = {"10", "100 ", "10 ", "10"};
		
		status = write(instructions);
		
		return status;
		
	}
	
	//Ensure there is only ever one ports list (reduce concurency issues)
	public static Vector<String> getPortsList() {
		if (portsList == null) {
			portsList = generatePortsList();
		}
		return portsList;
	}

	//Find all ports, get their properties and build a vector out of them
	@SuppressWarnings("unchecked")
	private static Vector<String> generatePortsList() {
		if (!fake) {
			Enumeration<CommPortIdentifier> portList;
			Vector<String> portVect = new Vector<String>();
			portList = CommPortIdentifier.getPortIdentifiers();

			CommPortIdentifier portId;
			while (portList.hasMoreElements()) {
				portId = (CommPortIdentifier) portList.nextElement();
				if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					portVect.add(portId.getName());
				}
			}

			return portVect;
		} else {
			if (debug) {
				System.out.println("A fake ports list was generated");
			}
			Vector<String> portVect = new Vector<String>();
			portVect.add("Fake Port 1");
			portVect.add("Fake Port 2");
			portVect.add("Fake Port 3");
			return portVect;
		}
	}

	//Ensure there is only ever one device list (reduce concurency issues)
	public static Vector<String> getDevicesList() {
		if (deviceList == null) {
			deviceList = generateDevicesList();
		}
		return deviceList;
	}
	
	public void setDebug(boolean b) {
		RobotHandler.debug = b;
	}
	
	//Find all devices, get their properties and build a vector out of them
	private static Vector<String> generateDevicesList() {
		if (!fake) {
			Vector<String> deviceList = phidget.listDevices();
			return deviceList;
		} else {
			Vector<String> deviceList = new Vector<String>();
			deviceList.add("Fake Device 1");
			deviceList.add("Fake Device 2");
			deviceList.add("Fake Device 3");
			return deviceList;
		}
	}

}
