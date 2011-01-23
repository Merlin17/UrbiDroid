package fr.flafla.android.urbi.robot;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import fr.flafla.android.urbi.control.Axes;
import fr.flafla.android.urbi.control.Axes.Axis;

/**
 * Classe de gestion du robot Spykee avec urbi.
 * 
 * @author merlin
 * 
 */
public class Spykee extends Robot {

	private static final byte[] detectString = "jpeg 320 240".getBytes();
	
	private Socket cameraSocket;

	/** Buffer d'image */
	byte[] buffer = new byte[320*240*3];
	
	/** Indique si la caméra est déjà initialisée */
	private boolean init = false;
	
	
	public Spykee(String ip, int port) {
		super(ip, port, new Axes[] {
				new Axes(null, new Axis(-100, 100)), new Axes(null, new Axis(-100, 100))
		});
		ensureSocket();
	}
	

	/**
	 * Décodage d'une image provenant du socket
	 * 
	 * @param buffer
	 * @param nb
	 * @return l'image décodé
	 */
	public static Bitmap decodeBitmap(byte[] buffer, int nb) {
		int offset;
		// Detect jpeg begin :
		// jpeg 320 240
		
		for (offset = 0; offset < nb; ++offset) {
			boolean ok = true;
			for (int i = 0; i < detectString.length; ++i)
				if (detectString[i] != buffer[offset+i]) {
					ok = false;
					break;
				}
			if (ok)
				break;
		}
		
		offset += detectString.length+1;
		
		// bouh bouh bouh...
		
		Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, offset, nb);
		
		return bitmap;
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
	 * Lecture d'une image par envoi de la commande "camera.val;"
	 */
	@Override
	protected Bitmap getImage() {
		try {
			Log.i("Robot", "ouverture de la connection");
			InputStream stream = cameraSocket.getInputStream();
			
			// Clear stream
			int nb;
			cameraSocket.getOutputStream().write(("camera.val;\n").getBytes());
			
			// Lecture de l'image
			nb = stream.read(buffer);
			Bitmap bitmap = decodeBitmap(buffer, nb);
			Log.i("Robot", "nb : "+(bitmap==null?0:bitmap.getRowBytes()));
			
			return bitmap;
		} catch (IOException e) {
			Log.e(getClass().getSimpleName(), "Erreur d'accès au robot", e);
			throw new RobotException("Erreur d'accès au robot", e);
		}
	}
	
	/**
	 * Envoi les valeurs sur chaque chenille.
	 */
	@Override
	public void go(int trackL, int trackR) {
		sendScript("trackL.val="+trackL+"&trackR.val="+trackR+";");
	}
}
