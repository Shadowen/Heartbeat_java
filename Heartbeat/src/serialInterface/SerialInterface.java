package serialInterface;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class SerialInterface implements SerialPortEventListener {

	private SerialPort serialPort;
	/** The input stream from the port */
	private InputStream input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;
	/** A list of DataListeners to be notified when data is available */
	private List<DataListener> dataListeners;

	// Possible states for the serial interface to be in
	/** Waiting for an identifier to arrive */
	private static final byte STATE_WAIT_IDENTIFIER = 0;
	/** Waiting for the length byte to arrive */
	private static final byte STATE_WAIT_LENGTH = 1;
	/** Waiting for the data to arrive */
	private static final byte STATE_WAIT_DATA = 2;
	/**
	 * The current state of the serial interface. Decides if it is waiting for a
	 * certain segment of the transmission. Must be one of:
	 * STATE_WAIT_IDENTIFIER, STATE_WAIT_LENGTH, or STATE_WAIT_DATA.
	 */
	private byte serialState;

	/** The id of the data being read. */
	private int currentId;
	/** The amount of bytes read in total for this piece of data */
	private int bytesReceived;
	/** How many bytes we are waiting for in total in the stream */
	private int bytesExpected;
	/**
	 * The data we are reading. May be incomplete when we are waiting for bytes
	 * to arrive.
	 */
	private ByteBuffer data;

	public SerialInterface() {
		dataListeners = new ArrayList<DataListener>();
	}

	public Enumeration getPortNames() {
		return CommPortIdentifier.getPortIdentifiers();
	}

	/**
	 * Connect the serial interface to the specified port.
	 * 
	 * @param portName
	 * @throws NoSuchPortException
	 */
	public void openPort(String portName) throws NoSuchPortException {
		System.out.print("Opening port: " + portName + "... ");

		// Find the matching port
		CommPortIdentifier portId = CommPortIdentifier
				.getPortIdentifier(portName);
		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

			// open the streams
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);

			serialState = STATE_WAIT_IDENTIFIER;

			System.out.println("success!");
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port. This will prevent
	 * port locking on platforms like Linux.
	 * 
	 */
	public synchronized void closePort() {
		if (serialPort != null) {
			System.out.println("Closing port: " + serialPort.getName());
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data into a buffer and
	 * notify listeners when a piece of data is complete.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				if (input.available() > 0) {
					switch (serialState) {
					case STATE_WAIT_IDENTIFIER:
						// Read identifier
						currentId = input.read();
						System.out.println("Recieving ID: " + currentId);
					case STATE_WAIT_LENGTH:
						// Read the length
						bytesExpected = input.read();
						// Could not find length in the stream
						if (bytesExpected == -1) {
							serialState = STATE_WAIT_LENGTH;
							break;
						}
						// Prepare for receiving the data
						data = ByteBuffer.allocate(bytesExpected);
						bytesReceived = 0;
						System.out.println("Expecting " + bytesExpected
								+ " bytes");
					case STATE_WAIT_DATA:
						// Read data
						bytesReceived += input.read(data.array(),
								bytesReceived, bytesExpected - bytesReceived);
						// Message is incomplete
						if (bytesReceived != bytesExpected) {
							serialState = STATE_WAIT_DATA;
							System.err.println("Still waiting for "
									+ String.valueOf(bytesExpected
											- bytesReceived) + " bytes.");
							break;
						}
						// Message is complete
						System.out.println("Data " + currentId + "("
								+ bytesExpected
								+ " bytes) successfully recieved");
						// Send data to the listener(s)
						for (DataListener l : dataListeners) {
							l.dataRecieved(currentId, data);
						}
						break;
					default:
						System.err.println("Error: Invalid state!");
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Ignore all the other eventTypes, but you should consider the other
		// ones.
	}

	/**
	 * Add an object that should be notified every time a data transmission is
	 * complete. Listeners are only notified when the complete object has been
	 * recieved.
	 * 
	 * @param listener
	 *            The object to be notified.
	 */
	public void addListener(DataListener listener) {
		dataListeners.add(listener);
	}

	/**
	 * This function is called to write data directly to the serial port.
	 * 
	 * @param id
	 *            The identifier to send the data as.
	 * @param data
	 *            The data to be written. Can be in any format compatible with
	 *            ByteBuffer.
	 * @throws IOException
	 *             Thrown if there is an error while writing to the output
	 *             stream.
	 * @throws SerialPortNotOpenException
	 *             Thrown if the serial port being written to has not been
	 *             opened.
	 */
	private void sendData(int id, ByteBuffer data) throws IOException,
			SerialPortNotOpenException {

		if (serialPort == null) {
			throw new SerialPortNotOpenException();
		}
		// ID
		output.write(id);
		// Length
		output.write(data.capacity());
		// Value
		output.write(data.array());

		System.out.println("Sent (" + id + ") [" + data.capacity() + "]: "
				+ new String(data.array()));
	}

	public void sendData(int id, int data) throws IOException,
			SerialPortNotOpenException {
		sendData(id, ByteBuffer.allocate(4).putInt(data));
	}

	public void sendData(int id, float data) throws IOException,
			SerialPortNotOpenException {
		sendData(id, ByteBuffer.allocate(4).putFloat(data));
	}

	public void sendData(int id, String data) throws IOException,
			SerialPortNotOpenException {
		sendData(id, ByteBuffer.wrap(data.getBytes()));
	}
}
