package fr.flafla.android.urbi;

/**
 * This class describe a message from urbi server.
 * 
 * @author merlin
 * 
 */
public class UMessage {
	/** The concerned tag */
	public final String tag;
	/** Urbi time */
	public final String time;
	/** Message */
	public final String msg;

	public UMessage(String tag, String time, String msg) {
		this.tag = tag;
		this.time = time;
		this.msg = msg;
	}

}
