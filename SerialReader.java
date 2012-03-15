/*
 * This script is a part of the robot controller package. To run, you must have the phidget
 * libraries already installed on your computer
 * 
 * Author: Jonathan Smith, Imperial College London
 * 
 * Liscence: LGPL
 */

package org.robotcommunicator;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * Handles the input coming from the serial port. A new line character
 * is treated as the end of a block in this example. 
 */
public class SerialReader implements SerialPortEventListener 
{
    private BufferedInputStream in;
    public String input;
    public static boolean instructionsRecieved;
    public static String stringStore;
    
    //Initialise
    public SerialReader ( BufferedInputStream in ) {
        this.in = in;
        SerialReader.instructionsRecieved = false;
        SerialReader.stringStore = null;
    }
    
    //Status mehtods
    public static void setDataAvailable(boolean set) {
    	SerialReader.instructionsRecieved = set;
    }
    
    public String getData() {
    	return SerialReader.stringStore;
    }
    
    public void resetData() {
    	SerialReader.stringStore = null;
    }
    
    //Upon serial event, record the data within this object
    public void serialEvent(SerialPortEvent arg0) {
    	
    	try {
    		Thread.sleep(50);
    	} catch (InterruptedException e) {
    		
    	}
    	
    	try {
    		
    		byte[] buffer = new byte[in.available()];
    		in.read(buffer);
    		String str = new String(buffer);
    		System.out.println(str);
    		stringStore = str;
    		instructionsRecieved = true;
    		
    	} catch (IOException e) {
    		
    	}

//        try {
//			byte[] buffer = new byte[in.available()];
//			in.read(buffer);
//			String str = new String(buffer);
//			System.out.println(str);
//			
//			if (str.contains("1READY") || SerialReader.stringStore.contains("1READY")) {
//				SerialReader.instructionsRecieved = true;
//				SerialReader.stringStore = null;
//			} else {
//				if (SerialReader.stringStore == null) {
//					SerialReader.stringStore = str;
//					System.out.println("String Store: " + SerialReader.stringStore);
//				} else {
//					SerialReader.stringStore = SerialReader.stringStore + str;
//					System.out.println("String Store: " + SerialReader.stringStore);
//				}
//			}
//		} catch (IOException e) {
//			
//		}
         
    }

}