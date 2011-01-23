package fr.flafla.android.urbi.robot;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import android.util.Log;
import fr.flafla.android.urbi.control.Axes;
import fr.flafla.android.urbi.control.Axes.Axis;
import fr.flafla.android.urbi.control.Camera;

/**
 * Classe de gestion du robot Spykee avec urbi.
 * 
 * @author merlin
 * 
 */
public class Spykee extends Robot {

	protected class SpykeeCamera extends Camera {
		private boolean init = false;

		@Override
		public void start() {
			if (!init) {
				sendScript("camera.format = 1;");
				sendScript("camera.getSlot(\"val\").notifyChange(uobjects_handle, function() {camera.val});");
				sendScript("var uimg = Channel.new(\"uimg\")|;");
			}
		}

		@Override
		public void stop() {

		}

		@Override
		protected InputStream getImage() {
			return null;
		}
		
	}

	private Socket cameraSocket;

	/** Buffer d'image */
	byte[] buffer = new byte[320*240*3];
	
	/** Indique si la caméra est déjà initialisée */
	private boolean init = false;
	
	
	public Spykee(String ip, int port) {
		super(ip, port);
		this.axes = new Axes[] {
				new Axes(null, new Axis(-100, 100)), new Axes(null, new Axis(-100, 100))
		};
		this.cameras = new Camera[] {
			new SpykeeCamera()
		};
		ensureSocket();
	}
	
	
	/**
	 * Initialisation de la camera : ajout du format et notification dans
	 * camera.val de toutes les nouvelles images.
	 */
	protected void initCamera() {
		if (!init) {
			sendScript("camera.format = 1;"); 
			sendScript("camera.getSlot(\"val\").notifyChange(uobjects_handle, function() { camera.val});");
			
			InputStream stream;
			try {
				cameraSocket = new Socket(IP, PORT);
				stream = cameraSocket.getInputStream();
				stream.read(buffer);
			} catch (IOException e) {
				Log.e(getClass().getSimpleName(), "Erreur d'accès au robot", e);
			}
			init = true;
		}
	}
	
	/**
	 * Envoi les valeurs sur chaque chenille.
	 */
	@Override
	public void move() {
		int trackL = axes[0].y.value;
		int trackR = axes[1].y.value;
		sendScript("trackL.val="+trackL+"&trackR.val="+trackR+";");
	}
}
