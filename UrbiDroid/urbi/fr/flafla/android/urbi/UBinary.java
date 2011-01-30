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

	public UBinary(String tag, String time, String msg, byte[] array) {
		super(tag, time, msg);
		this.array = array;
	}

}
