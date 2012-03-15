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
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class DropletGroup {
	
	String[] instructions = new String[4];
	JPanel panel;
	static JPanel parent;
	
	public DropletGroup(int jcb, String text1, String text2, String text3) {
		
		// Catch for non integers
		int intText1, intText2, intText3;
		try {
			intText1 = Integer.parseInt(text1);
			intText2 = Integer.parseInt(text2);
			intText3 = Integer.parseInt(text3);
		} catch (NumberFormatException e) {
			intText1 = -1;
			intText2 = -1;
			intText3 = -1;
		}
		
		// Test to see if integers are valid
		boolean test1, test2, test3, test4;
		
		if (intText1 > 0) {
			test1 = true;
		} else {
			test1 = false;
		}
		
		if (intText2 > 0 && intText2 < 2000) {
			test2 = true;
		} else {
			test2 = false;
		}
		
		if (intText3 > 0 && intText3 < 101) {
			test3 = true;
		} else {
			test3 = false;
		}
		
		if (jcb == -1) {
			test4 = false;
		} else {
			jcb++;
			test4 = true;
		}
		
		// Check to see that everything went well
		if (test1 != false && test2 != false && test3 != false && test4 != false) {
			// Instructions Constructor
			this.instructions[0] = text1;
			this.instructions[1] = text2;
			this.instructions[2] = text3;
			this.instructions[3] = Integer.toString(jcb);
			
			// JPanel Constructor
			this.addPanel(text1, text2, text3, jcb);
		} else {
			// Display user warning and do nothing (return)
		}

	}
	
	//Function to generate a paneled representation of the last droplet group created (i.e. location, number, size and frequency)
	private void addPanel(String text1, String text2, String text3, int jcb) {
		// Main Container
		this.panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		// Droplet Position container
		JPanel panelDropletPosition = new JPanel();
		panelDropletPosition.setPreferredSize(new Dimension(50,50));
		panelDropletPosition.setMinimumSize(new Dimension(100,50));
		panelDropletPosition.setMaximumSize(new Dimension(100,50));
		panelDropletPosition.setName("panelDropletPosition");
		panel.add(panelDropletPosition);
		panelDropletPosition.setLayout(new BoxLayout(panelDropletPosition, BoxLayout.Y_AXIS));
	
		JLabel lblPosition = new JLabel("<html><center>Sample Position:</center></html>");
		lblPosition.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelDropletPosition.add(lblPosition);
		
		JLabel lblR = new JLabel(Integer.toString(jcb));
		lblR.setName("lblR");
		lblR.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblR.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		panelDropletPosition.add(lblR);
		
		// Droplet Number container
		JPanel panelDropletNumber = new JPanel();
		panelDropletNumber.setPreferredSize(new Dimension (75,50));
		panelDropletNumber.setMinimumSize(new Dimension(100, 50));
		panelDropletNumber.setMaximumSize(new Dimension(100, 50));
		panelDropletNumber.setName("panelDropletNumber");
		panel.add(panelDropletNumber);
		panelDropletNumber.setLayout(new BoxLayout(panelDropletNumber, BoxLayout.Y_AXIS));
		
		JLabel lblDropletNumber = new JLabel("<html><center>Droplet Number:</center></html>");
		lblDropletNumber.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelDropletNumber.add(lblDropletNumber);
		
		JLabel lblN = new JLabel(text1);
		lblN.setName("lblN");
		lblN.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblN.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		panelDropletNumber.add(lblN);
		
		// Droplet Frequency container
		JPanel panelDropletFrequency = new JPanel();
		panelDropletFrequency.setPreferredSize(new Dimension(75,50));
		panelDropletFrequency.setMinimumSize(new Dimension(100, 50));
		panelDropletFrequency.setMaximumSize(new Dimension(100, 50));
		panelDropletFrequency.setName("panelDropletFrequency");
		panel.add(panelDropletFrequency);
		panelDropletFrequency.setLayout(new BoxLayout(panelDropletFrequency, BoxLayout.Y_AXIS));
		
		JLabel lblDropletFrequency = new JLabel("<html><center>Droplet Frequency:</center></html>");
		lblDropletFrequency.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelDropletFrequency.add(lblDropletFrequency);
		
		JLabel lblH = new JLabel(text2);
		lblH.setName("lblH");
		lblH.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblH.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		lblH.setHorizontalAlignment(SwingConstants.CENTER);
		panelDropletFrequency.add(lblH);
		
		// Droplet size container
		JPanel panelDropletSize = new JPanel();
		panelDropletSize.setPreferredSize(new Dimension(75,50));
		panelDropletSize.setMinimumSize(new Dimension(100, 50));
		panelDropletSize.setMaximumSize(new Dimension(100, 50));
		panelDropletSize.setName("panelDropletSize");
		panel.add(panelDropletSize);
		panelDropletSize.setLayout(new BoxLayout(panelDropletSize, BoxLayout.Y_AXIS));
		
		JLabel lblDropletSize = new JLabel("<html><center>Droplet Sizes (%):</center></html>");
		lblDropletSize.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelDropletSize.add(lblDropletSize);
		
		JLabel lblS = new JLabel(text3);
		lblS.setName("lblS");
		lblS.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblS.setAlignmentY(Component.BOTTOM_ALIGNMENT);
		panelDropletSize.add(lblS);
		
		// Remove button
		JButton btnRemove = new JButton("Remove");
		panel.add(btnRemove);
		ActionListener l = new RobotGUIListener();
		btnRemove.addActionListener(l);

		// Add the panel
		DropletGroup.parent.setPreferredSize(new Dimension(400,DropletGroup.parent.getHeight() + 100));
		DropletGroup.parent.add(panel);
		DropletGroup.parent.revalidate();
		
	}
	
	//Removes the panel
	static public void removePanel(JPanel p) {
		DropletGroup.parent.remove(p);
		DropletGroup.parent.setPreferredSize(new Dimension(400,DropletGroup.parent.getHeight() - 100));
		DropletGroup.parent.revalidate();
		DropletGroup.parent.repaint();
	}

}
