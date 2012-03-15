/*
 * This script is a part of the robot controller package. To run, you must have the phidget
 * libraries already installed on your computer
 * 
 * Author: Jonathan Smith, Imperial College London
 * 
 * Liscence: LGPL
 */

package org.robotcommunicator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class ConnectionGUI implements ActionListener, WindowListener {
	
	public boolean framePresent;
	public int portSelection;
	private JFrame frame;
	private JPanel contentPane;
	private JList list;
	
	//Object instantiation
	public ConnectionGUI(boolean type) {
		this.portSelection = -1;
		this.framePresent = false;
		this.buildOlimexGUI(type);
	}
	
	public int getSelectedPort() {
		
		return portSelection;
	}
	
	//Builds a GUI that allows the user to select from all attached comm port or stepper motor devices
	private void buildOlimexGUI(boolean type) {
		Vector<String> ports;
		if (type) {
			//Build olimex list here
			ports = RobotHandler.getPortsList();
		} else {
			//Build phidget list here
			ports = RobotHandler.getDevicesList();	
		}
		
		//Build GUI here
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(100,100,400,450);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.setContentPane(contentPane);
		frame.addWindowListener(this);
		
		JLabel lblConnectTo = new JLabel("Connect to:");
		
		JLabel lblNewLabel = new JLabel("Device");
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(this);
		
		JLabel lblDescriptiveTextGoes = new JLabel("Descriptive Text Goes Here");
		
		JLabel lblMayNeedSome = new JLabel("May Need Some Text Here Too!");
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblConnectTo)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(lblNewLabel)
							.addContainerGap(244, Short.MAX_VALUE))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(lblMayNeedSome)
							.addPreferredGap(ComponentPlacement.RELATED, 227, Short.MAX_VALUE)
							.addComponent(btnConnect))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(lblDescriptiveTextGoes)
							.addContainerGap(323, Short.MAX_VALUE))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblConnectTo)
						.addComponent(lblNewLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblDescriptiveTextGoes)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnConnect)
						.addComponent(lblMayNeedSome)))
		);
		
		JList list = new JList();
		list.setListData(ports);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(list);
		contentPane.setLayout(gl_contentPane);
		frame.setVisible(true);
		this.list = list;
		this.framePresent = true;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Connect")) {
			portSelection = this.list.getSelectedIndex(); // TODO May need to check that the index is not off by 1!
			frame.setVisible(false);
			frame.dispose();
		}
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		this.framePresent = false;
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
