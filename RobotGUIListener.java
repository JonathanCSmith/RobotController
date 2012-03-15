/*
 * This script is a part of the robot controller package. To run, you must have the phidget
 * libraries already installed on your computer
 * 
 * Author: Jonathan Smith, Imperial College London
 * 
 * Liscence: LGPL
 */

package org.robotcommunicator;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class RobotGUIListener implements ActionListener {
	
	public RobotGUIListener() {
		
	}

	//Action listeners for when the user interacts with the GUI
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// Which button was pressed
		if (e.getActionCommand().equals("Remove")) {
			// Remove the droplet group object GC should remove DropletGroup instantiation
			JButton src = (JButton) e.getSource();
			JPanel parent = (JPanel) src.getParent();
			DropletGroup.removePanel(parent);
			
		} else if (e.getActionCommand().equals("Add")) {
			
			String text1 = null;
			String text2 = null;
			String text3 = null;
			int jcb = -1;
			
			// Add the Droplet Group Object
			Object src = e.getSource();
			Object panel = ((JButton) src).getParent();
			if (src instanceof JButton) {
				Component[] components = ((JPanel) panel).getComponents();
				for (int k = 0; k<components.length; k++) {
					if (components[k] instanceof JPanel) {
						Component[] childComponents = ((JPanel) components[k]).getComponents();
						for (int l = 0; l < childComponents.length; l++) {
							if (childComponents[l] instanceof JTextField) {
								JTextField j1 = ((JTextField) childComponents[l]);
								if (j1.getName().equals("nDroplets")) {
									text1 = j1.getText();
									j1.setText("");
								} else if (j1.getName().equals("sDroplets")) {
									text3 = j1.getText();
									j1.setText("");
								} else if (j1.getName().equals("fDroplets")) {
									text2 = j1.getText();
									j1.setText("");
								}
							}
						}
					} else if (components[k] instanceof JComboBox) {
						JComboBox j1 = ((JComboBox) components[k]);
						jcb = j1.getSelectedIndex();
						j1.setSelectedIndex(0);
					}
				}
			}
			
			@SuppressWarnings("unused")
			DropletGroup d1 = new DropletGroup(jcb, text1, text2, text3);
			
			
		} else if (e.getActionCommand().equals("Run")) {
			RobotControllerGUI.instructions = true;
			RobotControllerGUI.loop = false;
			
		} else if (e.getActionCommand().equals("Retry")) {
			RobotHandler rh = RobotHandler.link();
			boolean success = rh.connect();
			if (success) {
				RobotControllerGUI rc = RobotControllerGUI.link();
				rc.updateStatus(true);
			}
			
		} else {
			System.out.println("Action command:" + e.getActionCommand());
		}
		
	}

}
