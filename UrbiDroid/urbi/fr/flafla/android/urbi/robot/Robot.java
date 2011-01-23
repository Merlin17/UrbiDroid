/**
 * 
 */
package fr.flafla.android.urbi.robot;


import fr.flafla.android.urbi.UClient;
import fr.flafla.android.urbi.control.Axes;
import fr.flafla.android.urbi.control.Camera;

/**
 * Classe abstraite d'accès aux fonctionnalité d'un robot géré par urbi
 * 
 * @author merlin
 *
 */
public abstract class Robot extends UClient {

	public final Axes[] axes;

	public final Camera[] cameras;

	/**
	 * Le robot géré par le système (un seul à la fois)
	 */
	public static Robot actuel;

	protected Thread thread;
	
	protected boolean stop = false;
	
	protected Robot(String ip, int port, Axes[] axes, Camera... cameras) {
		this.axes = axes;
		this.cameras = cameras;
	}

	public abstract void go(int trackL, int trackR);
	
	
	
}
