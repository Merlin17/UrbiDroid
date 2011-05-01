/**
 * 
 */
package fr.flafla.android.urbi;

import java.io.InputStream;

import android.content.Context;
import android.widget.Toast;
import fr.flafla.android.urbi.R;
import fr.flafla.android.urbi.control.Axes;
import fr.flafla.android.urbi.control.Axes.Axis;
import fr.flafla.android.urbi.control.Camera;
import fr.flafla.android.urbi.robot.Robot;

/**
 * Faux robot utile pour le développement. Les actions sont affichées à l'écran
 * et une image enregistrée est passée en boucle.
 * 
 * @author merlin
 * 
 */
public class FakeRobot extends Robot {

	protected class FakeCamera extends Camera {
		protected boolean interrupt = false;

		@Override
		public void start() {
			interrupt = false;
			Thread thread = new Thread() {
				@Override
				public void run() {
					while (!interrupt) {
						notifyHandlers(getImage());
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			thread.start();
		}

		@Override
		public void stop() {
			interrupt = true;
		}

		private InputStream getImage() {
			return context.getResources().openRawResource(R.raw.spykee);
		}

	}

	private Toast toast;
	private final Context context;

	public FakeRobot(Context context) {
		this.axes = new Axes[] {
				new Axes(null, new Axis(-100, 100)), new Axes(null, new Axis(-100, 100))
		// new Axes(new Axis(-100, 100), new Axis(-100, 100))
		};
		this.cameras = new Camera[] {
			new FakeCamera()
		};

		this.context = context;
	}
	
	@Override
	public void move() {
		// Get axes value
		int trackL;
		int trackR;
		if (axes.length == 2) {
			trackL = axes[0].y.value;
			trackR = axes[1].y.value;
		} else {
			trackL = axes[0].x.value;
			trackR = axes[0].y.value;
		}

		// Toast values
		if (toast == null)
			toast = Toast.makeText(context, "go(" + trackL + ", " + trackR
					+ ")", Toast.LENGTH_SHORT);
		else
			toast.setText("go(" + trackL + ", " + trackR + ")");
		toast.show();
	}

}
