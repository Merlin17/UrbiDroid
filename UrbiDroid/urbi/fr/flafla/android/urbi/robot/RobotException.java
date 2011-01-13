/**
 * 
 */
package fr.flafla.android.urbi.robot;

/**
 * Exception levée par le robot
 * 
 * @author merlin
 *
 */
public class RobotException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public RobotException() {
		super();
	}

	public RobotException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public RobotException(String detailMessage) {
		super(detailMessage);
	}

	public RobotException(Throwable throwable) {
		super(throwable);
	}

}
