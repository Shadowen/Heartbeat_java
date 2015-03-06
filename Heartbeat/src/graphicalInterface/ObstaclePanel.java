package graphicalInterface;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import oracle.CyclingSpinnerListModel;
import serialInterface.SerialInterface;
import serialInterface.SerialPortNotOpenException;

public class ObstaclePanel extends JPanel implements ActionListener {
	private static final String[] allowableHopperX = { "0", "1", "2", "3", "4",
			"5" };
	private static final String[] allowableHopperY = { "0", "1", "2" };
	private JSpinner xSpinner;
	private JSpinner ySpinner;
	private JButton addButton;

	private SerialInterface serialInterface;

	public ObstaclePanel(SerialInterface s) {
		serialInterface = s;

		setBorder(BorderFactory.createLineBorder(Color.BLUE));

		JLabel xLabel = new JLabel("X");
		xSpinner = new JSpinner(new CyclingSpinnerListModel(allowableHopperX));
		xLabel.setLabelFor(xSpinner);
		add(xLabel);
		add(xSpinner);

		JLabel yLabel = new JLabel("Y");
		ySpinner = new JSpinner(new CyclingSpinnerListModel(allowableHopperY));
		yLabel.setLabelFor(ySpinner);
		add(yLabel);
		add(ySpinner);

		addButton = new JButton("Add");
		addButton.addActionListener(this);
		add(addButton);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		byte x = Byte.valueOf((String) xSpinner.getValue());
		byte y = Byte.valueOf((String) ySpinner.getValue());

		ByteBuffer buffer = ByteBuffer.allocate(2);
		buffer.put(x);
		buffer.put(y);
		try {
			serialInterface.sendData(6, buffer);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (SerialPortNotOpenException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
}
