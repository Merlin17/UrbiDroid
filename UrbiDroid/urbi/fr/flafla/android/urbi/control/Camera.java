/**
 * 
 */
package fr.flafla.android.urbi.control;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Abstract class that describe a camera. Use with
 * 
 * <pre>
 * camera.addHandler(handler);
 * camera.start();
 * </pre>
 * 
 * @author merlin
 * 
 */
public abstract class Camera {
	public static interface ImageHandler {
		public void handle(InputStream bitmap);
	}

	private List<ImageHandler> handlers;

	/**
	 * Add an image handler to get image from robot
	 * @param handler
	 */
	public void addHandler(ImageHandler handler) {
		if (handlers == null)
			handlers = new ArrayList<Camera.ImageHandler>();
		handlers.add(handler);
	}

	public void removeHandler(ImageHandler handler) {
		if (handlers != null)
			handlers.remove(handler);
	}

	/**
	 * Method to start acquisition
	 */
	public abstract void start();

	/**
	 * Method to stop acquisition
	 */
	public abstract void stop();

	/**
	 * @return the InputStream that contains image
	 */
	protected abstract InputStream getImage();

}
