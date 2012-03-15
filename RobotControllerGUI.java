package org.robotcommunicator;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;

public class RobotControllerGUI implements Runnable, WindowListener{

	private JFrame frame;
	private JButton btnRun;
	private JButton btnAdd;
	private JTextField nDroplets;
	private JTextField fDroplets;
	private JTextField sDroplets;
	private JComboBox comboBox;
	private JPanel displayPanel;
	private JPanel indicator;
	private static RobotHandler handler;
	public static boolean loop;
	private static RobotControllerGUI instance;
	private static boolean connection;
	private static boolean debug;
	private static boolean framePresent;
	public static boolean instructions;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			(new Thread(new RobotControllerGUI(args[0], "0"))).start();
			if (args.length > 1 && args.length < 3) {
				(new Thread(new RobotControllerGUI(args[0],args[1]))).start();
			}
		} else {
			(new Thread(new RobotControllerGUI())).start();
		}
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					RobotControllerGUI window = new RobotControllerGUI();
//					window.frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
	}

	/**
	 * Create the application.
	 */
	private RobotControllerGUI(String s, String s1) {
		//Construct runtime vars
		RobotControllerGUI.loop = true;
		RobotControllerGUI.connection = Boolean.valueOf(s1);
		RobotControllerGUI.debug = Boolean.valueOf(s);
		RobotControllerGUI.framePresent = true;
		
		//Call connection
		RobotControllerGUI.handler = RobotHandler.link();
		RobotHandler.parent = (this);
		if (connection) {
			RobotHandler.fake = true;
		}
		if (debug) {
			handler.setDebug(true);
			System.out.println("Debug mode enabled");
		}
		RobotHandler.inialize();
		boolean status = handler.connect();
		
		//Update initialise properties
		initialize();
		this.frame.setVisible(true);
		updateStatus(status);
	}
	
	private RobotControllerGUI(String s) {
		this(s,"false");
	}
	
	private RobotControllerGUI() {
		this("true","false");
	}
	
	public static synchronized RobotControllerGUI link() {
		if (instance == null) {
			instance = new RobotControllerGUI();
		}
		return instance;
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setName("RobotController");
		frame.addWindowListener(this);
		frame.setBounds(100, 100, 649, 403);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		String[] locArray = {"Position 1","Position 2","Position 3","Position 4","Position 5","Position 6","Position 7","Position 8","Position 9","Position 10","Position 11","Position 12","Position 13","Position 14","Position 15"};
		
		JPanel dropletGroupSettings = new JPanel();
		
		JPanel headerPanel = new JPanel();
		
		JPanel statusPanel = new JPanel();
		
		JScrollPane scrollPane = new JScrollPane();
		
		// Add display panel to simulate when the user adds a droplet group
		displayPanel = new JPanel();
		scrollPane.setViewportView(displayPanel);
		displayPanel.setLayout(new BoxLayout(displayPanel, BoxLayout.Y_AXIS));
		displayPanel.setPreferredSize(new Dimension(400,100));
		DropletGroup.parent = displayPanel;

		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(headerPanel, GroupLayout.DEFAULT_SIZE, 637, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
								.addComponent(statusPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(dropletGroupSettings, GroupLayout.PREFERRED_SIZE, 172, Short.MAX_VALUE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, 33, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(dropletGroupSettings, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(statusPanel, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE))
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
					.addContainerGap())
		);
		
		ActionListener l1 = new RobotGUIListener();
		btnRun = new JButton("Run");
		btnRun.addActionListener(l1);
		
		JLabel lblRobotCommunicator = new JLabel("Robot Communicator");
		GroupLayout gl_headerPanel = new GroupLayout(headerPanel);
		gl_headerPanel.setHorizontalGroup(
			gl_headerPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_headerPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblRobotCommunicator)
					.addPreferredGap(ComponentPlacement.RELATED, 495, Short.MAX_VALUE)
					.addComponent(btnRun))
		);
		gl_headerPanel.setVerticalGroup(
			gl_headerPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_headerPanel.createParallelGroup(Alignment.BASELINE)
					.addComponent(btnRun, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
					.addComponent(lblRobotCommunicator))
		);
		headerPanel.setLayout(gl_headerPanel);
		
		JLabel txtpnStatus = new JLabel();
		txtpnStatus.setText("Status:");
		
		indicator = new JPanel();
		
		JButton btnRetry = new JButton("Retry");
		btnRetry.addActionListener(l1);
		
		GroupLayout gl_statusPanel = new GroupLayout(statusPanel);
		gl_statusPanel.setHorizontalGroup(
			gl_statusPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_statusPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_statusPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_statusPanel.createSequentialGroup()
							.addComponent(txtpnStatus, GroupLayout.PREFERRED_SIZE, 80, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(btnRetry))
						.addComponent(indicator, GroupLayout.PREFERRED_SIZE, 161, GroupLayout.PREFERRED_SIZE)))
		);
		gl_statusPanel.setVerticalGroup(
			gl_statusPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_statusPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_statusPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtpnStatus, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnRetry))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(indicator, GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
					.addContainerGap())
		);
		statusPanel.setLayout(gl_statusPanel);
		comboBox = new JComboBox(locArray);
		
		JPanel panel1 = new JPanel();
		
		JLabel txtpnNumberOfDroplets_1 = new JLabel();
		txtpnNumberOfDroplets_1.setText("<html>Droplet Frequency</html>");
		
		fDroplets = new JTextField();
		fDroplets.setHorizontalAlignment(SwingConstants.RIGHT);
		fDroplets.setColumns(3);
		fDroplets.setName("fDroplets");
		
		JPanel panel = new JPanel();
		
		JLabel txtpnNumberOfDroplets = new JLabel();
		txtpnNumberOfDroplets.setText("<html>Number of Droplets</html>");
		
		nDroplets = new JTextField();
		nDroplets.setHorizontalAlignment(SwingConstants.RIGHT);
		nDroplets.setColumns(3);
		nDroplets.setName("nDroplets");
		
		JPanel panel2 = new JPanel();
		
		JLabel txtpnDropletSize = new JLabel();
		txtpnDropletSize.setText("<html>Droplet Size</html>");
		
		sDroplets = new JTextField();
		sDroplets.setHorizontalAlignment(SwingConstants.RIGHT);
		sDroplets.setColumns(3);
		sDroplets.setName("sDroplets");
		
		GroupLayout gl_panel2 = new GroupLayout(panel2);
		gl_panel2.setHorizontalGroup(
			gl_panel2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel2.createSequentialGroup()
					.addContainerGap()
					.addComponent(txtpnDropletSize, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(sDroplets, GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel2.setVerticalGroup(
			gl_panel2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel2.createParallelGroup(Alignment.LEADING)
						.addComponent(sDroplets, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(txtpnDropletSize, GroupLayout.PREFERRED_SIZE, 36, GroupLayout.PREFERRED_SIZE))
					.addGap(5))
		);
		panel2.setLayout(gl_panel2);
		
		btnAdd = new JButton("Add");
		btnAdd.addActionListener(l1);
		
		GroupLayout gl_dropletGroupSettings = new GroupLayout(dropletGroupSettings);
		gl_dropletGroupSettings.setHorizontalGroup(
			gl_dropletGroupSettings.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_dropletGroupSettings.createSequentialGroup()
					.addGroup(gl_dropletGroupSettings.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_dropletGroupSettings.createParallelGroup(Alignment.LEADING, false)
							.addComponent(comboBox, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(panel2, GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
							.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(gl_dropletGroupSettings.createSequentialGroup()
							.addGap(48)
							.addComponent(btnAdd)))
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_dropletGroupSettings.setVerticalGroup(
			gl_dropletGroupSettings.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_dropletGroupSettings.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel1, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel2, GroupLayout.PREFERRED_SIZE, 47, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 37, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAdd)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		GroupLayout gl_panel1 = new GroupLayout(panel1);
		gl_panel1.setHorizontalGroup(
			gl_panel1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel1.createSequentialGroup()
					.addContainerGap()
					.addComponent(txtpnNumberOfDroplets_1, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(fDroplets, GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel1.setVerticalGroup(
			gl_panel1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel1.createParallelGroup(Alignment.TRAILING)
						.addComponent(fDroplets, Alignment.LEADING)
						.addComponent(txtpnNumberOfDroplets_1, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(32))
		);
		panel1.setLayout(gl_panel1);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addComponent(txtpnNumberOfDroplets, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(nDroplets, GroupLayout.DEFAULT_SIZE, 57, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel.createParallelGroup(Alignment.TRAILING)
						.addComponent(txtpnNumberOfDroplets, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
						.addComponent(nDroplets, Alignment.LEADING))
					.addGap(40))
		);
		panel.setLayout(gl_panel);
		dropletGroupSettings.setLayout(gl_dropletGroupSettings);
		frame.getContentPane().setLayout(groupLayout);
	}
	
	public void updateStatus(boolean status) {
		
		btnRun.setEnabled(status);
		btnAdd.setEnabled(status);
		nDroplets.setEnabled(status);
		fDroplets.setEnabled(status);
		sDroplets.setEnabled(status);
		comboBox.setEnabled(status);
		
		if (status) {
			this.setIndicatorColor(Color.green);
		} else {
			this.setIndicatorColor(Color.red);
		}
		
	}

	private String[][] getDropletSequence() {
		Component[] components = displayPanel.getComponents();
		String[][] instructions = new String[components.length][4];
		for (int i = 0; i < components.length; i++) {
			Component[] childComponents = ((JPanel) components[i]).getComponents();
			for (int j = 0; j < childComponents.length; j++) {
				if (childComponents[j].getName() != null && childComponents[j].getName().equals("panelDropletPosition")) {
					Component[] containerComponents = ((JPanel) childComponents[j]).getComponents();
					for (int k = 0; k < containerComponents.length; k++) {
						if (containerComponents[k].getName() != null && containerComponents[k].getName().equals("lblR")) {
							instructions[i][0] = ((JLabel) containerComponents[k]).getText();
						}
					}
				} else if (childComponents[j].getName() != null && childComponents[j].getName().equals("panelDropletFrequency")) {
					Component[] containerComponents = ((JPanel) childComponents[j]).getComponents();
					for (int k = 0; k < containerComponents.length; k++) {
						if (containerComponents[k].getName() != null && containerComponents[k].getName().equals("lblH")) {
							instructions[i][1] = ((JLabel) containerComponents[k]).getText() + " ";
						}
					}
					
				} else if (childComponents[j].getName() != null && childComponents[j].getName().equals("panelDropletNumber")) {
					Component[] containerComponents = ((JPanel) childComponents[j]).getComponents();
					for (int k = 0; k < containerComponents.length; k++) {
						if (containerComponents[k].getName() != null && containerComponents[k].getName().equals("lblN")) {
							instructions[i][2] = ((JLabel) containerComponents[k]).getText() + " ";
						}
					}
					
				} else if (childComponents[j].getName() != null && childComponents[j].getName().equals("panelDropletSize")) {
					Component[] containerComponents = ((JPanel) childComponents[j]).getComponents();
					for (int k = 0; k < containerComponents.length; k++) {
						if (containerComponents[k].getName() != null && containerComponents[k].getName().equals("lblS")) {
							instructions[i][3] = ((JLabel) containerComponents[k]).getText();
						}
					}
					
				}
			
			}
		}
		return instructions;
	}

	@Override
	public void run() {
		try {
			while (RobotControllerGUI.framePresent) {
				while(RobotControllerGUI.loop) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {

					} 
				}
				
				if (RobotControllerGUI.instructions) {

					String[][] instructions = getDropletSequence();

					for (int i = 0; i < instructions.length; i++) {
						boolean writeStatus = handler.write(instructions[i]);
						if (!writeStatus) {
							//TODO error handling here
						}

						while (RobotHandler.busy) {
							Thread.sleep(1000);
						}
					}
					RobotControllerGUI.loop = true;

				}
			}
		} catch (Throwable e) {

		} finally {
			System.out.println("This does get called on the window exit");
			handler.disconnect();
			System.exit(1);
		}
	}

	public void setIndicatorColor(Color c) {
		indicator.setBackground(c);
	}
	
	public boolean getDebugMode() {
		return debug;
	}
	
	public boolean getConnectionMode() {
		return connection;
	}

	@Override
	public void windowClosed(WindowEvent e) {
		System.out.println("The close window event has been called");
		Object src = e.getSource();
		if (src instanceof JFrame && ((JFrame) src).getName().equals("RobotController")) {
			RobotControllerGUI.instructions = false;
			RobotControllerGUI.loop = false;
			RobotControllerGUI.framePresent = false;
		}
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
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
