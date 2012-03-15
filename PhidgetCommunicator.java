/*
 * This script is a part of the robot controller package. To run, you must have the phidget
 * libraries already installed on your computer
 * 
 * Author: Jonathan Smith, Imperial College London
 * 
 * Liscence: LGPL
 */

package org.robotcommunicator;

import java.util.Vector;

import com.phidgets.PhidgetException;
import com.phidgets.StepperPhidget;
import com.phidgets.event.ErrorEvent;
import com.phidgets.event.ErrorListener;
import com.phidgets.event.StepperPositionChangeEvent;
import com.phidgets.event.StepperPositionChangeListener;

public class PhidgetCommunicator {
	
	private static PhidgetCommunicator instance;
	private static StepperPhidget stepper;
	public static boolean connected;
	public static boolean busy;
	private static boolean debug = false;
	public static int motorNumber;
	
	//Values represent locations of the sample wells
	private static final long[] locations = {0,192,400,592,800,992,1200,1392,1600,1792,2000,2192,2400,2592,2800,2992};
	
	StepperPositionChangeListener stepperListener;
	ErrorListener errorListener;
	
	//Singleton constructor - ensures there is only one object
	private PhidgetCommunicator() {
		try {
			stepper = new StepperPhidget();
			PhidgetCommunicator.connected = false;
			PhidgetCommunicator.busy = false;
			PhidgetCommunicator.motorNumber = -1;
		} catch (PhidgetException e) {
			
		}
	}
	
	//Allows objects to reference this object instead of instantiate a new one
	public static synchronized PhidgetCommunicator link() {
		if (instance == null) {
			instance = new PhidgetCommunicator();
		}
		
		return instance;
	}
	
	//Generate a list of the attached devices
	public Vector<String> listDevices() {
		Vector<String> deviceList;
		try {
			PhidgetCommunicator.stepper.openAny();
			PhidgetCommunicator.stepper.waitForAttachment();
			deviceList = new Vector<String>();
			for (int i = 0; i < stepper.getMotorCount(); i++) {
				deviceList.add(Integer.toString(i));
			}
		} catch (PhidgetException e) {
			if (debug){
				System.out.println("A phidget exception occured");
			}
			deviceList = null;
		}
		return deviceList;
	}
	
	//Connect protocol
	public boolean connect(int n) {
		boolean connected = false;
		PhidgetCommunicator.motorNumber = n;
		
		//Listeners that may be implemented later for debug
		stepper.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent e) {
				
			}
		});
		stepper.addStepperPositionChangeListener(new StepperPositionChangeListener() {
			public void stepperPositionChanged(StepperPositionChangeEvent e) {
				
			}
		});
		
		//Motor parameters set
		try {
			double minAccel = stepper.getAccelerationMin(n);
			double maxVel = stepper.getVelocityMax(n);
			
			stepper.setAcceleration(n, minAccel * 2);
			stepper.setVelocityLimit(n,maxVel/2);
			
			stepper.setEngaged(motorNumber, true);
			connected = true;
		} catch (PhidgetException e) {
			if (debug){
				System.out.println("A phidget exception occured");
			}
			return connected;
		}
		
		PhidgetCommunicator.connected = true;
		return connected;
	}
	
	
	//Disconnect protocol, ensures that the device doesn't remain locked
	public void disconnect() {
		System.out.println("disconnect entered");
		try {
			stepper.setEngaged(motorNumber, false);
			System.out.println("stepper disengaged");
			stepper.removeStepperPositionChangeListener(stepperListener);
            stepper.removeErrorListener(errorListener);
            stepper.close();
            stepper = null;
		} catch (PhidgetException e) {
			if (debug) {
				System.out.println("Could not disconnect phidget stepper motor");
				e.printStackTrace();
			}
		}
	}
	
	//Test to see if it is setup correctly
	public boolean handshake() {
		boolean success = false;
		if (busy) {
			return success;
		} else {
			try {
				stepper.setEngaged(motorNumber,true);
				int loc = getIndexLocation();
				int target = loc++;
				
				setTargetLocation(target);
				boolean data = waitForData(target);
				
				setTargetLocation(loc);
				boolean data2 = waitForData(target);
				
				if ((data) & (data2)) {
					success = true;
				} else {
					success = false;
				}
				
			} catch (PhidgetException e) {
				if (debug) {
					System.out.println("Could not disconnect phidget stepper motor");
					e.printStackTrace();
				}
			}
			return success;
		}
	}
	
	//Waits until the stepper motor has arrived at the target location
	public boolean waitForData(int loc) {
		
		long target = locations[loc];
		boolean success = false;
		boolean loop = true;
		long t0,t1;
		t0 = System.currentTimeMillis();
		try {
			do {
				t1 = System.currentTimeMillis();
				if ((t1-t0) > 10000) {
					success = false;
					loop = false;
				} else if (stepper.getCurrentPosition(motorNumber) == target) {
					success = true;
					loop = false;
				}
			} while (loop);
		} catch (PhidgetException e) {
			if (debug) {
				System.out.println("Could not disconnect phidget stepper motor");
				e.printStackTrace();
			}
		}
		return success;
	}
	
	//Sets the target location for the stepper motor
	public void setTargetLocation(int l){
		long loc = locations[l];
		try {
			stepper.setTargetPosition(motorNumber, loc);
		} catch (PhidgetException e) {
			if (debug) {
				System.out.println("Could not disconnect phidget stepper motor");
				e.printStackTrace();
			}
		}
	}
	
	//Gets the current location for the stepper motor
	private long getLocation(){
		try {
			return stepper.getCurrentPosition(motorNumber);
		} catch (PhidgetException e) {
			if (debug) {
				System.out.println("Could not disconnect phidget stepper motor");
				e.printStackTrace();
			}
			return 9999;
		}
	}
	
	//Gets the sample number
	public int getIndexLocation() {
		long l = getLocation();
		int loc = -1;
		long smallestDistanceFoundYet = Integer.MAX_VALUE;
		for (int i = 0; i < locations.length; i++) {
			if (l == locations[i]) {
				loc = i;
				break;
			} else {
				long d = Math.abs(l - locations[i]);
				if (d < smallestDistanceFoundYet) {
					smallestDistanceFoundYet = d;
					loc = i;
				}
			}
		}
		return loc;
	}
	
	public void setDebug(boolean b) {
		PhidgetCommunicator.debug = b;
	}

}
