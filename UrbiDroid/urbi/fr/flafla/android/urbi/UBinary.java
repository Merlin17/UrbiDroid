/**
 * 
 */
package fr.flafla.android.urbi;

/**
 * @author merlin
 *
 */
public class UBinary extends UMessage {
	public final byte[] array;
	public final int length;

	public UBinary(String tag, String time, String msg, int length, byte[] array) {
		super(tag, time, msg);
		this.length = length;
		this.array = array;
	}

	@Override
	public String toString() {
		return "UMessage [tag=" + tag + ", time=" + time + ", msg=" + msg + ", length = " + length + ", array.length = " + array.length + "]";
	}
}
