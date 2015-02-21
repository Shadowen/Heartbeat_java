package serialInterface;

import java.nio.ByteBuffer;

public interface DataListener {
	/**
	 * Callback for serialInterface to call.
	 * 
	 * @param id
	 *            The identifier for the data.
	 * @param data
	 *            The actual data in a ByteBuffer.
	 */
	public void dataRecieved(int id, ByteBuffer intData);
}
