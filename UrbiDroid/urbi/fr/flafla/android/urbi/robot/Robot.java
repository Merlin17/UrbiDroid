/**
 * 
 */
package fr.flafla.android.urbi.robot;


import fr.flafla.android.urbi.UClient;
import fr.flafla.android.urbi.control.Axes;
import fr.flafla.android.urbi.control.Camera;

/**
 * Abstract class that describe controller and devices of an urbi robot
 * 
 * @author merlin
 * 
 */
public abstract class Robot extends UClient {
	/**
	 * One only robot is managed
	 */
	public static Robot actuel;

	/** List of robot's axes */
	protected Axes[] axes;

	/** List of robot's cameras */
	protected Camera[] cameras;

	protected Robot(String ip, int port) {
		super(ip, port);
	}

	/**
	 * Method invoked to apply axes modification
	 */
	public abstract void move();

	/**
	 * @return list of robot's axes
	 */
	public Axes[] getAxes() {
		return axes;
	}

	/**
	 * @return list of robot's cameras
	 */
	public Camera[] getCameras() {
		return cameras;
	}
	
	
	protected void addTag(String name) {
		sendScript("var " + name + " = Channel.new(\"" + name + "\")|;");
	}

}
