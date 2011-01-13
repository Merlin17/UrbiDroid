/**
 * 
 */
package fr.flafla.android.urbi;

/**
 * Urbi Exception
 * 
 * @author merlin
 * 
 */
public class UrbiException extends RuntimeException {
	/** Serial ID */
	private static final long serialVersionUID = 1L;

	public UrbiException() {
		super();
	}

	public UrbiException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public UrbiException(String detailMessage) {
		super(detailMessage);
	}

	public UrbiException(Throwable throwable) {
		super(throwable);
	}

}
