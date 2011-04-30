package fr.flafla.android.urbi.robot;

import java.io.ByteArrayInputStream;

import fr.flafla.android.urbi.UBinary;
import fr.flafla.android.urbi.UCallback;
import fr.flafla.android.urbi.UClient;
import fr.flafla.android.urbi.UMessage;
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
	private static final double CAMERA_FREQUENCE = (double) 100 / 1000.;

	UClient uClient;
	SpykeeMovement threadMovement;

	protected class SpykeeMovement extends Thread {
		private int lastL = Integer.MAX_VALUE;
		private int lastR = Integer.MAX_VALUE;
		private boolean interrupt = false;

		@Override
		public void run() {
			while (!interrupt) {
				int trackL = axes[0].y.value;
				int trackR = axes[1].y.value;
				if (lastL != trackL || lastR != trackR) {
					move(trackL, trackR);
					lastL = trackL;
					lastR = trackR;
					System.out.println("move");
				}

				try {
					// Block the thread
					sleep(250);
				} catch (InterruptedException e) {
					// No error : just execute movement
				}
			}
		}

		public void stopThread() {
			interrupt = true;
		}
	}

	protected static class SpykeeCamera extends Camera {
		private static final String UIMG = "uimg";
		private boolean init = false;
		private UClient uClient;

		public SpykeeCamera(String ip, int port) {
			uClient = new UClient(ip, port);
		}

		@Override
		public void start() {
			if (!init) {
				uClient.addCallback(UIMG, new UCallback() {
					@Override
					public boolean handle(UMessage msg) {
						if (msg instanceof UBinary)
							notifyHandlers(new ByteArrayInputStream(((UBinary) msg).array));
						return true;
					}
				});

				uClient.addTag(UIMG);
				uClient.sendScript("camera.format = 1|;");
				uClient.sendScript("camera.getSlot(\"val\").notifyChange(uobjects_handle, function() {camera.val})|;");
				uClient.sendScript("every(" + CAMERA_FREQUENCE + "s) {" + UIMG + "<<camera.val},");
			}
		}

		@Override
		public void stop() {

		}
		
	}

	/**
	 * Initialize the spykee with 2 axes with one axis
	 * @param ip
	 * @param port
	 */
	public Spykee(String ip, int port) {
		super();

		uClient = new UClient(ip, port);
		threadMovement = new SpykeeMovement();

		this.axes = new Axes[] {
				new Axes(null, new Axis(-100, 100)), new Axes(null, new Axis(-100, 100))
		};
		this.cameras = new Camera[] {
			new SpykeeCamera(ip, port)
		};
		uClient.ensureSocket();
	}

	/**
	 * Send axes values to the spykee
	 */
	@Override
	public void move() {
		// TODO make a thread to make movement smooth
		// Just launch thread if necessary
		try {
			if (!threadMovement.isAlive())
				threadMovement.start();
		} catch (IllegalThreadStateException e) {
			// The thread is already in action
			e.printStackTrace();
		}
	}

	public void stop() {
		axes[0].y.value = 0;
		axes[1].y.value = 0;
		move(0, 0);
	}

	protected void move(int trackL, int trackR) {
		uClient.sendScript("trackL.val=" + trackL + "|&trackR.val=" + trackR + "|;");
	}
}
