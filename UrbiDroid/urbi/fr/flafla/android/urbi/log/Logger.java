/**
 * 
 */
package fr.flafla.android.urbi.log;

/**
 * This class is the default logger
 * 
 * @author merlin
 * 
 */
public class Logger {
	public void d(String tag, String msg) {
		System.out.println(msg);
	}

	public void d(String tag, String msg, Throwable e) {
		System.out.println(msg);
	}

	public void i(String tag, String msg) {
		System.out.println(msg);
	}

	public void i(String tag, String msg, Throwable e) {
		System.out.println(msg);
	}

	public void w(String tag, String msg) {
		System.out.println(msg);
	}

	public void w(String tag, String msg, Throwable e) {
		System.out.println(msg);
	}

	public void e(String tag, String msg) {
		System.out.println(msg);
	}

	public void e(String tag, String msg, Throwable e) {
		System.out.println(msg);
		e.printStackTrace();
	}
}
