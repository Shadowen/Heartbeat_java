package graphicalInterface;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import serialInterface.DataListener;
import serialInterface.SerialInterface;

public class DisplayFrame extends JFrame implements DataListener {

	private static final String PORT_NONE = "None";
	private static final String BLACK_TEXT = "Black";
	private static final String WHITE_TEXT = "White";
	private SerialInterface serialComm;
	private SerialMonitor serialMonitorPanel;
	private BoardStatePanel boardStatePanel;
	private NavigationPanel navigationPanel;

	public static void main(String[] args) {
		new DisplayFrame();
	}

	public DisplayFrame() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		Toolkit tk = Toolkit.getDefaultToolkit();
		setSize((int) tk.getScreenSize().getWidth(), (int) tk.getScreenSize()
				.getHeight());
		setTitle("Heartbeat");
		initMenuBar();

		getContentPane().setLayout(new GridBagLayout());

		// Serial interface
		serialComm = new SerialInterface();
		serialComm.addListener(this);

		// Serial monitor
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 0.5;
		gbc.weighty = 0.5;

		serialMonitorPanel = new SerialMonitor(serialComm);
		gbc.gridx = 1;
		gbc.gridy = 1;
		add(serialMonitorPanel, gbc);

		// Board state
		boardStatePanel = new BoardStatePanel();
		gbc.gridx = 2;
		gbc.gridy = 2;
		add(boardStatePanel, gbc);

		// Navigation Panel
		navigationPanel = new NavigationPanel();
		gbc.gridx = 2;
		gbc.gridy = 1;
		add(navigationPanel, gbc);

		// TODO
		System.out.println("Ports Available:");
		Enumeration e = serialComm.getPortNames();
		while (e.hasMoreElements()) {
			CommPortIdentifier cpi = (CommPortIdentifier) e.nextElement();
			System.out.println(cpi.getName());
		}

		setVisible(true);
	}

	/**
	 * Initialize a self-contained menu bar for the application.
	 * 
	 */
	private void initMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu;
		JMenuItem menuItem;
		ButtonGroup buttonGroup;

		menu = new JMenu("Port");
		menu.setMnemonic('p');
		ActionListener portChangeListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				serialComm.closePort();
				try {
					String action = e.getActionCommand();
					if (!action.equals(PORT_NONE)) {
						serialComm.openPort(action);
					}
				} catch (NoSuchPortException e1) {
					System.err.println("Port could not be opened");
				}
			}
		};
		buttonGroup = new ButtonGroup();
		menuItem = new JRadioButtonMenuItem(PORT_NONE);
		menuItem.setSelected(true);
		menuItem.setMnemonic('0');
		menuItem.addActionListener(portChangeListener);
		buttonGroup.add(menuItem);
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JRadioButtonMenuItem("COM1");
		menuItem.setMnemonic('1');
		menuItem.addActionListener(portChangeListener);
		buttonGroup.add(menuItem);
		menu.add(menuItem);
		menuItem = new JRadioButtonMenuItem("COM2");
		menuItem.setMnemonic('2');
		menuItem.addActionListener(portChangeListener);
		buttonGroup.add(menuItem);
		menu.add(menuItem);
		menuItem = new JRadioButtonMenuItem("COM3");
		menuItem.setMnemonic('3');
		menuItem.addActionListener(portChangeListener);
		buttonGroup.add(menuItem);
		menu.add(menuItem);
		menuItem = new JRadioButtonMenuItem("COM4");
		menuItem.setMnemonic('4');
		menuItem.addActionListener(portChangeListener);
		buttonGroup.add(menuItem);
		menu.add(menuItem);
		menuBar.add(menu);

		// What colour am I
		menu = new JMenu("Colour");
		menu.setMnemonic('c');
		ActionListener colourChangeListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boardStatePanel.setColour(e.getActionCommand().equals(
						BLACK_TEXT));
			}
		};
		buttonGroup = new ButtonGroup();
		menuItem = new JRadioButtonMenuItem(BLACK_TEXT);
		menuItem.setSelected(true);
		menuItem.setMnemonic('b');
		menuItem.addActionListener(colourChangeListener);
		buttonGroup.add(menuItem);
		menu.add(menuItem);
		menuItem = new JRadioButtonMenuItem(WHITE_TEXT);
		menuItem.setMnemonic('w');
		menuItem.addActionListener(colourChangeListener);
		buttonGroup.add(menuItem);
		menu.add(menuItem);
		menuBar.add(menu);

		menu = new JMenu("Help");
		menuItem = new JMenuItem("Legal Information");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								DisplayFrame.this,
								"1: A robot may not injure a human being or, through inaction, allow a human being to come to harm;\n"
										+ "2: A robot must obey the orders given it by human beings except where such orders would conflict with the First Law;\n"
										+ "3: A robot must protect its own existence as long as such protection does not conflict with the First or Second Law;\n"
										+ "The Zeroth Law: A robot may not harm humanity, or, by inaction, allow humanity to come to harm.",
								"The Three Laws of Robotics",
								JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu.add(menuItem);
		menu.addSeparator();
		menuItem = new JMenuItem("About");
		menuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								DisplayFrame.this,
								"Heartbeat v3.14\nBy Wesley Heung",
								"About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu.add(menuItem);
		menuBar.add(menu);

		setJMenuBar(menuBar);
	}

	@Override
	public void dataRecieved(int id, ByteBuffer data) {
		// Give a duplicate of the data to the serial monitor.
		serialMonitorPanel.dataRecieved(id, data.duplicate());
		// Split different data to different places as necessary.
		switch (id) {
		case 10:
		case 11:
		case 12:
			navigationPanel.updateGrid(id, data.asReadOnlyBuffer());
			break;
		case 15:
			boardStatePanel.updateBoardState(data.asReadOnlyBuffer());
			break;
		}
	}
}
