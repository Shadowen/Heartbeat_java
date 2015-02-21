package serialInterface;

public class SerialIdentifier {
	public final String name;
	public final int byteLength;

	SerialIdentifier(String in, int ibl) {
		byteLength = ibl;
		name = in;
	}
}
