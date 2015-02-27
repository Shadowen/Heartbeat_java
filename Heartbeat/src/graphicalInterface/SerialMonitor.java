package graphicalInterface;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.text.DefaultCaret;

import serialInterface.DataListener;
import serialInterface.SerialInterface;
import serialInterface.SerialPortNotOpenException;

public class SerialMonitor extends JPanel implements DataListener {
	private JScrollPane serialMonitorScrollPane;
	private JTextArea serialMonitorTextArea;
	private JTextField transmitTextField;

	private SerialInterface serialComm;

	/**
	 * Initializes a serial monitor that prints everything it recieves as a
	 * string. The serial monitor also includes a direct transmission box.
	 * 
	 * @param serialInterface
	 *            The serial interface connected to the serial monitor.
	 */
	public SerialMonitor(SerialInterface serialInterface) {
		serialComm = serialInterface;

		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		setLayout(new BorderLayout());

		serialMonitorTextArea = new JTextArea();
		serialMonitorTextArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret) serialMonitorTextArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		serialMonitorScrollPane = new JScrollPane(serialMonitorTextArea);
		add(serialMonitorScrollPane);

		transmitTextField = new JTextField(20);
		transmitTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					serialComm.sendData(0, transmitTextField.getText());
				} catch (IOException ioex) {
					System.err.println("Failed to send data.");
				} catch (SerialPortNotOpenException e1) {
					System.err
							.println("Attempted to send data with no port open!");
				}
				transmitTextField.setText("");
			}
		});
		add(transmitTextField, BorderLayout.PAGE_END);

	}

	/**
	 * The serial monitor simply prints the data it recieves as a string.
	 * 
	 */
	public void dataRecieved(int id, ByteBuffer data) {
		switch (id) {
		case 5:
		case 6:
			serialMonitorTextArea.append("(" + String.valueOf(id) + "): "
					+ data.get() + "\n");
			break;
		default:
			serialMonitorTextArea.append("(" + String.valueOf(id) + "): "
					+ new String(data.array()) + "\n");
		}
	}
}
